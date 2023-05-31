/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.junit.json.entity;

import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.TestExecution;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.DateUtils;
import com.clarolab.util.LogicalCondition;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Log
@Data
public class MainJunit extends JunitBase {

    //
    //https://maven.apache.org/surefire/maven-surefire-plugin/xsd/surefire-test-report-3.0.xsd
    //versions up to 2.22.0:: https://maven.apache.org/surefire/maven-surefire-plugin/xsd/surefire-test-report.xsd
    private String version;
    private int total;
    private int errors;
    private int skipped;
    private int failures;
    private List<JunitTestCase> testCase;

    @Builder
    private MainJunit(String name, long time, ApplicationContextService context, String group, String version, int total, int errors, int skipped, int failures, List<JunitTestCase> testCase){
        super(name, time, group);
        this.version = version;
        this.total = total;
        this.errors = errors;
        this.skipped = skipped;
        this.failures = failures;
        this.context = context;
        this.testCase = CollectionUtils.isNotEmpty(testCase) ? testCase : Lists.newArrayList();
    }

    public int getPassed(){
        return this.total - this.errors - this.skipped - this.failures;
    }

    public StatusType getStatus(){
        if(LogicalCondition.OR(failures > 0, errors > 0)) return StatusType.FAIL;
        if(skipped > 0) return StatusType.SKIP;
        if(LogicalCondition.AND(failures == 0, errors == 0, skipped == 0, this.getPassed() > 0)) return StatusType.PASS;
        return StatusType.UNKNOWN;
    }

    public int getFailed(){
        return this.failures + this.errors;
    }

    public List<TestExecution> getTests(){
        List<TestExecution> tests = Lists.newArrayList();
        testCase.forEach(test -> {
            TestExecution execution = TestExecution.builder()
                    .testCase(getTestCase(test.getName(), test.getClassName()))
                    .duration(test.getTime())
                    .status(test.getStatus())
                    .suiteName(this.getName())
                    .errorDetails(test.getError())
                    .errorStackTrace(test.getErrorDetail())
                    .enabled(true)
                    .timestamp(DateUtils.now())
                    .build();
            tests.add(execution);
        });
        return tests;
    }
}
