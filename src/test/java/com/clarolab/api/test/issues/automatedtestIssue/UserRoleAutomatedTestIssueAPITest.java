package com.clarolab.api.test.issues.automatedtestIssue;

import org.apache.http.HttpStatus;
import org.junit.Before;

public class UserRoleAutomatedTestIssueAPITest extends AutomatedTestIssueAPITest {

    @Before
    public void setUp() {
        regularUserSetUp();
    }

    @Override
    public void testDeleteAutomatedTestIssue() {
        stepsDeleteAutomatedTestIssue()
                .then().statusCode(HttpStatus.SC_FORBIDDEN);
    }

}
