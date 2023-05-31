/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.integration.test.reports;

import com.clarolab.connectors.impl.utils.report.allure.json.entity.MainAllure;
import com.clarolab.model.Report;
import com.clarolab.model.types.ReportType;
import com.clarolab.util.DateUtils;
import lombok.extern.java.Log;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

@Log
public class AllureReportTest extends BaseReportTest {

    @Test
    public void reportAllureTest(){

        for(String file: allureFiles){
            log.info("Testing allure file: " + file);
            String reportStr = super.getReportContentFromJson(super.getReport("allure", file));

            MainAllure mainAllure = MainAllure.builder().singleTest(reportStr).isForDebug(true).build();

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
        }
    }
}
