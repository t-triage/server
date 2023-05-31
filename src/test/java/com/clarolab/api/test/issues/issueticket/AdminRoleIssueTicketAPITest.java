package com.clarolab.api.test.issues.issueticket;

import org.apache.http.HttpStatus;

public class AdminRoleIssueTicketAPITest extends IssueTicketAPITest {

    @Override
    public void testDeleteIssueTicket() {
        stepsDeleteIssueTicket().then().statusCode(HttpStatus.SC_ACCEPTED);
    }

}
