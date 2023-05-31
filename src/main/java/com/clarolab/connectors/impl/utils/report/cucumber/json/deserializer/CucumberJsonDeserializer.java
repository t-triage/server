/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.cucumber.json.deserializer;

import com.clarolab.connectors.impl.utils.report.cucumber.json.entity.*;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.JsonUtils;
import com.google.common.collect.Lists;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.List;

public class CucumberJsonDeserializer implements JsonDeserializer<MainCucumber> {

    @Override
    public MainCucumber deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        MainCucumber mainCucumber = MainCucumber.builder().build();
        json.getAsJsonArray().forEach( element -> mainCucumber.addSuite(this.getCucumberSuite(element.getAsJsonObject())));
        return mainCucumber;
    }

    private CucumberSuite getCucumberSuite(JsonObject object){
        CucumberSuite cucumberSuite = CucumberSuite.builder()
                .id(JsonUtils.parseString(object, "id"))
                .name(JsonUtils.parseString(object, "name"))
                .description(JsonUtils.parseString(object, "description"))
                .uri(JsonUtils.parseString(object, "uri"))
                .tags(this.getCucumberSuiteTags(JsonUtils.getAsArray(object, "tags")))
                .testCases(this.getCucumberTestCases(JsonUtils.getAsArray(object, "elements")))
                .build();
        return cucumberSuite;
    }

    private List<String> getCucumberSuiteTags(JsonElement element){
        List<String> tags = Lists.newArrayList();
        if(element != null)
            element.getAsJsonArray().forEach(tag -> tags.add(JsonUtils.parseString(tag.getAsJsonObject(), "name")));
        return tags;
    }

    private List<CucumberTestCase> getCucumberTestCases(JsonArray elements){
        List<CucumberTestCase> tests = Lists.newArrayList();
        elements.forEach(test -> {
            JsonObject object = test.getAsJsonObject();
            CucumberTestCase testCase = CucumberTestCase.builder()
                    .id(JsonUtils.parseString(object, "id"))
                    .name(JsonUtils.parseString(object, "name"))
                    .description(JsonUtils.parseString(object, "description"))
                    .type(JsonUtils.parseString(object, "type"))
                    .tags(this.getCucumberSuiteTags(JsonUtils.getElement(object, "tags")))
                    .before(this.getCondition(JsonUtils.getElement(object, "before")))
                    .steps(this.getSteps(JsonUtils.getAsArray(object, "steps")))
                    .after(this.getCondition(JsonUtils.getElement(object, "after")))
                    .build();
            tests.add(testCase);
        });
        return tests;
    }

    private CucumberTestCaseCondition getCondition(JsonElement element){
        if(element != null) {
            JsonObject object = element.getAsJsonArray().get(0).getAsJsonObject();
            return CucumberTestCaseCondition.builder()
                    .name(JsonUtils.parseString(JsonUtils.getObject(object, "match"), "location"))
                    .duration(JsonUtils.parseLong(JsonUtils.getObject(object, "result"), "duration"))
                    .status(this.getStatus(JsonUtils.getObject(object, "result")))
                    .build();
        }
        return null;
    }

    private List<CucumberTestCaseStep> getSteps(JsonArray steps){
        List<CucumberTestCaseStep> stepList = Lists.newArrayList();
        steps.forEach(step -> {
            JsonObject object = step.getAsJsonObject();
            CucumberTestCaseStep cucumberTestCaseStep = CucumberTestCaseStep.builder()
                    .name(JsonUtils.parseString(object, "name"))
                    .keyword(JsonUtils.parseString(object, "keyword"))
                    .matchLocation(JsonUtils.parseString(JsonUtils.getObject(object, "match"), "location"))
                    .duration(JsonUtils.parseLong(JsonUtils.getObject(object, "result"), "duration"))
                    .status(this.getStatus(JsonUtils.getObject(object, "result")))
                    .build();

            JsonArray arrayParameter = JsonUtils.getAsArray(JsonUtils.getObject(object, "match"),"arguments" );
            if(arrayParameter != null)
                cucumberTestCaseStep.setParameters(getTestCaseStepParameters(arrayParameter));

            else{
                arrayParameter = JsonUtils.getAsArray(object, "rows");
                if(arrayParameter != null)
                    cucumberTestCaseStep.setParameters(getTestCaseStepParametersFromRow(arrayParameter));
            }

            stepList.add(cucumberTestCaseStep);
        });
        return stepList;
    }

    private CucumberTestCaseStatus getStatus(JsonObject object){
        return CucumberTestCaseStatus.builder()
                .status(StatusType.getStatus(JsonUtils.parseString(object, "status")))
                .error(JsonUtils.parseString(object, "error_message"))
                .build();
    }

    private List<CucumberTestCaseStepParameters> getTestCaseStepParameters(JsonArray parameters){
        List<CucumberTestCaseStepParameters> p = Lists.newArrayList();
        parameters.forEach(jsonElement -> p.add(CucumberTestCaseStepParameters.builder().value(JsonUtils.parseString(JsonUtils.getAsObject(jsonElement), "val")).build()));
        return p;
    }

    private List<CucumberTestCaseStepParameters> getTestCaseStepParametersFromRow(JsonArray parameters){
        List<CucumberTestCaseStepParameters> p = Lists.newArrayList();
        //first one can be or no the table header, there is no way to know that.
        for(JsonElement cell: parameters) {
            int position = 0;
            for(JsonElement element: JsonUtils.getAsArray(JsonUtils.getAsObject(cell), "cells")){
                try{
                    p.get(position).setValue(p.get(position).getValue()+","+JsonUtils.getAsString(element));
                }catch(IndexOutOfBoundsException e){
                    p.add(CucumberTestCaseStepParameters.builder().value(JsonUtils.getAsString(element)).build());
                }
                position++;
            }
        }
        return p;
    }

}
