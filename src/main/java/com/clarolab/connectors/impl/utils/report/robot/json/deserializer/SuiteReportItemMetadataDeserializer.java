/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.robot.json.deserializer;

import com.clarolab.connectors.impl.utils.report.robot.json.entities.SuiteReportItemMetadata;
import com.clarolab.connectors.impl.utils.report.robot.json.entities.SuiteReportItemMetadataElements;
import com.clarolab.util.JsonUtils;
import com.google.common.collect.Lists;
import com.google.gson.*;

import java.lang.reflect.Type;

public class SuiteReportItemMetadataDeserializer implements JsonDeserializer<SuiteReportItemMetadata> {
    @Override
    public SuiteReportItemMetadata deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            return SuiteReportItemMetadata.builder().item(Lists.newArrayList()).build();
        } else {
            JsonObject jsonObj = json.getAsJsonObject();
            SuiteReportItemMetadata metadata = SuiteReportItemMetadata.builder().item(Lists.newArrayList()).build();
            JsonUtils.getAsArray(jsonObj, "item").forEach(element ->
                    metadata.getItem()
                            .add(SuiteReportItemMetadataElements.builder()
                                    .name(JsonUtils.parseString(jsonObj, "name"))
                                    .content(JsonUtils.parseString(jsonObj, "content"))
                                    .build())
            );
            return metadata;
        }
    }
}
