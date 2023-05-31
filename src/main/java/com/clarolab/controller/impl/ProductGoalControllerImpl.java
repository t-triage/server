/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.controller.ProductGoalController;
import com.clarolab.dto.ProductGoalDTO;
import com.clarolab.model.Product;
import com.clarolab.service.ProductService;
import com.clarolab.service.TTriageService;
import com.clarolab.service.UserService;
import com.clarolab.serviceDTO.ProductGoalServiceDTO;
import com.clarolab.serviceDTO.ProductServiceDTO;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@CrossOrigin
@RestController
@Log
public class ProductGoalControllerImpl extends BaseControllerImpl<ProductGoalDTO> implements ProductGoalController {

    @Autowired
    private ProductGoalServiceDTO productGoalServiceDTO;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductServiceDTO productServiceDTO;

    @Autowired
    private ProductService productService;

    @Override
    protected TTriageService<ProductGoalDTO> getService() {
        return productGoalServiceDTO;
    }
    
    @Override
    @ApiIgnore
    public ResponseEntity<Long> delete(Long id) {
        return super.delete(id);
    }

    @Override
    public ResponseEntity<ProductGoalDTO> createNewGoal(Long productId, @RequestBody ProductGoalDTO entity) {
        ProductGoalDTO newGoal = getService().save(entity);
        Product product = productServiceDTO.findEntity(productId);
        //CREARLE EL GOAL A PRODUCT
        product.setGoal(productGoalServiceDTO.findEntity(newGoal.getId()));
        productService.update(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(newGoal);
    }
}
