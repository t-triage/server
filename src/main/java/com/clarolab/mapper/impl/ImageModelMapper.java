/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.ImageModelDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.ImageModel;
import com.clarolab.service.ImageModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class ImageModelMapper implements Mapper<ImageModel, ImageModelDTO> {


    @Autowired
    private ImageModelService imageModelService;

    @Override
    public ImageModelDTO convertToDTO(ImageModel entity) {

        ImageModelDTO dto = new ImageModelDTO();

        setEntryFields(entity, dto);

        dto.setDescription(entity.getDescription());
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        dto.setData(entity.getData());

        return dto;
    }

    @Override
    public ImageModel convertToEntity(ImageModelDTO dto) {
        if (dto == null) {
            return null;
        }
        ImageModel entity;
        if (dto.getId() == null || dto.getId() < 1) {
            entity = ImageModel.builder()
                    .id(null)
                    .enabled(true)
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .data(dto.getData())
                    .type(dto.getType())
                    .build();

        } else {
            entity = imageModelService.find(dto.getId());
            entity.setEnabled(dto.getEnabled());
            entity.setType(dto.getType());
            entity.setData(dto.getData());
            entity.setName(dto.getName());
            entity.setDescription(dto.getDescription());
        }
        return entity;
    }

}
