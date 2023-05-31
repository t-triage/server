/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl;

import com.clarolab.connectors.CIConnector;
import com.clarolab.connectors.services.exceptions.BuildServiceException;
import com.clarolab.connectors.services.exceptions.ContainerServiceException;
import com.clarolab.connectors.services.exceptions.ExecutorServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Build;
import com.clarolab.model.Container;
import com.clarolab.model.Executor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class HudsonConnector implements CIConnector {

    private ApplicationContextService context;

    @Override
    public CIConnector connect() {
        return null;
    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public List<Container> getAllContainers() throws ContainerServiceException {
        return null;
    }

    @Override
    public Container containerExists(Container container) throws ContainerServiceException {
        return null;
    }

    @Override
    public Container getContainer(String containerName) throws ContainerServiceException {
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
        return null;
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

    public ApplicationContextService getContext() {
        return context;
    }
}