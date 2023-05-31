package com.clarolab.connectors.impl.qtest;

import com.clarolab.connectors.CIConnector;
import com.clarolab.connectors.services.exceptions.BuildServiceException;
import com.clarolab.connectors.services.exceptions.ContainerServiceException;
import com.clarolab.connectors.services.exceptions.ExecutorServiceException;
import com.clarolab.connectors.services.impl.QTestConnectorService;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Build;
import com.clarolab.model.Container;
import com.clarolab.model.Executor;
import lombok.Builder;

import java.util.List;

//import com.clarolab.qtest.client.QTestApiClient;

public class QTestConnector implements CIConnector {

//    private QTestApiClient apiClient;
    private QTestConnectorService qTestConnectorService;
    private QTestCLService clService;
    private QTestTSService tsService;
    private ApplicationContextService context;

    @Builder
    private QTestConnector(String url, String passwordOrToken, ApplicationContextService context) {
        this.context = context;
//        this.apiClient = QTestApiClient.builder().baseUrl(url).userPasswordOrToken(passwordOrToken).hasToken(true).build();
//        qTestConnectorService = QTestConnectorService.builder().qTestApiClient(apiClient).build();
//        clService = QTestCLService.builder().qTestApiClient(apiClient).build();
//        tsService = QTestTSService.builder().qTestApiClient(apiClient).build();
    }

    @Override
    public CIConnector connect() {
        return this;
    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean isConnected() {
        return qTestConnectorService.getClientServiceStatus();
    }

    @Override
    public List<Container> getAllContainers() throws ContainerServiceException {
        return null; //clService.getAllCycleLifeAsContainers();
    }

    @Override
    public Container getContainer(String containerNameOrUrl) throws ContainerServiceException {
        return null; //clService.getCycleLifeCrossAllProjectsAsContainer(containerNameOrUrl);
    }

    @Override
    public Container containerExists(Container container) throws ContainerServiceException {
        return null;
    }

    @Override
    public boolean isExecutorAlive(Executor executor, Container container) throws ExecutorServiceException {
        return false;
    }

    @Override
    public boolean isExecutorAlive(Executor executor) throws ExecutorServiceException {
        return false;
    }

    @Override
    public List<Executor> getAllExecutors(Container container) throws ExecutorServiceException {
        return null;
    }

    @Override
    public List<Executor> getAllExecutors(Container container, int maxBuildsToRetrieve) throws ExecutorServiceException {
        return null; //tsService.getAllTestSuiteForCycleLifeAsExecutors(container.getName());
    }

    @Override
    public List<Executor> checkForNewExecutors(Container container, int maxBuildsToRetrieve) throws ExecutorServiceException {
        return null;
    }

    @Override
    public List<Build> getExecutorBuilds(Executor executor) throws BuildServiceException {
        return null;
    }

    @Override
    public List<Build> getExecutorBuilds(Executor executor, int maxBuildsToRetrieve) throws BuildServiceException {
        //return tsService.getTestSuiteOnCycleLifeAsExecutor(executor.getContainer().getName(), executor.getName());
        return null;
    }

    @Override
    public List<Build> getExecutorBuildsGreaterThan(Executor executor, int maxBuildsToRetrieve, int greaterThanBuildNumber) throws BuildServiceException {
        return null;
    }

    @Override
    public int getExecutorLatestBuild(Executor executor) throws BuildServiceException {
        return 0;
    }

    @Override
    public Build getExecutorBuild(Executor executor, int buildNumber) throws BuildServiceException {
        return null;
    }

    @Override
    public ApplicationContextService getContext() {
        return context;
    }
}
