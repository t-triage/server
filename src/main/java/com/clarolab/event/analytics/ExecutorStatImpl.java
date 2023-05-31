/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.event.analytics;

import com.clarolab.controller.impl.BaseControllerImpl;
import com.clarolab.dto.ExecutorStatDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.ExecutorStatServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@CrossOrigin
@RestController
public class ExecutorStatImpl extends BaseControllerImpl<ExecutorStatDTO> implements ExecutorStatController {

    @Autowired
    private ExecutorStatServiceDTO executorStatService;

    @Override
    protected TTriageService<ExecutorStatDTO> getService() {
        return executorStatService;
    }

    @Override
    @ApiIgnore
    public ResponseEntity<ExecutorStatDTO> save(ExecutorStatDTO dto) {
        return super.save(dto);
    }

    @Override
    @ApiIgnore
    public ResponseEntity<ExecutorStatDTO> find(@PathVariable Long id) {
        return super.find(id);
    }

    @Override
    @ApiIgnore
    public ResponseEntity<Long> delete(Long id) {
        return super.delete(id);
    }

    @Override
    @ApiIgnore
    public ResponseEntity<ExecutorStatDTO> update(ExecutorStatDTO dto) {
        return super.update(dto);
    }
}
