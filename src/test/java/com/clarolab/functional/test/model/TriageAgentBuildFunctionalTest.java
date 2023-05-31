/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.model;

import com.clarolab.agents.TriageAgent;
import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.Build;
import com.clarolab.model.BuildTriage;
import com.clarolab.model.Container;
import com.clarolab.model.Executor;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.BuildService;
import com.clarolab.service.BuildTriageService;
import com.clarolab.service.ContainerService;
import com.clarolab.service.ExecutorService;
import com.clarolab.util.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TriageAgentBuildFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private TriageAgent triageAgent;

    @Autowired
    private BuildTriageService buildTriageService;

    @Autowired
    private ExecutorService executorService;

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
        List<Build> builds = buildService.getNonProcessedBuilds(provider.getExecutor());
        Assert.assertEquals("Current builds were already processed", 0, containers.size() - initialSet.size());
        Assert.assertEquals("Current builds were already processed", 0, builds.size());
    }

    @Test
    public void twoPendingBuildsOneContainerSorted() {
        provider.getBuild(1);
        provider.getTestExecution();
        provider.clearForNewBuild();
        provider.getBuild(2);
        provider.getTestExecution();

        List<Build> builds = buildService.getNonProcessedBuilds(provider.getExecutor());
        Assert.assertEquals("Current builds were already processed", 2, builds.size());
        Assert.assertEquals("Build is not in correct order", 1, builds.get(0).getNumber());
        Assert.assertEquals("Build is not in correct order", 2, builds.get(1).getNumber());
    }

    @Test
    public void pendingBuildsInContainers() {
        int amount = 2;
        List<Build> builds = new ArrayList<>(amount);

        for (int i = 0; i < amount; i++) {
            provider.clear();
            builds.add(provider.getBuild(1));
            provider.getTestExecution();
            provider.getBuildTriage();
        }

        Assert.assertEquals("Builds to process", amount, builds.size());

        BuildTriage firstBuild = buildTriageService.find(builds.get(0));
        BuildTriage secondBuild = buildTriageService.find(builds.get(1));
        Assert.assertEquals("Build Triage should not be expired", false, firstBuild.isExpired());
        Assert.assertEquals("Build Triage should not be expired", false, secondBuild.isExpired());
    }

    @Test
    public void performanceFunctionalTriageAgent() {
        int amountTests = 2;
        int amountBuilds = 2;
        int amountExecutors = 2;

        /** amountTests = 500;
        amountBuilds = 2;
        amountExecutors = 5;*/

        int amountProcesses = 1;
        String prefix = "performanceFunctionalTriageAgent";
        provider.setName(prefix);


        for (int i = 0; i < amountExecutors; i++) {
            provider.setExecutor(null);
            provider.getExecutor();
            for (int j = 0; j < amountBuilds; j++) {
                provider.clearForNewBuild();
                provider.getBuild(j + 1);
                for (int k = 0; k < amountTests; k++) {
                    provider.setTestExecution(null);
                    provider.getTestExecution();
                }
            }
        }

        Instant start = DateUtils.instantNow();

        List<Executor> executors = executorService.findAll();
        executors.forEach(executor -> triageAgent.processExecutor(executor));

        System.out.println(String.format("Triage Agent Test Executed in %s.", DateUtils.getElapsedTime(start, DateUtils.instantNow())));

        List<Build> pendingBuilds = buildService.getNonProcessedBuilds();
        Assert.assertEquals("All builds should have been processed", 0, pendingBuilds.size());

        List<BuildTriage> generatedBuilds = buildTriageService.findAll();
    }
}
