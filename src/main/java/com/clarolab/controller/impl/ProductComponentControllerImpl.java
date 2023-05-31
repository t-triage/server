/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.controller.ProductComponentController;
import com.clarolab.dto.ProductComponentDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.ProductComponentServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class ProductComponentControllerImpl extends BaseControllerImpl<ProductComponentDTO> implements ProductComponentController {

    @Autowired
    private ProductComponentServiceDTO productComponentServiceDTO;

    @Override
    protected TTriageService<ProductComponentDTO> getService() {
        return productComponentServiceDTO;
    }

    @Override
    public ResponseEntity<Page<ProductComponentDTO>> search(String name, boolean defaultComponents1) {
        return ResponseEntity.ok(new PageImpl<>(productComponentServiceDTO.search(name, defaultComponents1)));
    }

    @Override
    public ResponseEntity<Page<ProductComponentDTO>> suggested(Long component1, Long component2) {
        return ResponseEntity.ok(new PageImpl<>(productComponentServiceDTO.suggested(component1, component2)));
    }

}
