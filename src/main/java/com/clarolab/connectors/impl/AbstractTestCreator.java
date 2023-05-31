/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl;

import com.clarolab.QAReportApplication;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.TestCase;
import com.clarolab.service.TestCaseService;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.context.ApplicationContext;

import java.util.logging.Level;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Log
public abstract class AbstractTestCreator {

    protected ApplicationContextService context;

    public TestCase getTestCase(String name, String path) {
        return getTestCase(name, path, false);
    }

    public TestCase getTestCase(String name, String path, boolean isForDebug) {
        if(isForDebug){
            return createTestCaseBase(name, path);
        }

        TestCaseService testCaseService = null;
        if (context == null) {
            log.severe(StringUtils.getSystemError("Application Context can not be null"));
            throw new RuntimeException("Application Context can not be null");
        }

        ApplicationContext applicationContext = QAReportApplication.getApplicationContext();
        if (applicationContext != null) {
            testCaseService = applicationContext.getBean(TestCaseService.class);
        }

        if (testCaseService == null) {
            testCaseService = context.getTestCaseService();
        }

        TestCase testCase = createTestCase(name, path);

        return testCaseService.newOrFind(testCase);

    }

    public TestCase createTestCaseBase(String name, String path){
        long now = DateUtils.now();
        return TestCase.builder()
                .enabled(true)
                .timestamp(now)
                .updated(now)
                .name(name)
                .locationPath(path)
                .build();
    }

    public TestCase createTestCase(String name, String path){
        TestCase baseTestCase = createTestCaseBase(name, path);
        baseTestCase.setProduct(context.getProduct());
        return baseTestCase;
    }

    protected long getTime(String value) {
        long defaultValue = 0l;
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        String longString = value.replaceAll("\\.", "");
        try {
            return Long.parseLong(longString);
        } catch (NumberFormatException ex) {
            log.log(Level.WARNING, "Couldn't parse long: " + value);
            return defaultValue;
        }
    }

}
