package com.clarolab.connectors.impl.utils.report.builder;

import com.clarolab.connectors.impl.utils.report.cypress.json.deserializer.CypressJsonDeserializer;
import com.clarolab.connectors.impl.utils.report.cypress.json.entity.MainCypress;
import com.google.gson.GsonBuilder;
import lombok.Builder;

@Builder
public class CypressReportBuilder extends ReportBuilder {

    @Override
    public GsonBuilder getBuilder() {
        return new GsonBuilder().registerTypeAdapter(MainCypress.class, new CypressJsonDeserializer());
    }
}
