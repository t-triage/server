/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.controller.ManualTestCaseController;
import com.clarolab.dto.ManualTestCaseDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.ManualTestCaseServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
public class ManualTestCaseControllerImpl extends BaseControllerImpl<ManualTestCaseDTO> implements ManualTestCaseController {

    @Autowired
    private ManualTestCaseServiceDTO manualTestCaseServiceDTO;

    @Override
    protected TTriageService<ManualTestCaseDTO> getService() {
        return manualTestCaseServiceDTO;
    }

    @Override
    public ResponseEntity<Page<ManualTestCaseDTO>> list(String[] criteria, Pageable pageable, String filter) throws IOException {
        return ResponseEntity.ok(manualTestCaseServiceDTO.filterList(criteria, pageable, getFilterDTO(filter)));
    }

    @Override
    public ResponseEntity<Page<String>> searchFunctionality(String name) {
        return ResponseEntity.ok(new PageImpl<>(manualTestCaseServiceDTO.searchFunctionality(name)));
    }

    @Override
    public ResponseEntity<Page<ManualTestCaseDTO>> searchAutomation() {
        return ResponseEntity.ok(new PageImpl<>(manualTestCaseServiceDTO.toAutomate()));
    }

    @Override
    public ResponseEntity<Boolean> importReport(String file) throws IOException {
        manualTestCaseServiceDTO.importReport(file);
        return ResponseEntity.ok(true);
    }

    @Override
    public ResponseEntity<Boolean> updateFunctionality() {
        manualTestCaseServiceDTO.updateFunctionalities();
        return ResponseEntity.ok(true);
    }

    @Override
    public ResponseEntity<Boolean> linkManualTestToAutomatedTest(Long manualTestId, Long automatedTestId) {
        try {
            manualTestCaseServiceDTO.linkedManualTestToAutomatedTest(manualTestId, automatedTestId);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            if (e instanceof NullPointerException || e instanceof IllegalArgumentException) {
                // Return bad request
                return ResponseEntity.status(400).body(false);
            }
            // Return internal server error
            return ResponseEntity.status(500).body(false);
        }

    }

    @Override
    public ResponseEntity<List<ManualTestCaseDTO>> getManualTestSince(long timestamp) {
        return ResponseEntity.ok(manualTestCaseServiceDTO.getManualTestCasesSince(timestamp));
    }
}
