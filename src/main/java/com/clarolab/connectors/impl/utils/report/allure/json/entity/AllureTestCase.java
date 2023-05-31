/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.allure.json.entity;

import com.clarolab.model.TestExecutionStep;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.StringUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log
@Builder
public class AllureTestCase {

    //https://github.com/allure-framework/allure2/tree/master/allure-plugin-api/src/main/java/io/qameta/allure/entity

    private String uid;
    private String name;
    private String fullName;
    private String description;
    private AllureTime time;
    private String status;
    private AllureStatusDetails statusDetails;
    private List<AllureMapEntity> labels;
    private List<AllureLink> links;
    //private List<AllureStageResult> beforeStages;
    //private List<AllureStageResult> afterStages;
    private List<AllureTestCaseStep> steps;
    private List<AllureMapEntity> parameters;
    private long start;
    private long stop;
    private List<AllureMapEntity> attachments;

    public String getTestCaseName(){
        if(StringUtils.isEmpty(name))
            return getLabel("testMethod");
        else
            return getName();
    }

    public String getTestCaseSuite(){
        return getLabel("suite");
    }

    public String getTestCaseSuiteName(){
        String[] elements = getTestCaseSuite().split("\\.");
        return elements[elements.length-1];
    }

    public StatusType getStatus(){
        if(isPass())
            return StatusType.PASS;
        if(isSkip())
            return StatusType.SKIP;
        if(isFail())
            return StatusType.FAIL;
        if(isBroken())
            return StatusType.BROKEN;
        return StatusType.UNKNOWN;
    }

    public boolean isPass(){
        return status.equals("passed");
    }

    public boolean isSkip(){
        return status.equals("skipped");
    }

    public boolean isFail(){
        return status.equals("failed");
    }

    public boolean isBroken(){
        return status.equals("broken");
    }

    public long getDuration(){
        if (time != null && time.getDuration()>0)
            return time.getDuration();
        return stop-start;
    }

    public String getError(){
        if(isSkip() || isFail() || isBroken())
            return statusDetails.getMessage();
        return StringUtils.getEmpty();
    }

    public String getErrorDetail(){
        if(statusDetails == null)
            return StringUtils.getEmpty();
        if(!Strings.isNullOrEmpty(statusDetails.getTrace()))
            return getError() + StringUtils.getLineSeparator() + statusDetails.getTrace();
        if(isSkip())
            return getError();
        return StringUtils.getEmpty();
    }

    public String skipReason(){
        return getError();
    }

    public String getAttachmentsAsString(){
        if(CollectionUtils.isNotEmpty(attachments))
            return attachments.stream().filter(element -> element.getName().toLowerCase().contains("screenshot")).map(AllureMapEntity::getSource).collect(Collectors.joining(","));
        return StringUtils.getEmpty();
    }

    public List<TestExecutionStep> getTestCaseSteps(){
        List<TestExecutionStep> steps = Lists.newArrayList();
        this.steps.forEach(step -> steps.add(TestExecutionStep.builder().name(step.getName()).stepOrder(steps.indexOf(step)).parameters(step.getParameters()).output(step.getOutput()).build()));
        return steps;
    }

    private String getLabel(String name){
        Optional<AllureMapEntity> first = labels
                .stream()
                .filter(parameter -> parameter.getName().equals(name))
                .findFirst();
        return first.isPresent() ? first.get().getValue() : "";
    }

    private String getName() {
        if(CollectionUtils.isNotEmpty(parameters)) {
            StringBuilder params = new StringBuilder();
            for(AllureMapEntity parameter: parameters) {
                params.append(String.format("%s:%s, ", parameter.getName(), parameter.getValue()));
            }
            //Removes last chars ", "
            params.deleteCharAt(params.length()-1);
            params.deleteCharAt(params.length()-1);
            return String.format("%s(%s)", name, params.toString());
        }
        return name;
    }

}
