/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.integration.test.reports;

import com.clarolab.connectors.impl.utils.report.builder.CucumberReportBuilder;
import com.clarolab.connectors.impl.utils.report.cucumber.json.entity.MainCucumber;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Report;
import com.clarolab.model.types.ReportType;
import com.clarolab.util.DateUtils;
import lombok.extern.java.Log;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

@Log
public class CucumberReportTest extends BaseReportTest {

    @Test
    public void reportCucumberTest(){

        for(String file: cucumberFiles){
            log.info("Testing cucumber file: " + file);
            String reportStr = super.getReportContentFromJson(super.getReport("cucumber", file));

            MainCucumber mainCucumber = CucumberReportBuilder.builder().build().getBuilder().create().fromJson(reportStr, MainCucumber.class);

            ApplicationContextService context = ApplicationContextService.builder().build();
            context.setTestCaseService(new TestCaseServiceMock());
            mainCucumber.setContextService(context);

            Report report =  Report.builder()
                    .type(ReportType.CUCUMBER)
                    .status(mainCucumber.getStatus())
                    .executiondate(0L)
                    .passCount(mainCucumber.getPassed())
                    .failCount(mainCucumber.getFailed())
                    .skipCount(mainCucumber.getSkipped())
                    .duration(mainCucumber.getDuration())
                    .enabled(true)
                    .timestamp(DateUtils.now())
                    .build();
            report.add(mainCucumber.getTests());

            MatcherAssert.assertThat(report, Matchers.notNullValue());
            //MatcherAssert.assertThat(report.getStatus().equals(StatusType.FAIL), Matchers.is(true));
        }
    }
}
