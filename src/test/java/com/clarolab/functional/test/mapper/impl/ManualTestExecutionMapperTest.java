/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.mapper.impl;

import com.clarolab.dto.ManualTestExecutionDTO;
import com.clarolab.functional.test.mapper.AbstractMapperTest;
import com.clarolab.mapper.impl.ManualTestCaseMapper;
import com.clarolab.mapper.impl.ManualTestExecutionMapper;
import com.clarolab.mapper.impl.ManualTestPlanMapper;
import com.clarolab.mapper.impl.UserMapper;
import com.clarolab.model.manual.ManualTestExecution;
import com.clarolab.serviceDTO.ManualTestStepServiceDTO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ManualTestExecutionMapperTest extends AbstractMapperTest<ManualTestExecution, ManualTestExecutionDTO> {

    @Autowired
    private ManualTestExecutionMapper mapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ManualTestCaseMapper manualTestCaseMapper;

    @Autowired
    private ManualTestPlanMapper manualTestPlanMapper;


    @Test
    public void testEntityToDTOConversion() {
        ManualTestExecution manualTestExecution = provider.getManualTestExecution();
        manualTestExecution.setTestCase(provider.getManualTestCase(2));
        ManualTestExecutionDTO manualTestExecutionDTO = mapper.convertToDTO(manualTestExecution);
        this.assertConversion(manualTestExecution, manualTestExecutionDTO);
    }

    @Test
    public void testDTOToEntityConversion() {
        ManualTestExecutionDTO manualTestExecutionDTO = getDTO();
        manualTestExecutionDTO.setTestCase(manualTestCaseMapper.convertToDTO(provider.getManualTestCase(2)));
        manualTestExecutionDTO.setTestPlan(manualTestPlanMapper.convertToDTO(provider.getManualTestPlan()));
        manualTestExecutionDTO.setAssignee(userMapper.convertToDTO(provider.getUser()));
        manualTestExecutionDTO.setComment("Random comment");
        manualTestExecutionDTO.setExecutionOrder(1);
        manualTestExecutionDTO.setStatus("UNDEFINED");
        ManualTestExecution manualTestExecution = mapper.convertToEntity(manualTestExecutionDTO);
        this.assertConversion(manualTestExecution, manualTestExecutionDTO);
    }

    @Override
    public void assertConversion(ManualTestExecution manualTestExecution, ManualTestExecutionDTO manualTestExecutionDTO) {
        super.assertConversion(manualTestExecution, manualTestExecutionDTO);

        Assert.assertEquals(manualTestExecution.getAssignee().getRealname(), manualTestExecutionDTO.getAssignee().getRealname());
        Assert.assertEquals(manualTestExecution.getComment(), manualTestExecutionDTO.getComment());
        Assert.assertEquals(manualTestExecution.getEnvironment(), manualTestExecutionDTO.getEnvironment());
        Assert.assertEquals(manualTestExecution.getExecutionOrder(), manualTestExecutionDTO.getExecutionOrder());
        Assert.assertEquals(manualTestExecution.getTestCase().getId(), manualTestExecutionDTO.getTestCase().getId());
        Assert.assertEquals(manualTestExecution.getTestPlan().getId(), manualTestExecutionDTO.getTestPlan().getId());

    }

    public ManualTestExecutionMapperTest() {
        super(ManualTestExecution.class, ManualTestExecutionDTO.class);
    }

}
