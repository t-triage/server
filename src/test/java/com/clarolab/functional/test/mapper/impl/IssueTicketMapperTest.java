/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.mapper.impl;

import com.clarolab.dto.IssueTicketDTO;
import com.clarolab.functional.test.mapper.AbstractMapperTest;
import com.clarolab.mapper.impl.IssueTicketMapper;
import com.clarolab.mapper.impl.UserMapper;
import com.clarolab.model.IssueTicket;
import com.clarolab.model.types.IssueType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class IssueTicketMapperTest extends AbstractMapperTest<IssueTicket, IssueTicketDTO> {

    @Autowired
    private IssueTicketMapper mapper;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testEntityToDTOConversion() {
        IssueTicket issueTicket = provider.getIssueTicket();
        IssueTicketDTO issueTicketDTO = mapper.convertToDTO(issueTicket);
        this.assertConversion(issueTicket, issueTicketDTO);
    }

    @Test
    public void testDTOToEntityConversion() {
        IssueTicketDTO issueTicketDTO = getDTO();
        IssueTicket issueTicket = mapper.convertToEntity(issueTicketDTO);
        this.assertConversion(issueTicket, issueTicketDTO);
    }

    @Override
    public void assertConversion(IssueTicket issueTicket, IssueTicketDTO issueTicketDTO) {
        super.assertConversion(issueTicket, issueTicketDTO);
    }

    public IssueTicketMapperTest() { super(IssueTicket.class, IssueTicketDTO.class); }

    public IssueTicketDTO getDTO() {
        IssueTicketDTO dto = super.getDTO();

        dto.setAssignee(userMapper.convertToDTO(provider.getUser()));
        dto.setIssueType(IssueType.OPEN.name());

        return dto;
    }

}
