/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.jenkins.report.robot;

import com.clarolab.connectors.impl.jenkins.report.JenkinsBaseReport;
import com.clarolab.connectors.impl.jenkins.report.JenkinsReportType;
import com.clarolab.connectors.impl.utils.report.ReportUtils;
import com.clarolab.connectors.impl.utils.report.robot.RobotReport;
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
import lombok.Data;
import lombok.extern.java.Log;
import org.json.XML;

import java.util.List;

import static com.clarolab.util.StringUtils.parseDataError;

@Data
@Log
public class JenkinsRobotReport extends JenkinsBaseReport implements JenkinsReportType {

    @Builder
    private JenkinsRobotReport(JenkinsBuild jenkinsBuild, ApplicationContextService context, String applicationTestingEnvironmentVersion, String cvsLogs){
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
        return ReportUtils.builder().applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion).build().getReport(allReports, ReportType.ROBOT);
    }

    @Override
    public Report getReportFromContentFilesCollection(List<String> xmls) throws ReportServiceException {
        //TODO: Pay attention if the content is not an xml type. It could be a json.
        List<Report> allReports = Lists.newArrayList();
        for(String xml: xmls){
            allReports.add(createReport(XML.toJSONObject(xml).toString()));
        };
        return ReportUtils.builder().applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion).build().getReport(allReports, ReportType.ROBOT);
    }

    @Override
    public Report createReport(String content) throws ReportServiceException {
        return RobotReport.builder().context(context).applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion).build().createReport(content, jenkinsBuild.getFullDisplayName());
    }

    private Report getReport(JenkinsArtifact artifact) throws ReportServiceException {
        try {
            String content = artifact.getContent();
            if(StringUtils.isJson(content))
                return createReport(content);
            else
                return createReport(XML.toJSONObject(content).toString(4));
        } catch (Exception e) {
            throw ReportServiceException.builder().message(parseDataError("Error getting report", jenkinsBuild.getFullDisplayName())).reason("Reason: " + StatusType.getStatus(StatusType.UNKNOWN)).type(ReportType.ROBOT).exception(e).build();
        }
    }

}
