/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.suite;

import com.clarolab.functional.test.*;
import com.clarolab.functional.test.model.*;
import com.clarolab.functional.test.rules.DisabledEngineRuleFunctionalTest;
import com.clarolab.functional.test.rules.FirstTriageRuleFunctionalTest;
import com.clarolab.functional.test.rules.ThreeTierRuleFunctionalTest;
import com.clarolab.functional.test.rules.TriageRuleFunctionalTest;
import com.clarolab.service.ReleaseStatusService;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({

        AutomatedTestIssueFunctionalTest.class,
        BuildTriageFunctionalTest.class,
        DataProviderPersistenceFunctionalTest.class,
        ErrorLogsFunctionalTest.class,
        ExpireBuildEventFunctionalTest.class,
        HistoricDataFunctionalTest.class,
        ReleaseStatusFunctionalTest.class,
        SuggestedSuitesFunctionalTest.class,
        TestCaseFunctionalTest.class,
        TestDetailFunctionalTest.class,
        PropertyFunctionalTest.class,
        TrendGoalFunctionalTest.class

})
public class FunctionalTestSuite {
}

