/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.junit.json.deserializer;

import com.clarolab.connectors.impl.utils.report.junit.json.entity.*;
import com.clarolab.util.DateUtils;
import com.clarolab.util.JsonUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.*;
import lombok.extern.java.Log;

import java.lang.reflect.Type;
import java.util.List;

@Log
public class JunitJsonDeserializer implements JsonDeserializer<MainJunit> {

    String version;

    @Override
    public MainJunit deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = JsonUtils.getObject(json, "testsuite");
        this.version = JsonUtils.parseString(object, "version");
        return MainJunit.builder()
                .name(JsonUtils.parseString(object, "name"))
                .group(JsonUtils.parseString(object, "group"))
                .time(DateUtils.convertDate(JsonUtils.parseString(object, "time")))
                .version(this.version)
                .total(JsonUtils.parseInt(object, "tests"))
                .skipped(JsonUtils.parseInt(object, "skipped"))
                .errors(JsonUtils.parseInt(object, "errors"))
                .failures(JsonUtils.parseInt(object, "failures"))
                .testCase(getTestCases(JsonUtils.getElement(object,"testcase")))
                .build();
    }

    private List<JunitTestCase> getTestCases(JsonElement element){
        if(JsonUtils.isJsonArray(element))
            return getTestCases(JsonUtils.getAsArray(element));
        else {
            List<JunitTestCase> list = Lists.newArrayList();
            list.add(getTestCase(JsonUtils.getAsObject(element)));
            return list;
        }
    }

    private List<JunitTestCase> getTestCases(JsonArray jsonArray){
        List<JunitTestCase> testCases = Lists.newArrayList();
        jsonArray.forEach(testCase -> testCases.add(getTestCase(testCase.getAsJsonObject())));
        return testCases;
    }

    private JunitTestCase getTestCase(JsonObject object){
        return JunitTestCase.builder()
                    .name(JsonUtils.parseString(object, "name"))
                    .className(JsonUtils.parseString(object, "classname"))
                    .time(DateUtils.convertDate(JsonUtils.parseString(object, "time")))
                    .group(JsonUtils.parseString(object, "group"))
                    .error(this.getError(object))
                    .systemOut(getSystemData(JsonUtils.getElement(object, "system-out")))
                    .systemErr(getSystemData(JsonUtils.getElement(object, "system-err")))
                    .build();
    }

    private JunitTestCaseError getError(JsonObject object){
        String[] errors = {"failure", "rerunFailure", "flakyFailure", "skipped", "error", "rerunError", "flakyError"};
        for(String errorType: errors){
            if(object!=null && object.get(errorType) != null){
                switch (errorType){
                    case "failure":
                    case "error":
                        return this.getTestCaseSimpleFailure(object.get(errorType));
                    case "rerunFailure":
                    case "flakyFailure":
                    case "rerunError":
                    case "flakyError":
                        return this.getTestCaseComplexFailure(object.get(errorType));
                    case "skipped":
                        return this.getTestCaseSkipped(object.get(errorType));
                }
            }
        }
        return null;
    }

    private JunitTestCaseError getTestCaseSimpleFailure(JsonElement element){
            return JunitTestCaseSimpleError.builder()
                    .type(JsonUtils.parseString(JsonUtils.getAsObject(element), "type")+" - "+JsonUtils.parseString(JsonUtils.getAsObject(element), "message"))
                    .text(JsonUtils.parseString(JsonUtils.getAsObject(element), "content"))
                    .build();
    }

    private JunitTestCaseError getTestCaseComplexFailure(JsonElement element){
        if(Strings.isNullOrEmpty(this.version))
            return this.getTestCaseSimpleFailure(element);
        else
            return JunitTestCaseComplexError.builder()
                    .text(JsonUtils.parseString(JsonUtils.getAsObject(element), "content"))
                    .type(JsonUtils.parseString(JsonUtils.getAsObject(element), "type")+" - "+JsonUtils.parseString(JsonUtils.getAsObject(element), "message"))
                    .stackTrace(JsonUtils.parseString(JsonUtils.getAsObject(element), "stackTrace"))
                    .systemOut(JsonUtils.parseString(JsonUtils.getAsObject(element), "system-out"))
                    .systemErr(JsonUtils.parseString(JsonUtils.getAsObject(element), "system-err"))
                    .build();
    }

    private JunitTestCaseError getTestCaseSkipped(JsonElement element){
        String reason;
        try{
            reason = JsonUtils.parseString(JsonUtils.getAsObject(element), "message");
        }catch (IllegalStateException e){
            reason = "Reason could not be determinated.\n" +
                     "Possible options are:\n" +
                     "1) This is an initializer test method that has failed previously.\n" +
                     "2) This test has an initializer configured with @Before annotation, that is currently failing.";
        }
        return JunitTestCaseSkipped.builder().message(reason).build();
    }

    private String getSystemData(JsonElement e){
        StringBuffer str = new StringBuffer();
        if(e == null)
            return str.toString();
        if(e.isJsonArray()){
            e.getAsJsonArray().forEach(element -> {
                if(element.isJsonArray()){
                    str.append(getSystemData(element));
                }else if(element.isJsonPrimitive()){
                    str.append(JsonUtils.getAsString(element));
                }
                str.append("\n");
            });
        }
        return str.toString();
    }

}
