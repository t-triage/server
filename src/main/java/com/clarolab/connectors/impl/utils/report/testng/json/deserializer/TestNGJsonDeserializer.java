/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.testng.json.deserializer;

import com.clarolab.connectors.impl.utils.report.testng.json.entity.*;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.JsonUtils;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import com.google.gson.*;
import lombok.extern.java.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import java.lang.reflect.Type;
import java.util.List;

@Log
public class TestNGJsonDeserializer implements JsonDeserializer<MainTestNG> {

    public TestNGJsonDeserializer(ApplicationContextService applicationContextService){
        this.applicationContextService = applicationContextService;
    }

    private ApplicationContextService applicationContextService;

    @Override
    public MainTestNG deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = JsonUtils.getAsObject(JsonUtils.getAsObject(json), "testng-results");
        MainTestNG mainTestNG = MainTestNG.builder()
                .reporter_output(this.getReporterOutput(JsonUtils.getElement(object, "reporter-output")))
                .suite(JsonUtils.getElement(object, "suite") == null ? null : this.getTestNGSuite(JsonUtils.getAsObject(object, "suite")))
                .build();
        if (mainTestNG.getContext() == null){
            mainTestNG.setContext(this.applicationContextService);
        }
        if(JsonUtils.parseInt(object, "total") != 0){
            mainTestNG.setTotal(JsonUtils.parseInt(object, "total"));
            mainTestNG.setPassed(JsonUtils.parseInt(object, "passed"));
            mainTestNG.setFailed(JsonUtils.parseInt(object, "failed"));
            mainTestNG.setSkipped(JsonUtils.parseInt(object, "skipped"));
        }else{
            mainTestNG.setTotal(mainTestNG.getTotalFromTests());
            mainTestNG.setPassed(mainTestNG.getPassedFromTests());
            mainTestNG.setFailed(mainTestNG.getFailedFromTests());
            mainTestNG.setSkipped(mainTestNG.getSkippedFromTests());
        }
        return mainTestNG;
    }

    private TestNGSuite getTestNGSuite(JsonObject object){
        return TestNGSuite.builder()
                .started_at(JsonUtils.parseString(object, "started-at"))
                .finished_at(JsonUtils.parseString(object, "finished-at"))
                .duration_ms(JsonUtils.parseLong(object, "duration-ms"))
                .name(JsonUtils.parseString(object, "name"))
                .tests(this.getTestNGSuiteTests(JsonUtils.getElement(object, "test")))
                .build();
    }

    private List<TestNGSuiteTest> getTestNGSuiteTests(JsonElement element){
        List<TestNGSuiteTest> testNGSuiteTests = Lists.newArrayList();
        if(element.isJsonObject()){
            testNGSuiteTests.add(getTestNGSuiteTests(JsonUtils.getAsObject(element)));
        }
        if(element.isJsonArray()){
            JsonUtils.getAsArray(element).forEach(arrayElement -> testNGSuiteTests.add(getTestNGSuiteTests(JsonUtils.getAsObject(arrayElement))));
        }
        return testNGSuiteTests;
    }

    private TestNGSuiteTest getTestNGSuiteTests(JsonObject object) {
        return TestNGSuiteTest.builder()
                .started_at(JsonUtils.parseString(object, "started-at"))
                .finished_at(JsonUtils.parseString(object, "finished-at"))
                .duration_ms(JsonUtils.parseLong(object, "duration-ms"))
                .name(JsonUtils.parseString(object, "name"))
                .clazz(JsonUtils.isJsonArray(JsonUtils.getElement(object, "class")) ?
                        this.getTestNGSuiteTestClass(JsonUtils.getAsArray(JsonUtils.getElement(object, "class"))) :
                        this.getTestNGSuiteTestClass(JsonUtils.getElement(object, "class")))
                .build();
    }


    private List<TestNGSuiteTestClass> getTestNGSuiteTestClass(JsonArray array){
        List<TestNGSuiteTestClass> testNGSuiteTestClassList = Lists.newArrayList();
        array.forEach(element -> {
            testNGSuiteTestClassList.addAll(getTestNGSuiteTestClass(element));
        });
        return testNGSuiteTestClassList;
    }

    private List<TestNGSuiteTestClass> getTestNGSuiteTestClass(JsonElement element){
        List<TestNGSuiteTestClass> testNGSuiteTestClassList = Lists.newArrayList();
        JsonObject object = element.getAsJsonObject();
        testNGSuiteTestClassList.add(TestNGSuiteTestClass.builder()
                .name(JsonUtils.parseString(object, "name"))
                .test_method(this.getTestNGSuiteTestClassTestMethod(JsonUtils.getElement(object, "test-method")))
                .build());
        return testNGSuiteTestClassList;
    }

    private List<TestNGSuiteTestClassTestMethod> getTestNGSuiteTestClassTestMethod(JsonElement element){
        List<TestNGSuiteTestClassTestMethod> testMethodElements = Lists.newArrayList();
        if(element.isJsonObject())
            testMethodElements.add(getTestNGSuiteTestClassTestMethodObject(element.getAsJsonObject()));
        else{
            element.getAsJsonArray().forEach(testMethod -> testMethodElements.add(getTestNGSuiteTestClassTestMethodObject(testMethod.getAsJsonObject())));
        }

        return testMethodElements;
    }

    private TestNGSuiteTestClassTestMethod getTestNGSuiteTestClassTestMethodObject(JsonObject object){
        return TestNGSuiteTestClassTestMethod.builder()
                .started_at(JsonUtils.parseString(object, "started-at"))
                .finished_at(JsonUtils.parseString(object, "finished-at"))
                .duration_ms(JsonUtils.parseLong(object, "duration-ms"))
                .name(JsonUtils.parseString(object, "name"))
                .signature(JsonUtils.parseString(object, "signature"))
                .status(StatusType.getStatus(JsonUtils.parseString(object, "status")))
                .reporter_output(this.getReporterOutput(JsonUtils.getElement(object, "reporter-output")))
                .exception(JsonUtils.getElement(object, "exception") == null ? null : this.getTestNGSuiteTestClassTestMethodException(JsonUtils.getAsObject(object, "exception")))
                .is_config(JsonUtils.parseBoolean(object, "is-config"))
                .data_provider(JsonUtils.parseString(object, "data-provider"))
                .parameters(getTestNGSuiteTestClassTestMethodParameters(object))
                .build();
    }

    private TestNGSuiteTestClassTestMethodException getTestNGSuiteTestClassTestMethodException(JsonObject object){
        return TestNGSuiteTestClassTestMethodException.builder()
                .full_stacktrace(JsonUtils.parseString(object, "full-stacktrace"))
                .clazz(JsonUtils.parseString(object, "class"))
                .message(JsonUtils.parseString(object, "message"))
                .build();
    }

    private String getReporterOutput(JsonElement element){
        if(element == null || element.isJsonPrimitive())
            return StringUtils.getEmpty();
        JsonElement line = JsonUtils.getElement(JsonUtils.getAsObject(element), "line");
        if(line != null){
            StringBuffer str = new StringBuffer();
            line.getAsJsonArray().forEach(lineElement -> str.append(lineElement));
            //Jsoup.parse(str.toString()).text();
            return Jsoup.clean(str.toString(), "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
        }else{
            return element.getAsString();
        }
    }

    private List<TestNGSuiteTestClassTestMethodParameter> getTestNGSuiteTestClassTestMethodParameters(JsonObject object){
        List<TestNGSuiteTestClassTestMethodParameter> parameters = Lists.newArrayList();
        try {
            JsonObject parametersObj = JsonUtils.getAsObject(object, "params");
            JsonElement parametersElement = JsonUtils.getElement(parametersObj,"param");
            if(JsonUtils.isJsonArray(parametersElement)){
                JsonUtils.getAsArray(parametersElement).forEach(parameter ->
                    parameters.add(TestNGSuiteTestClassTestMethodParameter.builder().element(parameter).build())
                );
            }else{
                parameters.add(TestNGSuiteTestClassTestMethodParameter.builder().element(parametersElement).build());
            }

        }catch (IllegalStateException | NullPointerException e){
            //log.warning("[getTestNGSuiteTestClassTestMethodParameters] : JsonObject does not contain parameters");
        }
        return parameters;
    }
}
