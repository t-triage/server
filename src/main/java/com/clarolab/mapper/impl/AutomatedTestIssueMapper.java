/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.AutomatedTestIssueDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.AutomatedTestIssue;
import com.clarolab.model.TestCase;
import com.clarolab.model.TestTriage;
import com.clarolab.model.helper.AutomationIssueHelper;
import com.clarolab.model.types.IssueType;
import com.clarolab.model.types.UserFixPriorityType;
import com.clarolab.service.*;
import com.clarolab.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.getNullableByID;
import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class AutomatedTestIssueMapper implements Mapper<AutomatedTestIssue, AutomatedTestIssueDTO> {

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private AutomatedTestIssueService automatedTestIssueService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private TestTriageMapper testTriageMapper;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private ProductService productService;

    @Autowired
    private NoteMapper noteMapper;


    @Override
    public AutomatedTestIssueDTO convertToDTO(AutomatedTestIssue entity){
        AutomatedTestIssueDTO dto = new AutomatedTestIssueDTO();
        setEntryFields(entity, dto);

        dto.setId(entity.getId());
        dto.setUserFixPriority(entity.getUserFixPriorityType() == null ? "UNDEFINED" : entity.getUserFixPriorityType().name());
        dto.setIssueType(entity.getIssueType().name());
        dto.setTestCaseId(entity.getTestCase().getId());
        dto.setTriager(entity.getTriager() == null ? null : userMapper.convertToDTO(entity.getTriager()));
        if (entity.getTestTriage() != null) {
            dto.setTestTriage(testTriageMapper.convertToAutomationList(entity.getTestTriage()));
            dto.setBuildTriageId(entity.getTestTriage().getBuildTriage().getId());

            TestTriage latestTriage = testTriageService.getLastTriage(entity.getTestTriage());
            if (latestTriage != null) {
                dto.setLastTestTriage(latestTriage.getId());
            }
        }
        dto.setCalculatedPriority(entity.getCalculatedPriority());
        dto.setNote(entity.getNote() == null ? null : noteMapper.convertToDTO(entity.getNote()));
        dto.setProductId(entity.findProduct() == null ? null : entity.findProduct().getId());
        dto.setFailTimes(entity.getFailTimes());
        dto.setReopenTimes(entity.getReopenTimes());
        dto.setSuccessTrend(entity.getSuccessTrend());
        dto.setTrendExplanation(AutomationIssueHelper.toString(entity));

        return dto;
    }

    @Override
    public AutomatedTestIssue convertToEntity(AutomatedTestIssueDTO dto) {
        AutomatedTestIssue entity = null;
        TestCase testCase = testCaseService.find(dto.getTestCaseId());

        // Try to find the automation instance using several methods
        if (dto.getId() != null && dto.getId() > 1) {
            entity = automatedTestIssueService.find(dto.getId());
        }
        if (entity == null && testCase!=null && testCase.getAutomatedTestIssue() != null) {
            entity = testCase.getAutomatedTestIssue();
        }

        if (entity == null) {
            entity = automatedTestIssueService.getAutomatedTestIssue(testCase);
        }


        if (entity != null) {
            if (!entity.getIssueType().equals(IssueType.valueOf(dto.getIssueType()))) {
                entity.setIssueType(IssueType.valueOf(dto.getIssueType()));
            }

            if (!entity.getUserFixPriorityType().equals(UserFixPriorityType.valueOf(dto.getUserFixPriority()))) {
                entity.setUserFixPriorityType(UserFixPriorityType.valueOf(dto.getUserFixPriority()));
            }

        } else {
            // It is not persistent
            entity = AutomatedTestIssue.builder()
                    .testCase(testCase)
                    .issueType(IssueType.valueOf(dto.getIssueType()))
                    .enabled(true)
                    .timestamp(DateUtils.now())
                    .updated(DateUtils.now())
                    .userFixPriorityType(UserFixPriorityType.valueOf(dto.getUserFixPriority()))
                    .build();

        }

        entity.setNote(dto.getNote() == null ? null : noteMapper.convertToEntity(dto.getNote()));
        entity.setTriager(dto.getTriager() == null ? null : userService.find(dto.getTriager().getId()));
        entity.setProduct(getNullableByID(dto.getProductId(), id -> productService.find(id)));
        TestTriage testTriage = dto.getTestTriage() == null ? null : testTriageService.find(dto.getTestTriage().getId());
        automatedTestIssueService.updateTriageError(entity, testTriage);

        return entity;
    }


}
