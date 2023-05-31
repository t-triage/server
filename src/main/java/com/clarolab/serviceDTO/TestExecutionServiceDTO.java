/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.DateStatsDTO;
import com.clarolab.dto.TestExecutionDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.TestExecutionMapper;
import com.clarolab.model.TestExecution;
import com.clarolab.service.TTriageService;
import com.clarolab.service.TestExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

@Component
public class TestExecutionServiceDTO implements BaseServiceDTO<TestExecution, TestExecutionDTO, TestExecutionMapper> {

    @Autowired
    private TestExecutionService service;

    @Autowired
    private TestExecutionMapper mapper;

    @Override
    public TTriageService<TestExecution> getService() {
        return service;
    }

    @Override
    public Mapper<TestExecution, TestExecutionDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<TestExecution, TestExecutionDTO, TestExecutionMapper> getServiceDTO() {
        return this;
    }

    public List<String> searchTestName(String testName) {
        return service.searchTestName(testName);
    }

    public List<DateStatsDTO> searchErrorTestStats(){
        return service.getErrorTestStats();
    }


}
