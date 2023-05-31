/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.controller.AutomatedComponentController;
import com.clarolab.dto.AutomatedComponentDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.AutomatedComponentServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class AutomatedComponentControllerImpl extends BaseControllerImpl<AutomatedComponentDTO> implements AutomatedComponentController {

    @Autowired
    private AutomatedComponentServiceDTO automatedComponentServiceDTO;

    @Override
    protected TTriageService<AutomatedComponentDTO> getService() {
        return automatedComponentServiceDTO;
    }

    @Override
    public ResponseEntity<Page<AutomatedComponentDTO>> search(String name) {
        return ResponseEntity.ok(new PageImpl<>(automatedComponentServiceDTO.search(name)));
    }

    @Override
    public ResponseEntity<Page<AutomatedComponentDTO>> suggestedDefaultComponents() {
        return ResponseEntity.ok(new PageImpl<>(automatedComponentServiceDTO.suggestedDefaultComponents()));
    }

    @Override
    public ResponseEntity<Page<AutomatedComponentDTO>> suggestedComponents(List<Long> automatedComponentIds) {
        return ResponseEntity.ok(new PageImpl<>(automatedComponentServiceDTO.suggestedComponents(automatedComponentIds)));
    }

    @Override
    public ResponseEntity<AutomatedComponentDTO> setComponentToTests(Long automatedComponentId, List<Long> testCaseIds) {
        return ResponseEntity.ok(automatedComponentServiceDTO.setComponentToTests(automatedComponentId, testCaseIds));
    }

    @Override
    public ResponseEntity<Long> deleteByComponentAndTestCase(Long automatedComponentId, Long testCaseId) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(automatedComponentServiceDTO.deleteByComponentAndTestCase(automatedComponentId, testCaseId));
    }
}
