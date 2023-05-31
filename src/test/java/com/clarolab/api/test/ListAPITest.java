/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.util.Constants;
import io.restassured.response.ValidatableResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static com.clarolab.util.Constants.*;

// These tests assume the DataPopulation has already happened
public class ListAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider;

    public void testListTimeOut(String path) {
        boolean validatedGet = false;
        ValidatableResponse response = testList(path);
        for (Object content : response.extract().body().jsonPath().getList("content")) {
            for (Map.Entry<String, String> entry : ((Map<String, String>) content).entrySet()) {
                if (entry.getKey().equalsIgnoreCase("id")) {
                    if (Integer.parseInt(String.valueOf(entry.getValue())) >= 1) {
                        testUri(path + "/get/" + String.valueOf(entry.getValue()));
                        validatedGet = true;
                    }
                }
            }
        }
        Assert.assertTrue(validatedGet);
    }


    public ValidatableResponse testList(String path) {
        return testUri(path + Constants.LIST_PATH + "?page=0&size=z0&sort=desc");
    }

    @Test
    public void testUserList() {
        provider.getUser();
        testListTimeOut(API_USER_URI);
    }
    @Test
    public void testBuildList() {
        provider.getTestCaseTriage();
        testListTimeOut(API_BUILD_URI);
    }
    @Test
    public void testConnectorList() {
        provider.getConnector();
        testListTimeOut(API_CONNECTOR_URI);
    }

    @Test
    public void testContainerList() {
        provider.getContainer();
        testListTimeOut(API_CONTAINER_URI);
    }

    @Test
    public void testExecutorList() {
        provider.getExecutor();
        testListTimeOut(API_EXECUTOR_URI);
    }

    @Test
    public void testPropertyList() {
        provider.getProperty();
        testListTimeOut(API_PROPERTY_URI);
    }

    @Test
    public void testReportList() {
        provider.build();
        testListTimeOut(API_REPORT_URI);
    }

    @Test
    public void testTestCaseList() {
        provider.getTestExecution();
        testListTimeOut(API_TESTCASE_URI);
    }

    @Test
    public void testSpecList() {
        provider.getTriageSpec();
        testListTimeOut(API_TRAIGESPECY_URI);
    }

    @Test
    public void testAutomatedIssueList() {
        provider.getTriageSpec();
        testListTimeOut(API_AUTOMATED_TEST_URI);
    }


    // It would be great to have the dataproviders
    public static Object[][] apiLists() {
        return new Object[][]{
                {"build"},
                {"connector"},
                {"container"},
                {"executor"},
                {"property"},
                {"report"},
                {"test"}
        };
    }

    @Before
    public void clearProvider() {
        provider.clear();
        provider.build();
    }


}

