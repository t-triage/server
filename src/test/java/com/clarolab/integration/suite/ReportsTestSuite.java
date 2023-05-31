/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.integration.suite;

import com.clarolab.integration.test.reports.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        BaseReportTest.class,
        CucumberReportTest.class,
        JunitReportTest.class,
        RobotReportTest.class,
        TestngReportTest.class,
        CypressReportTest.class
})
public class ReportsTestSuite {
}
