/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.FunctionalityDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.manual.Functionality;
import com.clarolab.model.manual.service.ManualTestCaseService;
import com.clarolab.service.FunctionalityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class FunctionalityMapper implements Mapper<Functionality, FunctionalityDTO> {

    @Autowired
    private ManualTestCaseService manualTestCaseService;

    @Autowired
    private FunctionalityService functionalityService;


    @Override
    public FunctionalityDTO convertToDTO(Functionality entry) {
        if (entry == null) {
            return null;
        }
        
        FunctionalityDTO dto = new FunctionalityDTO();

        setEntryFields(entry, dto);

        dto.setName(entry.getName());
        dto.setRisk(entry.getRisk());
        dto.setStory(entry.getStory());
        dto.setExternalId(entry.getExternalId());

        return dto;
    }

    @Override
    public Functionality convertToEntity(FunctionalityDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Functionality entity;
        if (dto.getId() == null || dto.getId() < 1) {
            entity = Functionality.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .name(dto.getName())
                    .risk(dto.getRisk())
                    .story(dto.getStory())
                    .externalId(dto.getExternalId())
                    .build();
        } else {
            entity = functionalityService.find(dto.getId());
            entity.setEnabled(dto.getEnabled());
            entity.setName(dto.getName());
            entity.setRisk(dto.getRisk());
            entity.setStory(dto.getStory());
            entity.setExternalId(dto.getExternalId());

        }
        
        return entity;
    }
}
