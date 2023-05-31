package com.clarolab.connectors.impl.jenkins.report.allure;

import com.clarolab.connectors.impl.jenkins.report.JenkinsBaseReport;
import com.clarolab.connectors.impl.jenkins.report.JenkinsReportType;
import com.clarolab.connectors.impl.utils.report.ReportUtils;
import com.clarolab.connectors.impl.utils.report.allure.AllureReport;
import com.clarolab.connectors.services.exceptions.ReportServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.entities.JenkinsArtifact;
import com.clarolab.entities.JenkinsBuild;
import com.clarolab.model.Report;
import com.clarolab.model.types.ReportType;
import com.clarolab.model.types.StatusType;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.extern.java.Log;

import java.util.List;

import static com.clarolab.util.StringUtils.parseDataError;

@Log
public class JenkinsAllureReport extends JenkinsBaseReport implements JenkinsReportType {

    @Builder
    private JenkinsAllureReport(JenkinsBuild jenkinsBuild, ApplicationContextService context, String applicationTestingEnvironmentVersion, String cvsLogs){
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
        return ReportUtils.builder().applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion).build().getReport(allReports, ReportType.ALLURE);
    }

    @Override
    public Report getReportFromContentFilesCollection(List<String> jsons) throws ReportServiceException {
        List<Report> allReports = Lists.newArrayList();
        for(String json: jsons){
            allReports.add(createReport(json));
        }
        return ReportUtils.builder().applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion).build().getReport(allReports, ReportType.ALLURE);
    }

    @Override
    public Report createReport(String content) throws ReportServiceException {
        log.info(String.format("Found ALLURE report for %s", jenkinsBuild.getFullDisplayName()));
        return AllureReport.builder().context(context).applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion).build().createReport(content, "This is a report for " + jenkinsBuild.getFullDisplayName());
    }

    private Report getReport(JenkinsArtifact artifact) throws ReportServiceException {
        try {
            return createReport(artifact.getContent());
        } catch (Exception e) {
            throw ReportServiceException.builder().message(parseDataError("Error getting report", jenkinsBuild.getFullDisplayName())).reason("Reason: " + StatusType.getStatus(StatusType.UNKNOWN)).type(ReportType.ALLURE).exception(e).build();
        }
    }
}
