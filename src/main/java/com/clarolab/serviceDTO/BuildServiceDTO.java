/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.BuildDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.BuildMapper;
import com.clarolab.model.Build;
import com.clarolab.service.BuildService;
import com.clarolab.service.TTriageService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log
public class BuildServiceDTO implements BaseServiceDTO<Build, BuildDTO, BuildMapper> {

    @Autowired
    private BuildService service;

    @Autowired
    private BuildMapper mapper;

    @Override
    public TTriageService<Build> getService() {
        return service;
    }

    @Override
    public Mapper<Build, BuildDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<Build, BuildDTO, BuildMapper> getServiceDTO() {
        return this;
    }

}
