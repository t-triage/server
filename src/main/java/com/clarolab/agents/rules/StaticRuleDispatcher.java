/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.agents.rules;

import com.clarolab.agents.rules.statics.Rule;
import com.clarolab.agents.rules.statics.UndefinedRule;
import com.clarolab.model.*;
import com.clarolab.model.helper.tag.TagHelper;
import com.clarolab.model.types.*;
import com.clarolab.service.PropertyService;
import com.clarolab.service.TestTriageService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.clarolab.model.helper.tag.TagHelper.getBaseTag;
import static com.clarolab.util.Constants.DEFAULT_RULE_ENGINE_ON;
import static com.clarolab.util.Constants.RULE_ENGINE_ON;

@Log
@Component
public class StaticRuleDispatcher {

    private Map<StatusType, Rule> currentNodeMap;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private PropertyService propertyService;

    private boolean engineEnabled = true;
    private boolean engineEnabledSet = false;

    @Autowired
    public void setRuleProcessorMap(List<Rule> ruleMap) {
        this.currentNodeMap = ruleMap.stream().collect(Collectors.toMap(Rule::statusType, Function.identity()));
    }

    public TestTriage process(TestExecution testExecution, Build build, TriageSpec spec) {
        Optional<TestTriage> previousTriage = testTriageService.findPreviousTriage(build.getExecutor(), testExecution.getTestCase(), build.getNumber());

        return process(testExecution, previousTriage, build, spec);
    }

    public TestTriage process(TestExecution testExecution, Optional<TestTriage> previousTriage, Build build, TriageSpec spec) {
        Rule rule = currentNodeMap.getOrDefault(testExecution.getStatus(), new UndefinedRule());

        TestTriage previousTriageOrNull = previousTriage.isPresent() ? previousTriage.get() : null;

        ApplicationFailType defaultApplication = ApplicationFailType.UNDEFINED;
        TestFailType defaultFailType = TestFailType.UNDEFINED;
        DeducedStateType currentState = DeducedStateType.builder()
                .currentState(previousTriage.isPresent() ? previousTriage.get().getCurrentState() : StateType.FAIL)
                .reasonType(DeducedReasonType.DEFAULT)
                .build();
        User defaultTriager = spec.getTriager();
        TestCase testCase = testExecution.getTestCase();

        if (previousTriage.isPresent()) {
            // keeping default assignments from previous triage

            // Setting status for product bug
            if (testCase.getIssueTicket() != null && !testCase.getIssueTicket().shouldPropagateStatus()) {
                defaultApplication = previousTriageOrNull.getApplicationFailType();
            }

            // Setting status for test bug
            if (testCase.getAutomatedTestIssue() != null && testCase.getAutomatedTestIssue().shouldPropagateStatus()) {
                defaultFailType = previousTriageOrNull.getTestFailType();
            }

            // Setting assignee
            if (previousTriageOrNull.getTriager() != null) {
                defaultTriager = previousTriageOrNull.getTriager();
            }
        }


        TestTriage.TestTriageBuilder newTriage = TestTriage.builder()
                .buildParent(build)
                .testExecution(testExecution)
                .executorName(build.getExecutorName())
                .currentState(currentState.getCurrentState())
                .stateReasonType(currentState.getReasonType())
                .previousErrorType(ErrorType.UNDEFINED)
                .applicationFailType(defaultApplication)
                .testFailType(defaultFailType)
                .file(previousTriage.map(TestTriage::getFile).orElse(null))
                .executionDate(build.getExecutedDate())
                .snooze(previousTriage.map(TestTriage::getSnooze).orElse(0L))
                .tags(TagHelper.empty())
                .triager(defaultTriager)
                .rank(0)
                .triaged(false)
                .previousTriage(previousTriageOrNull)
                .enabled(true)
                .note(null);


        try {
            if (!isEngineEnabled()) {
                // When the RULE_ENGINE_ON is off
                newTriage = rule.processDisabledEngine(newTriage, testExecution);
            } else if (testCase.isDataProvider()) {
                newTriage = rule.processNoUnique(newTriage, testExecution);
            } else if (previousTriage.isPresent()) {
                TestTriage triage = previousTriage.get();
                newTriage = rule.process(newTriage, testExecution, triage, getBaseTag(triage));
            } else {
                newTriage = rule.processFirstTriage(newTriage, testExecution);
            }
        } catch (Exception ex) {
            throw new RuntimeException(String.format("Error processing rule for testExecution %d", testExecution.getId()), ex);
        }

        return newTriage.build();
    }

    private void setEngineEnabledProp() {
        engineEnabled = propertyService.valueOf(RULE_ENGINE_ON, DEFAULT_RULE_ENGINE_ON);
        engineEnabledSet = true;
    }

    private boolean isEngineEnabled() {
        if (!engineEnabledSet) {
            setEngineEnabledProp();
        }
        return engineEnabled;
    }

    public void setEngineEnabled(boolean isEnabled) {
        engineEnabled = isEnabled;
        engineEnabledSet = true;
    }


}
