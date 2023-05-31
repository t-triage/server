/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.builder;

import com.clarolab.connectors.impl.utils.report.cucumber.json.deserializer.CucumberJsonDeserializer;
import com.clarolab.connectors.impl.utils.report.cucumber.json.entity.MainCucumber;
import com.google.gson.GsonBuilder;
import lombok.Builder;

@Builder
public class CucumberReportBuilder extends ReportBuilder {

    @Override
    public GsonBuilder getBuilder() {
        return new GsonBuilder().registerTypeAdapter(MainCucumber.class, new CucumberJsonDeserializer());
    }
}
