/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.ExecutorStatDTO;
import com.clarolab.event.analytics.ExecutorStat;
import com.clarolab.event.analytics.ExecutorStatService;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.ExecutorStatMapper;
import com.clarolab.service.TTriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExecutorStatServiceDTO implements BaseServiceDTO<ExecutorStat, ExecutorStatDTO, ExecutorStatMapper> {

    @Autowired
    private ExecutorStatService service;

    @Autowired
    private ExecutorStatMapper mapper;

    @Override
    public TTriageService<ExecutorStat> getService() {
        return service;
    }

    @Override
    public Mapper<ExecutorStat, ExecutorStatDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<ExecutorStat, ExecutorStatDTO, ExecutorStatMapper> getServiceDTO() {
        return this;
    }
}
