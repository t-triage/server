/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.rules;

import com.clarolab.agents.rules.StaticRuleDispatcher;
import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.functional.test.util.TestConfig;
import com.clarolab.model.TestTriage;
import com.clarolab.model.types.*;
import com.clarolab.populate.RealDataProvider;
import com.clarolab.populate.TestTriagePopulate;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.ErrorDetailService;
import com.clarolab.service.PropertyService;
import com.clarolab.service.TestExecutionService;
import com.clarolab.service.TestTriageService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.model.helper.tag.TagHelper.FLAKY_TRIAGE;
import static com.clarolab.model.helper.tag.TagHelper.SOLID_TEST;
import static com.clarolab.model.types.DeducedReasonType.*;

// This matches: https://docs.google.com/spreadsheets/d/1ckeRVOkDiZ8i0FXdyp-aD9DYHr-fhY7u4sOf2ihqffY/edit#gid=164143202
public class AbstractTriageRuleFunctionalTest extends BaseFunctionalTest {

    @Autowired
    protected UseCaseDataProvider provider;

    @Autowired
    protected TestExecutionService testExecutionService;

    @Autowired
    protected TestTriageService testTriageService;

    @Autowired
    protected ErrorDetailService errorDetailService;
    
    @Autowired
    protected RealDataProvider realDataProvider;

    @Autowired
    protected PropertyService propertyService;

    @Autowired
    protected StaticRuleDispatcher staticRuleDispatcher;


    @Before
    public void clearProvider() {
        provider.clear();
        staticRuleDispatcher.setEngineEnabled(true);
    }

    @Test
    public void id1() {
        TestConfig test = newTestConfig()
                .rank(3)
                .newState(StateType.PERMANENT)
                .previousState(StateType.FAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.VERY_SIMILAR)
                .applicationFailType(ApplicationFailType.NO_FAIL)
                .testFailType(TestFailType.NO_FAIL)
                .rule(Rule1)
                .tags(FLAKY_TRIAGE)
                .includeTags(true)
                .triaged(true)
                .build();

        createTest(test);
    }

    @Test
    public void id2() {
        TestConfig test = newTestConfig()
                .rank(2)
                .newState(StateType.PERMANENT)
                .previousState(StateType.FAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.VERY_SIMILAR)
                .applicationFailType(ApplicationFailType.EXTERNAL_CAUSE)
                .testFailType(TestFailType.NO_FAIL)
                .triaged(true)
                .rule(Rule2)
                .build();

        createTest(test);
    }

    @Test
    public void id3() {
        TestConfig test = newTestConfig()
                .rank(2)
                .newState(StateType.PERMANENT)
                .previousState(StateType.FAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.VERY_SIMILAR)
                .applicationFailType(ApplicationFailType.NO_FAIL)
                .testFailType(TestFailType.WONT_FILE) // else
                .triaged(true)
                .rule(Rule3)
                .build();

        createTest(test);
    }

    @Test
    public void id4() {
        TestConfig test = newTestConfig()
                .rank(2)
                .newState(StateType.PERMANENT)
                .previousState(StateType.FAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.VERY_SIMILAR)
                .applicationFailType(ApplicationFailType.FILED_TICKET)
                .testFailType(TestFailType.UNDEFINED) // any
                .triaged(true)
                .rule(Rule4)
                .build();

        createTest(test);
    }


    @Test
    public void id5_equal() {
        TestConfig test = newTestConfig()
                .rank(4)
                .newState(StateType.FAIL)
                .previousState(StateType.FAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.EQUAL)
                .applicationFailType(ApplicationFailType.UNDEFINED)
                .testFailType(TestFailType.UNDEFINED)
                .rule(Rule5)
                .build();

        createTest(test);
    }

    @Test
    public void id5_verySimilar() {
        TestConfig test = newTestConfig()
                .rank(5)
                .newState(StateType.FAIL)
                .previousState(StateType.FAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.VERY_SIMILAR)
                .applicationFailType(ApplicationFailType.UNDEFINED)
                .testFailType(TestFailType.UNDEFINED)
                .rule(Rule5)
                .build();

        createTest(test);
    }

    @Test
    public void id5_similar() {
        TestConfig test = newTestConfig()
                .rank(6)
                .newState(StateType.FAIL)
                .previousState(StateType.FAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.SIMILAR)
                .applicationFailType(ApplicationFailType.UNDEFINED)
                .testFailType(TestFailType.UNDEFINED)
                .rule(Rule5)
                .build();

        createTest(test);
    }

    @Test
    public void id5_notEqual() {
        TestConfig test = newTestConfig()
                .rank(7)
                .newState(StateType.FAIL)
                .previousState(StateType.FAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.NOT_EQUAL)
                .applicationFailType(ApplicationFailType.UNDEFINED)
                .testFailType(TestFailType.UNDEFINED)
                .rule(Rule5)
                .build();

        createTest(test);
    }

    @Test
    public void id5_notError() {
        TestConfig test = newTestConfig()
                .rank(7)
                .newState(StateType.FAIL)
                .previousState(StateType.FAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.NOT_EQUAL)
                .applicationFailType(ApplicationFailType.UNDEFINED)
                .testFailType(TestFailType.UNDEFINED)
                .rule(Rule5)
                .build();

        createTest(test);
    }


    @Test
    public void id6() {
        TestConfig test = newTestConfig()
                .rank(3)
                .newState(StateType.PERMANENT)
                .previousState(StateType.FAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.VERY_SIMILAR)
                .applicationFailType(ApplicationFailType.UNDEFINED)
                .testFailType(TestFailType.NO_FAIL) // else
                .triaged(true)
                .rule(Rule6)
                .build();

        createTest(test);
    }

    @Test
    public void id7() {
        TestConfig test = newTestConfig()
                .rank(4)
                .newState(StateType.FAIL)
                .previousState(StateType.FAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.SIMILAR)
                .applicationFailType(ApplicationFailType.NO_FAIL)
                .testFailType(TestFailType.NO_FAIL)
                .rule(Rule7)
                .build();

        createTest(test);
    }

    @Test
    public void id8() {
        TestConfig test = newTestConfig()
                .rank(3)
                .newState(StateType.PERMANENT)
                .previousState(StateType.FAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.NOT_EQUAL)
                .applicationFailType(ApplicationFailType.UNDEFINED)
                .testFailType(TestFailType.TEST_ASSIGNED_TO_FIX)
                .triaged(true)
                .rule(Rule8)
                .build();

        createTest(test);
    }

    @Test
    public void id9() {
        TestConfig test = newTestConfig()
                .rank(3)
                .newState(StateType.PERMANENT)
                .previousState(StateType.FAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.NOT_EQUAL)
                .applicationFailType(ApplicationFailType.FILED_TICKET)
                .testFailType(TestFailType.UNDEFINED) //any
                .triaged(true)
                .rule(Rule9)
                .build();

        createTest(test);
    }

    @Test
    public void id10() {
        TestConfig test = newTestConfig()
                .rank(1)
                .newState(StateType.PERMANENT)
                .previousState(StateType.PERMANENT)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.VERY_SIMILAR)
                .applicationFailType(ApplicationFailType.UNDEFINED) // any
                .testFailType(TestFailType.UNDEFINED) // any
                .triaged(true)
                .rule(Rule10)
                .build();

        createTest(test);
    }

    @Test
    public void id11() {
        TestConfig test = newTestConfig()
                .rank(4)
                .newState(StateType.FAIL)
                .previousState(StateType.PERMANENT)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.NOT_EQUAL)
                .applicationFailType(ApplicationFailType.FILED_TICKET)
                .testFailType(TestFailType.UNDEFINED) // any
                .rule(Rule11)
                .build();

        createTest(test);
    }

    @Test
    public void id12() {
        TestConfig test = newTestConfig()
                .rank(3)
                .newState(StateType.PERMANENT)
                .previousState(StateType.PERMANENT)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.NOT_EQUAL)
                .applicationFailType(ApplicationFailType.UNDEFINED) // any
                .testFailType(TestFailType.UNDEFINED) // any
                .triaged(true)
                .rule(Rule12)
                .build();

        createTest(test);
    }

    @Test
    public void id13() {
        TestConfig test = newTestConfig()
                .rank(5)
                .newState(StateType.FAIL)
                .previousState(StateType.NEWFAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.VERY_SIMILAR)
                .applicationFailType(ApplicationFailType.UNDEFINED)
                .testFailType(TestFailType.UNDEFINED) // any
                .rule(Rule5) // Old Rule13
                .build();

        createTest(test);
    }

    @Test
    public void id14_1() {
        TestConfig test = newTestConfig()
                .rank(3)
                .newState(StateType.PERMANENT)
                .previousState(StateType.NEWFAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.VERY_SIMILAR)
                .applicationFailType(ApplicationFailType.NO_FAIL)
                .testFailType(TestFailType.NO_FAIL)
                .triaged(true)
                .rule(Rule1)
                .build();

        createTest(test);
    }

    @Test
    public void id14_2() {
        TestConfig test = newTestConfig()
                .rank(2)
                .newState(StateType.PERMANENT)
                .previousState(StateType.NEWFAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.VERY_SIMILAR)
                .applicationFailType(ApplicationFailType.EXTERNAL_CAUSE)
                .testFailType(TestFailType.NO_FAIL)
                .triaged(true)
                .rule(Rule2)
                .build();

        createTest(test);
    }

    @Test
    public void id15() {
        TestConfig test = newTestConfig()
                .rank(4)
                .newState(StateType.NEWFAIL)
                .previousState(StateType.PASS)
                .previousStatus(StatusType.PASS)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.NOT_EQUAL)
                .applicationFailType(ApplicationFailType.FILED_TICKET)
                .testFailType(TestFailType.UNDEFINED) // any
                .rule(Rule15)
                .build();

        createTest(test);
    }

    @Test
    public void id16() {
        TestConfig test = newTestConfig()
                .rank(0)
                .newState(StateType.PASS)
                .previousState(StateType.PASS)
                .currentStatus(StatusType.PASS)
                .previousStatus(StatusType.PASS)
                .errorDifference(ErrorType.NOT_EQUAL)
                .applicationFailType(ApplicationFailType.NO_FAIL)
                .testFailType(TestFailType.NO_FAIL)
                .triaged(true)
                .rule(Rule16)
                .build();

        createTest(test);
    }

    @Test
    public void id16_solid() {
        TestConfig test = newTestConfig()
                .rank(0)
                .newState(StateType.PASS)
                .previousState(StateType.PASS)
                .previousStatus(StatusType.PASS)
                .currentStatus(StatusType.PASS)
                .errorDifference(ErrorType.NOT_EQUAL)
                .applicationFailType(ApplicationFailType.UNDEFINED)
                .testFailType(TestFailType.UNDEFINED) //any
                .rule(Rule16)
                .triaged(true)
                .tags(SOLID_TEST)
                .includeTags(true)
                .build();

        createTest(test);
    }

    @Test
    public void id17() {
        TestConfig test = newTestConfig()
                .rank(4)
                .newState(StateType.NEWPASS)
                .previousState(StateType.FAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.FIXED)
                .errorDifference(ErrorType.NOT_EQUAL) // any
                .applicationFailType(ApplicationFailType.UNDEFINED) // any
                .testFailType(TestFailType.UNDEFINED) // any
                .rule(Rule17)
                .build();

        createTest(test);
    }

    @Test
    public void id18() {
        TestConfig test = newTestConfig()
                .rank(5)
                .newState(StateType.NEWPASS)
                .previousState(StateType.PERMANENT)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.FIXED)
                .errorDifference(ErrorType.NOT_EQUAL) // any
                .applicationFailType(ApplicationFailType.UNDEFINED) // any
                .testFailType(TestFailType.UNDEFINED) // any
                .rule(Rule18)
                .build();

        createTest(test);
    }

    @Test
    public void id19() {
        TestConfig test = newTestConfig()
                .rank(0)
                .newState(StateType.PASS)
                .previousState(StateType.NEWPASS)
                .previousStatus(StatusType.PASS)
                .currentStatus(StatusType.FIXED)
                .errorDifference(ErrorType.NOT_EQUAL) // any
                .applicationFailType(ApplicationFailType.UNDEFINED) // any
                .testFailType(TestFailType.UNDEFINED) // any
                .triaged(true)
                .rule(Rule19)
                .build();

        createTest(test);
    }

    @Test
    public void id20() {
        TestConfig test = newTestConfig()
                .rank(0)
                .newState(StateType.PASS)
                .previousState(StateType.FAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.PASS)
                .errorDifference(ErrorType.NOT_EQUAL)
                .applicationFailType(ApplicationFailType.FILED_TICKET)
                .testFailType(TestFailType.UNDEFINED) // any
                .triaged(true)
                .tags(FLAKY_TRIAGE)
                .includeTags(true)
                .rule(Rule20)
                .build();

        createTest(test);
    }

    @Test
    public void id21() {
        TestConfig test = newTestConfig()
                .rank(2)
                .newState(StateType.NEWPASS)
                .previousState(StateType.FAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.PASS)
                .errorDifference(ErrorType.NOT_EQUAL)
                .applicationFailType(ApplicationFailType.UNDEFINED) // any
                .testFailType(TestFailType.UNDEFINED) // any
                .rule(Rule21)
                .triaged(true)
                .build();

        createTest(test);
    }

    @Test
    public void id22() {
        TestConfig test = newTestConfig()
                .rank(3)
                .newState(StateType.NEWPASS)
                .previousState(StateType.PERMANENT)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.PASS)
                .errorDifference(ErrorType.NOT_EQUAL) // any
                .applicationFailType(ApplicationFailType.UNDEFINED) // any
                .testFailType(TestFailType.UNDEFINED) // any
                .rule(Rule22)
                .triaged(true)
                .build();

        createTest(test);
    }

    @Test
    public void id23() {
        TestConfig test = newTestConfig()
                .rank(0)
                .newState(StateType.PASS)
                .previousState(StateType.NEWPASS)
                .previousStatus(StatusType.PASS)
                .currentStatus(StatusType.PASS)
                .errorDifference(ErrorType.NOT_EQUAL) // any
                .applicationFailType(ApplicationFailType.UNDEFINED) // any
                .testFailType(TestFailType.UNDEFINED) // any
                .triaged(true)
                .rule(Rule23)
                .build();

        createTest(test);
    }

    @Test
    public void id24_skip() {
        TestConfig test = newTestConfig()
                .rank(1)
                .newState(StateType.PERMANENT)
                .previousState(StateType.FAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.SKIP)
                .errorDifference(ErrorType.NOT_EQUAL) // any
                .applicationFailType(ApplicationFailType.FILED_TICKET)
                .testFailType(TestFailType.UNDEFINED) // any
                .triaged(true)
                .rule(Rule24)
                .build();

        createTest(test);
    }

    @Test
    public void id24_cancel() {
        TestConfig test = newTestConfig()
                .rank(1)
                .newState(StateType.PERMANENT)
                .previousState(StateType.FAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.CANCELLED)
                .errorDifference(ErrorType.NOT_EQUAL) // any
                .applicationFailType(ApplicationFailType.FILED_TICKET)
                .testFailType(TestFailType.UNDEFINED) // any
                .triaged(true)
                .rule(Rule24)
                .build();

        createTest(test);
    }

    @Test
    public void id25() {
        TestConfig test = newTestConfig()
                .rank(7)
                .newState(StateType.FAIL)
                .previousState(StateType.FAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.SKIP)
                .errorDifference(ErrorType.NOT_EQUAL) // any
                .applicationFailType(ApplicationFailType.UNDEFINED) // any
                .testFailType(TestFailType.UNDEFINED) // any
                .rule(Rule25)
                .build();

        createTest(test);
    }

    @Test
    public void id26() {
        TestConfig test = newTestConfig()
                .rank(3)
                .newState(StateType.PERMANENT)
                .previousState(StateType.PERMANENT)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.SKIP)
                .errorDifference(ErrorType.NOT_EQUAL) // any
                .applicationFailType(ApplicationFailType.UNDEFINED) // any
                .testFailType(TestFailType.UNDEFINED) // any
                .triaged(true)
                .rule(Rule26)
                .build();

        createTest(test);
    }

    @Test
    public void id27_abort() {
        TestConfig test = newTestConfig()
                .rank(5)
                .newState(StateType.FAIL)
                .previousState(StateType.FAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.ABORTED)
                .errorDifference(ErrorType.NOT_EQUAL) // any
                .applicationFailType(ApplicationFailType.UNDEFINED) // any
                .testFailType(TestFailType.UNDEFINED) // any
                .rule(Rule27)
                .build();

        createTest(test);
    }

    @Test
    public void id27_cancel() {
        TestConfig test = newTestConfig()
                .rank(5)
                .newState(StateType.FAIL)
                .previousState(StateType.FAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.CANCELLED)
                .errorDifference(ErrorType.NOT_EQUAL) // any
                .applicationFailType(ApplicationFailType.UNDEFINED) // any
                .testFailType(TestFailType.UNDEFINED) // any
                .rule(Rule27)
                .build();

        createTest(test);
    }

    @Test
    public void id28_abort() {
        TestConfig test = newTestConfig()
                .rank(2)
                .newState(StateType.PERMANENT)
                .previousState(StateType.PERMANENT)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.ABORTED)
                .errorDifference(ErrorType.NOT_EQUAL) // any
                .applicationFailType(ApplicationFailType.UNDEFINED) // any
                .testFailType(TestFailType.UNDEFINED) // any
                .triaged(true)
                .rule(Rule28)
                .build();

        createTest(test);
    }

    @Test
    public void id28_cancel() {
        TestConfig test = newTestConfig()
                .rank(2)
                .newState(StateType.PERMANENT)
                .previousState(StateType.PERMANENT)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.CANCELLED)
                .errorDifference(ErrorType.NOT_EQUAL) // any
                .applicationFailType(ApplicationFailType.UNDEFINED) // any
                .testFailType(TestFailType.UNDEFINED) // any
                .triaged(true)
                .rule(Rule28)
                .build();

        createTest(test);
    }

    @Test
    public void id29_abort() {
        TestConfig test = newTestConfig()
                .rank(6)
                .newState(StateType.FAIL)
                .previousState(StateType.NEWFAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.ABORTED)
                .errorDifference(ErrorType.NOT_EQUAL) // any
                .applicationFailType(ApplicationFailType.UNDEFINED) // any
                .testFailType(TestFailType.UNDEFINED) // any
                .rule(Rule29)
                .build();

        createTest(test);
    }

    @Test
    public void id29_cancel() {
        TestConfig test = newTestConfig()
                .rank(6)
                .newState(StateType.FAIL)
                .previousState(StateType.NEWPASS)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.CANCELLED)
                .errorDifference(ErrorType.NOT_EQUAL) // any
                .applicationFailType(ApplicationFailType.UNDEFINED) // any
                .testFailType(TestFailType.UNDEFINED) // any
                .rule(Rule29)
                .build();

        createTest(test);
    }

    @Test
    public void id29_skip() {
        TestConfig test = newTestConfig()
                .rank(6)
                .newState(StateType.SKIP)
                .previousState(StateType.SKIP)
                .previousStatus(StatusType.SKIP)
                .currentStatus(StatusType.SKIP)
                .errorDifference(ErrorType.NOT_EQUAL) // any
                .applicationFailType(ApplicationFailType.UNDEFINED) // any
                .testFailType(TestFailType.UNDEFINED) // any
                .rule(Rule29)
                .triaged(true)
                .build();

        createTest(test);
    }

    @Test
    public void id40_skip() {
        TestConfig test = newTestConfig()
                .rank(2)
                .newState(StateType.PERMANENT)
                .previousState(StateType.FAIL)
                .previousStatus(StatusType.FAIL)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.EQUAL) // any
                .applicationFailType(ApplicationFailType.UNDEFINED) // any
                .testFailType(TestFailType.WONT_FILE) // any
                .rule(Rule40)
                .triaged(true)
                .build();

        createTest(test);
    }

    @Test
    public void id41() {
        TestConfig test = newTestConfig()
                .rank(6)
                .newState(StateType.FAIL)
                .previousState(StateType.NEWPASS)
                .previousStatus(StatusType.PASS)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.NOT_EQUAL)
                .applicationFailType(ApplicationFailType.UNDEFINED)
                .testFailType(TestFailType.UNDEFINED) //any
                .rule(Rule41)
                .build();

        createTest(test);
    }

    @Test
    public void id42() {
        TestConfig test = newTestConfig()
                .rank(8)
                .newState(StateType.NEWFAIL)
                .previousState(StateType.PASS)
                .previousStatus(StatusType.PASS)
                .currentStatus(StatusType.FAIL)
                .errorDifference(ErrorType.NOT_EQUAL)
                .applicationFailType(ApplicationFailType.UNDEFINED)
                .testFailType(TestFailType.UNDEFINED) //any
                .rule(Rule42)
                .tags(SOLID_TEST)
                .includeTags(false)
                .build();

        createTest(test);
    }


    @Test
    public void errorNotEqual() {
        TestConfig test = newTestConfig()
                .rank(0)
                .newState(StateType.PASS)
                .previousState(StateType.PASS)
                .previousStatus(StatusType.PASS)
                .currentStatus(StatusType.PASS)
                .errorDifference(ErrorType.NOT_EQUAL) // any
                .applicationFailType(ApplicationFailType.UNDEFINED) // any
                .testFailType(TestFailType.UNDEFINED) // any
                .triaged(true)
                .rule(Rule16)
                .build();

        createTest(test);
    }

    @Test
    public void errorEqual() {
        TestConfig test = newTestConfig()
                .rank(0)
                .newState(StateType.PASS)
                .previousState(StateType.PASS)
                .previousStatus(StatusType.PASS)
                .currentStatus(StatusType.PASS)
                .errorDifference(ErrorType.EQUAL) // any
                .applicationFailType(ApplicationFailType.UNDEFINED) // any
                .testFailType(TestFailType.UNDEFINED) // any
                .triaged(true)
                .rule(Rule16)
                .build();

        createTest(test);
    }

    @Test
    public void errorVerySimilar() {
        TestConfig test = newTestConfig()
                .rank(0)
                .newState(StateType.PASS)
                .previousState(StateType.PASS)
                .previousStatus(StatusType.PASS)
                .currentStatus(StatusType.PASS)
                .errorDifference(ErrorType.VERY_SIMILAR) // any
                .applicationFailType(ApplicationFailType.UNDEFINED) // any
                .testFailType(TestFailType.UNDEFINED) // any
                .triaged(true)
                .rule(Rule16)
                .build();

        createTest(test);
    }

    @Test
    public void errorSimilar() {
        TestConfig test = newTestConfig()
                .rank(0)
                .newState(StateType.PASS)
                .previousState(StateType.PASS)
                .previousStatus(StatusType.PASS)
                .currentStatus(StatusType.PASS)
                .errorDifference(ErrorType.SIMILAR) // any
                .applicationFailType(ApplicationFailType.UNDEFINED) // any
                .testFailType(TestFailType.UNDEFINED) // any
                .triaged(true)
                .rule(Rule16)
                .build();

        createTest(test);
    }




    protected TestTriage createTest(TestConfig test) {
        TestTriagePopulate newTest = test.getNewTestPopulate();

        // Creates the previous build
        provider.getBuild(1);
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
        provider.setBuild(null);
        provider.setReport(null);
        provider.setTestExecution(null);
        provider.setBuildTriage(null);

        // Creates the latest build
        newTest = test.updateTestPopulate(newTest);
        provider.getBuild(2);
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
        Assert.assertEquals("Error Type dont match", test.getErrorDifference(), errorType);

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
