/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.builder;

import com.clarolab.connectors.impl.utils.report.protractor.json.deserializer.ProtractorV2JsonDeserializer;
import com.clarolab.connectors.impl.utils.report.protractor.json.entity.v2.MainProtractorV2;
import com.google.gson.GsonBuilder;
import lombok.Builder;

@Builder
public class ProtractorV2ReportBuilder extends ReportBuilder {

    @Override
    public GsonBuilder getBuilder() {
        return new GsonBuilder().registerTypeAdapter(MainProtractorV2.class, ProtractorV2JsonDeserializer.builder().build());
    }
}
