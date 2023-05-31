/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.aaa.util.AuthContextHelper;
import com.clarolab.dto.TestExecutionDTO;
import com.clarolab.dto.TestTriageDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.*;
import com.clarolab.model.types.ApplicationFailType;
import com.clarolab.model.types.StateType;
import com.clarolab.model.types.TestFailType;
import com.clarolab.service.AutomatedTestIssueService;
import com.clarolab.service.BuildService;
import com.clarolab.service.IssueTicketService;
import com.clarolab.service.TestTriageService;
import com.clarolab.serviceDTO.NoteServiceDTO;
import com.clarolab.serviceDTO.UserServiceDTO;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Level;

import static com.clarolab.mapper.MapperHelper.*;
import static com.clarolab.model.helper.DeduceExplanationHelper.getDeducedReasonExplanation;

@Component
@Log
public class TestTriageMapper implements Mapper<TestTriage, TestTriageDTO> {

    @Autowired
    private UserServiceDTO userServiceDTO;

    @Autowired
    private BuildService buildService;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private TestExecutionMapper testExecutionMapper;

    @Autowired
    private AutomatedTestIssueService automatedTestIssueService;

    @Autowired
    private NoteServiceDTO noteServiceDTO;

    @Autowired
    private AuthContextHelper authContextHelper;

    @Autowired
    private IssueTicketService issueTicketService;
    
    @Autowired
    private UserMapper userMapper;

    @Override
    public TestTriageDTO convertToDTO(TestTriage tt) {

        TestTriageDTO ttDTO = convertToKanban(tt);
        setEntryFields(tt, ttDTO);
        
        ttDTO.getTestExecution().setErrorDetails(tt.getTextExecutionErrorDetails());
        ttDTO.getTestExecution().setErrorStackTrace(tt.getTextExecutionStackTrace());
        ttDTO.setTags(tt.getTags());
        ttDTO.setExecutorName(tt.getExecutorName());
        ttDTO.setFile(tt.getFile());
        ttDTO.setExecutionDate(tt.getExecutionDate());

        ttDTO.setTriager(tt.getTriager() == null ? null : userMapper.convertToDTO(tt.getTriager()));

        AutomatedTestIssue automation = tt.getTestCase().getAutomatedTestIssue();
        if (tt.hasTestBug()) {
            ttDTO.setAutomatedTestIssueId(automation == null ? null : automation.getId());
        }
        if (tt.hasProductBug()) {
            IssueTicket ticket = tt.getTestCase().getIssueTicket();
            ttDTO.setIssueTicketId(ticket == null ? null : ticket.getId());
        }
        ttDTO.setSnooze(tt.getSnooze());
        ttDTO.setExpired(tt.isExpired());

        if (tt.getBuild() != null) {
            ttDTO.setProductPackages(tt.getProduct().getPackageNames());
            ttDTO.setContainerName(tt.getContainer().getName());
            ttDTO.setExternalBuildURL(tt.getBuildUrl());
            ttDTO.setBuildTriageId(tt.getBuildTriage().getId());
            ttDTO.setProductId(tt.getProduct() == null ? null : tt.getProduct().getId());

            // Past Runs
            ttDTO.setPreviousTriage(testTriageService.getPreviousTriageIds(tt));
            if (tt.hasNote()) {
                TestTriage lastTriageWithNote = testTriageService.findValidPreviousTriageWithNote(tt);
                if (lastTriageWithNote != null) {
                    ttDTO.setLastNote(noteServiceDTO.convertToDTO(lastTriageWithNote.getNote()));
                }
            }
        }

        if (automation != null && !tt.isTestWontFix() && !tt.hasTestBug()) {
            // The bug was filed in another triage (same or different executor)
            ttDTO.setAutomatedTestIssueId(automation.getId());
            ttDTO.setTestFailType(TestFailType.TEST_ASSIGNED_TO_FIX.name());
        }

        ttDTO.setHasSteps(tt.hasSteps());

        return ttDTO;
    }

    @Override
    public TestTriage convertToEntity(TestTriageDTO dto) {
        TestTriage testTriage;
        if (dto.getId() == null || dto.getId() < 1) {
            // This should never happen
            testTriage = TestTriage.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .buildParent(getNullableByID(dto.getBuild(), id -> buildService.find(id)))
                    .triager(userServiceDTO.convertToEntity(dto.getTriager()))
                    .testExecution(testExecutionMapper.convertToEntity(dto.getTestExecution()))
                    .note(noteServiceDTO.convertToEntity(dto.getNote()))

                    .currentState(StateType.valueOf(dto.getCurrentState()))
                    .applicationFailType(ApplicationFailType.valueOf(dto.getApplicationFailType()))
                    .testFailType(TestFailType.valueOf(dto.getTestFailType()))
                    .triaged(dto.isTriaged())
                    .tags(dto.getTags())
                    .executorName(dto.getExecutorName())
                    .rank(dto.getRank())
                    .snooze(dto.getSnooze())
                    .file(dto.getFile())
                    .build();

        } else {
            testTriage = testTriageService.find(dto.getId());
            testTriage.setEnabled(dto.getEnabled());
            testTriage.setTriager(dto.getTriager() == null ? authContextHelper.getCurrentUser() : userServiceDTO.getMapper().convertToEntity(dto.getTriager()));
            testTriage.setNote(dto.getNote() == null ? null : noteServiceDTO.convertToEntity(dto.getNote()));
            testTriage.setApplicationFailType(ApplicationFailType.valueOf(dto.getApplicationFailType()));
            testTriage.setTestFailType(TestFailType.valueOf(dto.getTestFailType()));
            testTriage.setTriaged(dto.isTriaged());
            testTriage.setRank(dto.getRank());
            testTriage.setSnooze(dto.getSnooze());
            testTriage.setFile(dto.getFile());
            testTriage.setUpdatedByUser(true);
        }

        return testTriage;
    }

    /**
     * It generates a new DTO with only top level objects with any embedded objects.
     * Used by Error and Test Occurrence list
     */
    static TestTriageDTO basicTestTriageDTO(TestTriage tt) {
        TestTriageDTO ttDTO = new TestTriageDTO();

        setEntryFields(tt, ttDTO);

        if (tt.getBuild() != null) {
            ttDTO.setBuild(tt.getBuildId());
            ttDTO.setExecutorName(tt.getExecutorName());
            ttDTO.setExecutorId(tt.getExecutorId());
            ttDTO.setBuildNumber(tt.getBuildNumber());
        }
        ttDTO.setTestCaseId(tt.getTestCase().getId());
        ttDTO.setTriaged(tt.isTriaged());
        ttDTO.setAutoTriaged(tt.isAutomatedTriaged());
        ttDTO.setFlaky(tt.isFlaky());
        ttDTO.setApplicationFailType(getEnumName(tt.getApplicationFailType()));
        ttDTO.setCurrentState(getEnumName(tt.getCurrentState()));
        ttDTO.setDeducedReason(getDeducedReasonExplanation(tt.getStateReasonType()));
        ttDTO.setTestFailType(getEnumName(tt.getTestFailType()));

        ttDTO.setFile(tt.getFile());
        ttDTO.setRank(tt.getRank());
        ttDTO.setExecutionDate(tt.getExecutionDate());

        return ttDTO;
    }

    // Data displayed at the kanban
    public TestTriageDTO convertToKanban(TestTriage tt) {

        TestTriageDTO ttDTO = basicTestTriageDTO(tt);

        if (tt.getBuild() != null) {
            ttDTO.setContainerId(tt.getContainer().getId());
        }

        TestExecutionDTO testExecution = testExecutionMapper.convertToDTO(tt.getTestExecution());
        ttDTO.setTestExecution(testExecution);
        ttDTO.setTestExecutionId(testExecution.getId());

        ttDTO.setNote(tt.getNote() == null ? null : noteServiceDTO.convertToDTO(tt.getNote()));
        ttDTO.setTestFailType(getEnumName(tt.getTestFailType()));
        ttDTO.setTags(tt.getTags());
        ttDTO.setApplicationFailType(getEnumName(tt.getApplicationFailType()));
        ttDTO.setAutomatedTestIssueId(tt.getAutomationIssueId());
        ttDTO.setTriager(tt.getTriager() == null ? null : userMapper.convertToDTOTiny(tt.getTriager()));

        // cleans data that is not displayed at kanban
        ttDTO.getTestExecution().setErrorStackTrace(null);
        ttDTO.getTestExecution().setErrorDetails(null);
        ttDTO.setUpdated(null);
        ttDTO.setTimestamp(null);
        ttDTO.setTags(null);
        ttDTO.setExecutorName(null);
        ttDTO.setFile(null);
        ttDTO.setExecutionDate(null);
        testExecution.setTimestamp(null);
        testExecution.setReport(null);
        
        // Important Past info
        TestTriage previous = tt.getPreviousTriage();
        if (previous != null) {
            try {
                ttDTO.setPastNote(previous.getNote() == null ? null : noteServiceDTO.convertToDTO(previous.getNote()));
                ttDTO.setPastApplicationFailType(getEnumName(previous.getApplicationFailType()));
                ttDTO.setPastState(getEnumName(previous.getCurrentState()));
                ttDTO.setPastTestFailType(getEnumName(previous.getTestFailType()));
                ttDTO.setPastTriageTimestamp(previous.getExecutionDate());
            } catch (Exception e) {
                log.log(Level.WARNING, "Error getting previous triage", e);
            }
        }

        ttDTO.setNewInfo(tt.isUpdatedByUser());

        AutomatedTestIssue automation = tt.getTestCase().getAutomatedTestIssue();
        if (automation != null && !tt.isTestWontFix() && !tt.hasTestBug()) {
            // The bug was filed in another triage (same or different executor)
            ttDTO.setTestFailType(TestFailType.TEST_ASSIGNED_TO_FIX.name());
            ttDTO.setNewInfo(true);
        }

        return ttDTO;
    }

    // Data displayed at the automation issue list
    public TestTriageDTO convertToAutomationList(TestTriage tt) {

        TestTriageDTO ttDTO = basicTestTriageDTO(tt);
        ttDTO.setTestExecution(testExecutionMapper.convertToDTO(tt.getTestExecution()));

        ttDTO.setNote(tt.getNote() == null ? null : noteServiceDTO.convertToDTO(tt.getNote()));
        ttDTO.setTestFailType(getEnumName(tt.getTestFailType()));
        ttDTO.setApplicationFailType(getEnumName(tt.getApplicationFailType()));


        return ttDTO;
    }


    // Data displayed at the Automation Repository
    public TestTriageDTO convertToRepository(TestTriage tt) {

        TestTriageDTO ttDTO = basicTestTriageDTO(tt);

        if (tt.getBuild() != null) {
            ttDTO.setContainerId(tt.getContainer().getId());
        }

        TestExecutionDTO testExecution = testExecutionMapper.convertToDTO(tt.getTestExecution());
        ttDTO.setTestExecution(testExecution);
        ttDTO.setTestExecutionId(testExecution.getId());
        ttDTO.setExpired(tt.isExpired());

        ttDTO.setTestFailType(getEnumName(tt.getTestFailType()));
        ttDTO.setTags(tt.getTags());
        ttDTO.setApplicationFailType(getEnumName(tt.getApplicationFailType()));
        ttDTO.setAutomatedTestIssueId(tt.getAutomationIssueId());
        ttDTO.setTriager(tt.getTriager() == null ? null : userMapper.convertToDTOTiny(tt.getTriager()));

        // cleans data that is not displayed at Automation Repository
        ttDTO.getTestExecution().setErrorStackTrace(null);
        ttDTO.getTestExecution().setErrorDetails(null);
        ttDTO.setUpdated(null);
        ttDTO.setTimestamp(null);
        ttDTO.setFile(null);
        testExecution.setTimestamp(null);
        testExecution.setReport(null);

        AutomatedTestIssue automation = tt.getTestCase().getAutomatedTestIssue();
        if (automation != null && !tt.isTestWontFix() && !tt.hasTestBug()) {
            // The bug was filed in another triage (same or different executor)
            ttDTO.setTestFailType(TestFailType.TEST_ASSIGNED_TO_FIX.name());
            ttDTO.setNewInfo(true);
        }

        if (tt.hasProductBug()) {
            IssueTicket ticket = tt.getTestCase().getIssueTicket();
            ttDTO.setIssueTicketId(ticket == null ? null : ticket.getId());
        }

        return ttDTO;
    }

    // Data displayed at the kanban
    public TestTriageDTO convertToDTO(Pipeline pipeline, TriageSpec triageSpec, TestCase tt) {

        TestTriageDTO ttDTO = new TestTriageDTO();
        setEntryFields(tt, ttDTO);

        ttDTO.setBuild(1L);
        ttDTO.setTestCaseId(tt.getId());
        ttDTO.setTriaged(false);
        ttDTO.setAutoTriaged(false);
        ttDTO.setFlaky(false);

        TestExecutionDTO testExecution = testExecutionMapper.convertToDTO(tt);
        ttDTO.setTestExecution(testExecution);
        ttDTO.setTestExecutionId(0L);
        ttDTO.setContainerId(triageSpec.getContainer().getId());
        ttDTO.setTriager(triageSpec.getTriager() == null ? null : userMapper.convertToDTOTiny(triageSpec.getTriager()));

        return ttDTO;
    }




}
