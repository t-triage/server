/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.TrendGoalDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.TrendGoalMapper;
import com.clarolab.model.TrendGoal;
import com.clarolab.service.TTriageService;
import com.clarolab.service.TrendGoalService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log
public class TrendGoalServiceDTO implements BaseServiceDTO<TrendGoal, TrendGoalDTO, TrendGoalMapper> {

    @Autowired
    private TrendGoalService service;

    @Autowired
    private TrendGoalMapper mapper;

    @Override
    public TTriageService<TrendGoal> getService() {
        return service;
    }

    @Override
    public Mapper<TrendGoal, TrendGoalDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<TrendGoal, TrendGoalDTO, TrendGoalMapper> getServiceDTO() {
        return this;
    }


}
