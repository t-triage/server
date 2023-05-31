/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.PropertyDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.PropertyMapper;
import com.clarolab.model.Property;
import com.clarolab.service.PropertyService;
import com.clarolab.service.TTriageService;
import com.clarolab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PropertyServiceDTO implements BaseServiceDTO<Property, PropertyDTO, PropertyMapper> {

    @Autowired
    private PropertyService service;

    @Autowired
    private PropertyMapper mapper;

    @Autowired
    private UserService userService;

    @Override
    public TTriageService<Property> getService() {
        return service;
    }

    @Override
    public Mapper<Property, PropertyDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<Property, PropertyDTO, PropertyMapper> getServiceDTO() {
        return this;
    }

    public List<PropertyDTO> findAllByName(String name) {
        return convertToDTO(service.findAllByName(name));
    }

    public PropertyDTO findByName(String name) {
        return convertToDTO(service.findByName(name));
    }

    public Boolean isInternalUserEnabled() {
        return userService.isInternalUserEnabled();
    }
}
