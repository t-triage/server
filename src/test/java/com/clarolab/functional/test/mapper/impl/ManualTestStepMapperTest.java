/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.mapper.impl;

import com.clarolab.dto.ManualTestStepDTO;
import com.clarolab.functional.test.mapper.AbstractMapperTest;
import com.clarolab.mapper.impl.ManualTestStepMapper;
import com.clarolab.model.manual.instruction.ManualTestStep;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ManualTestStepMapperTest extends AbstractMapperTest<ManualTestStep, ManualTestStepDTO> {

    @Autowired
    private ManualTestStepMapper mapper;

    @Test
    public void testEntityToDTOConversion() {
        ManualTestStep manualTestStep = provider.getNewManualTestStep();
        ManualTestStepDTO manualTestStepDTO = mapper.convertToDTO(manualTestStep);
        this.assertConversion(manualTestStep, manualTestStepDTO);
    }

    @Test
    public void testDTOToEntityConversion() {
        ManualTestStepDTO manualTestStepDTO = getDTO();
        ManualTestStep manualTestStep = mapper.convertToEntity(manualTestStepDTO);
        this.assertConversion(manualTestStep, manualTestStepDTO);
    }

    @Override
    public void assertConversion(ManualTestStep manualTestStep, ManualTestStepDTO manualTestStepDTO) {
        super.assertConversion(manualTestStep, manualTestStepDTO);

        Assert.assertEquals(manualTestStep.getExpectedResult(), manualTestStepDTO.getExpectedResult());
        Assert.assertEquals(manualTestStep.getData(), manualTestStepDTO.getData());
        Assert.assertEquals(manualTestStep.getStep(), manualTestStepDTO.getStep());
    }

    public ManualTestStepMapperTest() {
        super(ManualTestStep.class, ManualTestStepDTO.class);
    }

}
