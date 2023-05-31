/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.suite;

import com.clarolab.functional.test.pull.CircleFolderToTest;
import com.clarolab.functional.test.pull.JenkinsFolderToTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        JenkinsFolderToTest.class,
        CircleFolderToTest.class
})
public class PullTestSuite {
}
