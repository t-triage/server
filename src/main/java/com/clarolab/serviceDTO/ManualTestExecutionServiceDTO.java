/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.aaa.util.AuthContextHelper;
import com.clarolab.dto.ManualTestExecutionDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.ManualTestExecutionMapper;
import com.clarolab.model.manual.ManualTestCase;
import com.clarolab.model.manual.ManualTestExecution;
import com.clarolab.model.manual.ManualTestPlan;
import com.clarolab.model.manual.service.ManualTestCaseService;
import com.clarolab.model.manual.service.ManualTestExecutionService;
import com.clarolab.service.TTriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ManualTestExecutionServiceDTO implements BaseServiceDTO<ManualTestExecution, ManualTestExecutionDTO, ManualTestExecutionMapper> {

    @Autowired
    private ManualTestExecutionService service;

    @Autowired
    private ManualTestExecutionMapper mapper;

    @Autowired
    private AuthContextHelper authContextHelper;

    @Autowired
    private ManualTestCaseService manualTestCaseService;

    @Autowired
    private ManualTestExecutionService manualTestExecutionService;


    @Override
    public TTriageService<ManualTestExecution> getService() {
        return service;
    }

    @Override
    public Mapper<ManualTestExecution, ManualTestExecutionDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<ManualTestExecution, ManualTestExecutionDTO, ManualTestExecutionMapper> getServiceDTO() {
        return this;
    }

    @Override
    public ManualTestExecutionDTO update(ManualTestExecutionDTO manualTestExecutionDTO) {
        ManualTestExecution manualTestExecution = convertToEntity(manualTestExecutionDTO);
        ManualTestCase manualTestCase = manualTestExecution.getTestCase();
        /*if (manualTestExecution.getLastExecutionTime() == null)
            return super.update(manualTestExecution);*/
        if (manualTestCase.getLastExecution() == null || !manualTestExecution.getLastExecutionTime().equals(manualTestCase.getLastExecution().getLastExecutionTime())) {
            manualTestCase.setLastExecution(manualTestExecution);
            manualTestCase.setLastUpdater(authContextHelper.getCurrentUser());
            manualTestCaseService.update(manualTestCase);
        }
        return convertToDTO(manualTestExecutionService.update(manualTestExecution));
    }

    public Optional<ManualTestExecution> findByPlanAndCase(ManualTestPlan manualTestPlan, ManualTestCase manualTestCase) {
        return service.findByPlanAndCase(manualTestPlan, manualTestCase);
    }

    public List<ManualTestExecutionDTO> findManualTestExecutionSinceDTO(long timestamp){
        List<ManualTestExecution> list = manualTestExecutionService.findManualTestExecutionSince(timestamp);
        List<ManualTestExecutionDTO> listDTO = new ArrayList<>();
        for (ManualTestExecution m : list){
            listDTO.add(mapper.convertToDTO(m));
        }
        return listDTO;
    }
}
