/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.rules;

import com.clarolab.agents.rules.StaticRuleDispatcher;
import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.functional.test.util.TestConfig;
import com.clarolab.model.TestTriage;
import com.clarolab.model.types.StateType;
import com.clarolab.model.types.StatusType;
import com.clarolab.populate.TestTriagePopulate;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.TestExecutionService;
import com.clarolab.service.TestTriageService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.model.types.DeducedReasonType.*;

// The first time teh test is created, no previous builds
public class FirstTriageRuleFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private TestExecutionService testExecutionService;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private StaticRuleDispatcher staticRuleDispatcher;


    @Before
    public void clearProvider() {
        provider.clear();
        staticRuleDispatcher.setEngineEnabled(true);
    }

    @Test
    public void fail() {
        TestConfig test = TestConfig.builder()
                .rank(7)
                .newState(StateType.FAIL)
                .currentStatus(StatusType.FAIL)
                .rule(Rule34)
                .build();

        createTest(test);
    }

    @Test
    public void pass() {
        TestConfig test = TestConfig.builder()
                .rank(0)
                .newState(StateType.PASS)
                .currentStatus(StatusType.PASS)
                .triaged(true)
                .rule(Rule30)
                .build();

        createTest(test);
    }

    @Test
    public void skip() {
        TestConfig test = TestConfig.builder()
                .rank(6)
                .newState(StateType.SKIP)
                .currentStatus(StatusType.SKIP)
                .rule(Rule33)
                .build();

        createTest(test);
    }

    @Test
    public void cancel() {
        TestConfig test = TestConfig.builder()
                .rank(4)
                .newState(StateType.FAIL)
                .currentStatus(StatusType.CANCELLED)
                .rule(Rule31)
                .build();

        createTest(test);
    }

    @Test
    public void abort() {
        TestConfig test = TestConfig.builder()
                .rank(4)
                .newState(StateType.FAIL)
                .currentStatus(StatusType.ABORTED)
                .rule(Rule31)
                .build();

        createTest(test);
    }

    @Test
    public void fixed() {
        TestConfig test = TestConfig.builder()
                .rank(0)
                .newState(StateType.PASS)
                .currentStatus(StatusType.FIXED)
                .triaged(true)
                .rule(Rule30)
                .build();

        createTest(test);
    }

    @Test
    public void regression() {
        TestConfig test = TestConfig.builder()
                .rank(7)
                .newState(StateType.FAIL)
                .currentStatus(StatusType.REGRESSION)
                .rule(Rule35)
                .build();

        createTest(test);
    }

    @Test
    public void building() {
        TestConfig test = TestConfig.builder()
                .rank(4)
                .newState(StateType.FAIL)
                .currentStatus(StatusType.BUILDING)
                .rule(Rule31)
                .build();

        createTest(test);
    }





    private TestTriage createTest(TestConfig test) {
        TestTriagePopulate newTest = test.getNewTestPopulate();

        // Creates the build
        provider.getBuild(2);
        provider.getTestExecution(newTest);
        TestTriage lastTriage = provider.getTestCaseTriage();

        // Validate new test
        Assert.assertEquals("Wrong rule applied", test.getRule(), lastTriage.getStateReasonType());
        Assert.assertEquals("Deduced currentState does not match", test.getNewState(), lastTriage.getCurrentState());
        Assert.assertEquals("Rank don't match", test.getRank(), lastTriage.getRank());
        Assert.assertEquals("Triage status don't match", test.isTriaged(), lastTriage.isTriaged());
        if (test.getTags() != null && !test.getTags().isEmpty()) {
            Assert.assertTrue("Test is not flaky", lastTriage.containTag(test.getTags()));
        }

        return lastTriage;
    }

}
