package com.clarolab.connectors.impl.jenkins.report.cucumber;

import com.clarolab.connectors.impl.jenkins.report.JenkinsBaseReport;
import com.clarolab.connectors.impl.jenkins.report.JenkinsReportType;
import com.clarolab.connectors.impl.utils.report.ReportUtils;
import com.clarolab.connectors.impl.utils.report.cucumber.CucumberReport;
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
public class JenkinsCucumberReport extends JenkinsBaseReport implements JenkinsReportType {

    @Builder
    private JenkinsCucumberReport(JenkinsBuild jenkinsBuild, ApplicationContextService context, String applicationTestingEnvironmentVersion, String cvsLogs){
        this.jenkinsBuild = jenkinsBuild;
        this.context = context;
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
        return ReportUtils.builder().applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion).build().getReport(allReports, ReportType.CUCUMBER);
    }

    @Override
    public Report getReportFromContentFilesCollection(List<String> jsons) throws ReportServiceException {
        List<Report> allReports = Lists.newArrayList();
        for(String json: jsons){
            allReports.add(createReport(json));
        }
        return ReportUtils.builder().applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion).build().getReport(allReports, ReportType.JUNIT);
    }

    @Override
    public Report createReport(String content) throws ReportServiceException {
        log.info(String.format("Found CUCUMBER report for %s", jenkinsBuild.getFullDisplayName()));
        return CucumberReport.builder().context(context).applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion).build().createReport(content, "This is a report for " + jenkinsBuild.getFullDisplayName());
    }

    private Report getReport(JenkinsArtifact artifact) throws ReportServiceException {
        try {
            String content = artifact.getContent();
            if(StringUtils.isJson(content))
                return createReport(content);
            else
                return createReport(XML.toJSONObject(content).toString(4));
        } catch (Exception e) {
            throw ReportServiceException.builder().message(parseDataError("Error getting report", jenkinsBuild.getFullDisplayName())).reason("Reason: " + StatusType.getStatus(StatusType.UNKNOWN)).type(ReportType.CUCUMBER).exception(e).build();
        }
    }
}
