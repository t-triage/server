/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.suite;

import com.clarolab.functional.ci.DuplicateClassSampleTest;
import com.clarolab.functional.ci.JunitSampleTest;
import com.clarolab.unit.test.DataProviderNamedTest;
import com.clarolab.unit.test.DataProviderWithoutNameTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        JunitSampleTest.class,
        DuplicateClassSampleTest.class,
        DataProviderWithoutNameTest.class,
        DataProviderNamedTest.class,
        DuplicateClassSampleTest.class
})
public class SuiteJenkinsTestingFunctionalTest {
}
