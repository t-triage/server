/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.DeadlineDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.DeadlineMapper;
import com.clarolab.model.Deadline;
import com.clarolab.service.DeadlineService;
import com.clarolab.service.TTriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeadlineServiceDTO implements BaseServiceDTO<Deadline, DeadlineDTO, DeadlineMapper> {

    @Autowired
    private DeadlineService service;

    @Autowired
    private DeadlineMapper mapper;


    @Override
    public TTriageService<Deadline> getService() {
        return service;
    }

    @Override
    public Mapper<Deadline, DeadlineDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<Deadline, DeadlineDTO, DeadlineMapper> getServiceDTO() {
        return this;
    }
}
