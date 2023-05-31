/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.suite;

import com.clarolab.functional.test.search.UserSearchFunctionalTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        UserSearchFunctionalTest.class,
})
public class SearchTestSuite {
}
