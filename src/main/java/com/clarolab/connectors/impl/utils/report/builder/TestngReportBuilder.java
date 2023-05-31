/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.builder;

import com.clarolab.connectors.impl.utils.report.testng.json.deserializer.TestNGJsonDeserializer;
import com.clarolab.connectors.impl.utils.report.testng.json.entity.MainTestNG;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.google.gson.GsonBuilder;
import lombok.Builder;
import lombok.Setter;

@Builder
public class TestngReportBuilder extends ReportBuilder {

    @Setter
    private ApplicationContextService applicationContextService;

    @Override
    public GsonBuilder getBuilder() {
        return new GsonBuilder().registerTypeAdapter(MainTestNG.class, new TestNGJsonDeserializer(applicationContextService));
    }
}
