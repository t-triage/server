/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.suite;

import com.clarolab.api.test.*;
import com.clarolab.api.test.user.AdminRoleUsersAPITests;
import com.clarolab.api.test.user.UserRoleUsersAPITests;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Categories.class)
@Suite.SuiteClasses({
        ContainerSuggestionAPITest.class,
        TriageBuildAPITest.class,
        TriageTestAPITest.class,
        AdminRoleUsersAPITests.class,
        UserRoleUsersAPITests.class,
        InternalUserAPITest.class,
        ManualTestCaseAPITest.class,
})
public class SuiteSmokeAPITest {
}
