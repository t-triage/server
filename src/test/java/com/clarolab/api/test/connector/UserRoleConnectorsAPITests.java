package com.clarolab.api.test.connector;

import org.apache.http.HttpStatus;
import org.junit.Before;

import static com.clarolab.util.Constants.API_CONNECTOR_URI;
import static com.clarolab.util.Constants.DELETE;
import static com.clarolab.util.Constants.GET;

import static io.restassured.RestAssured.when;

public class UserRoleConnectorsAPITests extends ConnectorAPITest {

    @Before
    public void setUp() {
        regularUserSetUp();
    }

    @Override
    public void testList() {
        requestContainersList().then().statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Override
    public void testCreate() {
        createConnectorSteps().then().statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Override
    public void testUpdate() {
        updateContainerNameSteps("newName").then().statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Override
    public void testDelete() {
        when()
                .delete(API_CONNECTOR_URI + DELETE + "/1" )
                .then().statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Override
    public void testGet() {
        when()
                .get(API_CONNECTOR_URI + GET + "/" + provider.getConnector().getId())
                .then().statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Override
    public void testAContainer() {
        requestContainesOfAContainer().then().statusCode(HttpStatus.SC_FORBIDDEN);
    }
}
