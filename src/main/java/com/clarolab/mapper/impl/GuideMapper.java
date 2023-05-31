/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.GuideDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.onboard.Guide;
import com.clarolab.service.GuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class GuideMapper implements Mapper<Guide, GuideDTO> {

    @Autowired
    private GuideService guideService;

    @Override
    public GuideDTO convertToDTO(Guide entity) {
        GuideDTO dto = new GuideDTO();

        setEntryFields(entity, dto);

        dto.setElementType(entity.getElementType().getType());

        dto.setPageUrl(entity.getPageUrl());
        dto.setPageIdentifier(entity.getPageIdentifier());
        dto.setPageCondition(entity.getPageCondition());

        dto.setTitle(entity.getTitle());
        dto.setText(entity.getText());
        dto.setIcon(entity.getIcon());
        dto.setImage(entity.getImage());
        dto.setVideo(entity.getVideo());
        dto.setHtml(entity.getHtml());

        return dto;
    }

    @Override
    public Guide convertToEntity(GuideDTO dto) {
        Guide entity;
        if (dto.getId() == null || dto.getId() < 1) {
            entity = Guide.builder()
                    .build();
        } else {
            entity = guideService.find(dto.getId());
        }
        return entity;
    }
}
