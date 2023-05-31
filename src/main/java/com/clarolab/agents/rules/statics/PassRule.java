/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.agents.rules.statics;

import com.clarolab.model.TestExecution;
import com.clarolab.model.TestTriage;
import com.clarolab.model.helper.tag.Tag;
import com.clarolab.model.types.ErrorType;
import com.clarolab.model.types.StateType;
import com.clarolab.model.types.StatusType;
import com.clarolab.model.types.TestFailType;
import com.clarolab.service.PropertyService;
import com.clarolab.service.TestTriageService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.model.helper.tag.TagHelper.*;
import static com.clarolab.model.types.DeducedReasonType.*;
import static com.clarolab.model.types.StateType.NEWPASS;
import static com.clarolab.model.types.StateType.PASS;
import static com.clarolab.util.Constants.CONSECUTIVE_PASS_COUNT;
import static com.clarolab.util.Constants.DEFAULT_CONSECUTIVE_PASS_COUNT;

@Log
@Component
public class PassRule extends AbstractRule implements Rule {

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private PropertyService propertyService;

    private int sizePass = 0;

    @Override
    public StatusType statusType() {
        return StatusType.PASS;
    }

    @Override
    public TestTriage.TestTriageBuilder process(TestTriage.TestTriageBuilder testTriageBuilder, TestExecution testExecution, TestTriage previousTestTriage, Tag.Builder tagBuilder){

        //StatusType currentStatus = testExecution.getStatus();
        StateType previousState = previousTestTriage.getCurrentState();

        tagBuilder.with(fromTest(testExecution)).with(fromError(ErrorType.NO_ERROR));

        if (previousState == PASS) {
            updateTags(previousTestTriage, tagBuilder);

            return testTriageBuilder.currentState(PASS).rank(0).stateReasonType(Rule16).triaged(true).testFailType(TestFailType.NO_FAIL).tags(tagBuilder.with(AUTO_TRIAGED).with(PASS.triagedName()).build());
        }

        if (previousState == StateType.PERMANENT) {
            return testTriageBuilder.currentState(NEWPASS).rank(3).stateReasonType(Rule22).testFailType(TestFailType.NO_FAIL).triaged(true).tags(tagBuilder.with(AUTO_TRIAGED).with( NEWPASS.triagedName()).build());
        }

        if (previousState == StateType.FAIL && previousTestTriage.isFlaky()) {
            return testTriageBuilder.currentState(PASS).rank(0).stateReasonType(Rule20).triaged(true).testFailType(TestFailType.NO_FAIL).tags(tagBuilder.with(AUTO_TRIAGED).with(FLAKY_TRIAGE).with(PASS.triagedName()).build());
        }

        if (previousState == StateType.FAIL && !previousTestTriage.isFlaky()) {
            return testTriageBuilder.currentState(NEWPASS).rank(2).stateReasonType(Rule21).testFailType(TestFailType.NO_FAIL).triaged(true).tags(tagBuilder.with(AUTO_TRIAGED).with(NEWPASS.triagedName()).build());
        }

        testTriageBuilder.currentState(PASS).rank(0).stateReasonType(Rule23).triaged(true).testFailType(TestFailType.NO_FAIL).tags(tagBuilder.with(AUTO_TRIAGED).with(PASS.triagedName()).build());

        return testTriageBuilder;
    }

    private int getSolidLimit() {
        if (sizePass == 0) {
            sizePass = propertyService.valueOf(CONSECUTIVE_PASS_COUNT, DEFAULT_CONSECUTIVE_PASS_COUNT);
        }
        return sizePass;
    }

    private void updateTags(TestTriage previousTestTriage, Tag.Builder tagBuilder) {
        TestTriage noPassTest = testTriageService.lastTestWithoutStates(previousTestTriage, TestTriage.passStates());

        if (noPassTest != null && noPassTest.isFlaky() && !previousTestTriage.isFlaky()) {
            tagBuilder.with(FLAKY_TRIAGE);
        }
        if (previousTestTriage.isFlaky()) {
            if (noPassTest == null) {
                // mmm ... why the flaky tag was created if it has never failed?
                tagBuilder.remove(FLAKY_TRIAGE);
            } else {
                int consecutivePasses = previousTestTriage.getBuildNumber() - noPassTest.getBuildNumber();
                if (consecutivePasses < getSolidLimit()) {
                    // It is passing but not enough times to consider solid test
                    // nothing to do, it is already contained
                } else {
                    // After x consecutive passes we consider it is not a flaky test anymore.
                    tagBuilder.remove(FLAKY_TRIAGE);
                }
            }
        } else {
            if (previousTestTriage.isSolid()) {
                // Last triaged test was solid, but let's see if there is a non triaged test failing
                if (noPassTest == null || noPassTest.getBuildNumber() < previousTestTriage.getBuildNumber()) {
                    // it is still a solid test
                    tagBuilder.with(SOLID_TEST);
                }
            } else {
                if (noPassTest == null) {
                    // All tests were passed since it was created. let's see how many passes there were
                    long countTests = testTriageService.count(previousTestTriage.getTestCase());
                    if (countTests < getSolidLimit()) {
                        // nothing to do, it is not yet a solid test
                    }
                    else {
                        tagBuilder.with(SOLID_TEST);
                    }
                } else {
                    int consecutivePasses = previousTestTriage.getBuildNumber() - noPassTest.getBuildNumber();
                    if (consecutivePasses < getSolidLimit()) {
                        // nothing to do, it is not yet a solid test
                    } else {
                        // Let's get sure the builds between both build numbers have been really imported
                        long countBuilds = testTriageService.countTestsBetweenBuilds(previousTestTriage, noPassTest.getBuildNumber(), previousTestTriage.getBuildNumber());
                        if (countBuilds >= getSolidLimit()) {
                            tagBuilder.with(SOLID_TEST);
                        }
                    }
                }
            }
        }
    }

}

