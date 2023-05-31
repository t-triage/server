/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.ManualTestPlanDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.manual.ManualTestPlan;
import com.clarolab.model.manual.service.ManualTestPlanService;
import com.clarolab.model.manual.types.PlanStatusType;
import com.clarolab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class ManualTestPlanMapper implements Mapper<ManualTestPlan, ManualTestPlanDTO> {

    @Autowired
    private UserService userService;

    @Autowired
    private ManualTestPlanService manualTestPlanService;

    @Autowired
    private UserMapper userMapper;

    @Override
    public ManualTestPlanDTO convertToDTO(ManualTestPlan manualTestPlan) {
        /* El manualTestPlan en esta capa NO deberia ser null. Si llega null es porque hay algo mal*/
        /*if (manualTestPlan == null) {
            return null;
        }*/
        ManualTestPlanDTO manualTestPlanDTO = new ManualTestPlanDTO();

        setEntryFields(manualTestPlan, manualTestPlanDTO);

        manualTestPlanDTO.setName(manualTestPlan.getName());
        manualTestPlanDTO.setDescription(manualTestPlan.getDescription());
        manualTestPlanDTO.setEnvironment(manualTestPlan.getEnvironment());
        manualTestPlanDTO.setAssignee(manualTestPlan.getAssignee() == null ? null : userMapper.convertToDTO(manualTestPlan.getAssignee()));
        manualTestPlanDTO.setFromDate(manualTestPlan.getFromDate());
        manualTestPlanDTO.setToDate(manualTestPlan.getToDate());
        manualTestPlanDTO.setStatus(manualTestPlan.getStatus() == null ? PlanStatusType.UNDEFINED.name() : manualTestPlan.getStatus().name());

        return manualTestPlanDTO;
    }

    @Override
    public ManualTestPlan convertToEntity(ManualTestPlanDTO dto) {
        if (dto == null) {
            return null;
        }

        ManualTestPlan manualTestPlan;
        if (dto.getId() == null || dto.getId() < 1) {
            manualTestPlan = ManualTestPlan.builder()
                    .id(null)
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .environment(dto.getEnvironment())
                    .assignee(dto.getAssignee() == null ? null : userService.find(dto.getAssignee().getId()))
                    .fromDate(dto.getFromDate())
                    .toDate(dto.getToDate())
                    .status(dto.getStatus() == null ? null : PlanStatusType.valueOf(dto.getStatus()))
                    .build();
        } else {
            manualTestPlan = manualTestPlanService.find(dto.getId());
//            manualTestPlan.setId(); Don't allow to update this.
            manualTestPlan.setEnabled(dto.getEnabled());
//            manualTestPlan.setTimestamp(dto.getTimestamp()); Don't allow to update this.
//            manualTestPlan.setUpdated(dto.getUpdated()); Don't allow to update this.
            manualTestPlan.setName(dto.getName());
            manualTestPlan.setDescription(dto.getDescription());
            manualTestPlan.setEnvironment(dto.getEnvironment());
            manualTestPlan.setAssignee(dto.getAssignee() == null ? null : userService.find(dto.getAssignee().getId()));
            manualTestPlan.setFromDate(dto.getFromDate());
            manualTestPlan.setToDate(dto.getToDate());
            manualTestPlan.setStatus(PlanStatusType.valueOf(dto.getStatus()));
        }

        return manualTestPlan;
    }
}
