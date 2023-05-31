/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.populate.UseCaseDataProvider;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.util.Constants.ACTION_APPROVE_JOB;
import static com.clarolab.util.Constants.API_ACTIONS_URI;
import static io.restassured.RestAssured.given;

public class ApproveAutomaticTriageAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider useCaseDataProvider = new UseCaseDataProvider();


    /*
      The test below makes a request to a non-implemented endpoint. This is the reason we expect a 404 status code
      and a ErrorInfo class match with. The expected should be a 200 and BuildDTO.class.
     */
    @Test
    public void testBasic() {
        given()
                .queryParam("userid", useCaseDataProvider.getUser().getId())
                .queryParam("buildid", useCaseDataProvider.getBuildTriage().getId())
                .post(API_ACTIONS_URI + ACTION_APPROVE_JOB)
                .then().statusCode(HttpStatus.SC_NOT_FOUND)
                .extract().as(ErrorInfo.class);
    }

    @Before
    public void clearProvider() {
        useCaseDataProvider.clear();
    }

}
