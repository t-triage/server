/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.runner.suite;

import com.clarolab.api.suite.SuiteSmokeAPITest;
import com.clarolab.functional.suite.SuiteSmokeFunctionalTest;
import com.clarolab.unit.suite.SuiteSmokeUnitTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        SuiteSmokeAPITest.class,
        SuiteSmokeFunctionalTest.class,
        SuiteSmokeUnitTest.class
})
public class SmokeSuite {
}
