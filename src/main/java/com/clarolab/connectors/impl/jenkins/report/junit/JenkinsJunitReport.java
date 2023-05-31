/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.jenkins.report.junit;

import com.clarolab.connectors.impl.jenkins.report.JenkinsBaseReport;
import com.clarolab.connectors.impl.jenkins.report.JenkinsReportType;
import com.clarolab.connectors.impl.utils.report.ReportUtils;
import com.clarolab.connectors.impl.utils.report.junit.JUnitReport;
import com.clarolab.connectors.services.exceptions.ReportServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.entities.JenkinsArtifact;
import com.clarolab.entities.JenkinsBuild;
import com.clarolab.model.Report;
import com.clarolab.model.types.ReportType;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.extern.java.Log;
import org.json.XML;

import java.util.List;

import static com.clarolab.util.StringUtils.parseDataError;

@Log
public class JenkinsJunitReport extends JenkinsBaseReport implements JenkinsReportType {

    @Builder
    private JenkinsJunitReport(JenkinsBuild jenkinsBuild, ApplicationContextService context, String applicationTestingEnvironmentVersion, String cvsLogs) {
        this.context = context;
        this.jenkinsBuild = jenkinsBuild;
        this.applicationTestingEnvironmentVersion = applicationTestingEnvironmentVersion;
        this.cvsLogs = cvsLogs;
    }

    @Override
    public Report getReport(List<JenkinsArtifact> jenkinsArtifactList) throws ReportServiceException {
        List<Report> allReports = Lists.newArrayList();
        jenkinsArtifactList.forEach(artifact -> {
            try {
                allReports.add(getReport(artifact));
            } catch (ReportServiceException e) {
                throw new RuntimeException(e);
            }
        });
        return ReportUtils.builder().applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion).build().getReport(allReports, ReportType.JUNIT);
    }

    @Override
    public Report createReport(String content) throws ReportServiceException {
        log.info(String.format("Found JUNIT report for %s", jenkinsBuild.getFullDisplayName()));
        return JUnitReport.builder()
                .context(context)
                .applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion)
                .build()
                .createReport(content, "This is a report for " + jenkinsBuild.getFullDisplayName());
    }

    @Override
    public Report getReportFromContentFilesCollection(List<String> contents) throws ReportServiceException {
        List<Report> allReports = Lists.newArrayList();
        for(String content: contents){
            if(StringUtils.isJson(content))
                allReports.add(createReport(content));
            else
                allReports.add(createReport(XML.toJSONObject(content).toString()));
        }
        return ReportUtils.builder()
                .applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion)
                .build()
                .getReport(allReports, ReportType.JUNIT);
    }

    private Report getReport(JenkinsArtifact artifact) throws ReportServiceException {
        try {
            String content = artifact.getContent();
            if(StringUtils.isJson(content))
                return createReport(content);
            else
                return createReport(XML.toJSONObject(content).toString(4));
        } catch (Exception e) {
            throw ReportServiceException.builder()
                    .message(parseDataError("Error getting report", jenkinsBuild.getFullDisplayName()))
                    .reason("Reason: " + StatusType.getStatus(StatusType.UNKNOWN))
                    .type(ReportType.JUNIT)
                    .exception(e)
                    .build();
        }
    }
}
