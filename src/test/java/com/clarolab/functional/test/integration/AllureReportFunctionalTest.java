package com.clarolab.functional.test.integration;

import com.clarolab.connectors.impl.utils.report.allure.json.entity.MainAllure;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.integration.test.reports.AllureReportTest;
import com.clarolab.model.Report;
import com.clarolab.model.types.ReportType;
import com.clarolab.model.types.StatusType;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.TestCaseService;
import com.clarolab.util.DateUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AllureReportFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private TestCaseService testCaseService;

    private ApplicationContextService context;

    @Before
    public void clearProvider() {
        provider.clear();
        context = ApplicationContextService
                .builder()
                .testCaseService(testCaseService)
                .build();
    }

    @Test
    public void reportTwoAllureTestPassFirst() {
        String reportStr1 = getReportContentFromJson("reports/allure/allure3.json");
        String reportStr2 = getReportContentFromJson("reports/allure/allure2.json");

        MainAllure mainAllure = MainAllure.builder().singleTest(reportStr1).isForDebug(true).build();
        mainAllure.setContext(context);
        MainAllure mainAllure2 = MainAllure.builder().singleTest(reportStr2).isForDebug(true).build();
        mainAllure2.setContext(context);
        mainAllure.getTestCaseList().addAll(mainAllure2.getTestCaseList());
        mainAllure.setTestCases(false);

        Report report = Report.builder()
                .type(ReportType.ALLURE)
                .executiondate(mainAllure.getExecutionDate())
                .status(mainAllure.getStatus())
                .duration(mainAllure.getDuration())
                .passCount(mainAllure.getPassed())
                .skipCount(mainAllure.getSkipped())
                .failCount(mainAllure.getFailed())
                .timestamp(DateUtils.now())
                .enabled(true).build();

        report.add(mainAllure.getTests());
        MatcherAssert.assertThat(report, Matchers.notNullValue());
        Assert.assertEquals("Same test should have been discarded", 1, report.getTestExecutions().size());
        Assert.assertEquals("The remaining test should be the pass one", StatusType.PASS, report.getTestExecutions().get(0).getStatus());
    }

    @Test
    public void reportTwoAllureTestPassLast() {
        String reportStr1 = getReportContentFromJson("reports/allure/allure2.json");
        String reportStr2 = getReportContentFromJson("reports/allure/allure3.json");

        MainAllure mainAllure = MainAllure.builder().singleTest(reportStr1).isForDebug(true).build();
        mainAllure.setContext(context);
        MainAllure mainAllure2 = MainAllure.builder().singleTest(reportStr2).isForDebug(true).build();
        mainAllure2.setContext(context);
        mainAllure.getTestCaseList().addAll(mainAllure2.getTestCaseList());
        mainAllure.setTestCases(false);

        Report report = Report.builder()
                .type(ReportType.ALLURE)
                .executiondate(mainAllure.getExecutionDate())
                .status(mainAllure.getStatus())
                .duration(mainAllure.getDuration())
                .passCount(mainAllure.getPassed())
                .skipCount(mainAllure.getSkipped())
                .failCount(mainAllure.getFailed())
                .timestamp(DateUtils.now())
                .enabled(true).build();

        report.add(mainAllure.getTests());
        MatcherAssert.assertThat(report, Matchers.notNullValue());
        Assert.assertEquals("Same test should have been discarded", 1, report.getTestExecutions().size());
        Assert.assertEquals("The remaining test should be the pass one", StatusType.PASS, report.getTestExecutions().get(0).getStatus());
    }


    public String getReportContentFromJson(String filename) {
        AllureReportTest test = new AllureReportTest();
        return test.getReportContentFromJson(test.getReport("allure", filename));
    }

}
