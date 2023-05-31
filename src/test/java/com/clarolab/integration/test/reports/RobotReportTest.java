/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.integration.test.reports;

import com.clarolab.connectors.impl.utils.report.builder.RobotReportBuilder;
import com.clarolab.connectors.impl.utils.report.robot.json.entities.MainRobot;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.Report;
import com.clarolab.model.types.ReportType;
import com.clarolab.util.DateUtils;
import lombok.extern.java.Log;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

@Log
public class RobotReportTest extends BaseReportTest {

    @Test
    public void reportRobotTest(){
        for(String file: robotFiles){
            log.info("Testing Robot file: " + file);

            String reportStr = super.getReportContentFromXml(super.getReport("robot", file));

            MainRobot mainRobot = RobotReportBuilder.builder().build().getBuilder().create().fromJson(reportStr, MainRobot.class);

            ApplicationContextService context = ApplicationContextService.builder().build();
            context.setTestCaseService(new TestCaseServiceMock());
            mainRobot.setContext(context);

            Report report =  Report.builder()
                    .type(ReportType.ROBOT)
                    .status(mainRobot.getStatus())
                    .executiondate(mainRobot.getExecutedDate())
                    .passCount(mainRobot.getPassCount())
                    .failCount(mainRobot.getFailCount())
                    .skipCount(mainRobot.getSkipCount())
                    .duration(mainRobot.getDuration())
                    .enabled(true)
                    .timestamp(DateUtils.now())
                    .build();
            report.add(mainRobot.getTests());

            MatcherAssert.assertThat(report, Matchers.notNullValue());
//            MatcherAssert.assertThat(report.getStatus().equals(StatusType.FAIL), Matchers.is(true));
        }
    }
}
