/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.bamboo;

import com.clarolab.bamboo.client.BambooApiClient;
import com.clarolab.connectors.CIConnector;
import com.clarolab.connectors.services.exceptions.BuildServiceException;
import com.clarolab.connectors.services.exceptions.ContainerServiceException;
import com.clarolab.connectors.services.exceptions.ExecutorServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Build;
import com.clarolab.model.Container;
import com.clarolab.model.Executor;
import lombok.Builder;
import lombok.extern.java.Log;

import java.util.List;
import java.util.logging.Level;

@Log
public class BambooConnector implements CIConnector {

    private ApplicationContextService context;
    private BambooProjectService bambooProjectService;
    private BambooPlanService bambooPlanService;
    private BambooResultService bambooResultService;
    private BambooServerService bambooServerService;

    @Builder
    private BambooConnector(String url, String userName, String passwordOrToken, ApplicationContextService context){
        this.context = context;
        BambooApiClient bambooApiClient = BambooApiClient.builder().baseUrl(url).userName(userName).password(passwordOrToken).build();
        bambooProjectService = BambooProjectService.builder().bambooApiClient(bambooApiClient).build();
        bambooPlanService = BambooPlanService.builder().bambooApiClient(bambooApiClient).build();
        bambooResultService = BambooResultService.builder().bambooApiClient(bambooApiClient).applicationContextService(context).build();
        bambooServerService = BambooServerService.builder().bambooApiClient(bambooApiClient).build();
    }

    // *****************************************************************************************************************
    // ******************************************* Utilities for server ************************************************
    // *****************************************************************************************************************
    @Override
    public CIConnector connect() {
        if(isConnected())
            return this;
        return null;
    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean isConnected() {
        return bambooServerService.isServerActive();
    }

    // *****************************************************************************************************************
    // ****************************************** Utilities for container **********************************************
    // *****************************************************************************************************************

    @Override
    public List<Container> getAllContainers() throws ContainerServiceException {
        return bambooProjectService.getProjectsAsContainers();
    }

    @Override
    public Container getContainer(String containerNameOrUrl) throws ContainerServiceException {
        return bambooProjectService.getProjectAsContainer(containerNameOrUrl);
    }

    @Override
    public Container containerExists(Container container) throws ContainerServiceException {
        return bambooProjectService.getProjectIfItExistsAsContainer(container.getName());
    }

    // *****************************************************************************************************************
    // ****************************************** Utilities for executor ***********************************************
    // *****************************************************************************************************************

    @Override
    public boolean isExecutorAlive(Executor executor, Container container) throws ExecutorServiceException {
        try {
            if (container == null)
                return bambooPlanService.isAnExistingPlan(executor.getName());
            else
                return bambooPlanService.isPlanOnProject(executor.getName(), container.getName());
        } catch (Exception e){
            String msg = String.format("[isExecutorAlive] : An error occurred trying to get Executor(name=%s)]",executor.getName());
            if (container != null)
                msg += String.format(" at Container(name=%s)]", container.getName());
            throw new ExecutorServiceException(msg, e);
        }
    }

    @Override
    public boolean isExecutorAlive(Executor executor) throws ExecutorServiceException {
        return isExecutorAlive(executor, null);
    }

    @Override
    public List<Executor> getAllExecutors(Container container) throws ExecutorServiceException {
        List<Executor> executors = bambooPlanService.getAllPlansForProjectAsExecutors(container.getName());
        setContainerOnExecutors(executors, container);
        setBuildsOnExecutors(executors);
        return executors;
    }

    @Override
    public List<Executor> getAllExecutors(Container container, int maxBuildsToRetrieve) throws ExecutorServiceException {
        bambooResultService.setLimit(maxBuildsToRetrieve);
        List<Executor> executors = getAllExecutors(container);
        return executors;
    }

    @Override
    public List<Executor> checkForNewExecutors(Container container, int maxBuildsToRetrieve) throws ExecutorServiceException {
        List<Executor> executors = bambooPlanService.getNewPlansOnProjectAsExecutors(container);
        setContainerOnExecutors(executors, container);
        setBuildsOnExecutors(executors, maxBuildsToRetrieve);
        return executors;
    }

    // *****************************************************************************************************************
    // ******************************************** Utilities for build ************************************************
    // *****************************************************************************************************************

    @Override
    public List<Build> getExecutorBuilds(Executor executor) throws BuildServiceException {
        return getExecutorBuilds(executor, bambooResultService.getLimit());
    }

    @Override
    public List<Build> getExecutorBuilds(Executor executor, int maxBuildsToRetrieve) throws BuildServiceException {
        context.setExecutorToContext(executor);
        return bambooResultService.getResultsForPlanAsBuilds(executor, maxBuildsToRetrieve);
    }

    @Override
    public List<Build> getExecutorBuildsGreaterThan(Executor executor, int maxBuildsToRetrieve, int greaterThanBuildNumber) throws BuildServiceException {
        log.info("To recover bamboo results with build number greater than " + greaterThanBuildNumber);
        context.setExecutorToContext(executor);
        return bambooResultService.getResultsForPlanAsBuilds(executor, maxBuildsToRetrieve, greaterThanBuildNumber);
    }

    @Override
    public int getExecutorLatestBuild(Executor executor) throws BuildServiceException {
        return bambooResultService.getLatestResultForPlanAsBuildNumber(executor);
    }

    @Override
    public Build getExecutorBuild(Executor executor, int buildNumber) throws BuildServiceException {
        return getExecutorBuilds(executor, 0).stream().filter(build -> build.getNumber() == buildNumber).findFirst().orElse(null);
    }

    public ApplicationContextService getContext() {
        return context;
    }

    //***************************************************************************************
    // ************************ Private declarations ****************************************
    //***************************************************************************************

    private void setContainerOnExecutors(List<Executor> executors, Container container){
        executors.forEach(e -> e.setContainer(container));
    }

    private void setBuildsOnExecutors(List<Executor> executors){
        setBuildsOnExecutors(executors, -1);
    }

    private void setBuildsOnExecutors(List<Executor> executors, int buildsToRetrieve){
        executors.forEach(e -> {
            try {
                log.info(String.format("Getting builds for Executor(name=%s)",e.getName() ));
                if(buildsToRetrieve <= 0)
                    e.add(getExecutorBuilds(e));
                else
                    e.add(getExecutorBuilds(e, buildsToRetrieve));
            } catch (BuildServiceException exc) {
                log.log(Level.WARNING, "There was an error trying to get builds for executor " + e.getName(), exc);
            }
            //An Executor has been created. Report information on ApplicationContext need to be cleaned to get ready for next Executor.
            context.cleanRecentlyUsedReport();
        });
    }

}
