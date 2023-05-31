/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.api.util.RestPageImpl;
import com.clarolab.dto.AutomatedTestCaseDTO;
import com.clarolab.dto.TestTriageDTO;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.util.Constants;
import com.jayway.jsonpath.TypeRef;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.clarolab.util.Constants.API_AUTOMATED_TEST_CASE_URI;
import static io.restassured.RestAssured.given;

public class AutomatedTestCaseAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider;

    @Before
    public void clearProvider() {
        provider.clear();
    }


    @Test
    public void testTestCaseListTimeOut() {
        provider.getTestCaseTriage();
        //GET

        TypeRef<RestPageImpl<AutomatedTestCaseDTO>> responseType = new TypeRef<RestPageImpl<AutomatedTestCaseDTO>>() {
        };

        RestPageImpl<AutomatedTestCaseDTO> tests = given()
                .get(API_AUTOMATED_TEST_CASE_URI + Constants.LIST_PATH)
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

}
