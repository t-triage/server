package com.clarolab.functional.suite;

import com.clarolab.functional.test.*;
import com.clarolab.functional.test.model.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ApplicationDomainFunctionalTest.class,
        ApplicationEventFunctionalTest.class,
        BuildFunctionalTest.class,
        ConnectorFunctionalTest.class,
        ContainerFunctionalTest.class,
        ContainerProccessFunctionalTest.class,
        ExecutorFunctionalTest.class,
        NoteFunctionalTest.class,
        TriageAgentBuildFunctionalTest.class,


})
public class ModelTestFunctionalSuite {
}
