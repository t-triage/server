/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.suite;

import com.clarolab.api.connection.PushServiceAPILargeTest;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Categories.class)
@Suite.SuiteClasses({
        PushServiceAPILargeTest.class
})
public class SuiteLargeAPITest {
}
