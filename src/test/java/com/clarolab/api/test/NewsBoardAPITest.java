/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.dto.NewsBoardDTO;
import com.clarolab.populate.UseCaseDataProvider;
import com.jayway.jsonpath.TypeRef;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.clarolab.util.Constants.API_BOARD_URI;
import static com.clarolab.util.Constants.VIEW;
import static io.restassured.RestAssured.given;

public class NewsBoardAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void list() {
        provider.getNewsBoard("text");

        // Call the service and basic checks
        TypeRef<List<NewsBoardDTO>> responseType = new TypeRef<List<NewsBoardDTO>>() {
        };
        List<NewsBoardDTO> items = given()
                .get(API_BOARD_URI + VIEW).then().statusCode(HttpStatus.SC_OK)
                .extract().as(responseType.getType());

        Assert.assertNotNull(items);
        Assert.assertTrue(items.size() > 0);
    }

}
