package com.clarolab.functional.suite;

import com.clarolab.functional.test.rules.*;
import com.clarolab.functional.test.search.UserSearchFunctionalTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DisabledEngineRuleFunctionalTest.class,
        FirstTriageRuleFunctionalTest.class,
        ThreeTierRuleFunctionalTest.class,
        TriageRuleFunctionalTest.class,
        SameVersionRuleFunctionalTest.class,
})
public class RuleTestFunctionalSuite {
}
