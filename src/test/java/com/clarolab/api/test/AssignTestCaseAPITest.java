/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.dto.TestExecutionDTO;
import com.clarolab.model.TestTriage;
import com.clarolab.populate.UseCaseDataProvider;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.util.Constants.ACTION_ASSIGN_TEST;
import static com.clarolab.util.Constants.API_ACTIONS_URI;
import static io.restassured.RestAssured.given;

public class AssignTestCaseAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider = new UseCaseDataProvider();


    @Test
    public void testBasic() {
        TestTriage test = provider.getTestCaseTriage();
        given()
                .queryParam("userid", provider.getUser().getId())
                .queryParam("testid", test.getId())
                .post(API_ACTIONS_URI + ACTION_ASSIGN_TEST)
                .then().statusCode(HttpStatus.SC_OK)
                .extract().as(TestExecutionDTO.class);
    }

    @Before
    public void clearProvider() {
        provider.clear();
    }

}
