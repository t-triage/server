/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.robot.json.deserializer;

import com.clarolab.connectors.impl.utils.report.robot.json.entities.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.extern.java.Log;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Log
public class SuiteTestCaseDeserializer implements JsonDeserializer<List<SuiteReportTestCase>> {

    @Override
    public List<SuiteReportTestCase> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<SuiteReportTestCase> tests = new ArrayList<>();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<List<SuiteReportTestCase>>() {}.getType(),       new SuiteTestCaseDeserializer())
                .registerTypeAdapter(new TypeToken<List<SuiteReport>>() {}.getType(),               new SuiteTestCaseDeserializer())
                .registerTypeAdapter(SuiteReportItemMetadata.class,                                 new SuiteReportItemMetadataDeserializer())
                .registerTypeAdapter(SuiteReportArguments.class,                                    new SuiteReportArgumentsDeserializer())
                .registerTypeAdapter(new TypeToken<List<SuiteReportKeywordData>>() {}.getType(),    new SuiteReportKeywordDeserializer())
                .create();
        if (json.isJsonObject()) {
            tests.add(gson.fromJson(json.getAsJsonObject(), SuiteReportTestCase.class));
        } else {
            json.getAsJsonArray().forEach(test ->
                    tests.add(gson.fromJson(test.getAsJsonObject(), SuiteReportTestCase.class))
            );
        }
        return tests;
    }
}
