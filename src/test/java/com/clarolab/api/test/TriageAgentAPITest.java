/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test;

import com.clarolab.agents.TriageAgent;
import com.clarolab.api.BaseAPITest;
import com.clarolab.model.Build;
import com.clarolab.model.BuildTriage;
import com.clarolab.model.Executor;
import com.clarolab.model.TestTriage;
import com.clarolab.model.types.StatusType;
import com.clarolab.model.types.TestFailType;
import com.clarolab.populate.RealDataProvider;
import com.clarolab.populate.TestTriagePopulate;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.BuildService;
import com.clarolab.service.BuildTriageService;
import com.clarolab.service.TestTriageService;
import com.clarolab.util.DateUtils;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.clarolab.util.Constants.API_ACTIONS_URI;
import static com.clarolab.util.Constants.PROCESS;
import static io.restassured.RestAssured.given;

public class TriageAgentAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider = new UseCaseDataProvider();

    @Autowired
    private BuildTriageService buildTriageService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private TriageAgent triageAgent;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private RealDataProvider realDataProvider;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void triageOneBuild() {
        int amount = 1;
        String prefix = "triageOneBuild";
        provider.setName(prefix);
        provider.getExecutor();
        for (int i = 1; i <= amount; i++) {
            provider.clearForNewBuild();
            provider.getBuild(i);
            provider.getTestExecution();
        }

        executeTriageAgent();

        List<Build> pendingBuilds = buildService.getNonProcessedBuilds(provider.getExecutor());

        List<BuildTriage> triages = buildTriageService.findAllByExecutor(provider.getExecutor());

        Assert.assertEquals("There are pending builds but TriageAgent has run", 0, pendingBuilds.size());
        assertBuildTriages(provider.getExecutor(), triages, amount);
    }


    @Test
    public void performanceTriageAgent() {
        int amountTests = 2;
        int amountBuilds = 20;
        int amountExecutors = 1;
        int amountProcesses = 1;
        String prefix = "performanceTriageAgent";
        provider.setName(prefix);

        List<BuildTriage> existingBuilds = buildTriageService.findAll();


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

        executeTriageAgent();

        System.out.println(String.format("Triage Agent Test Executed in %s.", DateUtils.getElapsedTime(start, DateUtils.instantNow())));

        List<Build> pendingBuilds = buildService.getNonProcessedBuilds();
        Assert.assertEquals("All builds should have been processed", 0, pendingBuilds.size());

        List<BuildTriage> generatedBuilds = buildTriageService.findAll();

        Assert.assertEquals("BuildTriage should be the same amount of builds", (amountExecutors * amountBuilds) + existingBuilds.size(), generatedBuilds.size());

    }

    private void assertBuildTriages(Executor executor, List<BuildTriage> triages, int amount) {
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
    public void autoTriagedPass() {
        int amount = 1;
        String prefix = "autoTriagedPass";
        provider.setName(prefix);
        provider.getExecutor();
        for (int i = 1; i <= amount; i++) {
            provider.clearForNewBuild();
            provider.getBuild(i);
            provider.getTestExecution(StatusType.PASS);
        }

        executeTriageAgent();

        List<BuildTriage> triages = buildTriageService.findAllByExecutor(provider.getExecutor());

        BuildTriage buildTriage = triages.get(0);

        Assert.assertEquals("A pass build should be automatically triaged", true, buildTriage.isTriaged());
    }

    @Test
    public void noAutoTriagedEmptyBUilds() {
        int amount = 1;
        String prefix = "noAutoTriagedEmptyBUilds";
        provider.setName(prefix);
        provider.getExecutor();
        for (int i = 1; i <= amount; i++) {
            provider.clearForNewBuild();
            provider.getBuild(i);
        }

        executeTriageAgent();

        List<BuildTriage> triages = buildTriageService.findAllByExecutor(provider.getExecutor());

        BuildTriage buildTriage = triages.get(0);

        Assert.assertEquals("An empty build should not be triaged", false, buildTriage.isTriaged());
    }

    @Test
    public void autoTriagedPermanent() {
        int amount = 2;
        String prefix = "autoTriagedPermanent";
        TestTriagePopulate test = realDataProvider.getTest();
        test.setAs(StatusType.FAIL, 0, amount);

        provider.clearForNewBuild();
        provider.getBuild(1);
        provider.getTestExecution(test);
        TestTriage triage = provider.getTestCaseTriage();
        triage.setTestFailType(TestFailType.TEST_ASSIGNED_TO_FIX);
        triage.setTriaged(true);
        testTriageService.update(triage);

        executeTriageAgent();

        provider.clearForNewBuild();
        provider.getBuild(2);
        provider.getTestExecution(test);
        provider.getTestCaseTriage();

        executeTriageAgent();

        List<BuildTriage> triages = buildTriageService.findAllByExecutor(provider.getExecutor());

        BuildTriage buildTriage = triages.get(1);

        Assert.assertEquals("A pass build should be automatically triaged", true, buildTriage.isTriaged());
    }




    public void executeTriageAgent() {
        Boolean answer = given()
                .post(API_ACTIONS_URI + PROCESS)
                .then().statusCode(HttpStatus.SC_OK)
                .extract().as(Boolean.class);

        Assert.assertTrue("TriageAgent could not run properly.", answer);
    }

    public void executeTriageAgentAsync(int amountProcesses) {
        List<CompletableFuture> runningTasks = new ArrayList<>();
        for (int i = 0; i < amountProcesses; i++) {
            runningTasks.add(CompletableFuture.runAsync(() -> {
                executeTriageAgent();
            }));
        }

        while (!runningTasks.isEmpty()) {
            runningTasks.removeIf(CompletableFuture::isDone);
        }
    }
}
