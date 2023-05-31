/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.dto.AutomatedTestIssueDTO;
import com.clarolab.dto.TestTriageDTO;
import com.clarolab.dto.UpdateTriageDTO;
import com.clarolab.mapper.impl.AutomatedTestIssueMapper;
import com.clarolab.mapper.impl.TestTriageMapper;
import com.clarolab.model.AutomatedTestIssue;
import com.clarolab.model.TestTriage;
import com.clarolab.model.types.TestFailType;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.AutomatedTestIssueService;
import com.clarolab.service.TestTriageService;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;

import static com.clarolab.util.Constants.*;
import static io.restassured.RestAssured.given;

public class AutomatedTestIssueAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private AutomatedTestIssueService automatedTestIssueService;

    @Autowired
    private TestTriageMapper testTriageMapper;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private AutomatedTestIssueMapper automatedTestIssueMapper;

    @Before
    public void clearProvider() {
        provider.clear();
    }


    @Test @Description("Test to create for first time an automation issue for some testTriage")
    public void newAutomatedIssue() {

        UpdateTriageDTO updateTriageDTO = new UpdateTriageDTO();

        //Getting some testTriage
        TestTriage triage = provider.getTestCaseTriage();
        triage.setTestFailType(TestFailType.TEST_ASSIGNED_TO_FIX);
        TestTriageDTO triageDTO = testTriageMapper.convertToDTO(triage);
        updateTriageDTO.setTestTriageDTO(triageDTO);

        // create new DTO Automated Issue
        AutomatedTestIssueDTO automatedTestIssueDTO = provider.getAutomatedTestIssueDTO(triageDTO);
        updateTriageDTO.setAutomatedTestIssueDTO(automatedTestIssueDTO);

        // call endpoint to create the automated issue
        given()
                .body(updateTriageDTO)
                .when()
                .contentType(ContentType.JSON)
                .post(TRIAGE_PATH + ACTION_DRAFT_TEST)
                .then().statusCode(HttpStatus.SC_OK)
                .extract().as(UpdateTriageDTO.class);

        TestTriage dbTriage = testTriageService.find(triage.getId());

        Assert.assertNotNull(dbTriage);
        Assert.assertNotNull(automatedTestIssueService.getAutomatedTestIssue(triage.getTestCase()));
    }

    @Test @Description("Test to update testTriage with an existing automation issue")
    public void updateAutomatedIssue() {

        UpdateTriageDTO updateTriageDTO = new UpdateTriageDTO();

        AutomatedTestIssue automatedTestIssue = provider.getAutomatedTestIssue();
        updateTriageDTO.setTestTriageDTO(testTriageMapper.convertToDTO(automatedTestIssue.getTestTriage()));
        updateTriageDTO.setAutomatedTestIssueDTO(automatedTestIssueMapper.convertToDTO(automatedTestIssue));


        // call endpoint to create the automated issue
        given()
                .body(updateTriageDTO)
                .when()
                .contentType(ContentType.JSON)
                .post(TRIAGE_PATH + ACTION_DRAFT_TEST)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().as(UpdateTriageDTO.class);

        TestTriage dbTriage = testTriageService.find(automatedTestIssue.getTestTriage().getId());

        Assert.assertNotNull(dbTriage);
        Assert.assertNotNull(automatedTestIssueService.find(automatedTestIssue.getId()));
    }

}
