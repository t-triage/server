/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.TestExecutionStepDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.TestExecutionStepMapper;
import com.clarolab.model.TestExecutionStep;
import com.clarolab.service.TTriageService;
import com.clarolab.service.TestExecutionStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestExecutionServiceStepDTO implements BaseServiceDTO<TestExecutionStep, TestExecutionStepDTO, TestExecutionStepMapper> {

    @Autowired
    private TestExecutionStepService service;

    @Autowired
    private TestExecutionStepMapper mapper;

    @Autowired
    private TestExecutionStepService testExecutionStepService;

    @Override
    public TTriageService<TestExecutionStep> getService() {
        return service;
    }

    @Override
    public Mapper<TestExecutionStep, TestExecutionStepDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<TestExecutionStep, TestExecutionStepDTO, TestExecutionStepMapper> getServiceDTO() {
        return this;
    }

    public List<TestExecutionStepDTO> getTestSteps(Long id) {
        return convertToDTO(service.getTestSteps(id));
    }
}
