/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.suite;

import com.clarolab.functional.test.integration.RepositoryFunctionalTest;
import com.clarolab.functional.test.model.PipelineFunctionalTest;
import com.clarolab.functional.test.notifications.GuideFunctionalTest;
import com.clarolab.functional.test.notifications.NewsBoardFunctionalTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        FunctionalTestSuite.class,
        ManualFunctionalTestSuite.class,
        MapperTestSuite.class,
        ModelTestFunctionalSuite.class,
        NotificationTestSuite.class,
        RuleTestFunctionalSuite.class,
        SearchTestSuite.class,
        SuiteSmokeFunctionalTest.class,
        NewsBoardFunctionalTest.class,
        GuideFunctionalTest.class,
        RepositoryFunctionalTest.class,
        PipelineFunctionalTest.class
})
public class SuiteRegressionFunctionalTest {
}
