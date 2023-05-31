/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.robot.json.entities;

import com.clarolab.connectors.impl.AbstractTestCreator;
import com.clarolab.connectors.impl.utils.report.robot.json.entities.types.SuiteStatusResultType;
import com.clarolab.model.TestExecution;
import com.clarolab.util.DateUtils;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Data
@Builder
@Log
public class SuiteReport extends AbstractTestCreator {

    private SuiteReportItemMetadata metadata;
    private String name;
    private String doc;
    private String id;
    private String source;
    private SuiteStatusReport status;
    private List<SuiteReportKeywordData> kw;
    private List<SuiteReportTestCase> test;
    private List<SuiteReport> suite;

    public SuiteStatusResultType getExecutionStatus() {
        return status.getStatus();
    }

    public Long getDuration() {
        return DateUtils.convertDate(status.getEndtime()) - DateUtils.convertDate(status.getStarttime());
    }

    public boolean isTestCasesPresent() {
        return !CollectionUtils.isEmpty(test);
    }

    public List<TestExecution> getTestCases() {
        List<TestExecution> tests = Lists.newArrayList();
        test.forEach(test -> {
            TestExecution testExecution = TestExecution.builder()
                    .enabled(true)
                    .testCase(getTestCase(test.getName(), source))
                    .timestamp(DateUtils.now())
                    .suiteName(name)
                    .duration(test.getDuration())
                    .status(test.getStatus())
                    .errorDetails(test.getFailReason())
                    .errorStackTrace(test.getFailReasonDetail())
                    .hasSteps(CollectionUtils.isNotEmpty(test.getSteps()))
                    .build();
            testExecution.add(test.getSteps());
            tests.add(testExecution);
        });
        return tests;
    }

}
