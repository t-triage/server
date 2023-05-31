/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.TestTriageDTO;
import com.clarolab.model.TestExecution;
import com.clarolab.populate.DataProvider;
import com.clarolab.service.TestExecutionService;
import com.clarolab.util.Constants;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.util.Constants.API_TESTCASE_URI;
import static com.clarolab.util.Constants.longTimeOut;
import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TestExecutionAPITest extends BaseAPITest {


    @Autowired
    private TestExecutionService testExecutionService;


    private TestExecution createATestCase(TestExecution testExecution) {
        TestExecution save = testExecutionService.save(testExecution);
        Assert.assertNotNull(save);
        return save;
        /*return given()
                .body(testExecution)
                .when()
                .contentType(ContentType.JSON)
                .post(API_TESTCASE_URI + Constants.CREATE_PATH).as(TestExecution.class);*/
    }

    private void delete(TestExecution testExecution) {
        testExecutionService.delete(testExecution.getId());
    }

    @Test
    public void testTestCaseListTimeOut() {
        //GET
        given()
                .get(API_TESTCASE_URI + Constants.LIST_PATH)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .time(lessThan(longTimeOut));
    }

    @Test
    public void testTestCaseCreate() {
        TestExecution testExecution = DataProvider.getTestCase();

        //Create via Service
        Assert.assertNotNull(createATestCase(testExecution));

    }


    @Ignore //TODO move this to another service layer tests
            //TODO user the service layer to update it -> Get It via API -> Validate
    public void testTestCaseUpdate() {
        TestExecution testExecution = DataProvider.getTestCase();

        //Create via Service
        TestExecution as = createATestCase(testExecution);

        Assert.assertEquals(as, testExecution);
        testExecution.setName("UPDATED");

        //PUT
        as = given()
                .body(testExecution)
                .when()
                .contentType(ContentType.JSON)
                .put(API_TESTCASE_URI + Constants.UPDATE_PATH).as(TestExecution.class);

        Assert.assertEquals(as.getName(), "UPDATED");
    }

    @Test
    public void testTestCaseDeleteNotInList() {
        TestExecution testExecution = DataProvider.getTestCase();

        //Create via Service
        TestExecution as = createATestCase(testExecution);

        //DELETE via Service
        delete(as);

        //GET
        given()
                .get(API_TESTCASE_URI + Constants.LIST_PATH)
                .then()
                .assertThat()
                .body("content", not(contains(as)));
    }



    @Test
    public void testTestCaseDelete() {
        TestExecution testExecution = DataProvider.getTestCase();

        //Create via Service
        TestExecution as = createATestCase(testExecution);

        //DELETE via Service
        delete(as);

        //GET
        ErrorInfo result = expect()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .when()
                .get(API_TESTCASE_URI + "/get/" + as.getId()).as(ErrorInfo.class);

        Assert.assertEquals(result.getCode(), HttpStatus.SC_NOT_FOUND);
        Assert.assertEquals(result.getError(), "Not Found");

    }

    @Test
    public void testTestCaseListNotEmpty() {
        //Create via Service
        createATestCase(DataProvider.getTestCase());
        createATestCase(DataProvider.getTestCase());

        //GET
        given()
                .get(API_TESTCASE_URI + Constants.LIST_PATH)
                .then()
                .assertThat()
                .body("content", not(empty()));

    }

    @Test
    public void testTestCaseGetOne() {
        TestExecution testExecution = DataProvider.getTestCase();

        //Create via Service
        TestExecution as = createATestCase(testExecution);

        //GET
        TestTriageDTO result = expect()
                        .statusCode(HttpStatus.SC_OK)
                        .when()
                        .get(API_TESTCASE_URI + "/get/" + as.getId()).as(TestTriageDTO.class);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getId(), as.getId());

    }
}
