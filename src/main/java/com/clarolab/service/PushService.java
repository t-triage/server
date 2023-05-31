/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 *//*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.agents.TriageAgent;
import com.clarolab.connectors.impl.jenkins.services.JenkinsArtifactsService;
import com.clarolab.connectors.impl.utils.report.ReportUtils;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.dto.push.ArtifactDTO;
import com.clarolab.dto.push.DataDTO;
import com.clarolab.model.*;
import com.clarolab.model.auth.ServiceAuth;
import com.clarolab.model.types.*;
import com.clarolab.util.DateUtils;
import com.clarolab.util.JsonUtils;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@Log
public class PushService {

    @Autowired
    private ExecutorService executorService;
    @Autowired
    private PushService pushService;
    @Autowired
    private ReportService reportService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private TriageAgent triageAgent;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private ServiceAuthService serviceAuthService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private TriageSpecService triageSpecService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private UserService userService;

    public Build push(DataDTO dataDTO) {
        log.info("Processing push from " + dataDTO.getJobName());
        log.info("Artifacts: " + dataDTO.getArtifacts().size());
        if(dataDTO.getArtifacts().size()>1 && dataDTO.getArtifacts().get(0).getFileName().toLowerCase().contains("jest")){
            dataDTO.setJobName(dataDTO.getJobName()+ "/" + dataDTO.getArtifacts().get(0).getFileName());}
        Report report=null;
        Executor executor = null;
        long jobId = dataDTO.getJobId();
        Container container;
        if (jobId == 0) { //job = 0, means that the push is from a complete view.
            container = containerService.findByName(dataDTO.getViewName());
            if(container==null){
                //If the container doesnt exist in the DDBB, I will try to create a new one from some previous container in the connector and his product
                log.info(format("Trying to create container {%s} since is not in the system", dataDTO.getViewName()));
                ServiceAuth serviceAuth = serviceAuthService.findByClientId(dataDTO.getClientId()).orElse(null);

                if(serviceAuth != null) {
                    TriageSpec spec;
                    Connector connector = serviceAuth.getConnector();
                    Optional<Container> anyContainer = connector.getAnyContainer(); //Some container of the connector
                    if (anyContainer.isPresent()) {
                        container = createNewContainerFromConnector(dataDTO, connector, anyContainer); //The container has the product. So i will "clone" the container info
                        spec = createTriageFlowSpec(container, anyContainer.get(), executor);
                        triageSpecService.save(spec);
                    }
                    else {
                        log.info(format("There aren't any old container. Trying to create a new one for {%s}", dataDTO.getViewName()));
                        //There is not any container, then I have to create all the data including the product and the spec. However the connector must exist.
                        Product product  = createNewProductFrom(dataDTO, connector);
                        container = createNewContainerFromConnector(dataDTO, connector, product);
                        Optional<User> anyAdminUser = userService.getAnyAdminUser();
                        if(anyAdminUser.isPresent()) { //I need some admin user
                            spec = createTriageFlowSpec(container, executor, 2, 0, 50, "0 0 17 * * TUE,THU", 1, anyAdminUser.get());
                            triageSpecService.save(spec);
                        }
                        else{
                            log.severe("There aren't admin user needed to create flow spec for " + dataDTO.getClientId());
                            return null;
                        }
                    }

                }
                else {
                    log.severe("Unable to create container since there is not enough data");
                    return null;
                }
            }

            //TODO: Review
//            if(!Strings.isNullOrEmpty(container.getProductVersionExecutor()))
//                if(dataDTO.getJobName().equals(container.getProductVersionExecutor())) {
//                    ArtifactDTO artifact = dataDTO.getArtifacts().stream().filter(e -> e.getFileName().matches("(\\W|\\w)*(V|v)ersion\\.json")).findFirst().orElse(null);
//                    String content = artifact != null ? artifact.getContent() : StringUtils.getEmpty();
//                    container.setProductVersion(JsonUtils.getApplicationVersionFromJson(content));
//                    containerService.update(container);
//                    log.info(String.format("Container %s was updated with application testing environment version", container.getName()));
//                    return null;
//                }

            executor = getOrCreateExecutor(dataDTO, container);

        } else {
            //A build for an specific executor only.
            executor = executorService.find(dataDTO.getJobId());
        }

        if (executor == null) {
            log.severe( dataDTO.getJobName() + " not found as Job");
            return null;
        }

        if (!executor.isHierarchicalyEnabled()) {
            log.info("Processing push for disabled job " + dataDTO.getJobName() + ". Skipping");
            return null;
        }

        //Validate that the build is not already processed
        Build lastExecutedBuild = buildService.getLastBuild(executor);
        if(lastExecutedBuild!=null && lastExecutedBuild.getNumber() >= dataDTO.getBuildNumber()){
            log.info( dataDTO.getJobName() + " skipped due to it's already in the DDBB");
            return null;
        }

        //Since is a new build, i have to create it and then to triage it.
        Build build = createNewBuild(executor, StatusType.getStatus(dataDTO.getBuildStatus()), (int) dataDTO.getBuildNumber(), dataDTO.getJobName(), dataDTO.getTimestamp(), dataDTO.getArtifacts(), dataDTO.getBuildUrl());

        ApplicationContextService context = getActualAppContext(executor);

        if (dataDTO.getArtifacts() == null || dataDTO.getArtifacts().isEmpty())
            log.warning("Artifacts are empty! May be a problem!");

        ArtifactDTO artifact = dataDTO.getArtifacts().stream().filter(e -> e.getFileName().matches("(\\W|\\w)*(V|v)ersion\\.json")).findFirst().orElse(null);
        String content = artifact != null ? artifact.getContent() : StringUtils.getEmpty();
        String version = JsonUtils.getApplicationVersionFromJson(content);

        ArtifactDTO logArtifact = dataDTO.getArtifacts().stream().filter(e -> LogType.isCVSLog(e.getFileName())).findFirst().orElse(null);
         String logs = logArtifact != null ? logArtifact.getContent() : StringUtils.getEmpty();

        log.info("JobName: " + dataDTO.getJobName());

        if (executor.getInheritReportType() != null && executor.getInheritReportType().allowsMultipleArtifacts() ) {
            if (dataDTO.getArtifacts().size()>1) {
                List<ArtifactDTO> aux = new ArrayList<ArtifactDTO>(dataDTO.getArtifacts());
                int contador = 1;
                aux.remove(0);
                for (ArtifactDTO a : aux) {
                    DataDTO dataDTO1 = new DataDTO();
                    dataDTO1.setViewName(dataDTO.getViewName());
                    String auxiliar[] = dataDTO.getJobName().split("/");
                    String jobName = auxiliar[0] + "/" + a.getFileName();
                    dataDTO1.setJobName(jobName);
                    dataDTO1.setJobUrl(dataDTO.getJobUrl());
                    dataDTO1.setJobId(dataDTO.getJobId());
                    dataDTO1.setBuildNumber(dataDTO.getBuildNumber());
                    dataDTO1.setBuildStatus(dataDTO.getBuildStatus());
                    dataDTO1.setTimestamp(dataDTO.getTimestamp());
                    log.info("Artifacts: " + dataDTO.getArtifacts().size());
                    dataDTO1.setArtifacts(new ArrayList<ArtifactDTO>());
                    dataDTO1.getArtifacts().add(a);
                    if (dataDTO.getArtifacts().size() > contador) {
                        dataDTO.getArtifacts().remove(contador);
                    }
                    contador += 1;
                    pushService.push(dataDTO1);

                }
            }
        }

        if(dataDTO.getArtifacts().get(0).getFileName().toLowerCase().contains("jest")){
            report = ReportUtils.builder().context(context).applicationTestingEnvironmentVersion(version).cvsLogs(logs).build().getReportDataForJest(dataDTO);
        }
        else {
            report = ReportUtils.builder().context(context).applicationTestingEnvironmentVersion(version).cvsLogs(logs).build().getReportData(dataDTO);
        }

        if(report==null){
            log.severe("Report cannot be processed. Report is null, empty or malformed.");
            log.info("DataDTO: " + dataDTO.toJsonString());
            return null;
        }

        if (report.getType() == ReportType.CYPRESS) {
            for (TestExecution t : report.getTestExecutions()) {
                String last = t.getScreenshotURL();
                t.setScreenshotURL("https://jenkins.dev.lithium.com/view/" + dataDTO.getViewName() + "/job/" + dataDTO.getJobName() + "/" + dataDTO.getBuildNumber() + "/cypressreports/" + last);
            }
        }
        else {
            //Associate image, video, log from artifact to test case
            //TODO: find a way to decouple from JenkinsArtifactsService, it should be generic and current name can cause confusion.
            JenkinsArtifactsService jenkinsArtifactsService = JenkinsArtifactsService.builder().build();
            for (TestExecution testcase : report.getTestExecutions()) {
                testcase.setScreenshotURL(jenkinsArtifactsService.getImageArtifact(testcase, build.getArtifacts()));
                testcase.setVideoURL(jenkinsArtifactsService.getVideoArtifact(testcase, build.getArtifacts()));
            }
        }


        report.setExecutiondate(dataDTO.getTimestamp());
        build.setReport(report);
        build = buildService.save(build);
        executor.add(build);
        executorService.update(executor);
        // triageAgent.processExecutor(executor);
        log.info( dataDTO.getJobName() + " processed");
        return build;
    }

    private Product createNewProductFrom(DataDTO dataDTO, Connector connector) {

        String description = "AUTOGENERATED PRODUCT FROM - " + dataDTO.getViewName();
        Product product = Product
                .builder()
                .enabled(true)
                .description(description)
                .name(dataDTO.getViewName())
                .deadlines(Lists.newArrayList())
                .containers(Lists.newArrayList())
                .build();
        return productService.save(product);
    }

    private TriageSpec createTriageFlowSpec(Container container, Container oldContainer, Executor executor) {
        TriageSpec spec = triageSpecService.geTriageFlowSpecByContainer(oldContainer);
        return createTriageFlowSpec(container, executor, spec.getEveryWeeks(), spec.getExpectedMinAmountOfTests(), spec.getExpectedPassRate(), spec.getFrequencyCron(), spec.getPriority(), spec.getTriager());
    }

    private TriageSpec createTriageFlowSpec(Container container, Executor executor, int weeks, int minAmountOfTests, int expectedPassRate, String frequencyCron, int priority, User triager) {
        return TriageSpec
                .builder()
                .everyWeeks(weeks)
                .executor(executor)
                .expectedMinAmountOfTests(minAmountOfTests)
                .expectedPassRate(expectedPassRate)
                .frequencyCron(frequencyCron)
                .priority(priority)
                .triager(triager)
                .container(container)
                .build();
    }

    private Executor getOrCreateExecutor(DataDTO dataDTO, Container container) {
        Executor executor;
        executor = executorService.findExecutorByContainerAndName(container, dataDTO.getJobName());
        if (executor == null) {
            executor = Executor.builder()
                    .name(dataDTO.getJobName())
                    .description(container.getDescription() + ">>" + dataDTO.getJobName())
                    .url(dataDTO.getJobUrl())
                    .enabled(true)
                    .container(container)
                    .timestamp(DateUtils.now())
                    .build();
            executor = executorService.save(executor);
            container.add(executor);
        }

        //if the population is push... I have to change the population mode to push
        container.setPopulateMode(PopulateMode.PUSH);
        containerService.update(container);
        return executor;
    }

    private Container createNewContainerFromConnector(DataDTO dataDTO, Connector connector, Optional<Container> anyContainer) {
        Product product = anyContainer.get().getProduct();
        return createNewContainerFromConnector(dataDTO, connector, product);
    }

    private Container createNewContainerFromConnector(DataDTO dataDTO, Connector connector, Product product) {
        Container container = Container.builder()
                .name(dataDTO.getViewName())
                .url(dataDTO.getBuildUrl())
                .connector(connector)
                .product(product)
                .populateMode(PopulateMode.PUSH)
                .description("AUTOGENERATED CONTAINER FROM - " + dataDTO.getClientId())
                .build();

        log.info(format("Creating Container {%s} with Product {%s} and Connector {%s}, since is a new container from push service", container.getName(), product.getName(), connector.getName()));

        container = containerService.save(container);

        connector.add(container);
        product.add(container);

        connectorService.update(connector);
        productService.update(product);
        return container;
    }

    private ApplicationContextService getActualAppContext(Executor executor) {
        return ApplicationContextService
                .builder()
                .product(executor.getContainer().getProduct())
                .executorService(executorService)
                .buildService(buildService)
                .testCaseService(testCaseService)
                .propertyService(propertyService)
                .containerService(containerService)
                .transactionManager(transactionManager)
                .container(executor.getContainer())
                .build();
    }

    public Build createNewBuild(Executor executor, StatusType buildStatus, int buildNumber, String buildId, long buildTime, List<ArtifactDTO> artifacts, String buildUrl){
        return Build.builder()
                .number(buildNumber)
                .buildId(buildId)
                .displayName(executor.getName()+"#"+buildNumber)
                .url(buildUrl)
                .executedDate(buildTime)
                .status(buildStatus)
                .report(null)
                .executor(executor)
                .container(executor.getContainer())
                .artifacts(getBuildArtifacts(artifacts))
                .enabled(true)
                .populateMode(PopulateMode.PUSH)
                .timestamp(DateUtils.now())
                .build();
    }

    private List<Artifact> getBuildArtifacts(List<ArtifactDTO> artifactsDtos){
        List<Artifact> artifacts = Lists.newArrayList();

        artifactsDtos.stream().filter(artifact -> artifact.getFileType().equals("log")).collect(Collectors.toList()).forEach(artifactDTO ->
                artifacts.add(Artifact.builder().name(artifactDTO.getFileName()).url(artifactDTO.getUrl()).artifactType(ArtifactType.STANDARD_OUTPUT).build())
        );

        artifactsDtos
                .stream()
                .filter(artifact -> Artifact.isImageFile(artifact.getFileName()))
                .forEach(imageArtifact -> artifacts.add(Artifact.builder()
                        .name(imageArtifact.getFileName())
                        .url(imageArtifact.getUrl())
                        .artifactType(ArtifactType.IMAGE).build()));

        return artifacts;
    }
}