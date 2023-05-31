package com.clarolab.connectors.impl.utils.report.builder;

import com.clarolab.connectors.impl.utils.report.jest.json.deserializer.JestJsonDeserializer;
import com.clarolab.connectors.impl.utils.report.jest.json.entity.MainJest;
import com.google.gson.GsonBuilder;
import lombok.Builder;

@Builder
public class JestReportBuilder extends ReportBuilder {

    @Override
    public GsonBuilder getBuilder() {
        return new GsonBuilder().registerTypeAdapter(MainJest.class, new JestJsonDeserializer());
    }
}