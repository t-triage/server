/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.builder;

import com.clarolab.connectors.impl.utils.report.junit.json.deserializer.JunitJsonDeserializer;
import com.clarolab.connectors.impl.utils.report.junit.json.entity.MainJunit;
import com.google.gson.GsonBuilder;
import lombok.Builder;

@Builder
public class JunitReportBuilder extends ReportBuilder {

    @Override
    public GsonBuilder getBuilder() {
        return new GsonBuilder().registerTypeAdapter(MainJunit.class, new JunitJsonDeserializer());
    }
}
