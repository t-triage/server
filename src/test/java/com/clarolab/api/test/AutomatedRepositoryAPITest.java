/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.api.util.RestPageImpl;
import com.clarolab.dto.AutomatedTestCaseDTO;
import com.clarolab.dto.FilterDTO;
import com.clarolab.dto.TestTriageDTO;
import com.clarolab.populate.UseCaseDataProvider;
import com.jayway.jsonpath.TypeRef;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.clarolab.util.Constants.*;
import static io.restassured.RestAssured.given;

public class AutomatedRepositoryAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider;

    @Before
    public void clearProvider() {
        provider.clear();
    }


    @Test
    public void testTestCaseListShort() {
        String baseName = "testTestCaseListShort";
        provider.setName(baseName);
        provider.getTestCaseTriage();
        FilterDTO filter = newFilter();
        filter.setName(baseName);

        TypeRef<RestPageImpl<AutomatedTestCaseDTO>> responseType = new TypeRef<RestPageImpl<AutomatedTestCaseDTO>>() {
        };

        RestPageImpl<AutomatedTestCaseDTO> tests = given()
                .queryParam("filter", filter)
                .get(API_AUTOMATED_TEST_CASE_URI + LIST_PATH + FILTERS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().as(responseType.getType());


        Assert.assertNotNull(tests.getContent());
        Assert.assertFalse(tests.isEmpty());
        List<AutomatedTestCaseDTO> answer = tests.getContent();

        Assert.assertTrue(answer.size() >= 1);

        Optional<AutomatedTestCaseDTO> testOptional = answer.stream().filter(automatedTestCaseDTO -> provider.getTestCase().getName().equals(automatedTestCaseDTO.getName())).findFirst();

        Assert.assertTrue(testOptional.isPresent());

        Assert.assertTrue(testOptional.get().getTestTriageDTOList().size() > 0);

        TestTriageDTO testTriageDTO = testOptional.get().getTestTriageDTOList().get(0);

        Assert.assertEquals(provider.getTestCase().getName(), testTriageDTO.getTestExecution().getName());
    }

    @Test
    public void onePage() {
        String baseName = "onePage";
        int amount = 5;

        provider.setName(baseName);
        for (int i = 0; i < amount; i++) {
            provider.getTestCaseTriage();
            provider.setTestExecution(null);
        }

        FilterDTO filter = newFilter();
        filter.setName(baseName);

        int pageSize = 100;
        int pagePage = 0;
        String pageSort = "UNSORTED";

        TypeRef<RestPageImpl<AutomatedTestCaseDTO>> responseType = new TypeRef<RestPageImpl<AutomatedTestCaseDTO>>() {
        };
        RestPageImpl<AutomatedTestCaseDTO> tests = given()
                .queryParam("filter", filter)
                .queryParam("size", pageSize)
                .queryParam("page", pagePage)
                .queryParam("sort", pageSort)
                .get(API_AUTOMATED_TEST_CASE_URI + LIST_PATH + FILTERS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().as(responseType.getType());


        Assert.assertNotNull(tests.getContent());
        Assert.assertFalse(tests.isEmpty());

        List<AutomatedTestCaseDTO> answer = tests.getContent();

        Assert.assertEquals(amount, answer.size());
    }


    @Test
    public void secondPage() {
        String baseName = "secondPage";
        int amount = 50;

        provider.setName(baseName);
        for (int i = 0; i < amount; i++) {
            provider.getTestCaseTriage();
            provider.setTestExecution(null);
        }

        FilterDTO filter = newFilter();
        filter.setName(baseName);

        int pageSize = 10;
        int pagePage = 1;
        String pageSort = "UNSORTED";

        TypeRef<RestPageImpl<AutomatedTestCaseDTO>> responseType = new TypeRef<RestPageImpl<AutomatedTestCaseDTO>>() {
        };
        RestPageImpl<AutomatedTestCaseDTO> tests = given()
                .queryParam("filter", filter)
                .queryParam("size", pageSize)
                .queryParam("page", pagePage)
                .queryParam("sort", pageSort)
                .get(API_AUTOMATED_TEST_CASE_URI + LIST_PATH + FILTERS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().as(responseType.getType());


        Assert.assertNotNull(tests.getContent());
        Assert.assertFalse(tests.isEmpty());

        List<AutomatedTestCaseDTO> answer = tests.getContent();

        Assert.assertEquals(pageSize, answer.size());
    }


    private FilterDTO newFilter() {
        FilterDTO filter = new FilterDTO();
        filter.setHideNoSuite(false);
        filter.setHideDisabled(false);
        filter.setCurrentState("all");

        return filter;
    }

}
