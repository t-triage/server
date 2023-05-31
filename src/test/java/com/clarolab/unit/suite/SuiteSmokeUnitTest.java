/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.unit.suite;

import com.clarolab.unit.test.UserServiceUnitTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        UserServiceUnitTest.class
})
public class SuiteSmokeUnitTest {
}
