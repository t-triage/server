/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.agents.rules.statics;

import com.clarolab.model.TestExecution;
import com.clarolab.model.TestTriage;
import com.clarolab.model.helper.tag.Tag;
import com.clarolab.model.types.StateType;
import com.clarolab.model.types.StatusType;
import com.clarolab.model.types.TestFailType;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import static com.clarolab.model.helper.tag.TagHelper.*;
import static com.clarolab.model.types.DeducedReasonType.*;
import static com.clarolab.model.types.StateType.PASS;

@Log
@Component
public class FixedRule extends AbstractRule implements Rule {

    @Override
    public StatusType statusType() {
        return StatusType.FIXED;
    }

    @Override
    public TestTriage.TestTriageBuilder process(TestTriage.TestTriageBuilder testTriageBuilder, TestExecution testExecution, TestTriage previousTestTriage, Tag.Builder tagBuilder){

        //StatusType currentStatus = testExecution.getStatus();
        StateType previousState = previousTestTriage.getCurrentState();

        tagBuilder.with(fromTest(testExecution));

        if (previousState == StateType.FAIL) {
            return testTriageBuilder.currentState(StateType.NEWPASS).rank(4).stateReasonType(Rule17).testFailType(TestFailType.NO_FAIL).tags(tagBuilder.with(NEED_TRIAGE).with(PASS.triagedName()).build());
        }

        if (previousState == StateType.PERMANENT) {
            return testTriageBuilder.currentState(StateType.NEWPASS).rank(5).stateReasonType(Rule18).testFailType(TestFailType.NO_FAIL).tags(tagBuilder.with(NEED_TRIAGE).with(PASS.triagedName()).build());
        }

        testTriageBuilder.currentState(PASS).rank(0).stateReasonType(Rule19).testFailType(TestFailType.NO_FAIL).triaged(true).tags(tagBuilder.with(AUTO_TRIAGED).with(PASS.triagedName()).build());

        return testTriageBuilder;
    }

}
