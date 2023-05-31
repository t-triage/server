package com.clarolab.api.test.issues.issueticket;

import com.clarolab.api.BaseAPITest;
import com.clarolab.dto.IssueTicketDTO;
import com.clarolab.model.User;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.UserService;
import com.clarolab.serviceDTO.IssueTicketServiceDTO;
import com.clarolab.util.Constants;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.java.Log;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.util.Constants.API_ISSUETICKET_URI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@Log
public abstract class IssueTicketAPITest extends BaseAPITest {

    @Autowired
    private IssueTicketServiceDTO issueTicketService;

    @Autowired
    private UserService userService;

    @Autowired
    private UseCaseDataProvider provider = new UseCaseDataProvider();

    @Before
    public void clearProvider() {
        provider.clear();
    }


    // Abstract tests definitions

    @Test
    public abstract void testDeleteIssueTicket();


    // Non-abstract tests

    @Test
    public void testCreateIssueTicketOnlyWithUrl() {
        IssueTicketDTO issueTicketDTO = provider.getIssueTicketDTOOnlyWithUrl();
        Response response = stepsCreateIssueTicket(issueTicketDTO);
        validateCreatedIssueTicket(response, issueTicketDTO);
    }

    @Test
    public void testCreateWithAllFields() {
        IssueTicketDTO issueTicketDTO = provider.getIssueTicketDTO();
        Response response = stepsCreateIssueTicket(issueTicketDTO);
        validateCreatedIssueTicket(response, issueTicketDTO);
    }

    @Test
    public void testListIssueTickets() {
        provider.getIssueTicket();
        given()
                .get(API_ISSUETICKET_URI + Constants.LIST_PATH)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("content", not(empty()));
    }

    @Test
    public void testUpdateIssueTicket() {
        // Creates a new issue ticket and saves it throw IssueTicketService
        IssueTicketDTO issueTicketDTO = provider.getIssueTicketDTO();
        IssueTicketDTO createdIssueTicketDTO = issueTicketService.save(issueTicketDTO);

        // New string to update description of previously saved issue ticket
        String newDescription = RandomStringUtils.randomNumeric(100);
        createdIssueTicketDTO.setDescription(newDescription);

        // Makes a request to update the issue ticket
        IssueTicketDTO updatedIssueTicket = given()
                .when()
                .contentType(ContentType.JSON)
                .body(createdIssueTicketDTO)
                .put(API_ISSUETICKET_URI + Constants.UPDATE_PATH)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("description", equalTo(newDescription))
                .extract().as(IssueTicketDTO.class);

        // Gets and validates issue ticket
        given()
                .get(API_ISSUETICKET_URI + Constants.GET + "/" + issueTicketService.findEntity(updatedIssueTicket).getId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("description", equalTo(newDescription));
    }

    @Test
    public void testGetIssueTicket() {
        IssueTicketDTO issueTicketDTO = issueTicketService.save(provider.getIssueTicketDTO());

        given()
                .get(API_ISSUETICKET_URI + Constants.GET + "/" + issueTicketService.findEntity(issueTicketDTO).getId())
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testListUserIssueTickets() {
        User user = userService.findByUsername(username);

        given()
                .get(API_ISSUETICKET_URI + Constants.USER + "/" + user.getId() + Constants.LIST_PATH)
                .then().statusCode(HttpStatus.SC_OK);
    }


    // Step methods

    protected Response stepsCreateIssueTicket(IssueTicketDTO issueTicketDTO) {
        return given()
                .body(issueTicketDTO)
                .when()
                .contentType(ContentType.JSON)
                .post(API_ISSUETICKET_URI + Constants.CREATE_PATH);
    }

    protected Response stepsDeleteIssueTicket() {
        IssueTicketDTO issueTicketDTO = issueTicketService.save(provider.getIssueTicketDTO());

        return given()
                .delete(API_ISSUETICKET_URI + Constants.DELETE + "/" + issueTicketService.findEntity(issueTicketDTO).getId());
    }


    // Other methods

    protected void validateCreatedIssueTicket(Response response, IssueTicketDTO expected) {
        response.then().statusCode(HttpStatus.SC_CREATED);

        IssueTicketDTO dtoReturned = response.as(IssueTicketDTO.class);

        Assert.assertNotNull(response);
        Assert.assertEquals(expected.getUrl(), dtoReturned.getUrl());
        Assert.assertEquals(expected.getSummary(), dtoReturned.getSummary());
    }

}
