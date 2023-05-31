/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.manual;

import com.clarolab.dto.ManualTestCaseDTO;
import com.clarolab.dto.ManualTestExecutionDTO;
import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.mapper.impl.ManualTestPlanMapper;
import com.clarolab.model.User;
import com.clarolab.model.manual.ManualTestCase;
import com.clarolab.model.manual.ManualTestExecution;
import com.clarolab.model.manual.ManualTestPlan;
import com.clarolab.model.manual.service.ManualTestExecutionService;
import com.clarolab.model.manual.service.ManualTestPlanService;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.serviceDTO.ManualTestCaseServiceDTO;
import com.clarolab.serviceDTO.ManualTestExecutionServiceDTO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ManualTestExecutionFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private ManualTestExecutionService manualTestExecutionService;

    @Autowired
    private ManualTestExecutionServiceDTO manualTestExecutionServiceDTO;

    @Autowired
    private ManualTestCaseServiceDTO manualTestCaseServiceDTO;

    @Autowired
    private ManualTestPlanMapper manualTestPlanMapper;

    @Autowired
    private ManualTestPlanService manualTestPlanService;


    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void create() {
        ManualTestExecution test = provider.getManualTestExecution();
        ManualTestExecution dbTest = manualTestExecutionService.find(test.getId());

        Assert.assertNotNull("Test should have been created in the DB", dbTest);
    }

    @Test
    public void searchByUser() {
        User user = provider.getUser();
        ManualTestExecution userExecution1 = provider.getManualTestExecution();
        provider.setManualTestExecution(null);
        ManualTestExecution userExecution2 = provider.getManualTestExecution();
        provider.setManualTestExecution(null);
        provider.setUser(null);
        ManualTestExecution manchiTest = provider.getManualTestExecution();
        List<ManualTestExecution> manualTestExecutions = new ArrayList<>();
        manualTestExecutions.add(userExecution1);
        manualTestExecutions.add(userExecution2);
        List<ManualTestExecution> manualTestExecutionsQuery = manualTestExecutionService.findAllByAssignee(user);
        Assert.assertTrue(CollectionUtils.isEqualCollection(manualTestExecutions, manualTestExecutionsQuery));
    }

    @Test
    public void searchByPlan() {
        ManualTestPlan manualTestPlan = provider.getManualTestPlan();
        ManualTestExecution manualTestExecution = provider.getManualTestExecution();
        manualTestExecution.setTestCase(provider.getManualTestCase(2));
        List<ManualTestExecution> manualTestExecutions = Lists.newArrayList();
        manualTestExecutions.add(manualTestExecution);
        List<ManualTestExecution> manualTestExecutionsQuery = manualTestExecutionService.findByPlan(manualTestPlan);
        Assert.assertTrue(CollectionUtils.isEqualCollection(manualTestExecutions, manualTestExecutionsQuery));

    }

    @Test
    public void searchByPlanAndCase() {
        ManualTestPlan manualTestPlan = provider.getManualTestPlan();
        ManualTestCase manualTestCase = provider.getManualTestCase(2);
        ManualTestExecution manualTestExecution = provider.getManualTestExecution();
        manualTestExecution.setTestPlan(manualTestPlan);
        manualTestExecution.setTestCase(manualTestCase);
        List<ManualTestExecution> manualTestExecutions = Lists.newArrayList();
        manualTestExecutions.add(manualTestExecution);
        List<ManualTestExecution> manualTestExecutionsQuery = Lists.newArrayList();
        manualTestExecutionService.findByPlanAndCase(manualTestPlan, manualTestCase).ifPresent(manualTestExecutionsQuery::add);
        Assert.assertTrue(CollectionUtils.isEqualCollection(manualTestExecutions, manualTestExecutionsQuery));

    }

    @Test
    public void updateManualTestExecution() {

        //ManualTestCase w/lastExecutionTime & ManualTestExecution w/!= lastExecutionTime
        ManualTestCaseDTO manualTestCaseDTO = manualTestCaseServiceDTO.convertToDTO(provider.getManualTestCase(2));
        ManualTestExecutionDTO manualTestExecutionDto = new ManualTestExecutionDTO();
        manualTestExecutionDto.setStatus("PENDING");
        manualTestExecutionDto.setTestCase(manualTestCaseDTO);
        manualTestExecutionDto.setTestPlan(manualTestPlanMapper.convertToDTO(manualTestPlanService.save(provider.getManualTestPlan())));
        manualTestExecutionDto = manualTestExecutionServiceDTO.save(manualTestExecutionDto);
        manualTestExecutionDto.setStatus("PASS");
        manualTestExecutionServiceDTO.update(manualTestExecutionDto);
        ManualTestExecution updatedExecution = manualTestExecutionService.find(manualTestExecutionDto.getId());
        Assert.assertNotNull(updatedExecution.getLastExecutionTime());
    }

    @Test
    public void updateManualTestExecution2() {
        //ManualTestCase w/lastExecutionTime = null & ManualTestExecution w/!= lastExecutionTime
        ManualTestCaseDTO manualTestCaseDTO = manualTestCaseServiceDTO.convertToDTO(provider.getManualTestCase(2));
        ManualTestExecutionDTO manualTestExecutionDto = new ManualTestExecutionDTO();
        manualTestExecutionDto.setStatus("PENDING");
        manualTestExecutionDto.setTestCase(manualTestCaseDTO);
        manualTestExecutionDto.setTestPlan(manualTestPlanMapper.convertToDTO(manualTestPlanService.save(provider.getManualTestPlan())));
        manualTestExecutionDto = manualTestExecutionServiceDTO.save(manualTestExecutionDto);
        String updatedComment = "new comment";
        manualTestExecutionDto.setComment(updatedComment);
        ManualTestExecution updatedExecution = manualTestExecutionServiceDTO.convertToEntity(manualTestExecutionServiceDTO.update(manualTestExecutionDto));
        Assert.assertEquals(updatedComment, updatedExecution.getComment());
    }

    @Test
    public void updateManualTestExecution3() {
        //ManualTestCase w/lastExecutionTime & ManualTestExecution w/= lastExecutionTime
        ManualTestCaseDTO manualTestCaseDTO = manualTestCaseServiceDTO.convertToDTO(provider.getManualTestCase(2));
        ManualTestExecutionDTO manualTestExecutionDto = new ManualTestExecutionDTO();
        manualTestExecutionDto.setStatus("PENDING");
        manualTestExecutionDto.setTestCase(manualTestCaseDTO);
        manualTestExecutionDto.setTestPlan(manualTestPlanMapper.convertToDTO(manualTestPlanService.save(provider.getManualTestPlan())));
        manualTestExecutionDto = manualTestExecutionServiceDTO.save(manualTestExecutionDto);
        manualTestExecutionDto.setStatus("PASS");
        manualTestExecutionServiceDTO.update(manualTestExecutionDto);
        String updatedComment = "new comment";
        manualTestExecutionDto.setComment(updatedComment);
        ManualTestExecution updatedExecution = manualTestExecutionServiceDTO.convertToEntity(manualTestExecutionServiceDTO.update(manualTestExecutionDto));
        Assert.assertEquals(updatedComment, updatedExecution.getComment());
    }
}
