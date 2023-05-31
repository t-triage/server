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
public class SuiteReportDeserializer implements JsonDeserializer<List<SuiteReport>> {

    @Override
    public List<SuiteReport> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<SuiteReport> suites = new ArrayList<>();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<List<SuiteReportTestCase>>() {}.getType(),       new SuiteTestCaseDeserializer())
                .registerTypeAdapter(new TypeToken<List<SuiteReport>>() {}.getType(),               new SuiteReportDeserializer())
                .registerTypeAdapter(SuiteReportItemMetadata.class,                                 new SuiteReportItemMetadataDeserializer())
                .registerTypeAdapter(SuiteReportArguments.class,                                    new SuiteReportArgumentsDeserializer())
                .registerTypeAdapter(new TypeToken<List<SuiteReportKeywordData>>() {}.getType(),    new SuiteReportKeywordDeserializer())
                .create();
        if (json.isJsonObject()) {
            suites.add(gson.fromJson(json.getAsJsonObject(), SuiteReport.class));
        } else {
            json.getAsJsonArray().forEach(suite ->
                    suites.add(gson.fromJson(suite.getAsJsonObject(), SuiteReport.class))
            );
        }
        return suites;
    }
}
