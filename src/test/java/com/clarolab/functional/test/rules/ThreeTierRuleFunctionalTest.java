/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.rules;

import com.clarolab.agents.rules.StaticRuleDispatcher;
import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.functional.test.util.TestConfig;
import com.clarolab.model.Build;
import com.clarolab.model.TestTriage;
import com.clarolab.model.types.*;
import com.clarolab.populate.DataProvider;
import com.clarolab.populate.RealDataProvider;
import com.clarolab.populate.TestTriagePopulate;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.ErrorDetailService;
import com.clarolab.service.TestExecutionService;
import com.clarolab.service.TestTriageService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.model.helper.tag.TagHelper.FLAKY_TRIAGE;
import static com.clarolab.model.helper.tag.TagHelper.SOLID_TEST;
import static com.clarolab.model.types.DeducedReasonType.*;
import static com.clarolab.util.Constants.DEFAULT_CONSECUTIVE_PASS_COUNT;

// tests that requires 3 stages
public class ThreeTierRuleFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private TestExecutionService testExecutionService;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private StaticRuleDispatcher staticRuleDispatcher;

    @Autowired
    protected ErrorDetailService errorDetailService;

    @Autowired
    protected RealDataProvider realDataProvider;


    @Before
    public void clearProvider() {
        provider.clear();
        staticRuleDispatcher.setEngineEnabled(true);
    }

    @Test
    public void id43AutomationIssue() {
        TestConfig test = newTestConfig()
                .rank(2)
                .newState(StateType.PERMANENT)
                .previousState(StateType.PASS)
                .previousStatus(StatusType.PASS)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.NOT_EQUAL)
                .applicationFailType(ApplicationFailType.UNDEFINED)
                .testFailType(TestFailType.TEST_ASSIGNED_TO_FIX)
                .triaged(true)
                .rule(Rule43)
                .firstApplicationFailType(ApplicationFailType.UNDEFINED)
                .firstHasProductBug(false)
                .firstHasTestBug(true)
                .firstTestFailType(TestFailType.TEST_ASSIGNED_TO_FIX)
                .firstState(StateType.FAIL)
                .firstStatus(StatusType.FAIL)
                .build();

        createTest(test);
    }

    @Test
    public void id44WontFix() {
        TestConfig test = newTestConfig()
                .rank(2)
                .newState(StateType.PERMANENT)
                .previousState(StateType.PASS)
                .previousStatus(StatusType.PASS)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.NOT_EQUAL)
                .applicationFailType(ApplicationFailType.UNDEFINED)
                .testFailType(TestFailType.TEST_ASSIGNED_TO_FIX)
                .triaged(true)
                .rule(Rule43)
                .firstApplicationFailType(ApplicationFailType.UNDEFINED)
                .firstHasProductBug(false)
                .firstHasTestBug(false)
                .firstTestFailType(TestFailType.WONT_FILE)
                .firstState(StateType.FAIL)
                .firstStatus(StatusType.FAIL)
                .build();

        createTest(test);
    }

    @Test
    public void idSetSolidAllPass() {
        TestConfig test = newTestConfig()
                .rank(0)
                .newState(StateType.PASS)
                .previousState(StateType.PASS)
                .previousStatus(StatusType.PASS)
                .currentStatus(StatusType.PASS)
                .errorDifference(ErrorType.NO_ERROR)
                .applicationFailType(ApplicationFailType.UNDEFINED)
                .testFailType(TestFailType.UNDEFINED)
                .triaged(true)
                .rule(Rule16)
                .tags(SOLID_TEST)
                .includeTags(true)
                .build();

        int amount = DEFAULT_CONSECUTIVE_PASS_COUNT + 1;

        TestTriagePopulate testCase = new TestTriagePopulate();
        testCase.setTestCaseName(DataProvider.getRandomName("idSetSolid"));
        testCase.setAs(StatusType.PASS, 0, amount);

        for (int i = 1; i <= amount; i++) {
            provider.clearForNewBuild();
            provider.getBuild(i);
            provider.getTestExecution(testCase);
            provider.getBuildTriage();
        }

        TestTriage testTriage = provider.getTestCaseTriage();

        assertRules(test, testTriage, ErrorType.NO_ERROR);
    }

    @Test
    public void flackyTagFail() {
        TestConfig test = newTestConfig()
                .rank(6)
                .newState(StateType.FAIL)
                .currentStatus(StatusType.FAIL)
                .triaged(false)
                .tags(FLAKY_TRIAGE)
                .includeTags(true)
                .rule(Rule41)
                .build();

        TestTriagePopulate testSpec = test.getNewTestPopulate();
        testSpec.setAs(StatusType.PASS, 1, 1);
        testSpec.setAs(StatusType.FAIL, 2, 2);
        testSpec.setAs(StatusType.PASS, 3, 3);
        testSpec.setAs(StatusType.FAIL, 4, 5);

        TestTriage triage = null;

        for (int i = 1; i < 6; i++) {
            provider.clearForNewBuild();
            Build build = provider.getBuild(i);
            provider.getTestExecution(testSpec);
            triage = provider.getTestCaseTriage();
        }

        assertRules(test, triage, null);
    }

    @Test
    public void flackyTagPass() {
        TestConfig test = newTestConfig()
                .rank(0)
                .newState(StateType.PASS)
                .currentStatus(StatusType.PASS)
                .triaged(true)
                .tags(FLAKY_TRIAGE)
                .includeTags(true)
                .rule(Rule16)
                .build();

        TestTriagePopulate testSpec = test.getNewTestPopulate();
        testSpec.setAs(StatusType.FAIL, 1, 1);
        testSpec.setAs(StatusType.PASS, 2, 2);
        testSpec.setAs(StatusType.FAIL, 3, 3);
        testSpec.setAs(StatusType.PASS, 4, 4);
        testSpec.setAs(StatusType.FAIL, 5, 5);
        testSpec.setAs(StatusType.PASS, 6, 6);

        TestTriage triage = null;

        for (int i = 1; i < 7; i++) {
            provider.clearForNewBuild();
            Build build = provider.getBuild(i);
            provider.getTestExecution(testSpec);
            triage = provider.getTestCaseTriage();
        }

        assertRules(test, triage, null);
    }

    @Test
    public void permanentPassFail() {
        TestConfig test = newTestConfig()
                .rank(6)
                .newState(StateType.FAIL)
                .currentStatus(StatusType.FAIL)
                .previousState(StateType.PASS)
                .previousStatus(StatusType.PASS)
                .errorDifference(ErrorType.NOT_EQUAL)
                .applicationFailType(ApplicationFailType.UNDEFINED)
                .rule(Rule41)
                .firstApplicationFailType(ApplicationFailType.UNDEFINED)
                .firstTestFailType(TestFailType.UNDEFINED)
                .firstState(StateType.PERMANENT)
                .firstStatus(StatusType.FAIL)
                .build();

        createTest(test);
    }


    @Test
    public void permanentNewPassFail() {
        TestConfig test = newTestConfig()
                .rank(6)
                .newState(StateType.FAIL)
                .currentStatus(StatusType.FAIL)
                .rule(Rule41)
                .build();

        TestTriagePopulate testSpec = test.getNewTestPopulate();
        testSpec.setAs(StatusType.FAIL, 1, 1);
        testSpec.setAs(StatusType.PASS, 2, 7);
        testSpec.setAs(StatusType.FAIL, 8, 8);

        TestTriage triage = null;

        // Make test permanent
        provider.clearForNewBuild();
        Build build = provider.getBuild(1);
        provider.getTestExecution(testSpec);
        triage = provider.getTestCaseTriage();
        triage.setCurrentState(StateType.PERMANENT);
        triage.setTriaged(true);
        
        for (int i = 2; i < 9; i++) {
            provider.clearForNewBuild();
            build = provider.getBuild(i);
            provider.getTestExecution(testSpec);
            triage = provider.getTestCaseTriage();
        }

        assertRules(test, triage, null);
    }

    @Test
    public void permanentNewPassAndPass() {
        TestConfig test = newTestConfig()
                .rank(0)
                .newState(StateType.PASS)
                .currentStatus(StatusType.PASS)
                .rule(Rule23)
                .triaged(true)
                .build();

        TestTriagePopulate testSpec = test.getNewTestPopulate();
        testSpec.setAs(StatusType.FAIL, 1, 1);
        testSpec.setAs(StatusType.PASS, 2, 4);

        TestTriage triage = null;

        // Make test permanent
        provider.clearForNewBuild();
        Build build = provider.getBuild(1);
        provider.getTestExecution(testSpec);
        triage = provider.getTestCaseTriage();
        triage.setCurrentState(StateType.PERMANENT);
        triage.setTriaged(true);

        for (int i = 2; i < 4; i++) {
            provider.clearForNewBuild();
            build = provider.getBuild(i);
            provider.getTestExecution(testSpec);
            triage = provider.getTestCaseTriage();
        }

        assertRules(test, triage, null);
    }





    protected TestTriage createTest(TestConfig test) {
        TestTriagePopulate newTest = test.getNewTestPopulate();

        provider.getBuild(1);
        provider.getTestExecution(newTest);
        TestTriage firstTriage = provider.getTestCaseTriage();
        if (test.isFirstHasTestBug()) {
            provider.getAutomatedTestIssue();
        }
        if (test.isFirstHasProductBug()) {
            provider.getIssueTicket();
        }

        firstTriage.setCurrentState(test.getFirstState());
        firstTriage.setApplicationFailType(test.getFirstApplicationFailType());
        firstTriage.setTestFailType(test.getFirstTestFailType());
        firstTriage.setTriaged(true);
        if (test.getTags() != null && !test.getTags().isEmpty()) {
            firstTriage.addTag(test.getTags());
        }
        testTriageService.update(firstTriage);

        // Creates the previous build
        provider.clearForNewBuild();
        provider.getBuild(2);
        provider.getTestExecution(newTest);
        TestTriage previousTriage = provider.getTestCaseTriage();

        previousTriage.setCurrentState(test.getPreviousState());
        previousTriage.setApplicationFailType(test.getApplicationFailType());
        previousTriage.setTestFailType(test.getTestFailType());
        previousTriage.setTriaged(true);
        if (test.getTags() != null && !test.getTags().isEmpty()) {
            previousTriage.addTag(test.getTags());
        }
        testTriageService.update(previousTriage);

        // clear provider
        provider.clearForNewBuild();

        // Creates the latest build
        newTest = test.updateTestPopulate(newTest);
        provider.getBuild(3);
        provider.getTestExecution(newTest);

        TestTriage lastTriage = provider.getTestCaseTriage();

        // Validate how error was built
        ErrorType errorType = errorDetailService.processErrorStack(provider.getTestExecution().getStatus(), provider.getTestExecution(), previousTriage);

        assertRules(test, lastTriage, errorType);


        return lastTriage;
    }

    protected TestConfig.TestConfigBuilder newTestConfig() {
        return TestConfig.builder()
                .realDataProvider(realDataProvider)
                .errorDetailService(errorDetailService);
    }

    protected void assertRules(TestConfig test, TestTriage lastTriage, ErrorType errorType) {
        if (errorType != null) {
            Assert.assertEquals("Error Type dont match", test.getErrorDifference(), errorType);
        }

        // Validate deduction
        Assert.assertEquals("Wrong rule applied", test.getRule(), lastTriage.getStateReasonType());
        Assert.assertEquals(String.format("Deduced currentState does not match. Applied rule %s", lastTriage.getStateReasonType().name()), test.getNewState(), lastTriage.getCurrentState());
        Assert.assertEquals("Rank don't match", test.getRank(), lastTriage.getRank());
        Assert.assertEquals("Triage status don't match", test.isTriaged(), lastTriage.isTriaged());

        if (test.getTags() != null && !test.getTags().isEmpty()) {
            if (test.isIncludeTags()) {
                Assert.assertTrue(String.format("Tag not included: expected: %s actual: %s", test.getTags(), lastTriage.getTags()),lastTriage.containTag(test.getTags()));
            } else {
                Assert.assertFalse(String.format("Tag should not be included: not expected: %s actual: %s", test.getTags(), lastTriage.getTags()),lastTriage.containTag(test.getTags()));
            }
        }
    }


}
