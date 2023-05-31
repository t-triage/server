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

import static com.clarolab.model.helper.tag.TagHelper.NEED_TRIAGE;
import static com.clarolab.model.helper.tag.TagHelper.fromTest;
import static com.clarolab.model.types.StateType.UNDEFINED;
import static com.clarolab.model.types.StatusType.UNKNOWN;
import static com.clarolab.util.StringUtils.getSystemError;

@Log
@Component
public class UndefinedRule extends AbstractRule implements Rule {

    @Override
    public StatusType statusType() {
        return UNKNOWN;
    }

    @Override
    public TestTriage.TestTriageBuilder process(TestTriage.TestTriageBuilder testTriageBuilder, TestExecution testExecution, TestTriage previousTestTriage, Tag.Builder tagBuilder){
        log.severe(getSystemError(String.format("Undefined Current Node %d", testExecution.getId())));

        tagBuilder.with(fromTest(testExecution)).with(NEED_TRIAGE).with(UNDEFINED.triagedName());
        return testTriageBuilder.currentState(StateType.NEWFAIL).tags(tagBuilder.build());
    }
}
