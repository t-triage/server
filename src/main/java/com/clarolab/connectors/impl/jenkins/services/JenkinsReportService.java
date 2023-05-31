/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.jenkins.services;


import com.clarolab.connectors.impl.jenkins.report.allure.JenkinsAllureReport;
import com.clarolab.connectors.impl.jenkins.report.cucumber.JenkinsCucumberReport;
import com.clarolab.connectors.impl.jenkins.report.cypress.JenkinsCypressReport;
import com.clarolab.connectors.impl.jenkins.report.jest.JenkinsJestReport;
import com.clarolab.connectors.impl.jenkins.report.junit.JenkinsJunitReport;
import com.clarolab.connectors.impl.jenkins.report.robot.JenkinsRobotReport;
import com.clarolab.connectors.impl.jenkins.report.testng.JenkinsTestngReport;
import com.clarolab.connectors.services.exceptions.ReportServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.entities.JenkinsArtifact;
import com.clarolab.entities.JenkinsBuild;
import com.clarolab.model.CVSLog;
import com.clarolab.model.Report;
import com.clarolab.model.types.LogType;
import com.clarolab.model.types.ReportType;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.JsonUtils;
import com.clarolab.util.Pair;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
public class JenkinsReportService {

    private ApplicationContextService context;

    @Builder
    private JenkinsReportService(ApplicationContextService context){
        this.context = context;
    }

    public List<Report> getReportData(JenkinsBuild jenkinsBuild) {
        List<Report> reports = new ArrayList<Report>();
        ReportServiceException exception;

        try{
            return getFromRecentlyUsedReport(context.getRecentlyUsedReport(), jenkinsBuild);
        } catch (ReportServiceException e) {
            exception = e;
        }

        try {
            return getFromArtifact(jenkinsBuild);
        } catch (ReportServiceException e) {
            exception = e;
        }

        //At this point, all the attempts to get the report have failed, need to return an error report.
        log.log(Level.INFO, String.format("Attempts to get report have failed, returning an error report for %s", jenkinsBuild.getFullDisplayName()));
        Report report = Report.getDefault();
        report.setType(exception.getType());
        report.setDescription("This report belongs to " + jenkinsBuild.getFullDisplayName());
        report.setFailReason(exception.getMessage() + "::" + exception.getReason());
        report.setStatus(StatusType.getStatus(jenkinsBuild.getResult()));
        reports.add(report);
        return reports;

    }

    private List<Report> getFromArtifact(JenkinsBuild jenkinsBuild) throws ReportServiceException {
        Map<ReportType, List<JenkinsArtifact>> artifactMap = getReport(jenkinsBuild);

        if(MapUtils.isNotEmpty(artifactMap)) {
            String applicationTestingEnvironmentVersion = getApplicationTestingEnvironmentVersionFromArtifacts(jenkinsBuild.getArtifacts());
            Pair<LogType, String> cvsLogs = getCVSLogsFromArtifacts(jenkinsBuild.getArtifacts());

            log.log(Level.INFO, String.format("Artifact found for %s. Creating report from it.", jenkinsBuild.getFullDisplayName()));
            try {
                if (artifactMap.containsKey(ReportType.JUNIT)) {
                    List<Report> reports = new ArrayList<Report>();
                    JenkinsJunitReport jenkinsJunitReport = JenkinsJunitReport.builder()
                            .context(context)
                            .jenkinsBuild(jenkinsBuild)
                            .applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion)
                            .cvsLogs(cvsLogs.getValue())
                            .build();

                    Report report = jenkinsJunitReport.getReport(artifactMap.get(ReportType.JUNIT));
                    reports.add(report);
                    if (cvsLogs.getKey() != null && cvsLogs.getValue() != null) {
                        List<CVSLog> processedLogs = context.getCvsRepositoryService().readAndProcess(cvsLogs.getValue(), context.getProduct(), cvsLogs.getKey());
                        report.setLogs(processedLogs);
                    }

                    return reports;
                }
                if (artifactMap.containsKey(ReportType.ROBOT)) {
                    List<Report> reports = new ArrayList<Report>();
                    JenkinsRobotReport jenkinsRobotReport = JenkinsRobotReport.builder()
                            .context(context)
                            .jenkinsBuild(jenkinsBuild)
                            .applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion)
                            .cvsLogs(cvsLogs.getValue())
                            .build();

                    Report report = jenkinsRobotReport.getReport(artifactMap.get(ReportType.ROBOT));
                    reports.add(report);
                    List<CVSLog> processedLogs = context.getCvsRepositoryService().readAndProcess(cvsLogs.getValue(), context.getProduct(), cvsLogs.getKey());
                    report.setLogs(processedLogs);

                    return reports;
                }
                if (artifactMap.containsKey(ReportType.TESTNG)) {
                    List<Report> reports = new ArrayList<Report>();
                    JenkinsTestngReport jenkinsTestngReport = JenkinsTestngReport.builder()
                            .context(context)
                            .jenkinsBuild(jenkinsBuild)
                            .applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion)
                            .cvsLogs(cvsLogs.getValue())
                            .build();

                    Report report = jenkinsTestngReport.getReport(artifactMap.get(ReportType.TESTNG));
                    reports.add(report);
                    List<CVSLog> processedLogs = context.getCvsRepositoryService().readAndProcess(cvsLogs.getValue(), context.getProduct(), cvsLogs.getKey());
                    report.setLogs(processedLogs);

                    return reports;
                }
                if (artifactMap.containsKey(ReportType.CUCUMBER)) {
                    List<Report> reports = new ArrayList<Report>();
                    JenkinsCucumberReport jenkinsCucumberReport = JenkinsCucumberReport.builder()
                            .context(context)
                            .jenkinsBuild(jenkinsBuild)
                            .applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion)
                            .cvsLogs(cvsLogs.getValue())
                            .build();

                    Report report = jenkinsCucumberReport.getReport(artifactMap.get(ReportType.CUCUMBER));
                    reports.add(report);
                    List<CVSLog> processedLogs = context.getCvsRepositoryService().readAndProcess(cvsLogs.getValue(), context.getProduct(), cvsLogs.getKey());
                    report.setLogs(processedLogs);

                    return reports;
                }
                if (artifactMap.containsKey(ReportType.ALLURE)) {
                    List<Report> reports = new ArrayList<Report>();
                    JenkinsAllureReport jenkinsAllureReport = JenkinsAllureReport.builder()
                            .context(context)
                            .jenkinsBuild(jenkinsBuild)
                            .applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion)
                            .cvsLogs(cvsLogs.getValue())
                            .build();

                    Report report = jenkinsAllureReport.getReport(artifactMap.get(ReportType.ALLURE));
                    reports.add(report);
                    List<CVSLog> processedLogs = context.getCvsRepositoryService().readAndProcess(cvsLogs.getValue(), context.getProduct(), cvsLogs.getKey());
                    report.setLogs(processedLogs);

                    return reports;
                }
                if (artifactMap.containsKey(ReportType.CYPRESS)) {
                    List<Report> reports = new ArrayList<Report>();
                    JenkinsCypressReport jenkinsCypressReport = JenkinsCypressReport.builder()
                            .context(context)
                            .jenkinsBuild(jenkinsBuild)
                            .applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion)
                            .cvsLogs(cvsLogs.getValue())
                            .build();

                    Report report = jenkinsCypressReport.getReport(artifactMap.get(ReportType.CYPRESS));
                    reports.add(report);
                    if (cvsLogs.getKey() != null && cvsLogs.getValue() != null) {
                        List<CVSLog> processedLogs = context.getCvsRepositoryService().readAndProcess(cvsLogs.getValue(), context.getProduct(), cvsLogs.getKey());
                        report.setLogs(processedLogs);
                    }

                    return reports;
                }
                if (artifactMap.containsKey(ReportType.JEST)) {
                    List<Report> reports = new ArrayList<Report>();
                    int contador = artifactMap.size();
                    Report report = null;
                    while (contador!=0){
                        JenkinsJestReport jenkinsJestReport = JenkinsJestReport.builder()
                                .context(context)
                                .jenkinsBuild(jenkinsBuild)
                                .applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion)
                                .cvsLogs(cvsLogs.getValue())
                                .build();

                        report = jenkinsJestReport.getReport(artifactMap.get(ReportType.JEST));
                        reports.add(report);
                        contador-=1;}
                    if (cvsLogs.getKey() != null && cvsLogs.getValue() != null) {
                        List<CVSLog> processedLogs = context.getCvsRepositoryService().readAndProcess(cvsLogs.getValue(), context.getProduct(), cvsLogs.getKey());
                        report.setLogs(processedLogs);
                    }
                    return reports;
                }
            } catch (ReportServiceException e) {
                throw e;
            }
        }
        throw  ReportServiceException.builder().message(String.format("Report from artifact not found for %s.", jenkinsBuild.getFullDisplayName())).build();
    }

    private List<Report> getFromRecentlyUsedReport(ReportType reportType, JenkinsBuild jenkinsBuild) throws ReportServiceException {
        if(reportType == null || reportType == ReportType.UNKNOWN)
            throw ReportServiceException.builder().message(String.format("[getFromFrequentlyUsedReport] : There is no information about what report type is frequently used for %s",jenkinsBuild.getFullDisplayName() )).build();

        String applicationTestingEnvironmentVersion = getApplicationTestingEnvironmentVersionFromArtifacts(jenkinsBuild.getArtifacts());
        Pair<LogType, String> cvsLogs = getCVSLogsFromArtifacts(jenkinsBuild.getArtifacts());

        log.log(Level.INFO, String.format("Trying to get frequently used report for %s.", jenkinsBuild.getFullDisplayName()));

        List<JenkinsArtifact> artifactPaths;
        List<Report>reports = new ArrayList<Report>();
        Report report = null;

        switch (reportType) {
            case JUNIT:
                artifactPaths = getReportFromArtifact(jenkinsBuild, ReportType.JUNIT);
                if(CollectionUtils.isNotEmpty(artifactPaths)) {
                    JenkinsJunitReport jenkinsJunitReport = JenkinsJunitReport.builder()
                            .context(context)
                            .jenkinsBuild(jenkinsBuild)
                            .applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion)
                            .cvsLogs(cvsLogs.getValue())
                            .build();

                    report = jenkinsJunitReport.getReport(artifactPaths);
                    reports.add(report);

                    List<CVSLog> processedLogs = context.getCvsRepositoryService().readAndProcess(cvsLogs.getValue(), context.getProduct(), cvsLogs.getKey());
                    report.setLogs(processedLogs);
                }

            case ROBOT:
                artifactPaths = getReportFromArtifact(jenkinsBuild, ReportType.ROBOT);
                if(CollectionUtils.isNotEmpty(artifactPaths)) {
                    JenkinsRobotReport jenkinsRobotReport = JenkinsRobotReport.builder()
                            .context(context)
                            .jenkinsBuild(jenkinsBuild)
                            .applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion)
                            .cvsLogs(cvsLogs.getValue())
                            .build();

                    report = jenkinsRobotReport.getReport(artifactPaths);
                    reports.add(report);
                    List<CVSLog> processedLogs = context.getCvsRepositoryService().readAndProcess(cvsLogs.getValue(), context.getProduct(), cvsLogs.getKey());
                    report.setLogs(processedLogs);
                }

            case TESTNG:
                artifactPaths = getReportFromArtifact(jenkinsBuild, ReportType.TESTNG);
                if(CollectionUtils.isNotEmpty(artifactPaths)) {
                    JenkinsTestngReport jenkinsTestngReport = JenkinsTestngReport.builder()
                            .context(context)
                            .jenkinsBuild(jenkinsBuild)
                            .applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion)
                            .cvsLogs(cvsLogs.getValue())
                            .build();

                    report = jenkinsTestngReport.getReport(artifactPaths);
                    reports.add(report);
                    List<CVSLog> processedLogs = context.getCvsRepositoryService().readAndProcess(cvsLogs.getValue(), context.getProduct(), cvsLogs.getKey());
                    report.setLogs(processedLogs);
                }

            case CUCUMBER:
                artifactPaths = getReportFromArtifact(jenkinsBuild, ReportType.CUCUMBER);
                if(CollectionUtils.isNotEmpty(artifactPaths)) {
                    JenkinsCucumberReport jenkinsCucumberReport = JenkinsCucumberReport.builder()
                            .context(context)
                            .jenkinsBuild(jenkinsBuild)
                            .applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion)
                            .cvsLogs(cvsLogs.getValue())
                            .build();

                    report = jenkinsCucumberReport.getReport(artifactPaths);
                    reports.add(report);
                    List<CVSLog> processedLogs = context.getCvsRepositoryService().readAndProcess(cvsLogs.getValue(), context.getProduct(), cvsLogs.getKey());
                    report.setLogs(processedLogs);
                }

            case ALLURE:
                artifactPaths = getReportFromArtifact(jenkinsBuild, ReportType.ALLURE);
                if(CollectionUtils.isNotEmpty(artifactPaths)) {
                    JenkinsAllureReport jenkinsAllureReport = JenkinsAllureReport.builder()
                            .context(context)
                            .jenkinsBuild(jenkinsBuild)
                            .applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion)
                            .cvsLogs(cvsLogs.getValue())
                            .build();

                    report = jenkinsAllureReport.getReport(artifactPaths);
                    reports.add(report);
                    List<CVSLog> processedLogs = context.getCvsRepositoryService().readAndProcess(cvsLogs.getValue(), context.getProduct(), cvsLogs.getKey());
                    report.setLogs(processedLogs);
                }

            case CYPRESS:
                artifactPaths = getReportFromArtifact(jenkinsBuild, ReportType.CYPRESS);
                if(CollectionUtils.isNotEmpty(artifactPaths)) {
                    JenkinsCypressReport jenkinsCypressReport = JenkinsCypressReport.builder()
                            .context(context)
                            .jenkinsBuild(jenkinsBuild)
                            .applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion)
                            .cvsLogs(cvsLogs.getValue())
                            .build();

                    report = jenkinsCypressReport.getReport(artifactPaths);
                    reports.add(report);
                    List<CVSLog> processedLogs = context.getCvsRepositoryService().readAndProcess(cvsLogs.getValue(), context.getProduct(), cvsLogs.getKey());
                    report.setLogs(processedLogs);
                }
            case JEST:
                artifactPaths = getReportFromArtifact(jenkinsBuild, ReportType.JEST);
                if(CollectionUtils.isNotEmpty(artifactPaths)) {
                    JenkinsJestReport jenkinsJestReport = JenkinsJestReport.builder()
                            .context(context)
                            .jenkinsBuild(jenkinsBuild)
                            .applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion)
                            .cvsLogs(cvsLogs.getValue())
                            .build();

                    report = jenkinsJestReport.getReport(artifactPaths);
                    reports.add(report);
                    List<CVSLog> processedLogs = context.getCvsRepositoryService().readAndProcess(cvsLogs.getValue(), context.getProduct(), cvsLogs.getKey());
                    report.setLogs(processedLogs);
                }

            default:
                return reports;
        }
    }

    // ***********************************************************************************************
    // ***********************************************************************************************
    // ***********************************************************************************************

    private List<JenkinsArtifact> getReportFromArtifact(JenkinsBuild jenkinsBuild, ReportType type){
        return getReport(jenkinsBuild).get(type);
    }

    private Map<ReportType, List<JenkinsArtifact>> getReport(JenkinsBuild jenkinsBuild) {
        log.log(Level.INFO, String.format("Trying to get report from artifacts for %s.", jenkinsBuild.getFullDisplayName()));
        Map<ReportType, List<JenkinsArtifact>> out = Maps.newConcurrentMap();

        List<JenkinsArtifact> robotArtifacts = jenkinsBuild.getArtifactsWithRobotReports();
        if(CollectionUtils.isNotEmpty(robotArtifacts)){
            out.put(ReportType.ROBOT, robotArtifacts);
        }

        List<JenkinsArtifact> testNGArtifacts = jenkinsBuild.getArtifactsWithTestNGReports();
        if(CollectionUtils.isNotEmpty(testNGArtifacts)){
            out.put(ReportType.TESTNG, testNGArtifacts);
        }

        List<JenkinsArtifact> junitArtifacts = jenkinsBuild.getArtifactsWithJunitReports();
        if(CollectionUtils.isNotEmpty(junitArtifacts)){
            out.put(ReportType.JUNIT, junitArtifacts);
        }

        List<JenkinsArtifact> cucumberArtifacts = jenkinsBuild.getArtifactsWithCucumberReports().stream().filter(artifact -> !artifact.getFileName().matches("(\\W|\\w)*(V|v)ersion\\.json")).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(cucumberArtifacts)){
            out.put(ReportType.CUCUMBER, cucumberArtifacts);
        }

        List<JenkinsArtifact> allureArtifacts = jenkinsBuild.getArtifactsWithAllureReports().stream().filter(artifact -> artifact.getFileName().contains("result")).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(allureArtifacts)){
            out.put(ReportType.ALLURE, allureArtifacts);
        }

        List<JenkinsArtifact> cypressArtifacts = jenkinsBuild.getArtifactsWithCypressReports().stream().filter(artifact -> artifact.getFileName().contains("cypress")).collect(Collectors.toList());;
        if(CollectionUtils.isNotEmpty(cypressArtifacts)) {
            out.put(ReportType.CYPRESS, cypressArtifacts);
        }
        List<JenkinsArtifact> jestArtifacts = jenkinsBuild.getArtifactsWithJestReports().stream().filter(artifact -> artifact.getFileName().contains("jest")).collect(Collectors.toList());;
        if(CollectionUtils.isNotEmpty(jestArtifacts)) {
            out.put(ReportType.JEST, jestArtifacts);
        }

        return out;
    }

    private String getApplicationTestingEnvironmentVersionFromArtifacts(List<JenkinsArtifact> artifacts) {
        JenkinsArtifact artifact = artifacts.stream().filter(e -> e.getFileName().matches("(\\W|\\w)*(V|v)ersion\\.json")).findFirst().orElse(null);
        return  JsonUtils.getApplicationVersionFromJson(artifact != null ? artifact.getContent() : StringUtils.getEmpty());
    }

    public Pair<LogType, String> getCVSLogsFromArtifacts(List<JenkinsArtifact> artifacts) {
        if (artifacts == null || artifacts.isEmpty())
            return null;

        LogType logType = null;
        String fileContent = null;

        try {
            if (context.getProduct() != null && !StringUtils.isEmpty(context.getProduct().getPackageNames())) {
                for (JenkinsArtifact artifact : artifacts) {
                    if (LogType.isCVSLog(artifact.getFileName())) {
                        logType = LogType.getLogType(artifact.getFileName());
                        fileContent = artifact.getContent();
                    }
                }
            } else {
                log.log(Level.WARNING, "[getCVSLogs] There are CVS logs but product packages are not configured");
            }

            return new Pair<LogType, String>(logType, fileContent);

        } catch (Exception e) {
            log.log(Level.SEVERE, "[getCVSLogs] Error getting CVS logs from artifact", e);
            throw e;
        }
    }

}
