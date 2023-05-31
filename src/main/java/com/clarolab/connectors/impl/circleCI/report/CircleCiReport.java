/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.circleCI.report;

import com.clarolab.connectors.impl.circleCI.model.entities.CircleCIProjectBuildArtifactElementEntity;
import com.clarolab.connectors.services.exceptions.ConnectorServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.http.client.HttpClient;
import com.clarolab.model.Report;
import com.clarolab.util.JsonUtils;
import com.clarolab.util.StringUtils;
import lombok.Builder;
import lombok.extern.java.Log;

import java.util.logging.Level;

@Log
@Builder
public class CircleCiReport {
    private ApplicationContextService context;

    public Report getReport(CircleCIProjectBuildArtifactElementEntity circleCIProjectBuildArtifactElementEntityWithReport, CircleCIProjectBuildArtifactElementEntity circleCIProjectBuildArtifactElementEntityWithApplicationTestingEnvironmentVersion, HttpClient httpClient) throws ConnectorServiceException {
        String version = getApplicationTestingEnvironmentVersionFromArtifact(circleCIProjectBuildArtifactElementEntityWithApplicationTestingEnvironmentVersion, httpClient);
        if(circleCIProjectBuildArtifactElementEntityWithReport.itContainsTestNGFile())
            return CircleCITestNGReport.builder().context(context).build().getReport(circleCIProjectBuildArtifactElementEntityWithReport, version, httpClient);
        if(circleCIProjectBuildArtifactElementEntityWithReport.itContainsJunitFile())
            return CircleCIJunitReport.builder().context(context).build().getReport(circleCIProjectBuildArtifactElementEntityWithReport, version, httpClient);;
        if(circleCIProjectBuildArtifactElementEntityWithReport.itContainsRobotFile())
            return null;
        if(circleCIProjectBuildArtifactElementEntityWithReport.itContainsCucumberFile())
            return CircleCICucumberReport.builder().context(context).build().getReport(circleCIProjectBuildArtifactElementEntityWithReport, version, httpClient);
        return null;
    }

    private String getApplicationTestingEnvironmentVersionFromArtifact(CircleCIProjectBuildArtifactElementEntity circleCIProjectBuildArtifactElementEntityWithApplicationTestingEnvironmentVersion, HttpClient httpClient) {
        try {
            String json = httpClient.get(circleCIProjectBuildArtifactElementEntityWithApplicationTestingEnvironmentVersion.baseUrl(), String.format(circleCIProjectBuildArtifactElementEntityWithApplicationTestingEnvironmentVersion.getFileRequest(), httpClient.getUserName()));
            return  JsonUtils.getApplicationVersionFromJson(json);
        } catch (Exception e) {
            if (circleCIProjectBuildArtifactElementEntityWithApplicationTestingEnvironmentVersion == null) {
                log.log(Level.WARNING, "There was an error trying to get environment testing application version for NULL version.", e);
            } else {
                log.log(Level.WARNING, "There was an error trying to get environment testing application version for " + circleCIProjectBuildArtifactElementEntityWithApplicationTestingEnvironmentVersion.getUrl(), e);
            }
            return StringUtils.getEmpty();
        }
    }
}
