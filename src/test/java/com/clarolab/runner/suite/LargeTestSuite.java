/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.runner.suite;

import com.clarolab.api.suite.SuiteLargeAPITest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        SuiteLargeAPITest.class})
public class LargeTestSuite {
}
