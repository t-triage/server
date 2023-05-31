/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.controller.ProductController;
import com.clarolab.dto.ProductDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.ProductServiceDTO;
import com.clarolab.view.KeyValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class ProductControllerImpl extends BaseControllerImpl<ProductDTO> implements ProductController {

    @Autowired
    private ProductServiceDTO productService;

    @Override
    protected TTriageService<ProductDTO> getService() {
        return productService;
    }

    @Override
    public ResponseEntity<List<KeyValuePair>> getProductNames() {
        return ResponseEntity.ok(productService.getProductNames());
    }

    @Override
    public ResponseEntity<ProductDTO> update(Long id, String packageNames, String logPattern) {
        ProductDTO productDTO = productService.find(id);
        if (packageNames != null)
            productDTO.setPackageNames(packageNames);
        if (logPattern != null)
            productDTO.setLogPattern(logPattern);
        return ResponseEntity.ok(productService.update(productDTO));
    }

    @Override
    public ResponseEntity<Long> getProductAmount() {
        return ResponseEntity.ok(productService.count());
    }
}
