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

import static com.clarolab.model.helper.tag.TagHelper.*;
import static com.clarolab.model.types.StateType.*;

public interface Rule {

    StatusType statusType();

    TestTriage.TestTriageBuilder process(TestTriage.TestTriageBuilder testTriageBuilder, TestExecution testExecution, TestTriage previousTestTriage, Tag.Builder tagBuilder);

    default TestTriage.TestTriageBuilder processPermanent(TestTriage.TestTriageBuilder testTriageBuilder){
        Tag.Builder tagBuilder = new Tag.Builder().with(NEED_TRIAGE).with(FIRST_TRIAGE).with(PERMANENT.triagedName());
        return testTriageBuilder.currentState(PERMANENT).triaged(false).rank(3).tags(tagBuilder.build());
    }

    default TestTriage.TestTriageBuilder processPass(TestTriage.TestTriageBuilder testTriageBuilder){
        Tag.Builder tagBuilder = new Tag.Builder().with(AUTO_TRIAGED).with(FIRST_TRIAGE).with(PASS.triagedName());
        return testTriageBuilder.currentState(StateType.PASS).triaged(true).rank(0).testFailType(TestFailType.NO_FAIL).tags(tagBuilder.build());
    }

    default TestTriage.TestTriageBuilder processSkip(TestTriage.TestTriageBuilder testTriageBuilder){
        Tag.Builder tagBuilder = new Tag.Builder().with(FIRST_TRIAGE).with(SKIP.triagedName());
        return testTriageBuilder.currentState(SKIP).triaged(false).rank(6).tags(tagBuilder.build());
    }

    default TestTriage.TestTriageBuilder processFail(TestTriage.TestTriageBuilder testTriageBuilder){
        Tag.Builder tagBuilder = new Tag.Builder().with(NEED_TRIAGE).with(FIRST_TRIAGE).with(FAIL.triagedName());
        return testTriageBuilder.currentState(FAIL).triaged(false).rank(0).tags(tagBuilder.build());
    }

    default TestTriage.TestTriageBuilder processNewFail(TestTriage.TestTriageBuilder testTriageBuilder){
        Tag.Builder tagBuilder = new Tag.Builder().with(NEED_TRIAGE).with(FIRST_TRIAGE).with(NEWFAIL.triagedName());
        return testTriageBuilder.currentState(NEWFAIL).triaged(false).rank(0).tags(tagBuilder.build());
    }

    default TestTriage.TestTriageBuilder processFlaky(TestTriage.TestTriageBuilder testTriageBuilder){
        Tag.Builder tagBuilder = new Tag.Builder().with(AUTO_TRIAGED).with(FIRST_TRIAGE).with(FLAKY_TRIAGE).with(PASS.triagedName());
        return testTriageBuilder.currentState(StateType.PASS).triaged(true).rank(0).testFailType(TestFailType.NO_FAIL).tags(tagBuilder.build());
    }

    default TestTriage.TestTriageBuilder processNewPass(TestTriage.TestTriageBuilder testTriageBuilder){
        Tag.Builder tagBuilder = new Tag.Builder().with(TRIAGE_CANDIDATE).with(FIRST_TRIAGE).with(NEWPASS.triagedName());
        return testTriageBuilder.currentState(StateType.NEWPASS).triaged(false).rank(0).testFailType(TestFailType.NO_FAIL).tags(tagBuilder.build());
    }

    TestTriage.TestTriageBuilder processFirstTriage(TestTriage.TestTriageBuilder testTriageBuilder, TestExecution testExecution);

    // Analyzes when more than one test has the same name in the build
    TestTriage.TestTriageBuilder processNoUnique(TestTriage.TestTriageBuilder testTriageBuilder, TestExecution testExecution);

    // When the RULE_ENGINE_ON is off
    TestTriage.TestTriageBuilder processDisabledEngine(TestTriage.TestTriageBuilder testTriageBuilder, TestExecution testExecution);



}
