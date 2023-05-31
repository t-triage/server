/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test;

import com.clarolab.api.BaseAPITest;
import com.clarolab.dto.FunctionalityDTO;
import com.clarolab.dto.ManualTestCaseDTO;
import com.clarolab.dto.ManualTestStepDTO;
import com.clarolab.dto.UserDTO;
import com.clarolab.mapper.impl.FunctionalityMapper;
import com.clarolab.mapper.impl.ManualTestCaseMapper;
import com.clarolab.mapper.impl.UserMapper;
import com.clarolab.model.User;
import com.clarolab.model.manual.Functionality;
import com.clarolab.model.manual.ManualTestCase;
import com.clarolab.model.manual.instruction.ManualTestStep;
import com.clarolab.model.manual.service.ManualTestCaseService;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.util.Constants;
import com.google.common.collect.Lists;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static com.clarolab.util.Constants.API_MANUAL_TEST_URI;
import static io.restassured.RestAssured.given;

public class ManualTestCaseAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private ManualTestCaseMapper manualTestCaseMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ManualTestCaseService manualTestCaseService;
    
    @Autowired
    private FunctionalityMapper functionalityMapper;


    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void testCreateManualTestCaseWithoutSteps() {

        ManualTestCaseDTO manualTestCaseDTO = getManualTestCaseDTO();

        manualTestCaseDTO.setSteps(Lists.newArrayList());

        ManualTestCaseDTO answerDTO = given()
                .body(manualTestCaseDTO)
                .when()
                .contentType(ContentType.JSON)
                .post(API_MANUAL_TEST_URI + Constants.CREATE_PATH)
                .then().statusCode(HttpStatus.SC_CREATED)
                .extract().as(ManualTestCaseDTO.class);

        Assert.assertNotNull(answerDTO);
    }

    @Test
    public void testCreateManualTestCaseWithSteps() {

        ManualTestCaseDTO manualTestCaseDTO = getManualTestCaseDTO();

        manualTestCaseDTO.setSteps(createSteps());

        ManualTestCaseDTO answerDTO = given()
                .body(manualTestCaseDTO)
                .when()
                .contentType(ContentType.JSON)
                .post(API_MANUAL_TEST_URI + Constants.CREATE_PATH)
                .then().statusCode(HttpStatus.SC_CREATED)
                .extract().as(ManualTestCaseDTO.class);

        Assert.assertNotNull(answerDTO);

        manualTestCaseService.find(answerDTO.getId());
    }

    @Test
    public void testEditManualTestCaseWithSteps() {
        ManualTestCase test = provider.getManualTestCase(3);
        test.removeStep(test.getSteps().get(2));
        provider.setManualTestCase(null);

        test.getSteps().get(1).setData("NEW DATA");

        ManualTestStep newStep = provider.getNewManualTestStep();
        newStep.setStepOrder(2);
        test.addStep(newStep);

        ManualTestStep newStep2 = provider.getNewManualTestStep();
        newStep2.setStepOrder(3);
        test.addStep(newStep2);

        ManualTestCaseDTO manualTestCaseDTO = manualTestCaseMapper.convertToDTO(test);

        //TOGGLE COMMENT, if you want to test having a step with Main set to true
        manualTestCaseDTO.getSteps().get(2).setMain(true);

        ManualTestCaseDTO answerDTO = given()
                .body(manualTestCaseDTO)
                .when()
                .contentType(ContentType.JSON)
                .put(API_MANUAL_TEST_URI + Constants.UPDATE_PATH)
                .then().statusCode(HttpStatus.SC_OK)
                .extract().as(ManualTestCaseDTO.class);

        Assert.assertNotNull(answerDTO);

        Assert.assertEquals(manualTestCaseService.find(answerDTO.getId()).getName(), test.getName());
    }

    private ManualTestCaseDTO getManualTestCaseDTO() {
        ArrayList<String> techniqueTypes = Lists.newArrayList();
        techniqueTypes.add("SECURITY");

        ManualTestCaseDTO manualTestCaseDTO = new ManualTestCaseDTO();
        manualTestCaseDTO.setName("Name ");
        manualTestCaseDTO.setRequirementId(provider.getManualTestRequirement().getId());
        manualTestCaseDTO.setRequirement(provider.getManualTestRequirement().getName());
        manualTestCaseDTO.setNeedsUpdate(false);
        manualTestCaseDTO.setPriority("UNDEFINED");
        manualTestCaseDTO.setTechniques(techniqueTypes);
        manualTestCaseDTO.setSuite("SMOKE");
        manualTestCaseDTO.setAutomationStatus("PENDING_MEDIUM");
        manualTestCaseDTO.setProductId(provider.getProduct().getId());
        manualTestCaseDTO.setProductName(provider.getProduct().getName());
        manualTestCaseDTO.setComponent1Id(provider.getProductComponent().getId());
        manualTestCaseDTO.setComponent1Name(provider.getProductComponent().getName());
        provider.setProductComponent(null);
        manualTestCaseDTO.setComponent2Id(provider.getProductComponent().getId());
        manualTestCaseDTO.setComponent2Name(provider.getProductComponent().getName());
        provider.setProductComponent(null);
        manualTestCaseDTO.setComponent3Id(provider.getProductComponent().getId());
        manualTestCaseDTO.setComponent3Name(provider.getProductComponent().getName());

        Functionality functionality = provider.getFunctionalityEntity();
        FunctionalityDTO functionalityDTO = functionalityMapper.convertToDTO(functionality);
        manualTestCaseDTO.setFunctionalityEntity(functionalityDTO);

        User user = provider.getUser();
        UserDTO userDTO = userMapper.convertToDTO(user);
        manualTestCaseDTO.setOwner(userDTO);

        provider.setUser(null);
        user = provider.getUser();
        userDTO = userMapper.convertToDTO(user);
        manualTestCaseDTO.setLastUpdater(userDTO);

        provider.setUser(null);
        user = provider.getUser();
        userDTO = userMapper.convertToDTO(user);
        manualTestCaseDTO.setAutomationAssignee(userDTO);

        manualTestCaseDTO.setLastExecutionId(provider.getManualTestExecution().getId());
        return manualTestCaseDTO;
    }

    private List<ManualTestStepDTO> createSteps() {
        List<ManualTestStepDTO> list = Lists.newArrayList();
        for(int i = 1 ; i<51; i++){
            ManualTestStepDTO stepDTO = new ManualTestStepDTO(0l, "Step"+i, "Result", "Data", 0, false, 0);
            list.add(stepDTO);
        }

//        stepDTO = new ManualTestStepDTO(0, "Step2", "Result", "Data", 1, false, 0);
//        list.add(stepDTO);
//        stepDTO = new ManualTestStepDTO(0, "Step3", "Result", "Data", 2, false, 0);
//        list.add(stepDTO);
        return list;
    }

}

