/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.runner.suite;

import com.clarolab.api.suite.SuiteRegressionAPITest;
import com.clarolab.functional.suite.SuiteRegressionFunctionalTest;
import com.clarolab.integration.suite.SuiteRegressionIntegrationTest;
import com.clarolab.unit.suite.SuiteRegressionUnitTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        SuiteRegressionAPITest.class,
        SuiteRegressionFunctionalTest.class,
        SuiteRegressionIntegrationTest.class,
        SuiteRegressionUnitTest.class})
public class RegressionSuite {
}
