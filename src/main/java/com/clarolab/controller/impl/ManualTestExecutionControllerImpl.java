/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.controller.ManualTestExecutionController;
import com.clarolab.dto.ManualTestCaseDTO;
import com.clarolab.dto.ManualTestExecutionDTO;
import com.clarolab.model.manual.ManualTestExecution;
import com.clarolab.model.manual.service.ManualTestExecutionService;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.ManualTestExecutionServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class ManualTestExecutionControllerImpl extends BaseControllerImpl<ManualTestExecutionDTO> implements ManualTestExecutionController {

    @Autowired
    private ManualTestExecutionServiceDTO manualTestExecutionServiceDTO;

    @Autowired
    private ManualTestExecutionService manualTestExecutionService;

    @Override
    protected TTriageService<ManualTestExecutionDTO> getService() {
        return manualTestExecutionServiceDTO;
    }

    @Override
    public ResponseEntity<List<ManualTestExecutionDTO>> findByPlanId(@PathVariable Long id) {
        List<ManualTestExecution> executions = manualTestExecutionService.findAllByPlanId(id);
        return ResponseEntity.ok(manualTestExecutionServiceDTO.convertToDTO(executions));
    }

    @Override
    public ResponseEntity<Long> deleteByPlanAndCase(Long manualTestPlanId, Long manualTestCaseId) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(manualTestExecutionService.deleteByPlanAndCase(manualTestPlanId, manualTestCaseId));
    }

    @Override
    public ResponseEntity<List<ManualTestExecutionDTO>> getManualTestExecutionSince(long timestamp) {
        return ResponseEntity.ok(manualTestExecutionServiceDTO.findManualTestExecutionSinceDTO(timestamp));
    }
}
