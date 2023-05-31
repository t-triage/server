/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.model;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.Container;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.BuildService;
import com.clarolab.service.ContainerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ContainerProccessFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private BuildService buildService;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void noPendingContainers() {
        List<Container> initialSet = containerService.findContainersPendingToProcess();

        provider.getBuild(1);
        provider.getTestExecution();
        provider.getBuildTriage();

        List<Container> containers = containerService.findContainersPendingToProcess();
        Assert.assertEquals("Current builds were already processed", 0, containers.size() - initialSet.size());
    }

    @Test
    public void twoPendingBuildsOneContainer() {
        List<Container> initialSet = containerService.findContainersPendingToProcess();

        provider.getBuild(1);
        provider.getTestExecution();
        provider.clearForNewBuild();
        provider.getBuild(2);
        provider.getTestExecution();

        List<Container> containers = containerService.findContainersPendingToProcess();
        Assert.assertEquals("2 non Processed builds 1 container", 1, containers.size() - initialSet.size());
    }

    @Test
    public void pendingBuildsInContainers() {
        int amount = 2;

        // Creates one processed build
        provider.getBuild(1);
        provider.getTestExecution();
        provider.getBuildTriage();

        List<Container> initialContainers = containerService.findContainersPendingToProcess();

        for (int i = 0; i < amount; i++) {
            provider.clear();
            provider.getBuild(1);
            provider.getTestExecution();
        }

        List<Container> containers = containerService.findContainersPendingToProcess();
        Assert.assertEquals("2 non Processed builds 1 container", amount, containers.size() - initialContainers.size());
    }
}
