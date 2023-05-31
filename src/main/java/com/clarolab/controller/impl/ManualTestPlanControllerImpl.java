/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.controller.ManualTestPlanController;
import com.clarolab.dto.ManualTestExecutionDTO;
import com.clarolab.dto.ManualTestPlanDTO;
import com.clarolab.dto.ManualTestPlanStatDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.ManualTestPlanServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class ManualTestPlanControllerImpl extends BaseControllerImpl<ManualTestPlanDTO> implements ManualTestPlanController {

    @Autowired
    private ManualTestPlanServiceDTO manualTestPlanServiceDTO;

    @Override
    protected TTriageService<ManualTestPlanDTO> getService() {
        return manualTestPlanServiceDTO;
    }

    @Override
    public ResponseEntity<Page<ManualTestExecutionDTO>> assignToTestPlan(Long manualTestPlanId, List<Long> manualTestCaseIds) {
        return ResponseEntity.ok(new PageImpl<>(manualTestPlanServiceDTO.assignToTestPlan(manualTestPlanId, manualTestCaseIds)));
    }

    @Override
    public ResponseEntity<List<ManualTestPlanStatDTO>> getOngoingManualTestPlans() {
        return ResponseEntity.ok(manualTestPlanServiceDTO.getOngoingManualTestPlans());
    }

    @Override
    public ResponseEntity<Page<ManualTestPlanDTO>> list(String[] criteria, Pageable pageable) {
        // List<ManualTestPlanDTO> list = manualTestPlanServiceDTO.findAll(criteria, Sort.by(Sort.Direction.ASC, "status", "name"));
        List<ManualTestPlanDTO> list = manualTestPlanServiceDTO.sortByName();
        return ResponseEntity.ok(new PageImpl<>(list, pageable, list.size()));
    }
}
