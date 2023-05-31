/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.jenkins;

import com.clarolab.connectors.CIConnector;
import com.clarolab.connectors.impl.jenkins.services.JenkinsBuildService;
import com.clarolab.connectors.impl.jenkins.services.JenkinsJobService;
import com.clarolab.connectors.impl.jenkins.services.JenkinsViewService;
import com.clarolab.connectors.services.exceptions.BuildServiceException;
import com.clarolab.connectors.services.exceptions.ContainerServiceException;
import com.clarolab.connectors.services.exceptions.ExecutorServiceException;
import com.clarolab.connectors.services.impl.JenkinsConnectorService;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Build;
import com.clarolab.model.Container;
import com.clarolab.model.Executor;
import lombok.Builder;
import lombok.extern.java.Log;

import java.util.List;
import java.util.logging.Level;

@Log
public class JenkinsConnector implements CIConnector {

    private JenkinsConnectorService jenkinsConnectorService;
    private JenkinsViewService jenkinsViewService;
    private JenkinsJobService jenkinsJobService;
    private JenkinsBuildService jenkinsBuildService;

    private ApplicationContextService context;

    @Builder
    private JenkinsConnector(String url, String userName, String passwordOrToken, ApplicationContextService context) {
        this.context = context;
        jenkinsConnectorService = JenkinsConnectorService.builder().url(url).userName(userName).passwordOrToken(passwordOrToken).build();
        jenkinsViewService = JenkinsViewService.builder().jenkinsApiClient(jenkinsConnectorService.getJenkinsApiClient()).build();
        jenkinsJobService = JenkinsJobService.builder().jenkinsApiClient(jenkinsConnectorService.getJenkinsApiClient()).context(context).build();
        jenkinsBuildService = JenkinsBuildService.builder().jenkinsApiClient(jenkinsConnectorService.getJenkinsApiClient()).context(context).build();
    }

    // *****************************************************************************************************************
    // ******************************************* Utilities for server ************************************************
    // *****************************************************************************************************************
    @Override
    public CIConnector connect() {
        return this;
    }

    @Override
    public void disconnect() {
        if (this.isConnected()) {
            jenkinsConnectorService.cleanConnector();
        }
    }

    @Override
    public boolean isConnected() {
        return jenkinsConnectorService.getClientServiceStatus();
    }

    // *****************************************************************************************************************
    // ****************************************** Utilities for container **********************************************
    // *****************************************************************************************************************

    @Override
    public List<Container> getAllContainers() throws ContainerServiceException {
        return jenkinsViewService.getAllAsContainers();
    }

    @Override
    public Container getContainer(String containerPathOrUrl) throws ContainerServiceException {
        return jenkinsViewService.getAsContainer(containerPathOrUrl);
    }

    @Override
    public Container containerExists(Container container) {
        try {
            return jenkinsViewService.getAsContainer(container.getUrl());
        } catch (Exception e) {
            return null;
        }
    }

    // *****************************************************************************************************************
    // ****************************************** Utilities for executor ***********************************************
    // *****************************************************************************************************************

    @Override
    public boolean isExecutorAlive(Executor executor) throws ExecutorServiceException {
        return isExecutorAlive(executor, null);
    }

    @Override
    public boolean isExecutorAlive(Executor executor, Container container){
        try {
            return jenkinsJobService.jobExists(executor.getName(), container);
        } catch (ExecutorServiceException ex) {
            long executorId = executor.getId();
            long containerId = container != null ? container.getId() : 0;
            log.warning(String.format("[isExecutorAlive] : Executor #%d is not alive, should be deleted.", executorId, containerId));
            return false;
        }
    }

    @Override
    public List<Executor> getAllExecutors(Container container) throws ExecutorServiceException {
        return getAllExecutors(container, -1);
    }

    @Override
    public List<Executor> getAllExecutors(Container container, int maxBuildsToRetrieve) throws ExecutorServiceException {
        List<Executor> executors = jenkinsJobService.getJobsOnContainerAsExecutors(container);
        setContainerOnExecutors(executors, container);
        setBuildsOnExecutors(executors, maxBuildsToRetrieve);
        return executors;
    }

    @Override
    public List<Executor> checkForNewExecutors(Container container, int maxBuildsToRetrieve) throws ExecutorServiceException {
        List<Executor> executors = jenkinsJobService.getNewJobsOnContainerAsExecutors(container);
        setContainerOnExecutors(executors, container);
        setBuildsOnExecutors(executors, maxBuildsToRetrieve);
        return executors;
    }

    // *****************************************************************************************************************
    // ******************************************** Utilities for build ************************************************
    // *****************************************************************************************************************

    @Override
    public List<Build> getExecutorBuilds(Executor executor) throws BuildServiceException {
        context.setExecutorToContext(executor);
        return jenkinsBuildService.getBuilds(executor.getUrl(), -1, -1);
    }

    @Override
    public List<Build> getExecutorBuilds(Executor executor, int maxBuildsToRetrieve) throws BuildServiceException {
        context.setExecutorToContext(executor);
        return jenkinsBuildService.getBuilds(executor.getUrl(), maxBuildsToRetrieve, -1);
    }

    @Override
    public List<Build> getExecutorBuildsGreaterThan(Executor executor, int maxBuildsToRetrieve, int greaterThanBuildNumber) throws BuildServiceException {
        context.setExecutorToContext(executor);
        return jenkinsBuildService.getBuilds(executor.getUrl(), maxBuildsToRetrieve, greaterThanBuildNumber);
    }

    @Override
    public int getExecutorLatestBuild(Executor executor) throws BuildServiceException {
        try {
            return jenkinsJobService.getJob(executor.getName()).lastBuildNumber();
        } catch (ExecutorServiceException e) {
            throw new BuildServiceException(String.format("[getExecutorLatestBuild]: There was an error trying to get latest build number for Executor(name='%s')", executor.getName()), e);
        }
    }

    @Override
    public Build getExecutorBuild(Executor executor, int buildNumber) throws BuildServiceException{
        try {
            context.setExecutorToContext(executor);
            return jenkinsBuildService.getBuild(executor.getUrl(), buildNumber);
        } catch (Exception e) {
            throw new BuildServiceException(String.format("[getExecutorBuild]: There was an error trying to get build#%d for Executor(name='%s')", buildNumber, executor.getName()), e);
        }

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

    private void setBuildsOnExecutors(List<Executor> executors, int buildsToRetrieve){
        executors.forEach(executor -> {
            log.info(String.format("Getting builds for Executor(name=%s)",executor.getName()));
            try {
                executor.add(getExecutorBuilds(executor, buildsToRetrieve));
            } catch (BuildServiceException e) {
                log.log(Level.WARNING, "There was an error trying to get builds for executor " + executor.getName(), e);
            }
            //An Executor has been created. Report information on ApplicationContext need to be cleaned to get ready for next Executor.
            context.cleanRecentlyUsedReport();
        });

    }

}
