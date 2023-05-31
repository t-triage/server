/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors;

import com.clarolab.connectors.services.exceptions.BuildServiceException;
import com.clarolab.connectors.services.exceptions.ContainerServiceException;
import com.clarolab.connectors.services.exceptions.ExecutorServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Build;
import com.clarolab.model.Container;
import com.clarolab.model.Executor;
import com.clarolab.model.types.ConnectorType;

import java.util.List;

public interface CIConnector {


    /**
     * It performs a connection to the CI tool
     */
    CIConnector connect();

    /**
     * It performs a disconnection to the CI tool
     */
    void disconnect();

    /**
     * It returns the state of the CI tool
     *
     * @return true/false
     */
    default boolean isConnected() {
        return false;
    }

    /**
     * Returns all the @Container objects
     *
     * @return A collection of containers
     */
    List<Container> getAllContainers() throws ContainerServiceException;

    /**
     * Returns a @Container object
     *
     * @param containerNameOrUrl Container's name or url
     * @return A single container
     */
    Container getContainer(String containerNameOrUrl) throws ContainerServiceException;

    /**
     * Returns a @Container object
     *
     * @param container Container's view URL
     * @return A single container
     */
    Container containerExists(Container container) throws ContainerServiceException;

    /*
    * Evaluates if an @Executor still exists on a @Container
    * */
    boolean isExecutorAlive(Executor executor, Container container) throws ExecutorServiceException;

    /*
     * Evaluates if an @Executor still exists
     * */
    boolean isExecutorAlive(Executor executor) throws ExecutorServiceException;

    /**
     * Returns all the @Executor objects that belongs to a @Container
     *
     * @param container Container that contains executors
     * @return A collection of @Executor
     */
    List<Executor> getAllExecutors(Container container) throws ExecutorServiceException;


    /**
     * Returns all the @Executor objects that belongs to a @Container, and an amount of its @Build objects
     *
     * @param container Container that contains executors
     * @param maxBuildsToRetrieve limits to recover builds collection
     * @return A collection of executors
     */
    List<Executor> getAllExecutors(Container container, int maxBuildsToRetrieve) throws ExecutorServiceException;


    List<Executor> checkForNewExecutors(Container container, int maxBuildsToRetrieve) throws ExecutorServiceException;

    /**
     * Returns the @Build objects that belong to an @Executor
     *
     * @param executor Executor object
     * @return List of Build
     */
    List<Build> getExecutorBuilds(Executor executor) throws BuildServiceException;

    /**
     * Returns a range of @Build objects that belong to an @Executor
     *
     * @param executor   Executor object
     * @param maxBuildsToRetrieve limits to recover builds collection
     * @return List of Build
     *
     */
    List<Build> getExecutorBuilds(Executor executor, int maxBuildsToRetrieve) throws BuildServiceException;

    List<Build> getExecutorBuildsGreaterThan(Executor executor, int maxBuildsToRetrieve, int greaterThanBuildNumber) throws BuildServiceException;

    int getExecutorLatestBuild(Executor executor) throws BuildServiceException;

    /**
     * Returns an specific @Build of an @Executor
     *
     * @param executor    Executor object
     * @param buildNumber specific build
     * @return Build
     */
    Build getExecutorBuild(Executor executor, int buildNumber) throws BuildServiceException;

    static CIConnector getConnector(Container container) {
        return container.getCIConnector();
    }

    static ConnectorType getConnectorType(Executor executor){
        return executor.getContainer().getConnector().getType();
    }

    ApplicationContextService getContext();

}
