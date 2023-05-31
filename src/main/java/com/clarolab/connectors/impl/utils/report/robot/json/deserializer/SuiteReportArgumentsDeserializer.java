/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.robot.json.deserializer;

import com.clarolab.connectors.impl.utils.report.robot.json.entities.SuiteReportArguments;
import com.clarolab.util.JsonUtils;
import com.google.common.collect.Lists;
import com.google.gson.*;
import lombok.extern.java.Log;

import java.lang.reflect.Type;
import java.util.List;

@Log
public class SuiteReportArgumentsDeserializer implements JsonDeserializer<SuiteReportArguments> {
    @Override
    public SuiteReportArguments deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        //log.info("Running SuiteReportArgumentsDeserializer for element: "+json);
        List<String> arguments = Lists.newArrayList();
        if (json.isJsonPrimitive()) {
            arguments.add(json.getAsString());
        } else {
            JsonObject jsonObj = json.getAsJsonObject();
            if (JsonUtils.isJsonArray(JsonUtils.getElement(jsonObj, "arg"))) {
                JsonUtils.getAsArray(jsonObj, "arg").forEach(element ->
                        arguments.add(JsonUtils.getAsString(element))
                );
            } else {
                arguments.add(JsonUtils.parseString(jsonObj, "arg"));
            }
        }
        return SuiteReportArguments.builder().arg(arguments).build();
    }
}
