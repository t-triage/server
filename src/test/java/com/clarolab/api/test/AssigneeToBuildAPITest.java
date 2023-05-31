/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.dto.BuildTriageDTO;
import com.clarolab.dto.UserDTO;
import com.clarolab.model.BuildTriage;
import com.clarolab.model.User;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.TriageSpecService;
import com.clarolab.util.Constants;
import com.jayway.jsonpath.TypeRef;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.clarolab.util.Constants.*;
import static io.restassured.RestAssured.given;

public class AssigneeToBuildAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private TriageSpecService triageSpecService;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void assign() {
        User user = provider.getUser();
        BuildTriage originalTriage = provider.getBuildTriage();

        given()
                .queryParam("userid", user.getId())
                .queryParam("buildid", originalTriage.getId())
                .post(API_ACTIONS_URI + ACTION_ASSIGN_JOB)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().as(BuildTriageDTO.class);
    }

    @Test
    public void suggestedExecutors() {
        String url = API_VIEW_EXECUTORS_URI + SUGGESTED_URI;
        // Call the service and basic checks
        testUri(url);
    }

    // The first position will be the logged user in the application
    // Then the suggested order is 3, 2, 1, 4
    @Test
    public void suggestedUsersSort() {
        String url = API_VIEW_EXECUTORS_URI + Constants.USER + SUGGESTED_URI;

        // User owning the container
        provider.clear();
        provider.setName("us1");
        User containerUser = provider.getUser();
        provider.getProduct();
        provider.getDeadline();
        provider.getExecutor();
        provider.getBuild(1);
        provider.getTestCaseTriage();

        // User used to own the executor
        provider.setUser(null);
        provider.setBuild(null);
        provider.setBuildTriage(null);
        provider.setName("us2");
        User executorOldUser = provider.getUser();
        provider.setNewTriageSpec();
        provider.getBuild(2);
        provider.getTestCaseTriage();
        provider.setBuild(null);
        provider.setBuildTriage(null);
        provider.getTestCaseTriage();

        // Current owner of the executor
        provider.setUser(null);
        provider.setBuild(null);
        provider.setBuildTriage(null);
        provider.setName("us3");
        User executorUser = provider.getUser();
        provider.getBuild(3);
        provider.setNewTriageSpec();
        provider.getTestCaseTriage();
        long buildTriageId = provider.getBuildTriage().getId();

        // User assigned to other executor same container
        provider.setUser(null);
        provider.setExecutor(null);
        provider.setBuild(null);
        provider.setTestExecution(null);
        provider.setBuildTriage(null);
        provider.setName("us4");
        User otherExecutorUser = provider.getUser();
        provider.getExecutor();
        provider.getBuild(1);
        provider.setNewTriageSpec();
        provider.getTestCaseTriage();


        // Other users should not appear
        provider.clear();
        provider.setName("us5");
        User otherContainerUser = provider.getUser();


        // Call the service and basic checks
        TypeRef<List<UserDTO>> responseType = new TypeRef<List<UserDTO>>() {
        };
        List<UserDTO> suggested = given()
                .queryParam("buildid", buildTriageId)
                .get(url).then().statusCode(HttpStatus.SC_OK)
                .extract().as(responseType.getType());
        Assert.assertNotNull(suggested);
        Assert.assertTrue(suggested.size() == 5);

        Assert.assertEquals(executorUser.getRealname(), suggested.get(1).getRealname());
        Assert.assertEquals(executorOldUser.getRealname(), suggested.get(2).getRealname());
        Assert.assertEquals(containerUser.getRealname(), suggested.get(3).getRealname());
        Assert.assertEquals(otherExecutorUser.getRealname(), suggested.get(4).getRealname());

    }

    // It will only suggest the logged user and assignee
    @Test
    public void suggestedMinimumUsers() {
        String url = API_VIEW_EXECUTORS_URI + Constants.USER + SUGGESTED_URI;

        // User owning the container
        provider.clear();
        provider.setName("um1");
        User containerUser = provider.getUser();
        provider.getProduct();
        provider.getDeadline();
        provider.getExecutor();
        provider.getBuild(1);
        provider.getTestCaseTriage();
        long buildTriageId = provider.getBuildTriage().getId();


        // Call the service and basic checks
        TypeRef<List<UserDTO>> responseType = new TypeRef<List<UserDTO>>() {
        };
        List<UserDTO> suggested = given()
                .queryParam("buildid", buildTriageId)
                .get(url).then().statusCode(HttpStatus.SC_OK)
                .extract().as(responseType.getType());

        Assert.assertTrue(suggested.size() == 2);

        Assert.assertEquals(containerUser.getRealname(), suggested.get(1).getRealname());
    }


}
