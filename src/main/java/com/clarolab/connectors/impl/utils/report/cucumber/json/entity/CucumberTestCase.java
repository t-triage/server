/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.cucumber.json.entity;

import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.TestExecution;
import com.clarolab.model.TestExecutionStep;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.StringUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

import java.util.List;

@Data
@Log
public class CucumberTestCase extends CucumberBase{

    private String type;
    private CucumberTestCaseCondition before;
    private CucumberTestCaseCondition after;
    private List<CucumberTestCaseStep> steps;
    //private CucumberTestCaseStatus status;

    @Builder
    private CucumberTestCase(String id, String name, String description, List<String> tags, String type, CucumberTestCaseCondition before, CucumberTestCaseCondition after, List<CucumberTestCaseStep> steps, ApplicationContextService context){
        super(id, name, description, tags);
        super.context = context;
        this.type = type;
        this.before = before;
        this.after = after;
        this.steps = steps;
    }

    public StatusType getStatus(){
        if(this.getFailedStep() != null)
            return StatusType.FAIL;
        if(steps.stream().anyMatch(step -> step.getStatus().equals(StatusType.SKIP)))
            return StatusType.SKIP;
        return StatusType.PASS;
    }

    public TestExecution getTest(CucumberSuite suite){
        String name = isBackground() ? String.format(getName(), this.getName()) : getName();
        return TestExecution.builder()
                .testCase(getTestCase(name, suite.getUri()))
                .duration(this.getDuration())
                .suiteName(suite.getId())
                .status(this.getStatus())
                .errorDetails(this.getFailure())
                .errorStackTrace(this.getFailureCause())
                .build();
    }

    private CucumberTestCaseStep getFailedStep(){
        return steps.stream().filter(step -> step.getStatus().equals(StatusType.FAIL)).findFirst().orElse(null);
    }

    private String getFailure(){
        CucumberTestCaseStep step = this.getFailedStep();
        if(step != null)
            return step.getMatchLocation() + " has a failure.";

        if(before != null && before.hasFailure()){
            return before.getError();
        }

        return StringUtils.getEmpty();
    }

    private String getFailureCause(){
        CucumberTestCaseStep step = this.getFailedStep();
        if(step != null)
            return step.getError();

        if(before != null && before.hasFailure()){
            return before.getErrorCause();
        }

        return StringUtils.getEmpty();
    }

    public String getName(){
        return !Strings.isNullOrEmpty(super.getName()) ? super.getName() : (this.type.equals("background") ? "This is a background for '%s' suite" : "Unknown name");
    }

    public boolean isBackground(){
        return this.type.equals("background");
    }

    public long getDuration(){
        return steps.stream().mapToLong(CucumberTestCaseStep::getDuration).sum();
    }

    public List<TestExecutionStep> getSteps(){
        List<TestExecutionStep> testExecutionSteps = Lists.newArrayList();
        steps.forEach(step -> testExecutionSteps.add(TestExecutionStep.builder().name(step.getName()).parameters(step.getParameters()).build()));
        return testExecutionSteps;
    }

}
