/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.rules;

import com.clarolab.functional.test.util.TestConfig;
import com.clarolab.model.TestTriage;
import com.clarolab.model.types.ErrorType;
import com.clarolab.model.types.StateType;
import org.junit.Assert;
import org.junit.Before;

import static com.clarolab.model.types.DeducedReasonType.Rule38;
import static com.clarolab.model.types.DeducedReasonType.Rule39;

public class DisabledEngineRuleFunctionalTest extends AbstractTriageRuleFunctionalTest {

    @Before
    public void clearProvider() {
        super.clearProvider();

        staticRuleDispatcher.setEngineEnabled(false);
    }

    protected void assertRules(TestConfig test, TestTriage lastTriage, ErrorType errorType) {
        if (lastTriage.isPassed()) {
            Assert.assertEquals(StateType.PASS, lastTriage.getCurrentState());
            Assert.assertEquals(Rule38, lastTriage.getStateReasonType());
        } else {
            Assert.assertEquals(StateType.FAIL, lastTriage.getCurrentState());
            Assert.assertEquals(Rule39, lastTriage.getStateReasonType());
        }
    }
}
