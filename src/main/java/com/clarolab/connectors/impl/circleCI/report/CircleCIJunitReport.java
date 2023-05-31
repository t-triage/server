/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.circleCI.report;

import com.clarolab.connectors.impl.circleCI.model.entities.CircleCIProjectBuildArtifactElementEntity;
import com.clarolab.connectors.impl.utils.report.junit.JUnitReport;
import com.clarolab.connectors.services.exceptions.ConnectorServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.http.client.HttpClient;
import com.clarolab.model.Report;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

@Log
@Data
@Builder
public class CircleCIJunitReport implements CircleCIReportType {
    private ApplicationContextService context;

    @Override
    public Report getReport(CircleCIProjectBuildArtifactElementEntity circleCIProjectBuildArtifactElementEntityWithReport, String applicationTestingEnvironmentVersion, HttpClient httpClient) throws ConnectorServiceException {
        String json;
        try {
            json = httpClient.getJson(circleCIProjectBuildArtifactElementEntityWithReport.baseUrl(),
                    String.format(circleCIProjectBuildArtifactElementEntityWithReport.getFileRequest(),
                            httpClient.getUserName()));
        }catch(Exception e){
            throw new ConnectorServiceException(e, "[getReport] : There was an error trying to get content report from artifact.");
        }

        return JUnitReport.builder().context(context).applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion).build().createReport(json, "This is a JUnit report for CircleCI");
    }

}
