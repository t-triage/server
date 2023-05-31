/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.AutomatedComponentDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.component.AutomatedComponent;
import com.clarolab.service.AutomatedComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class AutomatedComponentMapper implements Mapper<AutomatedComponent, AutomatedComponentDTO> {

    @Autowired
    private AutomatedComponentService automatedComponentService;

    @Override
    public AutomatedComponentDTO convertToDTO(AutomatedComponent entity) {
        AutomatedComponentDTO automatedComponentDTO = new AutomatedComponentDTO();

        setEntryFields(entity, automatedComponentDTO);

        automatedComponentDTO.setName(entity.getName());
        automatedComponentDTO.setDescription(entity.getDescription());

        return automatedComponentDTO;
    }

    @Override
    public AutomatedComponent convertToEntity(AutomatedComponentDTO dto) {
        if (dto == null) {
            return null;
        }

        AutomatedComponent automatedComponent;
        if (dto.getId() == null || dto.getId() < 1) {
            automatedComponent = AutomatedComponent.builder()
                    .id(null)
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .build();
        } else {
            automatedComponent = automatedComponentService.find(dto.getId());
            automatedComponent.setName(dto.getName());
            automatedComponent.setDescription(dto.getDescription());
        }
        return automatedComponent;
    }
}
