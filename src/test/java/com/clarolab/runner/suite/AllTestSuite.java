/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.runner.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@Deprecated
@RunWith(Suite.class)
@Suite.SuiteClasses({
        RegressionSuite.class
})
public class AllTestSuite {
}
