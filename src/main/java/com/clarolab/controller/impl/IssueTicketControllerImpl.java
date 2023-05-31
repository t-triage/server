/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.controller.IssueTicketController;
import com.clarolab.dto.IssueTicketDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.IssueTicketServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class IssueTicketControllerImpl extends BaseControllerImpl<IssueTicketDTO> implements IssueTicketController {

    @Autowired
    private IssueTicketServiceDTO service;

    @Override
    protected TTriageService<IssueTicketDTO> getService() {
        return service;
    }

    @Override
    public ResponseEntity<Page<IssueTicketDTO>> list(Long id, Pageable pageable) {
        List<IssueTicketDTO> list = service.findAllByAndAssignee(id);
        return ResponseEntity.ok(PageableHelper.getPageable(pageable, list));
    }

}
