/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.dto.ManualTestExecutionDTO;
import com.clarolab.mapper.impl.ManualTestExecutionMapper;
import com.clarolab.mapper.impl.UserMapper;
import com.clarolab.model.User;
import com.clarolab.model.manual.ManualTestExecution;
import com.clarolab.model.manual.service.ManualTestExecutionService;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.UserService;
import com.clarolab.util.Constants;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.util.Constants.API_MANUAL_EXECUTION_URI;
import static io.restassured.RestAssured.given;

public class ManualTestExecutionAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private ManualTestExecutionMapper manualTestExecutionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ManualTestExecutionService manualTestExecutionService;

    @Autowired
    private UserService userService;


    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void testCreateManualTestExecution() {
        User user = provider.getUser();

        ManualTestExecution manualTestExecution = new ManualTestExecution();

        manualTestExecution.setAssignee(user);

        ManualTestExecutionDTO manualTestExecutionDTO = manualTestExecutionMapper.convertToDTO(manualTestExecution);

        ManualTestExecutionDTO answerDTO = given()
                .body(manualTestExecutionDTO)
                .when()
                .contentType(ContentType.JSON)
                .post(API_MANUAL_EXECUTION_URI + Constants.CREATE_PATH)
                .then().statusCode(HttpStatus.SC_CREATED)
                .extract().as(ManualTestExecutionDTO.class);

        Assert.assertNotNull(answerDTO);
    }

    @Test
    public void testEditManualTestExecution() {
        User originalUser = provider.getUser();
        provider.setUser(null);

        ManualTestExecution manualTestExecution = new ManualTestExecution();

        manualTestExecution.setAssignee(originalUser);

        ManualTestExecutionDTO manualTestExecutionDTO = manualTestExecutionMapper.convertToDTO(manualTestExecution);

        ManualTestExecutionDTO answerDTO = given()
                .body(manualTestExecutionDTO)
                .when()
                .contentType(ContentType.JSON)
                .put(API_MANUAL_EXECUTION_URI + Constants.UPDATE_PATH)
                .then().statusCode(HttpStatus.SC_OK)
                .extract().as(ManualTestExecutionDTO.class);

        Assert.assertNotNull(answerDTO);

    }

}

