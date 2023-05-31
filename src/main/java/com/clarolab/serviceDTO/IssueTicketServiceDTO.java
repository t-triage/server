/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.IssueTicketDTO;
import com.clarolab.dto.UpdateTriageDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.IssueTicketMapper;
import com.clarolab.model.IssueTicket;
import com.clarolab.service.IssueTicketService;
import com.clarolab.service.TTriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IssueTicketServiceDTO implements BaseServiceDTO<IssueTicket, IssueTicketDTO, IssueTicketMapper> {

    @Autowired
    private IssueTicketService service;

    @Autowired
    private IssueTicketMapper mapper;

    @Override
    public TTriageService<IssueTicket> getService() {
        return service;
    }

    @Override
    public Mapper<IssueTicket, IssueTicketDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<IssueTicket, IssueTicketDTO, IssueTicketMapper> getServiceDTO() {
        return this;
    }

    public List<IssueTicketDTO> findAllByAndAssignee(long id) {
        return convertToDTO(service.findAllByAndAssignee(id));
    }

    public IssueTicketDTO triage(UpdateTriageDTO updateTriageDTO, IssueTicketDTO dto) {
        if (dto == null) return null;


        return convertToDTO(service.triage(convertToEntity(dto)));
    }
}
