package com.clarolab.api.test.issues.issueticket;

import org.apache.http.HttpStatus;
import org.junit.Before;

public class UserRoleIssueTicketAPITest extends IssueTicketAPITest {

    @Before
    public void setUp() {
        regularUserSetUp();
    }

    @Override
    public void testDeleteIssueTicket() {
        stepsDeleteIssueTicket().then().statusCode(HttpStatus.SC_FORBIDDEN);
    }

}
