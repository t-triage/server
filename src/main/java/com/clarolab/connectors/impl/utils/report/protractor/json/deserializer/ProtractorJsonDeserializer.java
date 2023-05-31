package com.clarolab.connectors.impl.utils.report.protractor.json.deserializer;

import com.clarolab.connectors.impl.utils.report.protractor.json.entity.v1.MainProtractor;
import com.clarolab.connectors.impl.utils.report.protractor.json.entity.v1.ProtractorSuite;
import com.clarolab.connectors.impl.utils.report.protractor.json.entity.v1.ProtractorTestCase;
import com.clarolab.connectors.impl.utils.report.protractor.json.entity.v1.ProtractorTestCaseFailure;
import com.clarolab.util.JsonUtils;
import com.google.common.collect.Lists;
import com.google.gson.*;
import lombok.Builder;
import lombok.extern.java.Log;

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Level;

@Log
@Builder
public class ProtractorJsonDeserializer implements JsonDeserializer<MainProtractor> {

    @Override
    public MainProtractor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        MainProtractor mainProtractor = MainProtractor.builder().build();

        JsonElement suites = JsonUtils.getElement(JsonUtils.getAsObject(json), "testsuites");
        if(JsonUtils.isJsonObject(suites)){
            JsonElement suite = JsonUtils.getElement(JsonUtils.getAsObject(suites), "testsuite");
            mainProtractor.addSuite(getSuite(suite));
        }else{
            JsonUtils.getAsArray(suites).forEach(element -> mainProtractor.addSuite(getSuite(JsonUtils.getAsObject(element))));
        }

        return mainProtractor;
    }

    private ProtractorSuite getSuite(JsonElement suite){
        JsonObject obj = JsonUtils.getAsObject(suite);
        return ProtractorSuite.builder()
                .name(JsonUtils.parseString(obj, "name"))
                .tests(JsonUtils.parseInt(obj, "tests"))
                .failures(JsonUtils.parseInt(obj, "failures"))
                .errors(JsonUtils.parseInt(obj, "errors"))
                .skipped(JsonUtils.parseInt(obj, "skipped"))
                .time(JsonUtils.parseString(obj, "time"))
                .timestamp(JsonUtils.parseString(obj, "timestamp"))
                .testCases(getTestCases(JsonUtils.getElement(obj, "testcase")))
                .build();
    }

    private List<ProtractorTestCase> getTestCases(JsonElement tests){
        List<ProtractorTestCase> testCases = Lists.newArrayList();
        if(JsonUtils.isJsonObject(tests)){
            testCases.add(getTestCase(tests.getAsJsonObject()));
        }else{
            JsonUtils.getAsArray(tests).forEach(element -> testCases.add(getTestCase(JsonUtils.getAsObject(element))));
        }
        return testCases;
    }

    private ProtractorTestCase getTestCase(JsonElement test){
        JsonObject obj = JsonUtils.getAsObject(test);
        return ProtractorTestCase.builder()
                .classname(JsonUtils.parseString(obj, "classname"))
                .name(JsonUtils.parseString(obj, "name"))
                .time(JsonUtils.parseString(obj, "time"))
                .failure(getTestCaseFailure(JsonUtils.getElement(obj, "failure")))
                .build();
    }

    private ProtractorTestCaseFailure getTestCaseFailure(JsonElement failure) {
        JsonObject obj = null;
        try {
            if (failure == null || failure.isJsonObject()) {
                obj = JsonUtils.getAsObject(failure);
            } else if (failure.isJsonArray()) {
                JsonArray array = JsonUtils.getAsArray(failure);
                if (array.size() > 0) {
                    obj = JsonUtils.getAsObject(array.get(0));
                }
            }
            
        } catch (JsonSyntaxException jex) {
            log.log(Level.SEVERE, "Could not process Protractor Failure: " + failure.getAsString(), jex);
        }
       return ProtractorTestCaseFailure.builder()
                    .type(JsonUtils.parseString(obj, "type"))
                    .message(JsonUtils.parseString(obj, "message"))
                    .content(JsonUtils.parseString(obj, "content"))
                    .build();
    }
}
