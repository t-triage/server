/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.controller.AutomatedTestIssueController;
import com.clarolab.dto.AutomatedTestIssueDTO;
import com.clarolab.dto.NewsBoardDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.AutomatedTestIssueServiceDTO;
import com.clarolab.serviceDTO.NewsBoardServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
public class AutomatedTestIssueControllerImpl extends BaseControllerImpl<AutomatedTestIssueDTO> implements AutomatedTestIssueController {

    @Autowired
    private AutomatedTestIssueServiceDTO service;

    @Autowired
    private NewsBoardServiceDTO newsBoardServiceDTO;

    @Override
    protected TTriageService<AutomatedTestIssueDTO> getService() {
        return service;
    }

    @Override
    public ResponseEntity<Page<AutomatedTestIssueDTO>> list(String[] criteria, Pageable pageable, String filter) throws IOException {
        return ResponseEntity.ok(service.filterList(criteria, pageable, getFilterDTO(filter)));
    }

    @Override
    public ResponseEntity<Long> automationIssuesPendingToFix() {
        return ResponseEntity.ok(service.automationIssuesPendingToFix());
    }

    @Override
    public ResponseEntity<List<NewsBoardDTO>> latestNews() {
        return ResponseEntity.ok(newsBoardServiceDTO.latestNews());
    }

}
