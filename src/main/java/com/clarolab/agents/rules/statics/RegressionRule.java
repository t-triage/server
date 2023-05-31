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

import static com.clarolab.model.types.DeducedReasonType.Rule35;

@Log
@Component
public class RegressionRule extends AbstractRule implements Rule {

    @Override
    public StatusType statusType() {
        return StatusType.REGRESSION;
    }

    @Autowired
    FailRule failRule;

    @Override
    public TestTriage.TestTriageBuilder process(TestTriage.TestTriageBuilder testTriageBuilder, TestExecution testExecution, TestTriage previousTestTriage, Tag.Builder tagBuilder){
        return failRule.process(testTriageBuilder, testExecution, previousTestTriage, tagBuilder).stateReasonType(Rule35);
    }

    @Override
    public TestTriage.TestTriageBuilder processFirstTriage(TestTriage.TestTriageBuilder testTriageBuilder, TestExecution testExecution) {
        super.processFirstTriage(testTriageBuilder, testExecution);
        testTriageBuilder.rank(7);
        testTriageBuilder.stateReasonType(Rule35);

        return testTriageBuilder;
    }

}
