/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.circleCI;

import com.clarolab.connectors.CIConnector;
import com.clarolab.connectors.impl.circleCI.model.entities.CircleCIJobWithDetailsEntity;
import com.clarolab.connectors.services.exceptions.*;
import com.clarolab.connectors.services.impl.CircleCIConnectorService;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Build;
import com.clarolab.model.Container;
import com.clarolab.model.Executor;
import com.clarolab.model.Report;
import com.clarolab.util.Constants;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.extern.java.Log;
import org.apache.commons.collections4.MapUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log
@Transactional
public class CircleCIConnector implements CIConnector {

    private static CircleCIConnectorService circleCIConnectorService;
    private CircleCIApiClient circleCIApiClient;
    private ApplicationContextService context;

    @Builder
    public CircleCIConnector(String passwordOrToken, ApplicationContextService context){
        this.context = context;
        circleCIApiClient = CircleCIApiClient.builder().context(context).url(CircleCIApiEndpoints.CIRCLE_API_BASE_ENDPOINT).userName(StringUtils.getEmpty()).passwordOrToken(passwordOrToken).build();
        circleCIConnectorService = CircleCIConnectorService.builder().circleCIApiClient(circleCIApiClient).build();
    }

    @Override
    public CIConnector connect() {
        if(circleCIConnectorService.getClientServiceStatus())
            return this;
        return null;
    }

    @Override
    public Container containerExists(Container container) throws ContainerServiceException {
        //TODO to complete
        return null;
    }

    @Override
    public void disconnect() {
        circleCIConnectorService.cleanConnector();
    }

    @Override
    public boolean isConnected() {
        return circleCIConnectorService.getClientServiceStatus();
    }

    @Override
    public List<Container> getAllContainers() throws ContainerServiceException{
        List<Container> containerList = Lists.newArrayList();
        try {
            circleCIApiClient.getAllProjects().forEach( project -> containerList.add(Container.builder().name(project.getReponame()).hiddenData(project.getDescription()).url(project.getUrl()).build()));
        } catch (ConnectorServiceException e) {
            throw new ContainerServiceException(String.format("[getAllViews] : An error occurred trying to get containers"), e);
        }
        return containerList;
    }

    @Override
    public Container getContainer(String containerNameOrUrl) throws ContainerServiceException{
        String project;
        if(StringUtils.isURL(containerNameOrUrl)){
            containerNameOrUrl = StringUtils.removeLastWithCondition(containerNameOrUrl, "/");
            String[] urlElements = containerNameOrUrl.split("/");
            StringBuilder projectName = new StringBuilder(urlElements[urlElements.length-1]);
            project = projectName.toString();
        }else
            project = containerNameOrUrl;


        try{
                return this.getAllContainers().stream().filter(container -> container.getName().equals(project)).findFirst().orElse(null);
        } catch (ContainerServiceException e) {
            throw new ContainerServiceException(String.format("[getContainer] : An error occurred trying to get container " + containerNameOrUrl), e);
        }
    }

    //Container is the project who contains jobs. If the user does not follow the project, the job can be marked as not alive.
    @Override
    public boolean isExecutorAlive(Executor executor, Container container) throws ExecutorServiceException {
        if(container == null) {
            throw new ExecutorServiceException("CircleCI can not determine if " + executor.getName() + " exists, because it needs to know who is the project that contains it.");
        }

        try {
            return circleCIApiClient.getAllProjects().stream().filter(element -> container.getName().equals(element.getReponame())).count() > 0;
        } catch (ConnectorServiceException e) {
            throw new ExecutorServiceException(String.format("[Error trying to get executor %s]", executor.getName()), e);
        }
    }

    @Override
    public boolean isExecutorAlive(Executor executor) throws ExecutorServiceException {
        return this.isExecutorAlive(executor, executor.getContainer());
    }

    @Override
    public List<Executor> getAllExecutors(Container container) throws ExecutorServiceException {
        return this.getAllExecutors(container, Constants.DEFAULT_MAX_BUILDS_TO_PROCESS);
    }

    @Override
    public List<Executor> getAllExecutors(Container container, int maxBuildsToRetrieve) throws ExecutorServiceException {
        List<Executor> executors = Lists.newArrayList();
        try {
            Map<String, List<CircleCIJobWithDetailsEntity>> jobs = getAllJobs(container.getHiddenData(), maxBuildsToRetrieve);
            for (String job : jobs.keySet()) {
                Executor executor = Executor.builder().name(job).description(container.getDescription() + ">>" + job).url(null).enabled(true).timestamp(DateUtils.now()).build();
                context.setExecutorToContext(executor);
                jobs.get(job).forEach(circleCiJob -> {
                    try {
                        executor.add(generateBuildForExecutor(container, job, circleCiJob));
                    } catch (ReportServiceException e) {
                        throw new RuntimeException(String.format("Report could not be generated in container %d, executor: %d", container.getId(), executor.getId()), e);
                    }
                });

                Collections.reverse(executor.getBuilds());
                executors.add(executor);
            }

            List<Executor> answer = new ArrayList<>(executors.size());
            for (Executor executor : executors) {
                if (executor.getContainer() == null) {
                    executor.setContainer(container);
                    answer.add(context.getExecutorService().save(executor));
                } else {
                    answer.add(context.getExecutorService().update(executor));
                }
            }

            return answer;
        }catch(ConnectorServiceException e){
            throw new ExecutorServiceException(String.format("[getAllExecutors] : An error occurred trying to get executors for container %s", container.getName()), e);
        }
    }

    @Override
    public List<Executor> checkForNewExecutors(Container container, int maxBuildsToRetrieve) throws ExecutorServiceException {
        List<Executor> executors = Lists.newArrayList();
        try {
            Map<String, List<CircleCIJobWithDetailsEntity>> jobs = getAllJobs(container.getHiddenData(), maxBuildsToRetrieve);
            for (String job : jobs.keySet()) {
                if (!container.hasExecutor(job)) {
                    Executor executor = Executor.builder().name(job).description(container.getDescription() + ">>" + job).url(null).enabled(true).timestamp(DateUtils.now()).build();
                    context.setExecutorToContext(executor);
                    jobs.get(job).forEach(circleCiJob -> {
                        try {
                            executor.add(generateBuildForExecutor(container, job, circleCiJob));
                        } catch (ReportServiceException e) {
                            throw new RuntimeException(String.format("Report could not be generated in container %d, executor: %d", container.getId(), executor.getId()), e);
                        }
                    });

                    Collections.reverse(executor.getBuilds());
                    executors.add(executor);
                }
            }

            List<Executor> answer = new ArrayList<>(executors.size());
            for (Executor executor : executors) {
                if (executor.getContainer() == null) {
                    executor.setContainer(container);
                    answer.add(context.getExecutorService().save(executor));
                } else {
                    answer.add(context.getExecutorService().update(executor));
                }
            }

            return answer;
        }catch(ConnectorServiceException e){
            throw new ExecutorServiceException(String.format("[getAllExecutors] : An error occurred trying to get executors for container %s", container.getName()), e);
        }
    }

    @Override
    public List<Build> getExecutorBuilds(Executor executor) throws BuildServiceException {
        return this.getExecutorBuilds(executor, Constants.DEFAULT_MAX_BUILDS_TO_PROCESS);
    }

    @Override
    public List<Build> getExecutorBuilds(Executor executor, int maxBuildsToRetrieve) throws BuildServiceException {
        context.setExecutorToContext(executor);
        Container container = executor.getContainer();
        log.log(Level.INFO, String.format("Trying to get builds for executor %s on container %s", executor.getName(), container.getName()));
        List<Build> builds = Lists.newArrayList();
        try {

            Map<String, List<CircleCIJobWithDetailsEntity>> jobs = this.getAllJobs(container.getHiddenData(), maxBuildsToRetrieve);
            if(jobs.containsKey(executor.getName()))
                jobs.get(executor.getName()).forEach(circleCiJob -> {
                    try {
                        builds.add(generateBuildForExecutor(executor.getContainer(), circleCiJob.getJobName(), circleCiJob));
                    } catch (ReportServiceException e) {
                        throw new RuntimeException(e);
                    }
                });
            else
                return Lists.newArrayList();

            Collections.reverse(builds);
            return builds;
        } catch (ConnectorServiceException e) {
            throw new BuildServiceException("[getExecutorBuilds] : An error occurred trying to get builds for " + executor.getName());
        }
    }

    @Override
    public List<Build> getExecutorBuildsGreaterThan(Executor executor, int maxBuildsToRetrieve, int greaterThanBuildNumber) throws BuildServiceException {
        return null;
    }

    @Override
    public int getExecutorLatestBuild(Executor executor) throws BuildServiceException {
        try {
            int fromLimit = 0;
            Map<String, List<CircleCIJobWithDetailsEntity>> map;
            do {
                map = getAllJobs(executor.getContainer().getHiddenData(), fromLimit, Constants.DEFAULT_MAX_BUILDS_TO_RETRIEVE, Constants.DEFAULT_MAX_BUILDS_TO_PROCESS, true);
                if (map.containsKey(executor.getName()))
                    return map.get(executor.getName()).get(0).getBuild_num();
                fromLimit += Constants.DEFAULT_MAX_BUILDS_TO_RETRIEVE;
            } while (MapUtils.isNotEmpty(map));
        }catch(ConnectorServiceException e){
            //Error trying to get latest build for executor
        }
        throw new BuildServiceException("[getExecutorLatestBuild] : An error occurred trying to get latest build for " + executor.getName());
    }

    @Override
    public Build getExecutorBuild(Executor executor, int buildNumber) throws BuildServiceException {
        CircleCIJobWithDetailsEntity ciJobWithDetailsEntity;
        try {
            ciJobWithDetailsEntity = getAllJobs(executor.getContainer().getHiddenData())
                    .get(executor.getName()).stream().filter(job -> job.getBuild_num() == buildNumber).findFirst().orElse(null);
        } catch (ConnectorServiceException e) {
            throw new BuildServiceException("[getExecutorLatestBuild] : An error occurred trying to get latest build #"+buildNumber+" for " + executor.getName());
        }
        if(ciJobWithDetailsEntity != null)
            return ciJobWithDetailsEntity.getBuild();
        throw new BuildServiceException("[getExecutorLatestBuild] : An error occurred trying to get latest build #"+buildNumber+" for " + executor.getName());
    }

    private Map<String, List<CircleCIJobWithDetailsEntity>> getAllJobs(String[] parameters) throws ConnectorServiceException {
        return this.getAllJobs(parameters, Constants.DEFAULT_MAX_BUILDS_TO_RETRIEVE);
    }

    private Map<String, List<CircleCIJobWithDetailsEntity>> getAllJobs(String[] parameters, int maxBuildsToRetrieve) throws ConnectorServiceException {
        return getAllJobs(parameters, maxBuildsToRetrieve, false);
    }

    private Map<String, List<CircleCIJobWithDetailsEntity>> getAllJobs(String[] parameters, int maxBuildsToRetrieve, boolean plainJobs) throws ConnectorServiceException {
        return getAllJobs(parameters, 0, Constants.DEFAULT_MAX_BUILDS_TO_RETRIEVE, maxBuildsToRetrieve, plainJobs);
    }

    private Map<String, List<CircleCIJobWithDetailsEntity>> getAllJobs(String[] parameters, int fromPage, int showPerPage, int maxBuildsToRetrieve, boolean plainJobs) throws ConnectorServiceException {
        boolean shouldStop = false;
        Map<String, List<CircleCIJobWithDetailsEntity>> collectionMap = Maps.newHashMap();
        Map<String, List<CircleCIJobWithDetailsEntity>> collection = circleCIApiClient.getAllJobs(parameters[0], parameters[1], parameters[2], fromPage, showPerPage, plainJobs);

        while(MapUtils.isNotEmpty(collection) && !shouldStop){
            collectionMap = Stream.concat(collectionMap.entrySet().stream(), collection.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (value1, value2) -> Stream.concat(value1.stream(), value2.stream()).collect(Collectors.toList())));

            shouldStop = allElementsHasBuilds(collectionMap, maxBuildsToRetrieve);

            if(!shouldStop) {
                fromPage += showPerPage;
                collection = circleCIApiClient.getAllJobs(parameters[0], parameters[1], parameters[2], fromPage, showPerPage, plainJobs);
            }
        }

        for(Map.Entry<String, List<CircleCIJobWithDetailsEntity>> entry: collectionMap.entrySet()){
            collectionMap.put(entry.getKey(), collectionMap.get(entry.getKey()).stream().sorted(Comparator.comparing(CircleCIJobWithDetailsEntity::getBuild_num).reversed()).limit(maxBuildsToRetrieve).collect(Collectors.toList()));
        }

        return collectionMap;
    }

    private boolean allElementsHasBuilds(Map<String, List<CircleCIJobWithDetailsEntity>> collectionMap, int maxBuildsToRetrieve){
        return collectionMap.entrySet().stream().filter(element -> element.getValue().size() >= maxBuildsToRetrieve).count() == collectionMap.size();
    }

    private Build generateBuildForExecutor(Container container, String job, CircleCIJobWithDetailsEntity circleCiJob) throws ReportServiceException {
        log.info("Generating build for: "+container.getName()+ " / " +job + "#" + circleCiJob.getBuild_num());
        Build build = circleCiJob.getBuild();

        Report report = circleCIApiClient.generateReportFromArtifact(circleCiJob.getArtifactWithReport(), circleCiJob.getArtifactWithApplicationTestingEnvironmentVersion());

        if(report != null) {
                report.updateExecutionDateIfIsNeeded(build.getExecutedDate());
        }else{
            report = Report.getDefault();
            report.setFailReason("This report does not contain test cases.");
        }

        report.setDescription("This report belongs to " + container.getName() + " / " + job + "#" + build.getNumber());

        build.setReport(report);

        context.setBuild(build);
        context.save(build);

        return build;
    }

    public ApplicationContextService getContext() {
        return context;
    }
}
