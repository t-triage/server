/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.controller.ReportController;
import com.clarolab.dto.ReportDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.ReportServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@CrossOrigin
@RestController
public class ReportControllerImpl extends BaseControllerImpl<ReportDTO> implements ReportController {

    @Autowired
    private ReportServiceDTO reportService;

    @Override
    protected TTriageService<ReportDTO> getService() {
        return reportService;
    }

    @Override
    @ApiIgnore
    public ResponseEntity<ReportDTO> save(ReportDTO dto) {
        return super.save(dto);
    }

    @Override
    @ApiIgnore
    public ResponseEntity<Long> delete(Long id) {
        return super.delete(id);
    }

    @Override
    @ApiIgnore
    public ResponseEntity<ReportDTO> update(ReportDTO dto) {
        return super.update(dto);
    }
}
