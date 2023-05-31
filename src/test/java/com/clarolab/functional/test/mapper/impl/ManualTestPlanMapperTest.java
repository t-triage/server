/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.mapper.impl;

import com.clarolab.dto.ManualTestPlanDTO;
import com.clarolab.dto.ManualTestStepDTO;
import com.clarolab.functional.test.mapper.AbstractMapperTest;
import com.clarolab.mapper.impl.ManualTestPlanMapper;
import com.clarolab.mapper.impl.UserMapper;
import com.clarolab.model.manual.ManualTestPlan;
import com.clarolab.model.manual.types.*;
import com.clarolab.populate.DataProvider;
import com.clarolab.serviceDTO.ManualTestStepServiceDTO;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ManualTestPlanMapperTest extends AbstractMapperTest<ManualTestPlan, ManualTestPlanDTO> {

    @Autowired
    private ManualTestPlanMapper mapper;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testEntityToDTOConversion() {
        ManualTestPlan manualTestPlan = provider.getManualTestPlan();
        ManualTestPlanDTO manualTestPlanDTO = mapper.convertToDTO(manualTestPlan);
        this.assertConversion(manualTestPlan, manualTestPlanDTO);
    }

    @Test
    public void testDTOToEntityConversion() {
        ManualTestPlanDTO manualTestPlanDTO = getDTO();
        manualTestPlanDTO.setDescription("Description");
        manualTestPlanDTO.setEnvironment("Environment");
        manualTestPlanDTO.setAssignee(userMapper.convertToDTO(provider.getUser()));
        manualTestPlanDTO.setFromDate(provider.getCreationDate());
        manualTestPlanDTO.setToDate(DataProvider.getTimeAdd(provider.getCreationDate(), 5));
        manualTestPlanDTO.setStatus(PlanStatusType.COMPLETED.name());

        ManualTestPlan manualTestPlan = mapper.convertToEntity(manualTestPlanDTO);
        this.assertConversion(manualTestPlan, manualTestPlanDTO);
    }

    @Override
    public void assertConversion(ManualTestPlan manualTestPlan, ManualTestPlanDTO manualTestPlanDTO) {
        super.assertConversion(manualTestPlan, manualTestPlanDTO);

        Assert.assertEquals(manualTestPlan.getName(), manualTestPlanDTO.getName());
        Assert.assertEquals(manualTestPlan.getDescription(), manualTestPlanDTO.getDescription());
        Assert.assertEquals(manualTestPlan.getAssignee().getRealname(), manualTestPlanDTO.getAssignee().getRealname());
        Assert.assertEquals(manualTestPlan.getFromDate(), manualTestPlanDTO.getFromDate());
        Assert.assertEquals(manualTestPlan.getToDate(), manualTestPlanDTO.getToDate());
        Assert.assertEquals(manualTestPlan.getFromDate(), manualTestPlanDTO.getFromDate());
        Assert.assertEquals(manualTestPlan.getStatus(), PlanStatusType.valueOf(manualTestPlanDTO.getStatus()));

    }

    public ManualTestPlanMapperTest() {
        super(ManualTestPlan.class, ManualTestPlanDTO.class);
    }

}
