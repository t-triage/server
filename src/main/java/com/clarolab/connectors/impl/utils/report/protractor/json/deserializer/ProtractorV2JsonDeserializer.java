package com.clarolab.connectors.impl.utils.report.protractor.json.deserializer;

import com.clarolab.connectors.impl.utils.report.protractor.json.entity.v1.ProtractorTestCaseFailure;
import com.clarolab.connectors.impl.utils.report.protractor.json.entity.v2.MainProtractorV2;
import com.clarolab.connectors.impl.utils.report.protractor.json.entity.v2.ProtractorTestCaseStepV2;
import com.clarolab.connectors.impl.utils.report.protractor.json.entity.v2.ProtractorTestCaseV2;
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
public class ProtractorV2JsonDeserializer implements JsonDeserializer<MainProtractorV2> {

    @Override
    public MainProtractorV2 deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        MainProtractorV2 mainProtractor = MainProtractorV2.builder().build();

        JsonElement suites = JsonUtils.getElement(JsonUtils.getAsObject(json), "testsuites");
        JsonElement testCase = JsonUtils.getElement(JsonUtils.getAsObject(suites), "testsuite");
        mainProtractor.setTestCase(getTestCase(testCase));
        return mainProtractor;
    }

    private ProtractorTestCaseV2 getTestCase(JsonElement suite){
        JsonObject obj = JsonUtils.getAsObject(suite);
        return ProtractorTestCaseV2.builder()
                .name(JsonUtils.parseString(obj, "name"))
                .steps(JsonUtils.parseInt(obj, "tests"))
                .failedSteps(JsonUtils.parseInt(obj, "failures"))
                .errorsSteps(JsonUtils.parseInt(obj, "errors"))
                .skippedSteps(JsonUtils.parseInt(obj, "skipped"))
                .time(JsonUtils.parseString(obj, "time"))
                .timestamp(JsonUtils.parseString(obj, "timestamp"))
                .stepList(getTestCaseSteps(JsonUtils.getElement(obj, "testcase")))
                .build();
    }

    private List<ProtractorTestCaseStepV2> getTestCaseSteps(JsonElement steps){
        List<ProtractorTestCaseStepV2> testCaseSteps = Lists.newArrayList();
        if(JsonUtils.isJsonObject(steps)){
            testCaseSteps.add(getTestCaseStep(steps.getAsJsonObject()));
        }else{
            JsonArray stepArray = JsonUtils.getAsArray(steps);
            if (stepArray != null) {
                stepArray.forEach(element -> testCaseSteps.add(getTestCaseStep(JsonUtils.getAsObject(element))));
            }
        }
        return testCaseSteps;
    }

    private ProtractorTestCaseStepV2 getTestCaseStep(JsonElement step){
        JsonObject obj = JsonUtils.getAsObject(step);
        return ProtractorTestCaseStepV2.builder()
                .classname(JsonUtils.parseString(obj, "classname"))
                .name(JsonUtils.parseString(obj, "name"))
                .time(JsonUtils.parseString(obj, "time"))
                .failure(getTestCaseStepFailure(JsonUtils.getElement(obj, "failure")))
                .build();
    }

    private ProtractorTestCaseFailure getTestCaseStepFailure(JsonElement failure) {
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
