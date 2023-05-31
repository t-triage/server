/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.ContainerDTO;
import com.clarolab.dto.TriageSpecDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.TriageSpecMapper;
import com.clarolab.model.TriageSpec;
import com.clarolab.service.TTriageService;
import com.clarolab.service.TriageSpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TriageSpecServiceDTO implements BaseServiceDTO<TriageSpec, TriageSpecDTO, TriageSpecMapper> {

    @Autowired
    private TriageSpecService service;

    @Autowired
    private TriageSpecMapper mapper;

    @Override
    public TTriageService<TriageSpec> getService() {
        return service;
    }

    @Override
    public Mapper<TriageSpec, TriageSpecDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<TriageSpec, TriageSpecDTO, TriageSpecMapper> getServiceDTO() {
        return this;
    }

    public TriageSpecDTO save(TriageSpecDTO dto, ContainerDTO containerDTO) {
        dto.setContainer(containerDTO.getId());
        return save(dto);
    }
}
