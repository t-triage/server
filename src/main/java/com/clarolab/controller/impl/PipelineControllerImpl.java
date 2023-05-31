/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.controller.PipelineController;
import com.clarolab.dto.ContainerDTO;
import com.clarolab.dto.PipelineDTO;
import com.clarolab.dto.TestTriageDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.PipelineServiceDTO;
import com.clarolab.view.PipelineView;
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
public class PipelineControllerImpl extends BaseControllerImpl<PipelineDTO> implements PipelineController {

    @Autowired
    private PipelineServiceDTO pipelineServiceDTO;

    @Override
    protected TTriageService<PipelineDTO> getService() {
        return pipelineServiceDTO;
    }

    @Override
    public ResponseEntity<PipelineDTO> assignToPipeline(Long pipelineId, List<Long> testCaseIds) {
        return ResponseEntity.ok(pipelineServiceDTO.assignToPipeline(pipelineId, testCaseIds));
    }

    @Override
    public ResponseEntity<Page<PipelineDTO>> search(String name) {
        return ResponseEntity.ok(new PageImpl<>(pipelineServiceDTO.search(name)));
    }

    @Override
    public ResponseEntity<Long> deleteByPipelineAndCase(Long pipelineId, Long testCaseId) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pipelineServiceDTO.deleteByPipelineAndCase(pipelineId, testCaseId));
    }

    @Override
    public ResponseEntity<Page<TestTriageDTO>> ongoingTestTriageList(Long pipelineId) {
        return ResponseEntity.ok(new PageImpl<>(pipelineServiceDTO.ongoingTestTriages(pipelineId)));
    }

    @Override
    public ResponseEntity<PipelineView> ongoingTestTriage(Long pipelineId) {
        return ResponseEntity.ok(pipelineServiceDTO.detail(pipelineId, true));
    }

    public ResponseEntity<List<ContainerDTO>> containers() {
        return ResponseEntity.ok(pipelineServiceDTO.containers());
    }

    public ResponseEntity<List<PipelineDTO>> pipelinesEnabled() {
        return ResponseEntity.ok(pipelineServiceDTO.getPipelinesEnabled());
    }

    public ResponseEntity<List<PipelineDTO>> findPipelinesByContainer(Long containerId) {
        return ResponseEntity.ok(pipelineServiceDTO.findPipelinesByContainer(containerId));
    }
}