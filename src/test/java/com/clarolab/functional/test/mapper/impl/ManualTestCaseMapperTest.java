/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.mapper.impl;

import com.clarolab.dto.ManualTestCaseDTO;
import com.clarolab.dto.ManualTestStepDTO;
import com.clarolab.functional.test.mapper.AbstractMapperTest;
import com.clarolab.mapper.impl.ManualTestCaseMapper;
import com.clarolab.mapper.impl.UserMapper;
import com.clarolab.model.manual.ManualTestCase;
import com.clarolab.model.manual.types.AutomationStatusType;
import com.clarolab.model.manual.types.SuiteType;
import com.clarolab.model.manual.types.TechniqueType;
import com.clarolab.model.manual.types.TestPriorityType;
import com.clarolab.serviceDTO.ManualTestStepServiceDTO;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ManualTestCaseMapperTest extends AbstractMapperTest<ManualTestCase, ManualTestCaseDTO> {

    @Autowired
    private ManualTestCaseMapper mapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ManualTestStepServiceDTO manualTestStepServiceDTO;

    @Test
    public void testEntityToDTOConversion() {
        ManualTestCase manualTestCase = provider.getManualTestCase(2);
        ManualTestCaseDTO manualTestCaseDTO = mapper.convertToDTO(manualTestCase);
        this.assertConversion(manualTestCase, manualTestCaseDTO);
    }

    @Test
    public void testDTOToEntityConversion() {
        ManualTestCaseDTO manualTestCaseDTO = getDTO();
        manualTestCaseDTO.setComponent1Id(provider.getProductComponent().getId());
        provider.setProductComponent(null);
        manualTestCaseDTO.setComponent2Id(provider.getProductComponent().getId());
        provider.setProductComponent(null);
        manualTestCaseDTO.setComponent3Id(provider.getProductComponent().getId());
        provider.setProductComponent(null);
        manualTestCaseDTO.setPriority(TestPriorityType.AUTOMATIC.name());
        manualTestCaseDTO.setSuite(SuiteType.SMOKE.name());
        manualTestCaseDTO.setAutomationStatus(AutomationStatusType.DONE.name());
        manualTestCaseDTO.setOwner(userMapper.convertToDTO(provider.getUser()));
        provider.setUser(null);
        manualTestCaseDTO.setLastUpdater(userMapper.convertToDTO(provider.getUser()));
        provider.setUser(null);
        manualTestCaseDTO.setAutomationAssignee(userMapper.convertToDTO(provider.getUser()));
        List<String> techniques = Lists.newArrayList();
        techniques.add(TechniqueType.POSITIVE.name());
        manualTestCaseDTO.setTechniques(techniques);
        List<ManualTestStepDTO> steps = Lists.newArrayList();
        steps.add(manualTestStepServiceDTO.convertToDTO(provider.getNewManualTestStep()));
        steps.add(manualTestStepServiceDTO.convertToDTO(provider.getNewManualTestStep()));
        manualTestCaseDTO.setSteps(steps);
        ManualTestCase manualTestCase = mapper.convertToEntity(manualTestCaseDTO);
        this.assertConversion(manualTestCase, manualTestCaseDTO);
    }

    @Override
    public void assertConversion(ManualTestCase manualTestCase, ManualTestCaseDTO manualTestCaseDTO) {
        super.assertConversion(manualTestCase, manualTestCaseDTO);

        Assert.assertEquals(manualTestCase.getName(), manualTestCaseDTO.getName());
        Assert.assertEquals(manualTestCase.getComponent1().getId(), manualTestCaseDTO.getComponent1Id());
        Assert.assertEquals(manualTestCase.getOwner().getRealname(), manualTestCaseDTO.getOwner().getRealname());
        Assert.assertEquals(manualTestCase.getTechniques().iterator().next().name(), manualTestCaseDTO.getTechniques().iterator().next());
        Assert.assertEquals(manualTestCase.getTechniques().size(), manualTestCaseDTO.getTechniques().size());
        Assert.assertEquals(manualTestCase.getSteps().iterator().next().getStep(), manualTestCaseDTO.getSteps().iterator().next().getStep());

    }

    public ManualTestCaseMapperTest() {
        super(ManualTestCase.class, ManualTestCaseDTO.class);
    }

}
