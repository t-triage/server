/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.AutomatedTestIssue;
import com.clarolab.model.Executor;
import com.clarolab.model.TestExecution;
import com.clarolab.model.TestTriage;
import com.clarolab.model.types.*;
import com.clarolab.populate.DataProvider;
import com.clarolab.populate.RealDataProvider;
import com.clarolab.populate.TestTriagePopulate;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.AutomatedTestIssueService;
import com.clarolab.service.TestDetailService;
import com.clarolab.service.TestTriageService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AutomatedTestIssueFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private RealDataProvider realDataProvider;

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private AutomatedTestIssueService automatedTestIssueService;

    @Autowired
    private TestDetailService testDetailService;

    @Autowired
    private TestTriageService testTriageService;


    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void automationSameFailures() {
        TestTriagePopulate testSample = realDataProvider.getTest();

        provider.setName("SameFailures1");
        provider.getBuild(1);
        TestExecution test1 = provider.getTestExecution(testSample);
        TestTriage triage1 = provider.getTestCaseTriage();
        provider.getAutomatedTestIssue();

        AutomatedTestIssue automationIssue = automatedTestIssueService.get(triage1);
        Assert.assertNotNull(automationIssue);


        // BUILD 2
        provider.setName("SameFailures2");
        provider.setBuild(null);
        provider.setBuildTriage(null);
        provider.setTestExecution(null);
        provider.setAutomatedTestIssue(null);

        provider.getBuild(2);
        TestExecution test2 = provider.getTestExecution(testSample);
        TestTriage triage2 = provider.getTestCaseTriage();


        automationIssue = automatedTestIssueService.get(triage2);

        Assert.assertNotNull(automationIssue);
        Assert.assertNotNull(triage1.getTriager().getRealname());
        Assert.assertNotNull(triage2.getTriager().getRealname());

    }

    @Test
    public void test30Passes() {
        int amount = 14;
        RealDataProvider realProvider = new RealDataProvider();
        TestTriagePopulate testSample = realProvider.getTest(); // TestExecution like
        testSample.setTestCaseName(DataProvider.getRandomName("test30Passes"));
        testSample.setAs(StatusType.FAIL, 0, 1);
        testSample.setAs(StatusType.PASS, 2, amount);

        // First Test as Pass
        provider.getBuild(1);
        provider.getTestExecution(testSample);
        provider.getTestCaseTriage();
        provider.getAutomatedTestIssue();

        for (int i = 2; i <= amount; i++) {
            provider.clearForNewBuild();

            provider.getBuild(i);
            provider.getTestExecution(testSample);

            provider.getBuildTriage();
        }

        TestTriage testTriage = provider.getTestCaseTriage();

        int consecutivePasses = testTriageService.consecutiveTestsWithoutStates(testTriage, TestTriage.passStates());

        Assert.assertEquals(amount - 1, consecutivePasses);


        AutomatedTestIssue automatedTestIssue = automatedTestIssueService.getAutomatedTestIssue(testTriage.getTestCase());
        Assert.assertEquals(0, automatedTestIssue.getCalculatedPriority());
        Assert.assertEquals(IssueType.FIXED, automatedTestIssue.getIssueType());

    }


    @Test
    public void testHighPriority() {

        TestTriage testTriage = provider.getTestCaseTriage();
        testTriage.setTestFailType(TestFailType.TEST_ASSIGNED_TO_FIX);

        AutomatedTestIssue automatedTestIssue = DataProvider.getAutomatedTestIssue();
        automatedTestIssue.setUserFixPriorityType(UserFixPriorityType.HIGH);
        automatedTestIssue.setTestTriage(testTriage);
        automatedTestIssue.setTestCase(testTriage.getTestCase());

        automatedTestIssueService.updateAutomationIssue(testTriage, automatedTestIssue);
        automatedTestIssue = automatedTestIssueService.getAutomatedTestIssue(testTriage.getTestCase());
        Assert.assertEquals(250, automatedTestIssue.getCalculatedPriority());
        Assert.assertEquals(1, automatedTestIssue.getFailTimes());
    }

    @Test
    public void testMediumPriority() {
        TestTriage testTriage = provider.getTestCaseTriage();
        testTriage.setTestFailType(TestFailType.TEST_ASSIGNED_TO_FIX);

        AutomatedTestIssue automatedTestIssue = DataProvider.getAutomatedTestIssue();
        automatedTestIssue.setUserFixPriorityType(UserFixPriorityType.MEDIUM);
        automatedTestIssue.setTestTriage(testTriage);
        automatedTestIssue.setTestCase(testTriage.getTestCase());

        automatedTestIssueService.updateAutomationIssue(testTriage, automatedTestIssue);
        automatedTestIssue = automatedTestIssueService.getAutomatedTestIssue(testTriage.getTestCase());
        Assert.assertEquals(100, automatedTestIssue.getCalculatedPriority());
        Assert.assertEquals(1, automatedTestIssue.getFailTimes());
    }

    @Test
    public void testLowPriority() {
        TestTriage testTriage = provider.getTestCaseTriage();
        testTriage.setTestFailType(TestFailType.TEST_ASSIGNED_TO_FIX);

        AutomatedTestIssue automatedTestIssue = DataProvider.getAutomatedTestIssue();
        automatedTestIssue.setUserFixPriorityType(UserFixPriorityType.LOW);
        automatedTestIssue.setTestTriage(testTriage);
        automatedTestIssue.setTestCase(testTriage.getTestCase());


        automatedTestIssueService.updateAutomationIssue(testTriage, automatedTestIssue);
        automatedTestIssue = automatedTestIssueService.getAutomatedTestIssue(testTriage.getTestCase());
        Assert.assertEquals(10, automatedTestIssue.getCalculatedPriority());
        Assert.assertEquals(1, automatedTestIssue.getFailTimes());
    }

    @Test
    public void testAutomaticPriority() {
        TestTriage testTriage = provider.getTestCaseTriage();
        testTriage.setTestFailType(TestFailType.TEST_ASSIGNED_TO_FIX);

        AutomatedTestIssue automatedTestIssue = DataProvider.getAutomatedTestIssue();
        automatedTestIssue.setUserFixPriorityType(UserFixPriorityType.AUTOMATIC);
        automatedTestIssue.setIssueType(IssueType.OPEN);
        automatedTestIssue.setTestTriage(testTriage);
        automatedTestIssue.setTestCase(testTriage.getTestCase());

        automatedTestIssueService.updateAutomationIssue(testTriage, automatedTestIssue);
        automatedTestIssue = automatedTestIssueService.getAutomatedTestIssue(testTriage.getTestCase());
        Assert.assertEquals(10, automatedTestIssue.getCalculatedPriority());
        Assert.assertEquals(1, automatedTestIssue.getFailTimes());
    }

    @Test
    public void testWontFix() {
        //SCENARIO
        AutomatedTestIssue automatedTestIssue = provider.getAutomatedTestIssue();
        automatedTestIssue.getTestTriage().setTestFailType(TestFailType.WONT_FILE);
        TestTriage testTriage = automatedTestIssue.getTestTriage();

        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssue = automatedTestIssueService.getAutomatedTestIssue(testTriage.getTestCase());
        Assert.assertEquals(IssueType.WONT_FIX, automatedTestIssue.getIssueType());
        Assert.assertEquals(15, automatedTestIssue.getCalculatedPriority());
        Assert.assertEquals(1, automatedTestIssue.getFailTimes());
        Assert.assertEquals(0, automatedTestIssue.getReopenTimes());
    }

    @Test
    public void testFailAndReopen() {
        //SCENARIO
        AutomatedTestIssue automatedTestIssue = provider.getAutomatedTestIssue();
        automatedTestIssue.getTestTriage().setTestFailType(TestFailType.TEST_ASSIGNED_TO_FIX);
        automatedTestIssue.getTestTriage().setCurrentState(StateType.NEWFAIL);
        automatedTestIssue.setIssueType(IssueType.FIXED);

        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssue = automatedTestIssueService.getAutomatedTestIssue(automatedTestIssue.getTestTriage().getTestCase());
        Assert.assertEquals(IssueType.REOPEN, automatedTestIssue.getIssueType());
        Assert.assertEquals(15, automatedTestIssue.getCalculatedPriority());
        Assert.assertEquals(1, automatedTestIssue.getReopenTimes());
        Assert.assertEquals(1, automatedTestIssue.getFailTimes());
    }

    @Test
    public void testPassingAndFailAgain() {
        //SCENARIO
        AutomatedTestIssue automatedTestIssue = provider.getAutomatedTestIssue();
        automatedTestIssue.getTestTriage().setTestFailType(TestFailType.TEST_ASSIGNED_TO_FIX);
        automatedTestIssue.getTestTriage().setCurrentState(StateType.NEWFAIL);
        automatedTestIssue.setIssueType(IssueType.PASSING);

        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssue = automatedTestIssueService.getAutomatedTestIssue(automatedTestIssue.getTestTriage().getTestCase());
        Assert.assertEquals(IssueType.OPEN, automatedTestIssue.getIssueType());
    }

    @Test
    public void testFailingAndPassed() {
        //SCENARIO
        AutomatedTestIssue automatedTestIssue = provider.getAutomatedTestIssue();
        automatedTestIssue.getTestTriage().setTestFailType(TestFailType.TEST_ASSIGNED_TO_FIX);
        automatedTestIssue.getTestTriage().setCurrentState(StateType.NEWPASS);
        automatedTestIssue.setIssueType(IssueType.OPEN);
        automatedTestIssue.setCalculatedPriority(50);

        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssue = automatedTestIssueService.getAutomatedTestIssue(automatedTestIssue.getTestTriage().getTestCase());
        Assert.assertEquals(IssueType.PASSING, automatedTestIssue.getIssueType());
        Assert.assertEquals(35, automatedTestIssue.getCalculatedPriority());
    }

    @Test
    public void testPassingToFixed() {
        int amount = 5;
        RealDataProvider realProvider = new RealDataProvider();
        TestTriagePopulate testSample = realProvider.getTest(); // TestExecution like
        testSample.setAs(StatusType.FAIL, 0, 1);
        testSample.setAs(StatusType.PASS, 2, amount + 2);

        // First Test as Pass
        provider.getBuild(1);
        provider.getTestExecution(testSample);
        provider.getTestCaseTriage();
        AutomatedTestIssue automatedTestIssue = provider.getAutomatedTestIssue();

        for (int i = 2; i <= amount + 2; i++) {
            provider.clearForNewBuild();
            provider.getBuild(i);
            provider.getTestExecution(testSample);
            provider.getBuildTriage();
        }

        TestTriage testTriage = provider.getTestCaseTriage();

        automatedTestIssue = automatedTestIssueService.getAutomatedTestIssue(testTriage.getTestCase());
        Assert.assertEquals(0, automatedTestIssue.getCalculatedPriority());
        Assert.assertEquals(IssueType.FIXED, automatedTestIssue.getIssueType());
    }


    @Test
    public void solidBackToFail() {
        int amount = 14;
        RealDataProvider realProvider = new RealDataProvider();
        TestTriagePopulate testSample = realProvider.getTest(); // TestExecution like
        testSample.setTestCaseName(DataProvider.getRandomName("solidBackToFail"));
        testSample.setAs(StatusType.FAIL, 0, 1);
        testSample.setAs(StatusType.PASS, 2, amount);
        testSample.setAs(StatusType.FAIL, amount + 1, amount + 3);

        // First Test as Pass
        provider.getBuild(1);
        provider.getTestExecution(testSample);
        provider.getAutomatedTestIssue();
        provider.getTestCaseTriage();

        for (int i = 2; i <= amount; i++) {
            provider.clearForNewBuild();

            provider.getBuild(i);
            provider.getTestExecution(testSample);

            provider.getBuildTriage();
        }

        TestTriage fixedTestTriage = provider.getTestCaseTriage();
        AutomatedTestIssue fixedAutomatedTestIssue = automatedTestIssueService.getAutomatedTestIssue(fixedTestTriage.getTestCase());
        Assert.assertEquals(IssueType.FIXED, fixedAutomatedTestIssue.getIssueType());

        // Set the fail build
        provider.clearForNewBuild();
        provider.getBuild(amount + 1);
        provider.getTestExecution(testSample);
        provider.getBuildTriage();

        TestTriage testTriage = provider.getTestCaseTriage();
        automatedTestIssueService.updateAutomationIssue(testTriage, fixedAutomatedTestIssue);

        AutomatedTestIssue automatedTestIssue = automatedTestIssueService.getAutomatedTestIssue(testTriage.getTestCase());

        Assert.assertEquals(IssueType.REOPEN, automatedTestIssue.getIssueType());
        Assert.assertEquals(1, automatedTestIssue.getReopenTimes());
        Assert.assertNotNull("TestTriage should contain the latest fail test triage", automatedTestIssue.getTestTriage());
        Assert.assertEquals(2, automatedTestIssue.getFailTimes());
        Assert.assertEquals(UserFixPriorityType.AUTOMATIC, automatedTestIssue.getUserFixPriorityType());

    }

    @Test
    public void notPassBecauseOtherFailing() {
        int amount = 12;
        TestTriagePopulate test1 = realDataProvider.getTest();
        test1.setTestCaseName(DataProvider.getRandomName("notPassBecauseOtherFailing"));
        test1.setAs(StatusType.FAIL, 0, amount);

        TestTriagePopulate test2 = realDataProvider.getTest();
        test2.setTestCaseName(test1.getTestCaseName());
        test2.setAs(StatusType.FAIL, 0, 1);
        test2.setAs(StatusType.PASS, 2, amount);

        provider.setName(DataProvider.getRandomName("1Executor1"));
        Executor executor1 = provider.getExecutor();
        provider.getBuild(1);
        provider.getTestExecution(test1);
        provider.getAutomatedTestIssue();

        provider.setName(DataProvider.getRandomName("2Executor2"));
        provider.setExecutor(null);
        Executor executor2 = provider.getExecutor();
        for (int i = 1; i <= amount; i++) {
            provider.clearForNewBuild();
            provider.getBuild(i);
            provider.getTestExecution(test2);
            provider.getTestCaseTriage();
        }

        AutomatedTestIssue automatedTestIssue = provider.getTestCase().getAutomatedTestIssue();

        Assert.assertEquals(IssueType.OPEN, automatedTestIssue.getIssueType());
    }


    @Test
    public void passBecauseOtherPassing() {
        int amount = 5;
        TestTriagePopulate test1 = realDataProvider.getTest();
        test1.setTestCaseName(DataProvider.getRandomName("notPassBecauseOtherFailing"));
        test1.setAs(StatusType.FAIL, 0, 1);
        test1.setAs(StatusType.PASS, 2, amount);

        TestTriagePopulate test2 = realDataProvider.getTest();
        test2.setTestCaseName(test1.getTestCaseName());
        test2.setAs(StatusType.FAIL, 0, 1);
        test2.setAs(StatusType.PASS, 2, amount);

        Executor executor1 = provider.getExecutor();
        provider.getBuild(1);
        provider.getTestExecution(test1);
        provider.getAutomatedTestIssue();

        provider.setExecutor(null);
        Executor executor2 = provider.getExecutor();
        provider.clearForNewBuild();
        provider.getBuild(1);
        provider.getTestExecution(test2);
        provider.getTestCaseTriage();


        for (int i = 2; i <= amount; i++) {
            provider.setExecutor(executor1);
            provider.clearForNewBuild();
            provider.getBuild(i);
            provider.getTestExecution(test1);
            provider.getTestCaseTriage();

            provider.setExecutor(executor2);
            provider.clearForNewBuild();
            provider.getBuild(i);
            provider.getTestExecution(test2);
            provider.getTestCaseTriage();
        }

        AutomatedTestIssue automatedTestIssue = provider.getTestCase().getAutomatedTestIssue();

        Assert.assertEquals(IssueType.FIXED, automatedTestIssue.getIssueType());
    }

    @Test
    public void passingBecauseOtherPassing() {
        int amount = 3;
        TestTriagePopulate test1 = realDataProvider.getTest();
        test1.setTestCaseName(DataProvider.getRandomName("notPassBecauseOtherFailing"));
        test1.setAs(StatusType.FAIL, 0, 1);
        test1.setAs(StatusType.PASS, 2, amount);

        TestTriagePopulate test2 = realDataProvider.getTest();
        test2.setTestCaseName(test1.getTestCaseName());
        test2.setAs(StatusType.FAIL, 0, 1);
        test2.setAs(StatusType.PASS, 2, amount);

        Executor executor1 = provider.getExecutor();
        provider.getBuild(1);
        provider.getTestExecution(test1);
        provider.getAutomatedTestIssue();

        provider.setExecutor(null);
        Executor executor2 = provider.getExecutor();
        provider.clearForNewBuild();
        provider.getBuild(1);
        provider.getTestExecution(test2);
        provider.getTestCaseTriage();


        for (int i = 2; i <= amount; i++) {
            provider.setExecutor(executor1);
            provider.clearForNewBuild();
            provider.getBuild(i);
            provider.getTestExecution(test1);
            provider.getTestCaseTriage();

            provider.setExecutor(executor2);
            provider.clearForNewBuild();
            provider.getBuild(i);
            provider.getTestExecution(test2);
            provider.getTestCaseTriage();
        }

        AutomatedTestIssue automatedTestIssue = provider.getTestCase().getAutomatedTestIssue();

        Assert.assertEquals(IssueType.PASSING, automatedTestIssue.getIssueType());
    }


}
