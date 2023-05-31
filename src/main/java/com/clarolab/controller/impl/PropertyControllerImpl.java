/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.controller.PropertyController;
import com.clarolab.dto.PropertyDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.PropertyServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class PropertyControllerImpl extends BaseControllerImpl<PropertyDTO> implements PropertyController {

    @Autowired
    private PropertyServiceDTO propertyService;

    @Override
    protected TTriageService<PropertyDTO> getService() {
        return propertyService;
    }

    @Override
    public ResponseEntity<List<PropertyDTO>> findAllByName(@PathVariable String name) {
        return ResponseEntity.ok(propertyService.findAllByName(name));
    }

    @Override
    public ResponseEntity<PropertyDTO> getByName(@PathVariable String name) {
        return ResponseEntity.ok(propertyService.findByName(name));
    }

    @Override
    public ResponseEntity<Boolean> isInternalUserEnabled() {
        return ResponseEntity.ok(propertyService.isInternalUserEnabled());
    }
}
