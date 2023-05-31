/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.ProductGoalDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.ProductGoalMapper;
import com.clarolab.model.ProductGoal;
import com.clarolab.service.ProductGoalService;
import com.clarolab.service.TTriageService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log
public class ProductGoalServiceDTO implements BaseServiceDTO<ProductGoal, ProductGoalDTO, ProductGoalMapper> {

    @Autowired
    private ProductGoalService service;

    @Autowired
    private ProductGoalMapper mapper;

    @Override
    public TTriageService<ProductGoal> getService() {
        return service;
    }

    @Override
    public Mapper<ProductGoal, ProductGoalDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<ProductGoal, ProductGoalDTO, ProductGoalMapper> getServiceDTO() {
        return this;
    }


}
