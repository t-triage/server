/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.aaa.util.AuthContextHelper;
import com.clarolab.dto.ManualTestExecutionDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.manual.ManualTestExecution;
import com.clarolab.model.manual.service.ManualTestCaseService;
import com.clarolab.model.manual.service.ManualTestExecutionService;
import com.clarolab.model.manual.service.ManualTestPlanService;
import com.clarolab.model.manual.types.ExecutionStatusType;
import com.clarolab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class ManualTestExecutionMapper implements Mapper<ManualTestExecution, ManualTestExecutionDTO> {

    @Autowired
    private UserService userService;

    @Autowired
    private ManualTestExecutionService manualTestExecutionService;

    @Autowired
    private ManualTestCaseMapper manualTestCaseMapper;

    @Autowired
    private ManualTestPlanMapper manualTestPlanMapper;

    @Autowired
    private ManualTestCaseService manualTestCaseService;

    @Autowired
    private ManualTestPlanService manualTestPlanService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthContextHelper authContextHelper;


    @Override
    public ManualTestExecutionDTO convertToDTO(ManualTestExecution manualTestExecution) {
        /* El manualTestExecution en esta capa NO deberia ser null. Si llega null es porque hay algo mal*/
        /*if (manualTestExecution == null) {
            return null;
        }*/
        ManualTestExecutionDTO manualTestExecutionDTO = new ManualTestExecutionDTO();

        setEntryFields(manualTestExecution, manualTestExecutionDTO);

        manualTestExecutionDTO.setTestCase(manualTestExecution.getTestCase() == null ? null : manualTestCaseMapper.convertToDTO(manualTestExecution.getTestCase()));
        manualTestExecutionDTO.setTestPlan(manualTestExecution.getTestPlan() == null ? null : manualTestPlanMapper.convertToDTO(manualTestExecution.getTestPlan()));
        manualTestExecutionDTO.setAssignee(manualTestExecution.getAssignee() == null ? null : userMapper.convertToDTO(manualTestExecution.getAssignee()));
        manualTestExecutionDTO.setEnvironment(manualTestExecution.getEnvironment());
        manualTestExecutionDTO.setComment(manualTestExecution.getComment());
        manualTestExecutionDTO.setExecutionOrder(manualTestExecution.getExecutionOrder());
        manualTestExecutionDTO.setStatus(manualTestExecution.getStatus() == null ? ExecutionStatusType.PENDING.name() : manualTestExecution.getStatus().name());

        return manualTestExecutionDTO;
    }

    @Override
    public ManualTestExecution convertToEntity(ManualTestExecutionDTO dto) {
        if (dto == null) {
            return null;
        }

        ManualTestExecution manualTestExecution;
        if (dto.getId() == null || dto.getId() < 1) {
            manualTestExecution = ManualTestExecution.builder()
                    .id(null)
                    .testCase(dto.getTestCase() == null ? null : manualTestCaseService.find(dto.getTestCase().getId()))
                    .testPlan(dto.getTestPlan() == null ? null : manualTestPlanService.find(dto.getTestPlan().getId()))
                    .assignee(dto.getAssignee() == null ? null : userService.find(dto.getAssignee().getId()))
                    .environment(dto.getEnvironment())
                    .status(ExecutionStatusType.valueOf(dto.getStatus()))
                    .comment(dto.getComment())
                    .executionOrder(dto.getExecutionOrder())
                    .build();
        } else {
            manualTestExecution = manualTestExecutionService.find(dto.getId());
//            manualTestExecution.setId(); Don't allow to update this.
            manualTestExecution.setEnabled(dto.getEnabled());
//            manualTestExecution.setTimestamp(dto.getTimestamp()); Don't allow to update this.
//            manualTestExecution.setUpdated(dto.getUpdated()); Don't allow to update this.
            manualTestExecution.setTestCase(dto.getTestCase() == null ? null : manualTestCaseService.find(dto.getTestCase().getId()));
            manualTestExecution.setTestPlan(dto.getTestPlan() == null ? null : manualTestPlanService.find(dto.getTestPlan().getId()));
            manualTestExecution.setEnvironment(dto.getEnvironment());
            manualTestExecution.setComment(dto.getComment());
            manualTestExecution.setExecutionOrder(dto.getExecutionOrder());
            ExecutionStatusType newStatus = ExecutionStatusType.valueOf(dto.getStatus());
            ExecutionStatusType oldStatus = manualTestExecution.getStatus();
            manualTestExecution.setStatus(newStatus);
            manualTestExecution.setAssignee(dto.getAssignee() == null ? null : userService.find(dto.getAssignee().getId()));
            if (oldStatus != null && newStatus != null && !oldStatus.equals(newStatus)) {
                manualTestExecution.setAssignee(authContextHelper.getCurrentUser());
            }
        }

        return manualTestExecution;
    }
}
