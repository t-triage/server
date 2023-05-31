/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.builder;

import com.clarolab.connectors.impl.utils.report.robot.json.deserializer.*;
import com.clarolab.connectors.impl.utils.report.robot.json.entities.*;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Builder;

import java.util.List;

@Builder
public class RobotReportBuilder extends ReportBuilder {

    @Override
    public GsonBuilder getBuilder() {
        return new GsonBuilder()
                .registerTypeAdapter(new TypeToken<List<SuiteReportTestCase>>() {}.getType(),       new SuiteTestCaseDeserializer())
                .registerTypeAdapter(new TypeToken<List<SuiteReport>>() {}.getType(),               new SuiteReportDeserializer())
                .registerTypeAdapter(SuiteReportItemMetadata.class,                                 new SuiteReportItemMetadataDeserializer())
                .registerTypeAdapter(SuiteReportArguments.class,                                    new SuiteReportArgumentsDeserializer())
                .registerTypeAdapter(new TypeToken<List<SuiteReportKeywordData>>() {}.getType(),    new SuiteReportKeywordDeserializer())
                .registerTypeAdapter(SuiteReportMessage.class,                                      new SuiteReportMessageDeserializer());
    }
}
