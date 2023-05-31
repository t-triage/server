package com.clarolab.connectors.impl.utils.report;

import com.clarolab.connectors.impl.utils.report.allure.AllureReport;
import com.clarolab.connectors.impl.utils.report.cucumber.CucumberReport;
import com.clarolab.connectors.impl.utils.report.cypress.CypressReport;
import com.clarolab.connectors.impl.utils.report.jest.JestReport;
import com.clarolab.connectors.impl.utils.report.junit.JUnitReport;
import com.clarolab.connectors.impl.utils.report.protractor.ProtractorReport;
import com.clarolab.connectors.impl.utils.report.protractor.ProtractorReportV2;
import com.clarolab.connectors.impl.utils.report.python.PythonReport;
import com.clarolab.connectors.impl.utils.report.robot.RobotReport;
import com.clarolab.connectors.impl.utils.report.testng.TestNGReport;
import com.clarolab.connectors.services.exceptions.ReportServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.dto.push.ArtifactDTO;
import com.clarolab.dto.push.DataDTO;
import com.clarolab.model.CVSLog;
import com.clarolab.model.Report;
import com.clarolab.model.TestExecution;
import com.clarolab.model.types.ReportType;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Setter;
import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONException;
import org.json.XML;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;


@Log
@Builder
public class ReportUtils {

    public static final String REPORT_NAME_ROBOT = "output.xml";
    public static final String REPORT_NAME_TESTNG = "testng-results.xml";
    public static final String REPORT_NAME_JUNIT = "TEST-(\\w|\\W)*.xml";
    public static final String REPORT_NAME_CUCUMBER = "(\\w|\\W)*cucumber(\\w|\\W)*.json";
    public static final String REPORT_NAME_PROTRACTOR = "(\\w|\\W)*-ProtractorOutput(\\w|\\W)*.xml";
    public static final String REPORT_NAME_ALLURE = "(\\w|\\W)*-result.json";
    public static final String REPORT_NAME_CYPRESS = "(\\w|\\W)*cypress(\\w|\\W)*.json";
    public static final String REPORT_NAME_JEST = "(\\w|\\W)*jest(\\w|\\W)*.json";
    public static final String REPORT_NAME_PYTHON = "(\\w|\\W)*python(\\w|\\W)*.json";

    private ApplicationContextService context;

    @Setter
    private String applicationTestingEnvironmentVersion;

    @Setter
    private String cvsLogs;

    //**************************************************************************************************
    // Utilities to evaluate a list of reports
    //**************************************************************************************************

    public Report getReport(List<Report> allReports){
        if(allReports.isEmpty())
            return Report.getDefault();

        if(allReportsHaveSameType(allReports)){
            Report firstNotNullElement = allReports.stream().filter(r -> r != null).findFirst().orElse(null);
            if (firstNotNullElement == null) {
                return null;
            }
            return getReport(allReports, firstNotNullElement.getType());
        }
        else
            return getReport(allReports, null);
    }

    public Report getReport(List<Report> allReports, ReportType type){
        Report report = Report.builder()
                .type(type != null ? type : ReportType.UNKNOWN)
                .status(getStatus(allReports))
                .executiondate(0L)
                .passCount(getPassed(allReports))
                .failCount(getFailed(allReports))
                .skipCount(getSkipped(allReports))
                .duration(getDuration(allReports))
                .productVersion(applicationTestingEnvironmentVersion)
                .enabled(true)
                .timestamp(DateUtils.now())
                .build();
        report.add(getTestCases(allReports));
        report.addLogs(getLogs(allReports));
        return report;
    }
    public List<Report> getJestReport(List<Report> allReports, ReportType type){
        List<Report>answer = new ArrayList<Report>();
        for(Report r: allReports) {
            Report report = Report.builder()
                    .type(type != null ? type : ReportType.UNKNOWN)
                    .status(getStatus(allReports))
                    .executiondate(0L)
                    .passCount(getPassed(allReports))
                    .failCount(getFailed(allReports))
                    .skipCount(getSkipped(allReports))
                    .duration(getDuration(allReports))
                    .productVersion(applicationTestingEnvironmentVersion)
                    .enabled(true)
                    .timestamp(DateUtils.now())
                    .build();
            report.add(getTestCases(allReports));
            report.addLogs(getLogs(allReports));
            answer.add(r);
        }
        return answer;
    }

    private StatusType getStatus(List<Report> reports){
        if(reports.stream().filter(report -> report.getStatus().equals(StatusType.FAIL)).count()>0)
            return StatusType.FAIL;
        if(reports.stream().filter(report -> report.getStatus().equals(StatusType.SKIP)).count()>0)
            return StatusType.SKIP;
        return StatusType.PASS;
    }

    private List<TestExecution> getTestCases(List<Report> reports){
        List<TestExecution> tests = Lists.newArrayList();
        reports.forEach(report -> tests.addAll(report.getTestExecutions()));
        return tests;
    }

    private List<CVSLog> getLogs(List<Report> reports){
        List<CVSLog> logs = Lists.newArrayList();
        reports.forEach(report -> {
            if (report.getLogs() != null)
                logs.addAll(report.getLogs());
        });
        return logs;
    }

    private int getTotal(List<Report> reports){
        return reports.stream().mapToInt(report -> report.getTotalTest()).sum();
    }

    private int getFailed(List<Report> reports){
        return reports.stream().mapToInt(report -> report.getFailCount()).sum();
    }

    private int getSkipped(List<Report> reports){
        return reports.stream().mapToInt(report -> report.getSkipCount()).sum();
    }

    private int getPassed(List<Report> reports){
        return reports.stream().mapToInt(report -> report.getPassCount()).sum();
    }

    private double getDuration(List<Report> reports){
        return reports.stream().mapToDouble(report -> report.getDuration()).sum();
    }

    public Report createReport(String content, ReportType reportType, String notes) throws ReportServiceException {
        if (context != null && context.getExecutor() != null && context.getExecutor().hasReportType()) {
            reportType = context.getExecutor().getReportType();
        } else if (context != null && context.getContainer() != null && context.getContainer().hasReportType()) {
            reportType = context.getContainer().getReportType();
        }

        if (content.toLowerCase().contains("jest")) {
            log.log(Level.INFO, String.format("Changing type: %s.", reportType));
            reportType = ReportType.JEST;
        }

        switch (reportType){
            case JUNIT:
                return JUnitReport.builder().context(context).applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion).build().createReport(content, notes);
            case ROBOT:
                return RobotReport.builder().context(context).applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion).build().createReport(content, notes);
            case TESTNG:
                return TestNGReport.builder().context(context).applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion).build().createReport(content, notes);
            case CUCUMBER:
                return CucumberReport.builder().context(context).applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion).build().createReport(content, notes);
            case PROTRACTOR:
                return ProtractorReport.builder().context(context).applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion).build().createReport(content, notes);
            case PROTRACTOR_STEPS:
                return ProtractorReportV2.builder().context(context).applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion).build().createReport(content, notes);
            case ALLURE:
                return AllureReport.builder().context(context).applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion).build().createReport(content, notes);
            case CYPRESS:
                return CypressReport.builder().context(context).applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion).build().createReport(content, notes);
            case JEST:
                return JestReport.builder().context(context).applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion).build().createReport(content, notes);
            case PYTHON:
                return PythonReport.builder().context(context).applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion).build().createReport(content, notes);
            default:
//                return Report.getDefault();
                return null;
        }
    }

    public ReportType getReportTypeFromFileName(String fileName){
        if(fileName.equals(ReportUtils.REPORT_NAME_ROBOT))
            return ReportType.ROBOT;
        if(fileName.equals(ReportUtils.REPORT_NAME_TESTNG))
            return ReportType.TESTNG;
        if(fileName.matches(ReportUtils.REPORT_NAME_JUNIT))
            return ReportType.JUNIT;
        if(fileName.matches(ReportUtils.REPORT_NAME_ALLURE))
            return ReportType.ALLURE;
        if(fileName.matches(ReportUtils.REPORT_NAME_CUCUMBER))
            return ReportType.CUCUMBER;
        if(fileName.matches(ReportUtils.REPORT_NAME_PROTRACTOR))
            return ReportType.PROTRACTOR;
        if(fileName.matches(ReportUtils.REPORT_NAME_CYPRESS))
            return ReportType.CYPRESS;
        if(fileName.matches(ReportUtils.REPORT_NAME_JEST))
            return ReportType.JEST;
        if(fileName.matches(ReportUtils.REPORT_NAME_PYTHON))
            return ReportType.PYTHON;
        return ReportType.UNKNOWN;
    }

    private boolean allReportsHaveSameType(List<Report> reports){
        Report firstNotNullElement = reports.stream().filter(r -> r != null).findFirst().orElse(null);
        return reports.stream().filter(r -> r != null)
                .allMatch(r -> r.getType().getReportType() == firstNotNullElement.getType().getReportType());
    }

    // *****************************************************************************************************************
    // ************************************* Utilities to be used for PUSH action **************************************
    // *****************************************************************************************************************
    public Map<ReportType, List<String>> getReport(DataDTO dataDTO) {
        log.log(Level.INFO, String.format("Trying to get report from artifacts for %s#%d.", dataDTO.getJobName(), dataDTO.getBuildNumber()));
        ReportType type = ReportType.UNKNOWN;
        log.log(Level.INFO, String.format("Filename %s.", dataDTO.getArtifacts().get(0).getFileName()));
        Map<ReportType, List<String>> out = Maps.newConcurrentMap();

        List<ArtifactDTO> artifactsDTO = dataDTO.getArtifacts().stream().filter(element -> element.getFileName().equals(ReportUtils.REPORT_NAME_ROBOT)).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(artifactsDTO))
            type = ReportType.ROBOT;
        else {
            artifactsDTO = dataDTO.getArtifacts().stream().filter(element -> element.getFileName().equals(ReportUtils.REPORT_NAME_TESTNG)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(artifactsDTO))
                type = ReportType.TESTNG;
            else {
                artifactsDTO = dataDTO.getArtifacts().stream().filter(element -> element.getFileName().matches(ReportUtils.REPORT_NAME_JUNIT)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(artifactsDTO))
                    type = ReportType.JUNIT;
                else {
                    artifactsDTO = dataDTO.getArtifacts().stream().filter(element -> element.getFileName().matches(ReportUtils.REPORT_NAME_ALLURE)).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(artifactsDTO))
                        type = ReportType.ALLURE;
                    else {
                        artifactsDTO = dataDTO.getArtifacts().stream().filter(element -> element.getFileName().matches(ReportUtils.REPORT_NAME_CUCUMBER)).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(artifactsDTO))
                            type = ReportType.CUCUMBER;
                        else {
                            artifactsDTO = dataDTO.getArtifacts().stream().filter(element -> element.getFileName().matches(ReportUtils.REPORT_NAME_PROTRACTOR)).collect(Collectors.toList());
                            if (CollectionUtils.isNotEmpty(artifactsDTO))
                                type = ReportType.PROTRACTOR;
                            else {
                                artifactsDTO = dataDTO.getArtifacts().stream().filter(element -> element.getFileName().matches(ReportUtils.REPORT_NAME_CYPRESS)).collect(Collectors.toList());
                                if (CollectionUtils.isNotEmpty(artifactsDTO)) {
                                    type = ReportType.CYPRESS;
                                }
                                else {
                                    artifactsDTO = dataDTO.getArtifacts().stream().filter(element -> element.getFileName().matches(ReportUtils.REPORT_NAME_JEST)).collect(Collectors.toList());
                                    if (CollectionUtils.isNotEmpty(artifactsDTO)) {
                                        type = ReportType.JEST;
                                    }
                                    else {
                                            artifactsDTO = dataDTO.getArtifacts().stream().filter(element -> element.getFileName().matches(ReportUtils.REPORT_NAME_PYTHON)).collect(Collectors.toList());
                                            if (CollectionUtils.isNotEmpty(artifactsDTO)) {
                                                type = ReportType.PYTHON;
                                            }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (CollectionUtils.isNotEmpty(artifactsDTO)){
            List<String> artifactList = Lists.newArrayList();
            artifactsDTO.forEach(artifact -> artifactList.add(artifact.getContent()));
            if(dataDTO.getViewName().toLowerCase().contains("jest")){
                type=ReportType.JEST;
            }
            out.put(type, artifactList);
        }

        return out;
    }

    //TODO: Replace usage of this method currently on JenkinsReport.getReportData
    public Report getReportData(DataDTO dataDTO) {
        Report report = null;
        Map<ReportType, List<String>> map = getReport(dataDTO);

        log.log(Level.INFO, String.format("Report type %s.", dataDTO.getArtifacts().get(0).getFileName()));

        String jobName = dataDTO.getJobName().contains("#") ? dataDTO.getJobName().substring(0, dataDTO.getJobName().indexOf("#")-1) : dataDTO.getJobName();
        if (dataDTO.getArtifacts().get(0).getFileName().toLowerCase().contains("jest")){
            List<String>list = new ArrayList<String>();
            for(ReportType s: map.keySet()){
                list.addAll(map.get(s));
            }
            log.info("Changing type");
            log.log(Level.INFO, String.format("Map List %s.", list));
            map.put(ReportType.JEST,list);
        }

        try {
            if (map.containsKey(ReportType.JUNIT))
                return getReportFromContentFilesCollection(map.get(ReportType.JUNIT), ReportType.JUNIT, jobName+"#"+Math.toIntExact(dataDTO.getBuildNumber()));
            if (map.containsKey(ReportType.ROBOT))
                return getReportFromContentFilesCollection(map.get(ReportType.ROBOT), ReportType.ROBOT, jobName+"#"+Math.toIntExact(dataDTO.getBuildNumber()));
            if (map.containsKey(ReportType.TESTNG))
                return getReportFromContentFilesCollection(map.get(ReportType.TESTNG), ReportType.TESTNG, jobName+"#"+Math.toIntExact(dataDTO.getBuildNumber()));
            if (map.containsKey(ReportType.PROTRACTOR))
                return getReportFromContentFilesCollection(map.get(ReportType.PROTRACTOR), ReportType.PROTRACTOR, jobName+"#"+Math.toIntExact(dataDTO.getBuildNumber()));
            if (map.containsKey(ReportType.ALLURE))
                return getReportFromContentFilesCollection(map.get(ReportType.ALLURE), ReportType.ALLURE, jobName+"#"+Math.toIntExact(dataDTO.getBuildNumber()));
            if (map.containsKey(ReportType.CYPRESS)){
                return getReportFromContentFilesCollection(map.get(ReportType.CYPRESS), ReportType.CYPRESS, jobName+"#"+Math.toIntExact(dataDTO.getBuildNumber()));}
            if (map.containsKey(ReportType.JEST)){
                return getReportFromContentFilesCollection(map.get(ReportType.JEST), ReportType.JEST, jobName+"#"+Math.toIntExact(dataDTO.getBuildNumber()));}
            if (map.containsKey(ReportType.PYTHON)){
                return getReportFromContentFilesCollection(map.get(ReportType.PYTHON), ReportType.PYTHON, jobName+"#"+Math.toIntExact(dataDTO.getBuildNumber()));}
        }
         catch (ReportServiceException exception) {
            report = Report.getDefault();
            report.setType(exception.getType() != null ? exception.getType() : ReportType.UNKNOWN);
            report.setDescription("This report belongs to " + jobName);
            report.setFailReason(exception.getMessage() + "::" + exception.getReason());
            report.setStatus(StatusType.UNKNOWN);
            report.setExecutiondate(dataDTO.getTimestamp());
        }
        return report;
    }

    public Report getReportDataForJest(DataDTO dataDTO) {
        Report report = null;
        Map<ReportType, List<String>> map = getReport(dataDTO);

        String jobName = dataDTO.getJobName().contains("#") ? dataDTO.getJobName().substring(0, dataDTO.getJobName().indexOf("#")-1) : dataDTO.getJobName();

        try {
            if (map.containsKey(ReportType.JEST))
                return getReportFromContentFilesCollection(map.get(ReportType.JEST), ReportType.JEST, jobName+"#"+Math.toIntExact(dataDTO.getBuildNumber()));
        }
        catch (ReportServiceException exception) {
            report = Report.getDefault();
            report.setType(exception.getType() != null ? exception.getType() : ReportType.UNKNOWN);
            report.setDescription("This report belongs to " + jobName);
            report.setFailReason(exception.getMessage() + "::" + exception.getReason());
            report.setStatus(StatusType.UNKNOWN);
            report.setExecutiondate(dataDTO.getTimestamp());
        }
        return report;
    }

    private Report getReportFromContentFilesCollection(List<String> contents, ReportType reportType, String notes) throws ReportServiceException {
        List<Report> allReports = Lists.newArrayList();
        for(String content: contents){
            if(StringUtils.isJson(content))
                allReports.add(createReport(content, reportType, notes));
            else {
                try {
                    allReports.add(createReport(XML.toJSONObject(content).toString(), reportType, notes));
                } catch (JSONException e) {
                    log.log(Level.SEVERE, "Could not parse json: " + content, e);
                }

            }
        }
        return ReportUtils.builder().applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion).cvsLogs(cvsLogs).build().getReport(allReports, reportType);
    }

    private Report getReportFromContentFilesCollectionForJest(List<String> contents, ReportType reportType, String notes) throws ReportServiceException {
        List<Report> allReports = Lists.newArrayList();
        for(String content: contents){
            if(StringUtils.isJson(content))
                allReports.add(createReport(content, reportType, notes));
            else {
                try {
                    allReports.add(createReport(XML.toJSONObject(content).toString(), reportType, notes));
                } catch (JSONException e) {
                    log.log(Level.SEVERE, "Could not parse json: " + content, e);
                }

            }
        }
        return ReportUtils.builder().applicationTestingEnvironmentVersion(applicationTestingEnvironmentVersion).cvsLogs(cvsLogs).build().getReport(allReports, reportType);
    }
}
