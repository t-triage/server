/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.controller.AutomatedTestCaseController;
import com.clarolab.dto.AutomatedTestCaseDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.AutomatedTestCaseServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@CrossOrigin
@RestController
public class AutomatedTestCaseControllerImpl extends BaseControllerImpl<AutomatedTestCaseDTO> implements AutomatedTestCaseController {

    @Autowired
    private AutomatedTestCaseServiceDTO service;

    @Override
    protected TTriageService<AutomatedTestCaseDTO> getService() {
        return service;
    }

    @Override
    public ResponseEntity<Page<AutomatedTestCaseDTO>> list(String[] criteria, Pageable pageable, String filter) throws IOException {
        return ResponseEntity.ok(service.filterList(criteria, pageable, getFilterDTO(filter)));
    }
}
