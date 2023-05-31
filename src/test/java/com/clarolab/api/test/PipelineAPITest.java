/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.model.Pipeline;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.PipelineService;
import io.restassured.internal.RestAssuredResponseImpl;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static com.clarolab.util.Constants.API_PIPELINE;
import static com.clarolab.util.Constants.ONGOING;
import static io.restassured.RestAssured.given;

public class PipelineAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private PipelineService pipelineService;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void list() {
        int amount = 3;
        List<Long> testCaseIds = new ArrayList<>(amount);
        provider.getContainer();
        Pipeline pipeline = provider.getPipeline();

        for (int i = 0; i < amount; i++) {
            provider.setTestExecution(null);
            testCaseIds.add(provider.getTestCase().getId());
        }

        pipelineService.assignToPipeline(pipeline, testCaseIds);

        // Call the service and basic checks
        ExtractableResponse<Response> answer = given()
                .queryParam("pipelineId", pipeline.getId())
                .get(API_PIPELINE + ONGOING + "List" ).then().statusCode(HttpStatus.SC_OK)
                .extract();

        Assert.assertNotNull(answer);

        JSONObject view = new JSONObject(((RestAssuredResponseImpl) answer).getBody().prettyPrint());

        Assert.assertEquals(amount, view.optInt("totalTests"));
    }

}
