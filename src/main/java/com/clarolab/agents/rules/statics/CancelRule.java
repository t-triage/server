/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.agents.rules.statics;

import com.clarolab.model.TestExecution;
import com.clarolab.model.TestTriage;
import com.clarolab.model.helper.tag.Tag;
import com.clarolab.model.types.StateType;
import com.clarolab.model.types.StatusType;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import static com.clarolab.model.helper.tag.TagHelper.*;
import static com.clarolab.model.types.ApplicationFailType.FILED_TICKET;
import static com.clarolab.model.types.DeducedReasonType.*;
import static com.clarolab.model.types.StateType.FAIL;
import static com.clarolab.model.types.StateType.PERMANENT;

@Log
@Component
public class CancelRule extends AbstractRule implements Rule {

    @Override
    public StatusType statusType() {
        return StatusType.CANCELLED;
    }

    @Override
    public TestTriage.TestTriageBuilder process(TestTriage.TestTriageBuilder testTriageBuilder, TestExecution testExecution, TestTriage previousTestTriage, Tag.Builder tagBuilder){

        //StatusType currentStatus = testExecution.getStatus();
        StateType previousState = previousTestTriage.getCurrentState();

        tagBuilder.with(NEED_TRIAGE).with(fromTest(testExecution));

        if (hasPassedInVersion(previousTestTriage, testExecution)) {
            return testTriageBuilder.currentState(StateType.FAIL).rank(3).stateReasonType(Rule45).triaged(true).tags(tagBuilder.with(AUTO_TRIAGED).with(FAIL.triagedName()).build());
        }

        if (previousTestTriage.hasProductBug()) {
            return testTriageBuilder.currentState(PERMANENT).rank(1).applicationFailType(FILED_TICKET).stateReasonType(Rule24).triaged(true).tags(tagBuilder.with(PERMANENT.triagedName()).build());
        }

        if (previousState == FAIL) {
            return testTriageBuilder.currentState(FAIL).rank(5).stateReasonType(Rule27).tags(tagBuilder.with(FAIL.triagedName()).build());
        }

        if (previousState == PERMANENT) {
            return testTriageBuilder.currentState(PERMANENT).rank(2).stateReasonType(Rule28).triaged(true).tags(tagBuilder.with(PERMANENT.triagedName()).build());
        }

        testTriageBuilder.currentState(FAIL).rank(6).stateReasonType(Rule29).tags(tagBuilder.with(FAIL.triagedName()).build());

        return testTriageBuilder;
    }

}
