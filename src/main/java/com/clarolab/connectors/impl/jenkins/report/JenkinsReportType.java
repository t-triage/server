/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.jenkins.report;

import com.clarolab.connectors.services.exceptions.ReportServiceException;
import com.clarolab.entities.JenkinsArtifact;
import com.clarolab.model.Report;

import java.util.List;

public interface JenkinsReportType {

    Report getReport(List<JenkinsArtifact> jenkinsArtifactList) throws ReportServiceException;

    Report getReportFromContentFilesCollection(List<String> xmls) throws ReportServiceException;

    Report createReport(String content) throws ReportServiceException;
}
