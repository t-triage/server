/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.integration.test.connectors;

import com.clarolab.connectors.CIConnector;
import com.clarolab.connectors.services.exceptions.BuildServiceException;
import com.clarolab.connectors.services.exceptions.ContainerServiceException;
import com.clarolab.connectors.services.exceptions.ExecutorServiceException;
import com.clarolab.integration.BaseIntegrationTest;
import com.clarolab.model.Build;
import com.clarolab.model.Connector;
import com.clarolab.model.Container;
import com.clarolab.model.Executor;
import com.clarolab.runner.category.GitlabCIConnectorCategory;
import com.clarolab.util.DateUtils;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import lombok.extern.java.Log;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.stream.Collectors;

import static com.clarolab.util.Constants.DEFAULT_MAX_BUILDS_TO_PROCESS;

@Log
@Category(GitlabCIConnectorCategory.class)
@Rollback(false)
public class GitLabCIConnectorTest extends BaseIntegrationTest {

    @Ignore
    @Test
    public void checkVPN() {
        CIConnector connector = ciConnectors.get("GITLAB_FLUXIT").connect();
        MatcherAssert.assertThat(connector.isConnected(), Matchers.is(true));

    }

    @Ignore
    @Test
    public void verifyDisconnectionTest() {
        CIConnector connector = ciConnectors.get("GITLAB_FLUXIT").connect();
        connector.disconnect();
        MatcherAssert.assertThat(connector.isConnected(), Matchers.is(false));
        refreshConnectors();
    }

    @Ignore
    @Test
    public void getAllContainerTest() throws ContainerServiceException {
        CIConnector connector = ciConnectors.get("GITLAB_FLUXIT").connect();

        List<Container> containers = connector.getAllContainers();
        log.info("Size: " + containers.size());
        containers.forEach(this::showContainerInfo);
        MatcherAssert.assertThat(containers.size(), Matchers.greaterThan(0));
        connector.disconnect();
        MatcherAssert.assertThat(connector.isConnected(), Matchers.is(false));
        refreshConnectors();
    }

    @Ignore
    @Test
    public void getContainerTest() throws ContainerServiceException {
        CIConnector connector = ciConnectors.get("GITLAB_FLUXIT").connect();
        Container container = connector.getContainer("banco-galicia/back-apps/553");
        showContainerInfo(container);
        MatcherAssert.assertThat(container, Matchers.notNullValue());
        connector.disconnect();
        MatcherAssert.assertThat(connector.isConnected(), Matchers.is(false));
        refreshConnectors();
    }

    @Ignore
    @Test
    public void getContainerFromUrlTest() throws ContainerServiceException {
        CIConnector connector = ciConnectors.get("GITLAB_FLUXIT").connect();
        Container container = connector.getContainer(urls.get("GITLAB_FLUX_PROJECT"));
        showContainerInfo(container);
        MatcherAssert.assertThat(container, Matchers.notNullValue());
        connector.disconnect();
        MatcherAssert.assertThat(connector.isConnected(), Matchers.is(false));
        refreshConnectors();
    }

    @Ignore
    @Test
    public void getExecutorsOnContainerTest() throws ContainerServiceException, ExecutorServiceException {
        Connector c = connectorService.findByName("GITLAB_FLUXIT");
        if(c == null){
            c = connectors.get("GITLAB_FLUXIT");
            c = connectorService.save(c);
        }
        CIConnector connector = ciConnectors.get("GITLAB_FLUXIT").connect();

        Container container = connector.getContainer("banco-galicia/back-apps/553");
        evaluateContainer(c, container);
    }

    @Ignore
    @Test
    public void getExecutorsWithRangeOnContainerTest() throws ContainerServiceException, ExecutorServiceException {
        Connector c = connectorService.findByName("GITLAB_FLUXIT");
        if(c == null){
            c = connectors.get("GITLAB_FLUXIT");
            c = connectorService.save(c);
        }
        CIConnector connector = ciConnectors.get("GITLAB_FLUXIT").connect();

        Container container = connector.getContainer("banco-galicia/back-apps/553");
        evaluateContainer(c, container, 5);

        connector.disconnect();
        MatcherAssert.assertThat(connector.isConnected(), Matchers.is(false));
        refreshConnectors();
    }

    @Ignore
    @Test
    public void isJobAlive() throws ContainerServiceException, ExecutorServiceException {
        Connector c = connectorService.findByName("GITLAB_FLUXIT");
        if(c == null){
            c = connectors.get("GITLAB_FLUXIT");
            connectorService.save(c);
        }
        CIConnector connector = ciConnectors.get("GITLAB_FLUXIT").connect();

        Container container = connector.getContainer("banco-galicia/back-apps/553");
        Executor executor = Executor.builder().container(container).name("automation-tests").description("description").url("https://fake.com").enabled(true).timestamp(DateUtils.now()).build();
        MatcherAssert.assertThat(connector.isExecutorAlive(executor), Matchers.is(true));
    }

    @Ignore
    @Test
    public void isJobNotAlive() throws ContainerServiceException, ExecutorServiceException {
        Connector c = connectorService.findByName("GITLAB_FLUXIT");
        if(c == null){
            c = connectors.get("GITLAB_FLUXIT");
            connectorService.save(c);
        }
        CIConnector connector = ciConnectors.get("GITLAB_FLUXIT").connect();

        Container container = connector.getContainer("banco-galicia/back-apps/553");
        Executor executor = Executor.builder().container(container).name("automation-tests-not-exist").description("description").url("https://fake.com").enabled(true).timestamp(DateUtils.now()).build();
        MatcherAssert.assertThat(connector.isExecutorAlive(executor), Matchers.is(false));
    }

    @Ignore
    @Test
    public void checkNewExecutors() throws ContainerServiceException, ExecutorServiceException {
        Connector c = connectorService.findByName("GITLAB_FLUXIT");
        if(c == null){
            c = connectors.get("GITLAB_FLUXIT");
            c = connectorService.save(c);
        }
        CIConnector connector = ciConnectors.get("GITLAB_FLUXIT").connect();

        Container container = connector.getContainer("banco-galicia/back-apps/553");
        container.setConnector(c);
        containerService.save(container);
        connector = container.getCIConnector();
        initContext(connector);
        connector.connect();

        Executor executor = Executor.builder().name("automation-tests-not-exist").description("description").url("https://fake.com").enabled(true).timestamp(DateUtils.now()).build();
        container.add(executor);
        executorService.save(executor);

        MatcherAssert.assertThat(container.getExecutors().size(), Matchers.is(1));
        log.info("Executors are " + container.getExecutors().size());
        List<Executor> executors = connector.checkForNewExecutors(container, DEFAULT_MAX_BUILDS_TO_PROCESS);
        log.info("Executors are " + executors.size());
        MatcherAssert.assertThat(container.getExecutors().size(), Matchers.is(executors.size()+1));
        log.info("Executors are " + container.getExecutors().size());
    }

    @Ignore
    @Test
    public void checkExecutorBuilds() throws ContainerServiceException, BuildServiceException {
        Connector c = connectorService.findByName("GITLAB_FLUXIT");
        if(c == null){
            c = connectors.get("GITLAB_FLUXIT");
            c = connectorService.save(c);
        }
        CIConnector connector = ciConnectors.get("GITLAB_FLUXIT").connect();

        Container simulatedContainerOnDB = connector.getContainer("banco-galicia/back-apps/553");
        simulatedContainerOnDB.setConnector(c);
        simulatedContainerOnDB = containerService.save(simulatedContainerOnDB);

        connector.getContext().setContainer(simulatedContainerOnDB);
        c.add(simulatedContainerOnDB);

        Executor simulatedExecutorOnDB = Executor.builder().name("automation-tests").description("description").url("https://fake.com").builds(Lists.newArrayList()).enabled(true).timestamp(DateUtils.now()).build();
        connector.getContext().setExecutorToContext(simulatedExecutorOnDB);
        simulatedContainerOnDB.add(simulatedExecutorOnDB);
        List<Build> builds = connector.getExecutorBuilds(simulatedExecutorOnDB, 5);
        int originalSize = builds.size();
        MatcherAssert.assertThat(originalSize, Matchers.lessThanOrEqualTo(5));
        int lastPosition = builds.size()-1;
        builds.remove(lastPosition);
        lastPosition = builds.size()-1;
        builds.remove(lastPosition);
        lastPosition = builds.size()-1;
        builds.remove(lastPosition);
        simulatedExecutorOnDB.setBuilds(null);
        simulatedExecutorOnDB.add(builds);
        int simulateLatestBuildOnDB = simulatedExecutorOnDB.getLastExecutedBuild().getNumber();
        int latestBuildOnCI = connector.getExecutorLatestBuild(simulatedExecutorOnDB);
        log.info(String.format("For executor '%s' was found as last executed build: #%d on database and #%d on CI tool.",simulatedExecutorOnDB.getName(), simulateLatestBuildOnDB, latestBuildOnCI));
        if (simulateLatestBuildOnDB < latestBuildOnCI) {
            builds = connector.getExecutorBuilds(simulatedExecutorOnDB, DEFAULT_MAX_BUILDS_TO_PROCESS).stream().filter(build -> build.getNumber() > simulateLatestBuildOnDB).collect(Collectors.toList());
            MatcherAssert.assertThat(builds.size(), Matchers.lessThanOrEqualTo(DEFAULT_MAX_BUILDS_TO_PROCESS));
            simulatedExecutorOnDB.add(builds);
        }

        MatcherAssert.assertThat(simulatedExecutorOnDB.getBuilds().size(), Matchers.is(Ints.checkedCast(simulatedExecutorOnDB.getBuilds().stream().filter(distinctByKey(Build::getBuildId)).count())));

    }

    private void showContainerInfo(Container container){
        log.info(container.getName()+ " ; "+ container.getHiddenData()[0]+ " ; "+ container.getHiddenData()[1]+ " ; "+ container.getHiddenData()[2]+ " ; "+ container.getUrl()+ " ; "+ container.getDescription());
    }


}
