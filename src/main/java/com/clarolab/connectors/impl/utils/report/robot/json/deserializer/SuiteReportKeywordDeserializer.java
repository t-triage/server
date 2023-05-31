/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.robot.json.deserializer;

import com.clarolab.connectors.impl.utils.report.robot.json.entities.*;
import com.clarolab.connectors.impl.utils.report.robot.json.entities.types.SuiteKeywordType;
import com.clarolab.util.JsonUtils;
import com.google.common.collect.Lists;
import com.google.gson.*;
import lombok.extern.java.Log;

import java.lang.reflect.Type;
import java.util.List;

@Log
public class SuiteReportKeywordDeserializer implements JsonDeserializer<List<SuiteReportKeywordData>> {

    @Override
    public List<SuiteReportKeywordData> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return this.getKeyword(json);
    }

    private List<SuiteReportKeywordData> getKeyword(JsonElement element) {
        List<SuiteReportKeywordData> out = Lists.newArrayList();
        if (element != null) {
            if (element.isJsonObject())
                out.add(this.getKeywordObj(element.getAsJsonObject()));
            else
                out.addAll(this.getKeywordList(element.getAsJsonArray()));
        }
        return out;
    }

    private List<SuiteReportKeywordData> getKeywordList(JsonArray array) {
        List<SuiteReportKeywordData> out = Lists.newArrayList();
        array.getAsJsonArray().forEach(element ->
                out.addAll(this.getKeyword(element))
        );
        return out;
    }

    private SuiteReportKeywordData getKeywordObj(JsonObject object) {
        if (object.isJsonNull())
            return SuiteReportKeywordData.builder().build();
        else {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(SuiteReportItemMetadata.class, new SuiteReportItemMetadataDeserializer())
                    .registerTypeAdapter(SuiteReportArguments.class, new SuiteReportArgumentsDeserializer())
                    //.registerTypeAdapter(SuiteReportKeyword.class, new SuiteReportKeywordDeserializer())
                    .create();

            SuiteStatusReport status = gson.fromJson(JsonUtils.getElement(object, "status"), SuiteStatusReport.class);

            SuiteReportArguments arguments = gson.fromJson(JsonUtils.getElement(object, "arguments"), SuiteReportArguments.class);

            List<SuiteReportMessageElement> msgs = Lists.newArrayList();
            if (JsonUtils.getElement(object, "msg") == null)
                msgs.add(SuiteReportMessageElement.builder().build());
            else {
                if (JsonUtils.getElement(object, "msg").isJsonObject()) {
                    msgs.add(new Gson().fromJson(JsonUtils.getObject(object, "msg"), SuiteReportMessageElement.class));
                } else {
                    JsonUtils.getAsArray(object, "msg").forEach(msg ->
                            msgs.add(new Gson().fromJson(msg.getAsJsonObject(), SuiteReportMessageElement.class))
                    );
                }
            }

            List<SuiteReportKeywordData> keywords = this.getKeyword(JsonUtils.getElement(object, "kw"));

            SuiteReportKeywordData data = SuiteReportKeywordData.builder()
                    .name(JsonUtils.parseString(object, "name"))
                    .doc(JsonUtils.parseString(object, "doc"))
                    .type(SuiteKeywordType.getType(JsonUtils.parseString(object, "type")))
                    .arguments(arguments)
                    .status(status)
                    .msg(msgs)
                    .kw(keywords)
                    .build();

            return data;
        }
    }
}
