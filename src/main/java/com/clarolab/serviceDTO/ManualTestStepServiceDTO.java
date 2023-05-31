/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.ManualTestStepDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.ManualTestStepMapper;
import com.clarolab.model.manual.instruction.ManualTestStep;
import com.clarolab.model.manual.service.ManualTestStepService;
import com.clarolab.service.TTriageService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log
public class ManualTestStepServiceDTO implements BaseServiceDTO<ManualTestStep, ManualTestStepDTO, ManualTestStepMapper> {

    @Autowired
    private ManualTestStepService service;

    @Autowired
    private ManualTestStepMapper mapper;

    @Override
    public TTriageService<ManualTestStep> getService() {
        return service;
    }

    @Override
    public Mapper<ManualTestStep, ManualTestStepDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<ManualTestStep, ManualTestStepDTO, ManualTestStepMapper> getServiceDTO() {
        return this;
    }
}

