/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.circleCI.report;

import com.clarolab.connectors.impl.circleCI.model.entities.CircleCIProjectBuildArtifactElementEntity;
import com.clarolab.connectors.services.exceptions.ConnectorServiceException;
import com.clarolab.http.client.HttpClient;
import com.clarolab.model.Report;

public interface CircleCIReportType {

    Report getReport(CircleCIProjectBuildArtifactElementEntity circleCIProjectBuildArtifactElementEntityWithReport, String applicationTestingEnvironmentVersion, HttpClient httpClient) throws ConnectorServiceException;
}
