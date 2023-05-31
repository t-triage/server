/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test;

import com.clarolab.dto.DateStatsDTO;
import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.BuildTriage;
import com.clarolab.model.TestTriage;
import com.clarolab.model.types.StatusType;
import com.clarolab.populate.DataProvider;
import com.clarolab.populate.RealDataProvider;
import com.clarolab.populate.TestTriagePopulate;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.TestDetailService;
import com.clarolab.service.TestTriageService;
import com.clarolab.serviceDTO.BuildTriageServiceDTO;
import com.clarolab.serviceDTO.TestExecutionServiceDTO;
import com.clarolab.view.GroupedStatView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDetailFunctionalTest extends BaseFunctionalTest {


    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private TestDetailService testDetailService;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private BuildTriageServiceDTO buildTriageServiceDTO;

    @Autowired
    TestExecutionServiceDTO testExecutionServiceDTO;

    @Before
    public void clearProvider() {
        provider.clear();
    }


    @Test
    public void sameErrorInOtherSuite() {
        String prefix = "sameErrorInOtherJob";
        RealDataProvider realProvider = new RealDataProvider();
        TestTriagePopulate testSample = realProvider.getTest();

        provider.setName(prefix + "1");
        provider.getTestExecution(testSample);
        provider.getBuildTriage();
        TestTriage testSuite1 = provider.getTestCaseTriage();

        provider.clear();
        provider.setName(prefix + "2");
        testSample.setTestCaseName("2nd" + testSample.getTestCaseName());
        provider.getTestExecution(testSample);
        provider.getBuildTriage();
        TestTriage testSuite2 = provider.getTestCaseTriage();

        List<TestTriage> sameAs1 = testTriageService.findLastSameTests(testSuite1);
        List<TestTriage> sameAs2 = testTriageService.findLastSameTests(testSuite2);

        Assert.assertNotNull(sameAs1);
        Assert.assertNotNull(sameAs2);
        Assert.assertEquals(1, sameAs1.size());
        Assert.assertEquals(1, sameAs2.size());
        assertBuildTriage(provider.getBuildTriage());
    }

    @Test
    public void consecutiveFails() {
        int amount = 5;
        String prefix = "consecutiveFails";
        RealDataProvider realProvider = new RealDataProvider();
        TestTriagePopulate failSample = realProvider.getTest();
        TestTriagePopulate passSample = realProvider.getTest();

        // Prepares the pass sample as the first
        passSample.setTestCaseName(failSample.getTestCaseName());
        passSample.setSuiteName(failSample.getSuiteName());
        passSample.setPath(failSample.getPath());
        Map<Integer, StatusType> buildSpec = new HashMap<>();
        buildSpec.put(1, StatusType.PASS);
        passSample.setBuildSpec(buildSpec);
        passSample.setErrorDetails("");
        passSample.setErrorStackTrace("");

        provider.setName(prefix + "1");
        provider.getBuild(1);
        provider.getTestExecution(passSample);
        provider.getBuildTriage();

        provider.setName(prefix + "2");
        for (int i = 1; i < amount+1; i++) {
            provider.setBuild(null);
            provider.setTestExecution(null);
            provider.setBuildTriage(null);

            provider.getBuild(i + 1);
            provider.getTestExecution(failSample);
            provider.getBuildTriage();
        }
        TestTriage testTriage = provider.getTestCaseTriage();

        TestTriage passTriage = testTriageService.lastTestWithoutStates(testTriage, TestTriage.failStates());

        Assert.assertNotNull(passTriage);
        Assert.assertEquals(1, passTriage.getBuildNumber());
        assertBuildTriage(provider.getBuildTriage());
    }

    @Test
    public void historicPasses() {
        int amount = 5;
        String prefix = "historicPasses";
        TestTriagePopulate testSample = new TestTriagePopulate();
        testSample.setTestCaseName(DataProvider.getRandomName(prefix));
        testSample.setPath(DataProvider.getRandomName(prefix));

        // Prepares the build configuration
        testSample.setAs(StatusType.FAIL, 1, 1);
        testSample.setAs(StatusType.PASS, 2, amount);

        provider.setName(prefix + "2");
        for (int i = 1; i <= amount; i++) {
            provider.clearForNewBuild();

            provider.getBuild(i);
            provider.getTestExecution(testSample);
            provider.getBuildTriage();
        }
        TestTriage testTriage = provider.getTestCaseTriage();

        long count = testTriageService.countTestWith(testTriage.getTestCase(), TestTriage.passStates());

        // The first one is failed, the second one new pass, then amount passes
        Assert.assertEquals(amount - 1, count);
        assertBuildTriage(provider.getBuildTriage());
    }

    @Test
    public void historicFails() {
        int amount = 5;
        String prefix = "historicFails";
        TestTriagePopulate testSample = new TestTriagePopulate();
        testSample.setTestCaseName(DataProvider.getRandomName(prefix));
        testSample.setPath(DataProvider.getRandomName(prefix));

        // Prepares the build configuration
        testSample.setAs(StatusType.FAIL, 1, 2);
        testSample.setAs(StatusType.PASS, 3, amount);

        provider.setName(prefix + "2");
        for (int i = 1; i <= amount; i++) {
            provider.clearForNewBuild();

            provider.getBuild(i);
            provider.getTestExecution(testSample);
            provider.getBuildTriage();
        }
        TestTriage testTriage = provider.getTestCaseTriage();


        long count = testTriageService.countTestWith(provider.getTestCaseTriage().getTestCase(), TestTriage.failStates());
        Assert.assertEquals(2, count);
        assertBuildTriage(provider.getBuildTriage());
    }

    @Test
    public void consecutivePasses() {
        consecutiveFails();

        TestTriage passTriage = testTriageService.lastTestWithoutStates(provider.getTestCaseTriage(), TestTriage.failStates());
        Assert.assertEquals(1, passTriage.getBuildNumber());
        assertBuildTriage(provider.getBuildTriage());
    }

    private void assertBuildTriage(BuildTriage buildTriage) {
        String view = buildTriageServiceDTO.getTextDetail(buildTriage.getId());
        Assert.assertNotNull(view);
        System.out.println(view);
    }
}
