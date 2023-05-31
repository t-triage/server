/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.TestExecutionStepDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.TestExecutionStep;
import com.clarolab.model.types.ReportType;
import com.clarolab.util.StringUtils;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class TestExecutionStepMapper implements Mapper<TestExecutionStep, TestExecutionStepDTO> {

    @Override
    public TestExecutionStepDTO convertToDTO(TestExecutionStep step) {

        TestExecutionStepDTO stepDTO = new TestExecutionStepDTO();

        setEntryFields(step, stepDTO);

        stepDTO.setOutput(step.getOutput());
        stepDTO.setName(parseAsShortName(step));
        stepDTO.setParameters(step.getParameters());
        return stepDTO;
    }

    private String parseAsShortName(TestExecutionStep step) {
        if (step.getTestExecution().getReport().getType() == ReportType.CYPRESS) {
            return step.getName();
        }
        return StringUtils.classTail(step.getName());
    }

    @Override
    public TestExecutionStep convertToEntity(TestExecutionStepDTO dto) {
        //TestExecutionStep is read only
        return null;
    }

}
