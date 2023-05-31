/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.suite;

import com.clarolab.functional.test.manual.ManualTestCaseFunctionalTest;
import com.clarolab.functional.test.manual.ManualTestExecutionFunctionalTest;
import com.clarolab.functional.test.manual.ManualTestPlanFunctionalTest;
import com.clarolab.functional.test.manual.ProductComponentFunctionalTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ManualTestCaseFunctionalTest.class,
        ManualTestPlanFunctionalTest.class,
        ManualTestExecutionFunctionalTest.class,
        ProductComponentFunctionalTest.class
})
public class ManualFunctionalTestSuite {
}

