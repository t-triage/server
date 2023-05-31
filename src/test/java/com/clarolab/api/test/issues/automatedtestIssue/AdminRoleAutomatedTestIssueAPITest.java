package com.clarolab.api.test.issues.automatedtestIssue;

import org.apache.http.HttpStatus;

public class AdminRoleAutomatedTestIssueAPITest extends AutomatedTestIssueAPITest {

    @Override
    public void testDeleteAutomatedTestIssue() {
        stepsDeleteAutomatedTestIssue()
                .then().statusCode(HttpStatus.SC_ACCEPTED);
    }

}
