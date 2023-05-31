/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test.build;

import com.clarolab.api.BaseAPITest;
import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.BuildDTO;
import com.clarolab.dto.push.DataDTO;
import com.clarolab.model.Build;
import com.clarolab.model.types.StatusType;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.util.Constants;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.clarolab.util.Constants.*;
import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;

public abstract class BuildAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider;

    @Before
    public void clearProvider() {
        provider.clear();
    }


    // Abstract tests to be implemented under different roles

    @Test
    public abstract void testDeleteBuild();


    // Non-abstract tests to run under different roles

    @Test
    public void testPushBuild() {
        DataDTO dataDTO = provider.getDataDTO(StatusType.PASS);

        given()
                .contentType(ContentType.JSON)
                .body(dataDTO)
                .post(API_BUILD_URI + PUSH_PATH)
                .then().statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void testBuildListTimeOut() {
        //GET
        given()
                .get(API_BUILD_URI + Constants.LIST_PATH)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .time(lessThan(longTimeOut));
    }

    @Test
    public void testBuildPresentInList() {
        Build as = provider.getBuild(1);

        List<BuildDTO> response = requestBuildListSuccess();

        Assert.assertTrue(response.stream().anyMatch(b -> b.getId().compareTo(as.getId()) == 0));
    }

    @Test
    public void testBuildDeleteNotInList() {
        //Create via Service
        Build as = provider.getBuild(1);

        //DELETE
        deleteThruService(as);

        List<BuildDTO> response = requestBuildListSuccess();

        Assert.assertTrue(response.stream().anyMatch(b -> b.getId().compareTo(as.getId()) != 0));
    }

    @Test
    public void testBuildListNotEmpty() {
        //Create via Service
        Build as = provider.getBuild(1);
        provider.clearForNewBuild();
        provider.getBuild(2);

        //GET
        given()
                .get(API_BUILD_URI + Constants.LIST_PATH)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("content", not(empty()));
    }

    @Test
    public void testBuildGetOne() {
        Build build = provider.getBuild(1);

        //GET
        BuildDTO result = expect()
                .statusCode(HttpStatus.SC_OK)
                .when()
                .get(API_BUILD_URI + GET + "/" + build.getId()).as(BuildDTO.class);

        Assert.assertEquals(result.getId(), build.getId());
        assertNotNull(result);
    }

    @Test
    public void testBuildDeleteNotFound() {
        //Create via Service
        Build as = provider.getBuild(1);

        //Delete via Service since I cannot delete ir via API
        deleteThruService(as);

        //GET
        ErrorInfo result = expect()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .when()
                .get(API_BUILD_URI + GET + "/" + as.getId()).as(ErrorInfo.class);

        Assert.assertEquals(result.getCode(), HttpStatus.SC_NOT_FOUND);
        Assert.assertEquals(result.getError(), "Not Found");

    }


    // Step methods

    protected Response stepsDeleteBuild() {
        Build as = provider.getBuild(1);
        return given()
                .delete(API_BUILD_URI + DELETE + "/" + as.getId());
    }


    // Other methods

    private List<BuildDTO> requestBuildListSuccess() {
        return given()
                .get(API_BUILD_URI + Constants.LIST_PATH)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().jsonPath().getList("content", BuildDTO.class);
    }

}
