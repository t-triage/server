/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl.test;

import com.clarolab.controller.TestExecutionController;
import com.clarolab.controller.impl.BaseControllerImpl;
import com.clarolab.dto.TestExecutionDTO;
import com.clarolab.dto.TestExecutionStepDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.TestExecutionServiceDTO;
import com.clarolab.serviceDTO.TestExecutionServiceStepDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@CrossOrigin
@RestController
public class TestExecutionControllerImpl extends BaseControllerImpl<TestExecutionDTO> implements TestExecutionController {

    @Autowired
    private TestExecutionServiceDTO testExecutionService;

    @Autowired
    private TestExecutionServiceStepDTO testExecutionServiceStepDTO;

    @Override
    protected TTriageService<TestExecutionDTO> getService() {
        return testExecutionService;
    }

    @Override
    @ApiIgnore
    public ResponseEntity<TestExecutionDTO> save(TestExecutionDTO dto) {
        return super.save(dto);
    }

    @Override
    @ApiIgnore
    public ResponseEntity<Long> delete(Long id) {
        return super.delete(id);
    }

    @Override
    @ApiIgnore
    public ResponseEntity<TestExecutionDTO> update(TestExecutionDTO dto) {
        return super.update(dto);
    }

    @Override
    public ResponseEntity<Page<String>> searchTestName(String name) {
        return ResponseEntity.ok(new PageImpl<>(testExecutionService.searchTestName(name)));
    }

    @Override
    public ResponseEntity<List<TestExecutionStepDTO>> getTestSteps(Long id) {
        return ResponseEntity.ok(testExecutionServiceStepDTO.getTestSteps(id));
    }
}
