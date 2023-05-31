/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.agents.rules.statics;

import com.clarolab.model.TestExecution;
import com.clarolab.model.TestTriage;
import com.clarolab.model.helper.tag.Tag;
import com.clarolab.model.types.DeducedReasonType;
import com.clarolab.model.types.StateType;
import com.clarolab.model.types.StatusType;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import static com.clarolab.model.helper.tag.TagHelper.*;
import static com.clarolab.model.types.ApplicationFailType.FILED_TICKET;
import static com.clarolab.model.types.DeducedReasonType.*;
import static com.clarolab.model.types.StateType.*;

@Log
@Component
public class SkipRule extends AbstractRule implements Rule {

    @Override
    public StatusType statusType() {
        return StatusType.SKIP;
    }

    @Override
    public TestTriage.TestTriageBuilder processFirstTriage(TestTriage.TestTriageBuilder testTriageBuilder, TestExecution testExecution) {
        super.processFirstTriage(testTriageBuilder, testExecution);
        testTriageBuilder.stateReasonType(DeducedReasonType.Rule33);
        processSkip(testTriageBuilder);
        return testTriageBuilder;
    }

    @Override
    public TestTriage.TestTriageBuilder process(TestTriage.TestTriageBuilder testTriageBuilder, TestExecution testExecution, TestTriage previousTestTriage, Tag.Builder tagBuilder){

        //StatusType currentStatus = testExecution.getStatus();
        StateType previousState = previousTestTriage.getCurrentState();

        tagBuilder.with(fromTest(testExecution)).with(NEED_TRIAGE);

        if (hasPassedInVersion(previousTestTriage, testExecution)) {
            return testTriageBuilder.currentState(StateType.FAIL).rank(3).stateReasonType(Rule45).triaged(true).tags(tagBuilder.with(AUTO_TRIAGED).with(SKIP.triagedName()).build());
        }

        if (previousTestTriage.hasProductBug()) {
            return testTriageBuilder.currentState(PERMANENT).rank(1).triaged(true).applicationFailType(FILED_TICKET).stateReasonType(Rule24).triaged(true).tags(tagBuilder.with(PERMANENT.triagedName()).build());
        }

        if (previousState == FAIL) {
            return testTriageBuilder.currentState(FAIL).rank(7).stateReasonType(Rule25).tags(tagBuilder.with(FAIL.triagedName()).build());
        }

        if (previousState == PERMANENT) {
            return testTriageBuilder.currentState(PERMANENT).rank(3).stateReasonType(Rule26).triaged(true).tags(tagBuilder.remove(NEED_TRIAGE).with(AUTO_TRIAGED).with(PERMANENT.triagedName()).build());
        }
        if (previousState == SKIP && previousTestTriage.isTriaged()) {
            return testTriageBuilder.currentState(SKIP).rank(6).stateReasonType(Rule29).triaged(true).tags(tagBuilder.remove(NEED_TRIAGE).with(AUTO_TRIAGED).with(SKIP.triagedName()).build());
        }

        return testTriageBuilder.currentState(FAIL).rank(6).stateReasonType(Rule29).tags(tagBuilder.with(FAIL.triagedName()).build());
    }

}
