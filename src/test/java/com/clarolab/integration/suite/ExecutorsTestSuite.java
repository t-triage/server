/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.integration.suite;

import com.clarolab.integration.test.executors.ExecutorPersistenceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ExecutorPersistenceTest.class,
})
public class ExecutorsTestSuite {
}
