/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.PropertyDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.Property;
import com.clarolab.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class PropertyMapper implements Mapper<Property, PropertyDTO> {

    @Autowired
    private PropertyService propertyService;

    @Override
    public PropertyDTO convertToDTO(Property property) {
        if (property == null) {
            return null;
        }

        PropertyDTO propertyDTO = new PropertyDTO();

        setEntryFields(property, propertyDTO);

        propertyDTO.setName(property.getName());
        propertyDTO.setDescription(property.getDescription());
        if (property.isUseLongValue()) {
            propertyDTO.setValue(property.getLongValue());
        } else {
            propertyDTO.setValue(property.getValue());
        }

        return propertyDTO;
    }

    @Override
    public Property convertToEntity(PropertyDTO dto) {
        Property property;
        if (dto.getId() == null || dto.getId() < 1) {
            property = Property.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .build();
            if (dto.getValue().length() > 200) {
                property.setUseLongValue(true);
                property.setLongValue(dto.getValue());
            } else {
                property.setUseLongValue(false);
                property.setValue(dto.getValue());
            }
        } else {
            property = propertyService.find(dto.getId());
            property.setEnabled(dto.getEnabled());
            property.setName(dto.getName());
            property.setDescription(dto.getDescription());

            if (property.isUseLongValue()) {
                String oldValue = property.getLongValue();
                property.setLongValue(dto.getValue());
                propertyService.valueChanged(property.getName(), oldValue, dto.getValue());
            } else {
                String oldValue = property.getValue();
                property.setValue(dto.getValue());
                propertyService.valueChanged(property.getName(), oldValue, dto.getValue());
            }
        }
        return property;
    }

}
