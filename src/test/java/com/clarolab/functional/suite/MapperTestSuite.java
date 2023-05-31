/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.suite;

import com.clarolab.functional.test.mapper.impl.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AutomatedTestIssueMapperTest.class,
        BuildMapperTest.class,
        BuildTriageMapperTest.class,
        ConnectorMapperTest.class,
        ContainerMapperTest.class,
        ExecutorMapperTest.class,
        IssueTicketMapperTest.class,
        NoteMapperTest.class,
        ProductMapperTest.class,
        ReportMapperTest.class,
        TestCaseMapperTest.class,
        TestExecutionMapperTest.class,
        TestTriageMapperTest.class,
        TriageSpecMapperTest.class,
        UserMapperTest.class,
        UserPreferenceMapperTest.class,
        ManualTestCaseMapperTest.class,
        ManualTestPlanMapperTest.class,
        ManualTestStepMapperTest.class
})
public class MapperTestSuite {
}
