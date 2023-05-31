/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.api.util.FilterParam;
import com.clarolab.populate.UseCaseDataProvider;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.modelmapper.internal.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static com.clarolab.util.Constants.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.lessThan;

public class WorkspacePageAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider = new UseCaseDataProvider();

    @Test
    public void getSuites() {
        testUri(API_EXECUTOR_URI + LIST_PATH);
    }


    @Test
    public void getSomeSuite() {
        provider.getTestCaseTriage();
        String path = API_VIEW_EXECUTORS_URI + LIST_PATH;

        FilterParam filterParam = FilterParam.builder().build().builder()
                .containerId(provider.getContainer().getId()) // Set containerId, all other attributes are the default ones specified in FilterParam class
                .build();

        ValidatableResponse views = given()
                .queryParam("filter", filterParam.toExecutorViewIssueJsonString())
                .get(path)
                .then()
                .time(lessThan(longTimeOut))
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

        Map<String, String> contentView;

        for (Object content : views.extract().body().jsonPath().getList("content")) {
            Assert.notNull(content);
            contentView = (Map<String, String>) content;

            // We can validate several ExportView variables here
            Assert.notNull(contentView.get("deadlineTooltip"));
        }

    }

    @Test
    public void getSuggestedSuites() {
        provider.getTestCaseTriage();
        String path = API_VIEW_EXECUTORS_URI + SUGGESTED_URI;
        ValidatableResponse views = testUri(path);
    }

    @Test
    public void getContainers() {
        testUri(API_CONTAINER_URI + LIST_PATH);
    }

    @Test
    public void getUsers() {
        testUri(API_USER_URI + LIST_PATH);
    }

    @Before
    public void clearProvider() {
        provider.clear();
    }
}
