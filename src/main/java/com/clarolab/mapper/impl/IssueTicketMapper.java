/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.IssueTicketDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.IssueTicket;
import com.clarolab.model.TestCase;
import com.clarolab.model.types.IssueType;
import com.clarolab.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class IssueTicketMapper implements Mapper<IssueTicket, IssueTicketDTO> {

    private ProductService productService;
    private NoteService noteService;
    private UserService userService;

    @Autowired
    private IssueTicketService issueTicketService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TestExecutionMapper testExecutionMapper;

    @Override
    public IssueTicketDTO convertToDTO(IssueTicket entity) {
        IssueTicketDTO dto = new IssueTicketDTO();
        setEntryFields(entity, dto);

        dto.setSummary(entity.getSummary());
        dto.setUrl(entity.getUrl());
        dto.setUrlKey(entity.getUrlKey());
        dto.setComponent(entity.getComponent());
        dto.setFile(entity.getFile());
        dto.setPriority(entity.getPriority());
        dto.setDueDate(entity.getDueDate());
        if ((entity.getDescription() == null || entity.getDescription().isEmpty()) && entity.getTestCase() != null) {
            dto.setDescription(testExecutionMapper.getGroupName(entity.getTestCase()) + "." + entity.getTestCase().getName());
        } else {
            dto.setDescription(entity.getDescription());
        }

        dto.setNote(entity.getNote() == null ? null : entity.getNote().getId());
        dto.setProduct(entity.getProduct() == null ? null : entity.getProduct().getId());
        dto.setAssignee(entity.getAssignee() == null ? null : userMapper.convertToDTO(entity.getAssignee()));
        dto.setIssueType(entity.getIssueType() == null ? null : entity.getIssueType().name());
        dto.setTestCaseId(entity.getTestCase().getId());

        return dto;
    }

    @Override
    public IssueTicket convertToEntity(IssueTicketDTO dto) {
        IssueTicket entity = null;
        TestCase testCase = testCaseService.find(dto.getTestCaseId());

        if (dto.getId() != null && dto.getId() > 1) {
            entity = issueTicketService.find(dto.getId());
        }
        if (entity == null && testCase!=null && testCase.getIssueTicket() != null) {
            entity = testCase.getIssueTicket();
        }
        if (entity == null) {
            entity = issueTicketService.find(testCase);
        }

        if  (entity != null && entity.isResolved()) {
            entity.setIssueType(IssueType.REOPEN);
            entity.setReopenTimes(entity.getReopenTimes() + 1);
        }
        if (entity == null) {
            // Ok, it is really a new issue
            entity = IssueTicket.builder().enabled(true).issueType(IssueType.OPEN).build();
        }

        entity.setSummary(dto.getSummary());
        entity.setUrl(dto.getUrl());
        entity.setComponent(dto.getComponent());
        entity.setFile(dto.getFile());
        entity.setPriority(dto.getPriority());
        entity.setDueDate(dto.getDueDate());
        entity.setDescription(dto.getDescription());

        entity.setNote(dto.getNote() == null ? null : noteService.find(dto.getNote()));
        entity.setProduct(dto.getProduct() == null ? null : productService.find(dto.getProduct()));
        entity.setAssignee(dto.getAssignee() == null ? null : userService.find(dto.getAssignee().getId()));
        entity.setIssueType(dto.getIssueType() == null ? null : IssueType.valueOf(dto.getIssueType()));
        entity.setTestCase(testCaseService.find(dto.getTestCaseId()));

        return entity;
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

}
