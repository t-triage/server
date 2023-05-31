/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.suite;

import com.clarolab.api.connection.PushServiceAPITest;
import com.clarolab.api.test.*;
import com.clarolab.api.test.build.UserRoleBuildAPITest;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Categories.class)
@Suite.SuiteClasses({
        ApproveAutomaticTriageAPITest.class,
        AssigneePriorityToBuildAPITest.class,
        AssigneeToBuildAPITest.class,
        AssignTestCaseAPITest.class,
        AutomatedTestIssueAPITest.class,
        ContainerSuggestionAPITest.class,
        DisableBuildTriageAPITest.class,
        ExecutorStatAPITest.class,
        InternalUserAPITest.class,
        InvalidBuildTriageAPITest.class,
        ListAPITest.class,
        PushServiceAPITest.class,
        TestExecutionAPITest.class,
        TriageAgentAPITest.class,
        TriageBuildAPITest.class,
        TriageTestAPITest.class,
        WorkspacePageAPITest.class,
        com.clarolab.api.test.auth.SignupAPITests.class,
        com.clarolab.api.test.auth.AdminRoleLoginTests.class,
        com.clarolab.api.test.auth.UserRoleLoginTests.class,
        UserRoleBuildAPITest.class,
        com.clarolab.api.test.connector.AdminRoleConnectorsAPITests.class,
        com.clarolab.api.test.connector.UserRoleConnectorsAPITests.class,
        com.clarolab.api.test.export.AdminRoleExportAPITest.class,
        com.clarolab.api.test.export.UserRoleExportAPITest.class,
        com.clarolab.api.test.issues.automatedtestIssue.AdminRoleAutomatedTestIssueAPITest.class,
        com.clarolab.api.test.issues.automatedtestIssue.UserRoleAutomatedTestIssueAPITest.class,
        com.clarolab.api.test.issues.issueticket.AdminRoleIssueTicketAPITest.class,
        com.clarolab.api.test.issues.issueticket.UserRoleIssueTicketAPITest.class,
        com.clarolab.api.test.product.AdminRoleProductAPITest.class,
        com.clarolab.api.test.product.UserRoleProductAPITest.class,
        com.clarolab.api.test.user.AdminRoleUsersAPITests.class,
        com.clarolab.api.test.user.UserRoleUsersAPITests.class,
        com.clarolab.api.test.view.AdminRoleViewAPITests.class,
        com.clarolab.api.test.view.UserRoleViewAPITests.class,
        ManualTestCaseAPITest.class,
        ManualTestPlanAPITest.class,
        NewsBoardAPITest.class,
        OnboardAPITest.class,
        AutomatedTestCaseAPITest.class
})
public class SuiteRegressionAPITest {
}
