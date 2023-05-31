/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.agents.rules.statics;

import com.clarolab.model.TestExecution;
import com.clarolab.model.TestTriage;
import com.clarolab.model.helper.tag.Tag;
import com.clarolab.model.types.DeducedReasonType;
import com.clarolab.model.types.ErrorType;
import com.clarolab.model.types.StateType;
import com.clarolab.model.types.StatusType;
import com.clarolab.service.ErrorDetailService;
import com.clarolab.service.TestTriageService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.model.helper.tag.TagHelper.*;
import static com.clarolab.model.types.DeducedReasonType.*;
import static com.clarolab.model.types.ErrorType.UNDEFINED;
import static com.clarolab.model.types.ErrorType.*;
import static com.clarolab.model.types.StateType.*;

@Log
@Component
public class FailRule extends AbstractRule implements Rule {

    @Autowired
    private ErrorDetailService errorDetailService;

    @Autowired
    private TestTriageService testTriageService;

    @Override
    public StatusType statusType() {
        return StatusType.FAIL;
    }

    @Override
    public TestTriage.TestTriageBuilder processFirstTriage(TestTriage.TestTriageBuilder testTriageBuilder, TestExecution testExecution) {

        // if same test in another suite has error
        if (!testExecution.isHasMultipleEnvironment() && testExecution.getTestCase().getIssueTicket() != null && testExecution.getTestCase().getIssueTicket().shouldPropagateStatus()) {
            Tag.Builder tagBuilder = new Tag.Builder();
            return testTriageBuilder.currentState(StateType.PERMANENT).rank(2).stateReasonType(Rule46).triaged(true).tags(tagBuilder.with(AUTO_TRIAGED).with(PERMANENT.triagedName()).with(SAME_ERROR_TRIAGED_BEFORE.name()).build());
        }

        // if same test in another suite has error
        if (!testExecution.isHasMultipleEnvironment() && testExecution.getTestCase().getAutomatedTestIssue() != null && testExecution.getTestCase().getAutomatedTestIssue().shouldPropagateStatus()) {
            Tag.Builder tagBuilder = new Tag.Builder();
            return testTriageBuilder.currentState(StateType.PERMANENT).rank(2).stateReasonType(Rule47).triaged(true).tags(tagBuilder.with(AUTO_TRIAGED).with(PERMANENT.triagedName()).with(SAME_ERROR_TRIAGED_BEFORE.name()).build());
        }

        super.processFirstTriage(testTriageBuilder, testExecution);
        testTriageBuilder.rank(7);
        testTriageBuilder.stateReasonType(DeducedReasonType.Rule34);

        return testTriageBuilder;
    }

    @Override
    public TestTriage.TestTriageBuilder process(TestTriage.TestTriageBuilder testTriageBuilder, TestExecution testExecution, TestTriage previousTestTriage, Tag.Builder tagBuilder){
        StatusType currentStatus = testExecution.getStatus();
        StateType previousState = previousTestTriage.getCurrentState();
        ErrorType errorType = errorDetailService.processErrorStack(testExecution.getStatus(), testExecution, previousTestTriage);
        boolean errorVerySimilar = errorType == EQUAL || errorType == VERY_SIMILAR;
        boolean isProductSomehowWorking = previousTestTriage.isProductWorking() || previousTestTriage.isProductWorkingWithExternalCause();
        boolean isTestSomehowWorking = previousTestTriage.isTestWorking() || previousTestTriage.isTestWorkingWithExternalCause();
        boolean errorNotEqual = errorType == NOT_EQUAL || errorType == UNDEFINED || errorType == NO_ERROR;
        boolean isPreviousFail = previousState == NEWFAIL || previousState == StateType.FAIL;
        boolean isPreviousPassed = previousState == PASS || previousState == NEWPASS;

        testTriageBuilder.previousErrorType(errorType);

        tagBuilder.with(fromTest(testExecution)).with(fromError(errorType));
        tagBuilder.remove(SOLID_TEST);

        // SETTING DEFAULT TRIAGE VALUES
        if (currentStatus == StatusType.FAIL && (previousTestTriage.hasTestBug() || previousTestTriage.isTestWontFix())) {
            testTriageBuilder.testFailType(previousTestTriage.getTestFailType());
        }
        if (currentStatus == StatusType.FAIL && previousTestTriage.hasProductBug()) {
            testTriageBuilder.applicationFailType(previousTestTriage.getApplicationFailType());
        }


        // SETTING RANK AND CURRENT STATE

        if (currentStatus == StatusType.FAIL && hasPassedInVersion(previousTestTriage, testExecution)) {
            return testTriageBuilder.currentState(StateType.FAIL).rank(3).stateReasonType(Rule45).triaged(true).tags(tagBuilder.with(AUTO_TRIAGED).with(FLAKY_TRIAGE).with(FAIL.triagedName()).build());
        }
        
        if (currentStatus == StatusType.FAIL && isPreviousFail && errorVerySimilar && previousTestTriage.isProductWorking() && previousTestTriage.isTestWorking()) {
            return testTriageBuilder.currentState(StateType.PERMANENT).rank(3).stateReasonType(Rule1).triaged(true).tags(tagBuilder.with(AUTO_TRIAGED).with(FLAKY_TRIAGE).with(PERMANENT.triagedName()).with(SAME_ERROR_TRIAGED_BEFORE.name()).build());
        }

        if (currentStatus == StatusType.FAIL && isPreviousFail && errorVerySimilar && previousTestTriage.isProductWorkingWithExternalCause() && previousTestTriage.isTestWorking()) {
            return testTriageBuilder.currentState(StateType.PERMANENT).rank(2).stateReasonType(Rule2).triaged(true).tags(tagBuilder.with(AUTO_TRIAGED).with(PERMANENT.triagedName()).with(SAME_ERROR_TRIAGED_BEFORE.name()).build());
        }

        if (currentStatus == StatusType.FAIL && isPreviousFail && errorVerySimilar && isProductSomehowWorking) {
            return testTriageBuilder.currentState(StateType.PERMANENT).rank(2).stateReasonType(Rule3).triaged(true).tags(tagBuilder.with(AUTO_TRIAGED).with(PERMANENT.triagedName()).with(SAME_ERROR_TRIAGED_BEFORE.name()).build());
        }

        if (currentStatus == StatusType.FAIL && isPreviousFail && errorVerySimilar && previousTestTriage.hasProductBug()) {
            return testTriageBuilder.currentState(StateType.PERMANENT).rank(2).stateReasonType(Rule4).triaged(true).tags(tagBuilder.with(AUTO_TRIAGED).with(PERMANENT.triagedName()).with(SAME_ERROR_TRIAGED_BEFORE.name()).build());
        }

        if (currentStatus == StatusType.FAIL && isPreviousFail && previousTestTriage.isTestWontFix()) {
            return testTriageBuilder.currentState(StateType.PERMANENT).rank(2).stateReasonType(Rule40).triaged(true).tags(tagBuilder.with(AUTO_TRIAGED).with(PERMANENT.triagedName()).with(SAME_ERROR_TRIAGED_BEFORE.name()).build());
        }
        
        if (testTriageService.isFlaky(previousTestTriage)) {
            tagBuilder.with(FLAKY_TRIAGE);
        }

        if (currentStatus == StatusType.FAIL && isPreviousFail && previousTestTriage.isProductSkip() && previousTestTriage.isTestSkip()) {

            testTriageBuilder.currentState(FAIL).rank(7).stateReasonType(Rule5);
            String TRIAGE_TAG = NEED_TRIAGE;

            switch (errorType) {
                case EQUAL:
                    testTriageBuilder.rank(4);
                    break;
                case VERY_SIMILAR:
                    testTriageBuilder.rank(5);
                    break;
                case NOT_EQUAL:
                    testTriageBuilder.rank(7);
                    break;
                case NO_ERROR:
                    testTriageBuilder.rank(7);
                    break;
                case SIMILAR:
                    testTriageBuilder.rank(6);
                    break;
                case UNDEFINED:
                    testTriageBuilder.rank(7);
                    break;
            }

            return testTriageBuilder.tags(tagBuilder.with(TRIAGE_TAG).with(previousTestTriage.getCurrentState().triagedName()).build());
        }

        if (currentStatus == StatusType.FAIL && isPreviousFail && errorVerySimilar && previousTestTriage.isProductSkip()) {
            return testTriageBuilder.currentState(StateType.PERMANENT).rank(3).stateReasonType(Rule6).triaged(true).tags(tagBuilder.with(AUTO_TRIAGED).with(PERMANENT.triagedName()).with(SAME_ERROR_TRIAGED_BEFORE.name()).build());
        }


        if (currentStatus == StatusType.FAIL && isPreviousFail && errorType == SIMILAR && isProductSomehowWorking && isTestSomehowWorking) {
            return testTriageBuilder.currentState(previousTestTriage.getCurrentState()).rank(4).stateReasonType(Rule7).tags(tagBuilder.with(NEED_TRIAGE).with(previousTestTriage.getCurrentState().triagedName()).build());
        }

        if (currentStatus == StatusType.FAIL && isPreviousFail && previousTestTriage.hasTestBug()) {
            return testTriageBuilder.currentState(StateType.PERMANENT).rank(3).stateReasonType(Rule8).triaged(true).tags(tagBuilder.with(AUTO_TRIAGED).with(PERMANENT.triagedName()).build());
        }

        if (currentStatus == StatusType.FAIL && isPreviousFail && previousTestTriage.hasProductBug()) {
            return testTriageBuilder.currentState(StateType.PERMANENT).rank(3).stateReasonType(Rule9).triaged(true).tags(tagBuilder.with(AUTO_TRIAGED).with(PERMANENT.triagedName()).build());
        }

        if (currentStatus == StatusType.FAIL && previousState == StateType.PERMANENT && errorVerySimilar) {
            return testTriageBuilder.currentState(StateType.PERMANENT).rank(1).stateReasonType(Rule10).triaged(true).tags(tagBuilder.with(AUTO_TRIAGED).with(PERMANENT.triagedName()).with(SAME_ERROR_TRIAGED_BEFORE.name()).build());
        }

        if (currentStatus == StatusType.FAIL && previousState == StateType.PERMANENT && previousTestTriage.hasProductBug()) {
            return testTriageBuilder.currentState(StateType.FAIL).rank(4).stateReasonType(Rule11).tags(tagBuilder.with(NEED_TRIAGE).with(FAIL.triagedName()).build());
        }

        if (currentStatus == StatusType.FAIL && previousState == StateType.PERMANENT) {
            return testTriageBuilder.currentState(StateType.PERMANENT).rank(3).stateReasonType(Rule12).triaged(true).tags(tagBuilder.with(AUTO_TRIAGED).with(PERMANENT.triagedName()).build());
        }

        if (currentStatus == StatusType.FAIL && previousState == StateType.PASS && previousTestTriage.hasProductBug()) {
            return testTriageBuilder.currentState(NEWFAIL).rank(4).stateReasonType(Rule15).tags(tagBuilder.with(NEED_TRIAGE).with(NEWFAIL.triagedName()).build());
        }

        if (currentStatus == StatusType.FAIL && previousState == StateType.PASS && previousTestTriage.isSolid()) {
            return testTriageBuilder.currentState(NEWFAIL).rank(8).stateReasonType(Rule42).tags(tagBuilder.with(NEED_TRIAGE).with(NEWFAIL.triagedName()).build());
        }

        if (currentStatus == StatusType.FAIL && isPreviousPassed && previousTestTriage.hasTestBug()) {
            return testTriageBuilder.currentState(PERMANENT).rank(2).stateReasonType(Rule43).triaged(true).tags(tagBuilder.with(AUTO_TRIAGED).with(PERMANENT.triagedName()).build());
        }

        if (currentStatus == StatusType.FAIL && isPreviousPassed && previousTestTriage.isTestWontFix()) {
            return testTriageBuilder.currentState(PERMANENT).rank(2).stateReasonType(Rule44).triaged(true).tags(tagBuilder.with(AUTO_TRIAGED).with(PERMANENT.triagedName()).build());
        }


        testTriageBuilder.currentState(StateType.FAIL).rank(6).stateReasonType(Rule41).tags(tagBuilder.with(NEED_TRIAGE).with(StateType.FAIL.triagedName()).build());

        return testTriageBuilder;
    }

}
