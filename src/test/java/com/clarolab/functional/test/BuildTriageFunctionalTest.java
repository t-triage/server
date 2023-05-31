/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test;

import com.clarolab.agents.TriageAgent;
import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.Build;
import com.clarolab.model.BuildTriage;
import com.clarolab.model.Executor;
import com.clarolab.model.Product;
import com.clarolab.populate.DataProvider;
import com.clarolab.populate.TestTriagePopulate;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.BuildService;
import com.clarolab.service.BuildTriageService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BuildTriageFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private BuildTriageService buildTriageService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private TriageAgent triageAgent;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void testBase() {
        int amount = 6;
        String prefix = "testBaseBuildTriage";
        provider.setName(prefix);
        provider.getExecutor();
        for (int i = 1; i <= amount; i++) {
            provider.clearForNewBuild();
            provider.setTimestamp(DataProvider.getTimeAdd(-1 * (amount + 1 - i)));
            provider.getBuild(i);
            provider.getTestExecutionFail();
        }

        triageAgent.processExecutor(provider.getExecutor());

        List<BuildTriage> triages = buildTriageService.findAllByExecutor(provider.getExecutor());

        assertBuildTriages(provider.getExecutor(), triages, amount);
    }


    @Test
    public void testBaseByBuildNumber() {
        int amount = 6;
        String prefix = "testBaseByBuildNumber";
        provider.setName(prefix);
        provider.getExecutor();
        for (int i = 1; i <= amount; i++) {
            provider.setTimestamp(DataProvider.getTimeAdd(-1 * (amount + 1 - i)));
            provider.clearForNewBuild();
            provider.getBuild(i);
            provider.getTestExecutionFail();
        }

        List<Build> pendingBuilds = buildService.getNonProcessedBuilds(provider.getExecutor());

        Assert.assertEquals("Builds pending to process should be the ones the test has just created", amount, pendingBuilds.size());

        triageAgent.processBuilds(pendingBuilds);

        List<BuildTriage> triages = buildTriageService.findAllByExecutor(provider.getExecutor());

        Assert.assertEquals("A BuildTriage should be created by each build", pendingBuilds.size(), triages.size());

        assertBuildTriages(provider.getExecutor(), triages, amount);
    }

    private void assertBuildTriages(Executor executor, List<BuildTriage> triages, int amount) {
        for (Build build : executor.getBuilds()) {
            Assert.assertTrue(String.format("Build should have been processed. Number: %d", build.getNumber()), build.isProcessed());
        }

        Assert.assertTrue("There should be BuildTriage created", !triages.isEmpty());
        boolean hasTriage = false;
        for (BuildTriage triage : triages) {
            // System.out.println(triage.getExecutor().getName() + " - " + triage.createNewBuild().getNumber()  + " - " + triage.isTriaged() );
            if (triage.getNumber() == amount) {
                Assert.assertTrue(String.format("Last build should not be triaged %d", triage.getNumber()), !triage.isTriaged());
            } else {
                Assert.assertTrue(String.format("Previous builds should be triaged %d", triage.getNumber()), triage.isTriaged());
                Assert.assertTrue(String.format("Previous builds should be expired %d", triage.getNumber()), triage.isExpired());
            }
        }
    }


    @Test
    public void testTriageIndividual() {
        int amount = 5;
        String prefix = "testTriageIndividual";
        provider.setName(prefix);
        provider.getExecutor();
        for (int i = 1; i <= amount; i++) {
            provider.setTimestamp(DataProvider.getTimeAdd(-1 * (amount + 1 - i)));
            provider.clearForNewBuild();
            provider.getBuild(i);
            provider.getTestExecutionFail();
            provider.getBuildTriage();
        }

        List<BuildTriage> triages = buildTriageService.findAllByExecutor(provider.getExecutor());

        assertBuildTriages(provider.getExecutor(), triages, amount);
    }

    @Test
    public void testTriageWithoutTest() {
        String prefix = "testTriageWithoutTest";
        provider.setName(prefix);
        provider.getExecutor();
        provider.getBuild();

        triageAgent.processExecutor(provider.getExecutor());

        List<BuildTriage> triages = buildTriageService.findAll();
        Assert.assertTrue(!triages.isEmpty());
    }

    @Test
    public void searchTestWithSpaces() {
        String prefix = "search test";

        TestTriagePopulate test = new TestTriagePopulate();
        test.setTestCaseName(DataProvider.getRandomName(prefix));

        provider.getExecutor();
        provider.getBuild(1);
        provider.getTestExecution(test);

        provider.getTestCaseTriage();

        List<BuildTriage> buildTriages = buildTriageService.getBuildTriageBy(prefix);

        Assert.assertEquals("It should find 1 executor", 1, buildTriages.size());

    }
}
