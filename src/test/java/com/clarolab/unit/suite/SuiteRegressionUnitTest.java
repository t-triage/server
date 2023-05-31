/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.unit.suite;

import com.clarolab.unit.test.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        BuilderServiceUnitTest.class,
        DataProviderNamedTest.class,
        DataProviderWithoutNameTest.class,
        DateUtilsUnitTest.class,
        StatusOptionGeneratorTest.class,
        StringUtilsUnitTest.class,
        UserServiceUnitTest.class,
        WeekFrequencyCalculationTest.class,
        ErrorLogUnitTest.class

})
public class SuiteRegressionUnitTest {
}
