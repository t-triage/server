/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.dto.TestTriageDTO;
import com.clarolab.dto.UpdateTriageDTO;
import com.clarolab.mapper.impl.TestTriageMapper;
import com.clarolab.model.TestTriage;
import com.clarolab.model.types.ApplicationFailType;
import com.clarolab.model.types.TestFailType;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.TestTriageService;
import io.restassured.http.ContentType;
import lombok.extern.java.Log;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.util.Constants.*;
import static io.restassured.RestAssured.given;

@Log
public class TriageTestAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider = new UseCaseDataProvider();

    @Autowired
    private TestTriageMapper testTriageMapper;

    @Autowired
    private TestTriageService testTriageService;


    @Test
    public void testBasic() {
        TestTriage triage = provider.getTestCaseTriage();

        Assert.assertNotNull("Something wrong, the testTriage was not generated", triage);

        given()
                .queryParam("userid", provider.getUser().getId())
                .queryParam("testid", triage.getId())
                .post(API_ACTIONS_URI + ACTION_TRIAGED_TEST)
                .then().statusCode(HttpStatus.SC_OK)
                .extract().as(TestTriageDTO.class);

    }

    @Test
    public void testUpdate() {
        UpdateTriageDTO updateTriageDTO = new UpdateTriageDTO();
        provider.getTestExecutionFail();
        TestTriage triage = provider.getTestCaseTriage();

        Assert.assertEquals(TestFailType.UNDEFINED, triage.getTestFailType());
        Assert.assertEquals(ApplicationFailType.UNDEFINED, triage.getApplicationFailType());

        triage.setTestFailType(TestFailType.EXTERNAL_CAUSE);
        triage.setApplicationFailType(ApplicationFailType.EXTERNAL_CAUSE);
        updateTriageDTO.setTestTriageDTO(testTriageMapper.convertToDTO(triage));

        UpdateTriageDTO dtoReturned = given()
                .body(updateTriageDTO)
                .when()
                .contentType(ContentType.JSON)
                .post(TRIAGE_PATH + ACTION_DRAFT_TEST)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(UpdateTriageDTO.class);

        TestTriage dbTriage = testTriageService.find(triage.getId());

        Assert.assertNotNull(dbTriage);
        Assert.assertEquals(TestFailType.EXTERNAL_CAUSE, triage.getTestFailType());
        Assert.assertEquals(ApplicationFailType.EXTERNAL_CAUSE, triage.getApplicationFailType());
    }

    @Test
    public void pin() {
        TestTriage triage = provider.getTestCaseTriage();

        given()
                .queryParam("testid", triage.getId())
                .post(API_TESTCASEEPORT_URI + PIN)
                .then().statusCode(HttpStatus.SC_OK)
                .extract().as(TestTriageDTO.class);

    }

    @Before
    public void clearProvider() {
        provider.clear();
    }

}
