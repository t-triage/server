/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.integration.test.reports;

import com.clarolab.connectors.impl.utils.report.builder.TestngReportBuilder;
import com.clarolab.connectors.impl.utils.report.testng.json.entity.MainTestNG;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Report;
import com.clarolab.model.types.ReportType;
import com.clarolab.util.DateUtils;
import lombok.extern.java.Log;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

@Log
public class TestngReportTest extends BaseReportTest {

    @Test
    public void reportTestngTest(){
        for(String file: testngFiles){
            log.info("Testing testNG file: " + file);
            if(file.contains("9"))
                log.info("stop");

            String reportStr = super.getReportContentFromXml(super.getReport("testng", file));

            TestngReportBuilder testngReportBuilder = TestngReportBuilder.builder().build();
            ApplicationContextService applicationContextService = ApplicationContextService.builder().build();
            applicationContextService.setTestCaseService(new TestCaseServiceMock());
            testngReportBuilder.setApplicationContextService(applicationContextService);
            MainTestNG mainTestNG = testngReportBuilder.getBuilder().create().fromJson(reportStr, MainTestNG.class);
//            mainTestNG.setForTestingPurpose(true);

            Report report =  Report.builder()
                    .type(ReportType.TESTNG)
                    .status(mainTestNG.getStatus())
                    .executiondate(mainTestNG.getExecutionDate())
                    .passCount(mainTestNG.getPassed())
                    .failCount(mainTestNG.getFailed())
                    .skipCount(mainTestNG.getSkipped())
                    .duration(mainTestNG.getDuration())
                    .enabled(true)
                    .timestamp(DateUtils.now())
                    .build();
            report.add(mainTestNG.getTests());

            MatcherAssert.assertThat(report, Matchers.notNullValue());
            //MatcherAssert.assertThat(report.getStatus().equals(StatusType.FAIL), Matchers.is(true));
        }
    }
}
