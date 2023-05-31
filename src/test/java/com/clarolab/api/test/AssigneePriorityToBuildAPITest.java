/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.model.BuildTriage;
import com.clarolab.model.TriageSpec;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.TriageSpecService;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.util.Constants.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class AssigneePriorityToBuildAPITest extends BaseAPITest {

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
        BuildTriage originalTriage = provider.getBuildTriage();

        Response response = given()
                .queryParam("buildid", originalTriage.getId())
                .queryParam("priority", "P1")
                .post(API_VIEW_EXECUTORS_URI + PRIORITY + ASSIGN);

        response.then().statusCode(HttpStatus.SC_OK).body(equalTo("P1"));
    }

    @Test
    public void overwrite() {
        String priority = "P2";
        provider.getBuild(1);
        TriageSpec spec = provider.getTriageSpec();
        spec.setPriority(4);
        triageSpecService.update(spec);

        BuildTriage originalTriage = provider.getBuildTriage();

        Response response = given()
                .queryParam("priority", priority)
                .queryParam("buildid", originalTriage.getId())
                .post(API_VIEW_EXECUTORS_URI + PRIORITY + ASSIGN);

        response.then().statusCode(HttpStatus.SC_OK).body(equalTo(priority));
    }

}
