package com.clarolab.functional.suite;

import com.clarolab.functional.test.BuildTriageFunctionalTest;
import com.clarolab.functional.test.TestCaseFunctionalTest;
import com.clarolab.functional.test.TestDetailFunctionalTest;
import com.clarolab.functional.test.rules.DisabledEngineRuleFunctionalTest;
import com.clarolab.functional.test.rules.FirstTriageRuleFunctionalTest;
import com.clarolab.functional.test.rules.ThreeTierRuleFunctionalTest;
import com.clarolab.functional.test.rules.TriageRuleFunctionalTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DisabledEngineRuleFunctionalTest.class,
        FirstTriageRuleFunctionalTest.class,
        ThreeTierRuleFunctionalTest.class,
        TriageRuleFunctionalTest.class,
        BuildTriageFunctionalTest.class,
        TestCaseFunctionalTest.class,
        TestDetailFunctionalTest.class,
        TriageRuleFunctionalTest.class,

})
public class TriageFunctionalSuite {
}
