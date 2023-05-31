/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.builder;

import com.clarolab.connectors.impl.utils.report.protractor.json.deserializer.ProtractorJsonDeserializer;
import com.clarolab.connectors.impl.utils.report.protractor.json.entity.v1.MainProtractor;
import com.google.gson.GsonBuilder;
import lombok.Builder;

@Builder
public class ProtractorReportBuilder extends ReportBuilder {

    @Override
    public GsonBuilder getBuilder() {
        return new GsonBuilder().registerTypeAdapter(MainProtractor.class, ProtractorJsonDeserializer.builder().build());
    }
}
