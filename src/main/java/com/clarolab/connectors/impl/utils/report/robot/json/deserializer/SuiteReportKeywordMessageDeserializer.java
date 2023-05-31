/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.robot.json.deserializer;

import com.clarolab.connectors.impl.utils.report.robot.json.entities.SuiteReportMessageElement;
import com.google.common.collect.Lists;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.List;

public class SuiteReportKeywordMessageDeserializer implements JsonDeserializer<List<SuiteReportMessageElement>> {

    @Override
    public List<SuiteReportMessageElement> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<SuiteReportMessageElement> messages = Lists.newArrayList();
        if (json.isJsonObject()) {
            messages.add(new Gson().fromJson(json.getAsJsonObject(), SuiteReportMessageElement.class));
        } else {
            json.getAsJsonArray().forEach(msg ->
                    messages.add(new Gson().fromJson(msg.getAsJsonObject(), SuiteReportMessageElement.class))
            );
        }
        return messages;
    }
}
