/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.suite;

import com.clarolab.functional.test.*;
import com.clarolab.functional.test.model.ApplicationDomainFunctionalTest;
import com.clarolab.functional.test.model.ApplicationEventFunctionalTest;
import com.clarolab.functional.test.model.ContainerProccessFunctionalTest;
import com.clarolab.functional.test.model.TriageAgentBuildFunctionalTest;
import com.clarolab.functional.test.rules.FirstTriageRuleFunctionalTest;
import com.clarolab.functional.test.rules.ThreeTierRuleFunctionalTest;
import com.clarolab.functional.test.rules.TriageRuleFunctionalTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ApplicationDomainFunctionalTest.class,
        ApplicationEventFunctionalTest.class,
        AutomatedTestIssueFunctionalTest.class,
        BuildTriageFunctionalTest.class,
        DataProviderPersistenceFunctionalTest.class,
        TestCaseFunctionalTest.class,
        FirstTriageRuleFunctionalTest.class,
        TestDetailFunctionalTest.class,
        TriageRuleFunctionalTest.class,
        ContainerProccessFunctionalTest.class,
        TriageAgentBuildFunctionalTest.class,
        ThreeTierRuleFunctionalTest.class

})

public class SuiteSmokeFunctionalTest {
}
