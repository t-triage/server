/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.ProductGoalDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.ProductGoal;
import com.clarolab.service.ProductGoalService;
import com.clarolab.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class ProductGoalMapper implements Mapper<ProductGoal, ProductGoalDTO> {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductGoalService productGoalService;


    @Override
    public ProductGoalDTO convertToDTO(ProductGoal entry) {
        ProductGoalDTO dto = new ProductGoalDTO();

        setEntryFields(entry, dto);

        dto.setExpectedTestCase(entry.getExpectedTestCase());
        dto.setRequiredTestCase(entry.getRequiredTestCase());
        dto.setExpectedPassRate(entry.getExpectedPassRate());
        dto.setRequiredPassRate(entry.getRequiredPassRate());

        return dto;
    }

    @Override
    public ProductGoal convertToEntity(ProductGoalDTO dto) {
        ProductGoal entity;
        if (dto.getId() == null || dto.getId() < 1) {
            entity = ProductGoal.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .expectedTestCase(dto.getExpectedTestCase())
                    .requiredTestCase(dto.getRequiredTestCase())
                    .expectedPassRate(dto.getExpectedPassRate())
                    .requiredPassRate(dto.getRequiredPassRate())
                    .build();
        } else {
            entity = productGoalService.find(dto.getId());
            entity.setEnabled(dto.getEnabled());
            entity.setExpectedTestCase(dto.getExpectedTestCase());
            entity.setRequiredTestCase(dto.getRequiredTestCase());
            entity.setExpectedPassRate(dto.getExpectedPassRate());
            entity.setRequiredPassRate(dto.getRequiredPassRate());
        }
        
        return entity;
    }
}
