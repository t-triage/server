/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.dto.ManualTestPlanDTO;
import com.clarolab.mapper.impl.UserMapper;
import com.clarolab.populate.DataProvider;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.util.Constants;
import com.clarolab.util.DateUtils;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.util.Constants.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.lessThan;

public class ManualTestPlanAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private UserMapper userMapper;


    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void testCreateManualTestPlan() {

        ManualTestPlanDTO manualTestPlanDTO = new ManualTestPlanDTO();

        manualTestPlanDTO.setName("Name");
        manualTestPlanDTO.setDescription("Description");
        manualTestPlanDTO.setEnvironment("Environment");
        manualTestPlanDTO.setFromDate(DateUtils.now());
        manualTestPlanDTO.setToDate(DataProvider.getTimeAdd(provider.getCreationDate(), 5));
        manualTestPlanDTO.setAssignee(userMapper.convertToDTO(provider.getUser()));
        manualTestPlanDTO.setStatus("PENDING");


        ManualTestPlanDTO answerDTO = given()
                .body(manualTestPlanDTO)
                .when()
                .contentType(ContentType.JSON)
                .post(API_MANUAL_PLAN_URI + Constants.CREATE_PATH)
                .then().statusCode(HttpStatus.SC_CREATED)
                .extract().as(ManualTestPlanDTO.class);

        Assert.assertNotNull(answerDTO);
        Assert.assertEquals(manualTestPlanDTO.getName(), answerDTO.getName());
        Assert.assertEquals(manualTestPlanDTO.getAssignee(), answerDTO.getAssignee());
    }

    @Test
    public void getOngoingManualTestPlans() {

        // creates data to retrieve
        int amount = 1;
        for (int i = 0; i < amount; i++) {
            provider.getManualTestExecution();
            provider.setManualTestExecution(null);
        }

        String path = API_MANUAL_PLAN_URI + API_MANUAL_PLAN;

        given()
                .get(path)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .time(lessThan(longTimeOut));

    }

}
