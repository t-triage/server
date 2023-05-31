/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl.executor;

import com.clarolab.aaa.util.AuthContextHelper;
import com.clarolab.controller.ExecutorViewController;
import com.clarolab.dto.FilterDTO;
import com.clarolab.dto.ReportDTO;
import com.clarolab.dto.UserDTO;
import com.clarolab.dto.db.TestTriagePassedDTO;
import com.clarolab.model.Executor;
import com.clarolab.service.ExecutorService;
import com.clarolab.serviceDTO.BuildTriageServiceDTO;
import com.clarolab.serviceDTO.ExecutorServiceDTO;
import com.clarolab.view.ExecutorView;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;

@CrossOrigin
@RestController
@Log
public class ExecutorViewControllerImpl implements ExecutorViewController {

    @Autowired
    private ExecutorServiceDTO executorServiceDTO;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private BuildTriageServiceDTO buildTriageService;

    @Autowired
    private AuthContextHelper authContextHelper;

    @Override
    public ResponseEntity<ExecutorView> find(Long id) {
        Executor executor = executorService.find(id);
        ExecutorView executorView =  executorServiceDTO.getExecutorViewOfLatestBuild(executor, true, true);
        return ResponseEntity.ok(executorView);
    }

    @Override
    public ResponseEntity<List<TestTriagePassedDTO>> findTestPass(Long id) {
        Executor executor = executorService.find(id);
        List<TestTriagePassedDTO> result =  executorServiceDTO.getExecutorViewOfLatestBuildAndTestPass(executor, true, true);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<Page<ExecutorView>> list(Pageable pageable, String filter) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        filter = filter.replaceAll("&quot;","\"");
        FilterDTO filterDTO = objectMapper.readValue(filter, FilterDTO.class);
        return ResponseEntity.ok(executorServiceDTO.list(pageable, filterDTO));
    }

    @Override
    public ResponseEntity<List<ExecutorView>> suggested() {
        UserDTO user = authContextHelper.getCurrentUserAsDTO();
        List<ExecutorView> list = buildTriageService.getTopViews(user);

        return ResponseEntity.ok(list);
    }

    // It builds the list with the most likely assignees
    @Override
    public ResponseEntity<LinkedHashSet<UserDTO>> suggestedAssignee(Long buildId) {
        return ResponseEntity.ok(buildTriageService.suggestedAssignee(buildId));
    }

    // Set the new priority to the Build Triage. It answers the set priority.
    @Override
    public ResponseEntity<String> assignPriority(Long buildId, String priority) {
        return ResponseEntity.ok(buildTriageService.assignPriority(buildId, priority));
    }

    @Override
    public ResponseEntity<List<ReportDTO>> history(Long id) {
        return ResponseEntity.ok(executorServiceDTO.history(id));
    }
}
