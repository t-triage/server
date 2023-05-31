package com.clarolab.connectors.impl.utils.report.junit;

import com.clarolab.connectors.impl.AbstractTestCreator;
import com.clarolab.connectors.impl.utils.report.builder.JunitReportBuilder;
import com.clarolab.connectors.impl.utils.report.junit.json.entity.*;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Report;
import com.clarolab.model.TestExecution;
import com.clarolab.model.types.ReportType;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.DateUtils;
import com.clarolab.util.LogicalCondition;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import lombok.Builder;
import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Log
public class JUnitReport extends AbstractTestCreator {

    private String applicationTestingEnvironmentVersion;

    @Builder
    private JUnitReport(ApplicationContextService context, String applicationTestingEnvironmentVersion){
        super.context = context;
        this.applicationTestingEnvironmentVersion = applicationTestingEnvironmentVersion;
    }

    public Report createReport(String json, String observations) {
        MainJunit mainJunit = JunitReportBuilder.builder().build().getBuilder().create().fromJson(json, MainJunit.class);
        mainJunit.setContext(context);

        Report report = Report.builder()
                .type(ReportType.JUNIT)
                .description(observations)
                .status(mainJunit.getStatus())
                .executiondate(0L)
                .passCount(mainJunit.getPassed())
                .failCount(mainJunit.getFailed())
                .skipCount(mainJunit.getSkipped())
                .duration(mainJunit.getTime())
                .productVersion(applicationTestingEnvironmentVersion)
                .enabled(true)
                .timestamp(DateUtils.now())
                .build();

        List<TestExecution> tests = mainJunit.getTests();
        Collections.reverse(tests);

        report.add(tests);
        log.info(String.format("Found JUNIT report for %s", observations));
        context.configureRecentlyUsedReport(ReportType.JUNIT);
        return report;
    }

    public Report getReportFromJson(String json){
        return getReportFromJson(json, null);
    }

    public Report getReportFromJson(String json, String description) {
        JunitTestResult result = new Gson().fromJson(json, JunitTestResult.class);
        List<JunitTestSuites> suites = Lists.newArrayList();
        Report reportToReturn = Report.builder()
                .type(ReportType.JUNIT)
                .description(description != null ? description : "This is a JUnit Report")
                .productVersion(applicationTestingEnvironmentVersion)
                .enabled(true)
                .timestamp(DateUtils.now())
                .build();

        if (CollectionUtils.isEmpty(result.getSuites())) {
            JunitTestReport report = new Gson().fromJson(json, JunitTestReport.class);
            reportToReturn.setStatus(getStatus(report));
            reportToReturn.setDuration(Double.valueOf(report.getChildReports().stream().mapToDouble(childReport -> childReport.getResult().getDuration()).sum()).longValue());
            reportToReturn.setPassCount(report.getTotalCount() - (report.getFailCount() + report.getSkipCount()));
            reportToReturn.setFailCount(report.getFailCount());
            reportToReturn.setSkipCount(report.getSkipCount());

            report.getChildReports().forEach(childReport -> suites.addAll(childReport.getResult().getSuites()));
        } else {
            reportToReturn.setStatus(getStatus(result));
            reportToReturn.setDuration(Double.valueOf(result.getDuration()).longValue());
            reportToReturn.setPassCount(result.getPassCount());
            reportToReturn.setFailCount(result.getFailCount());
            reportToReturn.setSkipCount(result.getSkipCount());

            suites.addAll(result.getSuites());
        }

        suites.forEach(currentSuite -> reportToReturn.add(this.getTestCases(currentSuite)));
        log.info(String.format("Found JUNIT report for %s", description));
        context.configureRecentlyUsedReport(ReportType.JUNIT);
        return reportToReturn;
    }

    public StatusType getStatus(JunitTestResult testResult) {
        if (testResult.getFailCount() > 0) return StatusType.FAIL;
        if (testResult.getSkipCount() > 0) return StatusType.SKIP;
        if (LogicalCondition.AND(testResult.getFailCount() == 0, testResult.getSkipCount() == 0, testResult.getPassCount() > 0))
            return StatusType.PASS;
        return StatusType.UNKNOWN;
    }

    public StatusType getStatus(JunitTestReport testReport) {
        if (testReport.getFailCount() > 0) return StatusType.FAIL;
        if (testReport.getSkipCount() > 0) return StatusType.SKIP;
        if (LogicalCondition.AND(testReport.getFailCount() == 0, testReport.getSkipCount() == 0))
            return StatusType.PASS;
        return StatusType.UNKNOWN;
    }

    private List<TestExecution> getTestCases(JunitTestSuites suite) {
        List<TestExecution> testExecutions = Lists.newArrayList();
        suite.getCases().forEach(currentTestcase -> {
            TestExecution testExecution = this.getTestCaseData(currentTestcase, suite.getName());
            testExecutions.add(testExecution);
        });
        return testExecutions;
    }

    private TestExecution getTestCaseData(JunitTestCaseV2 testCase, String suiteName) {
        return TestExecution.builder()
                .testCase(getTestCase(testCase.getName(), testCase.getClassName()))
                .duration(Double.valueOf(testCase.getDuration()).longValue())
                .suiteName(suiteName)
                .status(StatusType.getTestCaseStatus(testCase))
                .errorDetails(testCase.getErrorDetails())
                .errorStackTrace(testCase.getErrorStackTrace())
                .failedSince(testCase.getFailedSince())
                .enabled(true)
                .timestamp(DateUtils.now())
                .build();
    }
}
