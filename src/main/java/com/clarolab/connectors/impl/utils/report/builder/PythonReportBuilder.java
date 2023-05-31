package com.clarolab.connectors.impl.utils.report.builder;

import com.clarolab.connectors.impl.utils.report.python.json.deserializer.PythonJsonDeserializer;
import com.clarolab.connectors.impl.utils.report.python.json.entity.MainPython;
import com.google.gson.GsonBuilder;
import lombok.Builder;

@Builder
public class PythonReportBuilder extends ReportBuilder {

    @Override
    public GsonBuilder getBuilder() {
        return new GsonBuilder().registerTypeAdapter(MainPython.class, new PythonJsonDeserializer());
    }
}