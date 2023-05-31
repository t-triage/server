/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.integration.test.reports;

import com.clarolab.connectors.impl.utils.report.builder.JunitReportBuilder;
import com.clarolab.connectors.impl.utils.report.junit.json.entity.MainJunit;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Report;
import com.clarolab.model.types.ReportType;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.DateUtils;
import lombok.extern.java.Log;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

@Log
public class JunitReportTest extends BaseReportTest {

    @Test
    public void reportJunitTest(){
        for(String file: junitFiles){
            log.info("Testing Junit file: " + file);

            String reportStr = super.getReportContentFromXml(super.getReport("junit", file));

            MainJunit mainJunit = JunitReportBuilder.builder().build().getBuilder().create().fromJson(reportStr, MainJunit.class);
            ApplicationContextService context = ApplicationContextService.builder().build();
            context.setTestCaseService(new TestCaseServiceMock());
            mainJunit.setContext(context);

            Report report =  Report.builder()
                    .type(ReportType.JUNIT)
                    .status(mainJunit.getStatus())
                    .executiondate(0L)
                    .passCount(mainJunit.getPassed())
                    .failCount(mainJunit.getFailed())
                    .skipCount(mainJunit.getSkipped())
                    .duration(mainJunit.getTime())
                    .enabled(true)
                    .timestamp(DateUtils.now())
                    .build();
            report.add(mainJunit.getTests());

            MatcherAssert.assertThat(report, Matchers.notNullValue());
            //MatcherAssert.assertThat(report.getStatus().equals(StatusType.FAIL), Matchers.is(true));
        }
    }

    @Test
    public void reportJunitTestTen(){
        String reportStr = super.getReportContentFromXml(super.getReport("junit", getJunitBasePath()+"junit10.xml"));

        MainJunit mainJunit = JunitReportBuilder.builder().build().getBuilder().create().fromJson(reportStr, MainJunit.class);

        ApplicationContextService context = ApplicationContextService.builder().build();
        context.setTestCaseService(new TestCaseServiceMock());
        mainJunit.setContext(context);

        Report report =  Report.builder()
                .type(ReportType.JUNIT)
                .status(mainJunit.getStatus())
                .executiondate(0L)
                .passCount(mainJunit.getPassed())
                .failCount(mainJunit.getFailed())
                .skipCount(mainJunit.getSkipped())
                .duration(mainJunit.getTime())
                .enabled(true)
                .timestamp(DateUtils.now())
                .build();
        report.add(mainJunit.getTests());

        MatcherAssert.assertThat(report.getStatus().equals(StatusType.FAIL), Matchers.is(true));
    }

    @Test
    public void reportJunitTestTwelve(){
        String reportStr = super.getReportContentFromXml(super.getReport("junit", getJunitBasePath()+"junit12.xml"));

        MainJunit mainJunit = JunitReportBuilder.builder().build().getBuilder().create().fromJson(reportStr, MainJunit.class);

        ApplicationContextService context = ApplicationContextService.builder().build();
        context.setTestCaseService(new TestCaseServiceMock());
        mainJunit.setContext(context);

        Report report =  Report.builder()
                .type(ReportType.JUNIT)
                .status(mainJunit.getStatus())
                .executiondate(0L)
                .passCount(mainJunit.getPassed())
                .failCount(mainJunit.getFailed())
                .skipCount(mainJunit.getSkipped())
                .duration(mainJunit.getTime())
                .enabled(true)
                .timestamp(DateUtils.now())
                .build();
        report.add(mainJunit.getTests());

        MatcherAssert.assertThat(report.getStatus().equals(StatusType.SKIP), Matchers.is(true));
    }

}
