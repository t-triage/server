/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.agents.rules.statics;

import com.clarolab.model.TestExecution;
import com.clarolab.model.TestTriage;
import com.clarolab.model.helper.tag.Tag;
import com.clarolab.model.types.StatusType;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log
@Component
public class AbortRule extends AbstractRule implements Rule {

    @Autowired
    CancelRule cancelRule;

    @Override
    public StatusType statusType() {
        return StatusType.ABORTED;
    }

    @Override
    public TestTriage.TestTriageBuilder process(TestTriage.TestTriageBuilder testTriageBuilder, TestExecution testExecution, TestTriage previousTestTriage, Tag.Builder tagBuilder){
        return cancelRule.process(testTriageBuilder, testExecution, previousTestTriage, tagBuilder);
    }

}
