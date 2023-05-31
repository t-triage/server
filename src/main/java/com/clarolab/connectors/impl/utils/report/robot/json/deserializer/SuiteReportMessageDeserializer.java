/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.robot.json.deserializer;

import com.clarolab.connectors.impl.utils.report.robot.json.entities.SuiteReportMessage;
import com.clarolab.connectors.impl.utils.report.robot.json.entities.SuiteReportMessageElement;
import com.clarolab.connectors.impl.utils.report.robot.json.entities.types.SuiteMessageType;
import com.clarolab.util.JsonUtils;
import com.clarolab.util.LogicalCondition;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SuiteReportMessageDeserializer implements JsonDeserializer<SuiteReportMessage> {

    @Override
    public SuiteReportMessage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<SuiteReportMessageElement> messages = new ArrayList<>();
        if (LogicalCondition.NOT(json.isJsonPrimitive())) {
            JsonObject jsonObj = JsonUtils.getAsObject(json);
            if (JsonUtils.getElement(jsonObj, "msg").isJsonObject()) {
                messages.add(this.getMessage(JsonUtils.getElement(jsonObj, "msg")));
                SuiteReportMessage.builder().msg(messages).build();
            } else {
                JsonUtils.getAsArray(JsonUtils.getElement(jsonObj, "msg")).forEach(element ->
                        messages.add(this.getMessage(element))
                );
            }
        }

        return SuiteReportMessage.builder().msg(messages).build();
    }

    private SuiteReportMessageElement getMessage(JsonElement json) {
        return SuiteReportMessageElement.builder()
                .content(JsonUtils.parseString(JsonUtils.getAsObject(json), "content"))
                .level(SuiteMessageType.getStatus(JsonUtils.parseString(JsonUtils.getAsObject(json), "level")))
                .timestamp(JsonUtils.parseString(JsonUtils.getAsObject(json), "timestamp"))
                .build();
    }

}
