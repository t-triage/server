/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.FunctionalityDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.FunctionalityMapper;
import com.clarolab.model.manual.Functionality;
import com.clarolab.service.FunctionalityService;
import com.clarolab.service.TTriageService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log
public class FunctionalityServiceDTO implements BaseServiceDTO<Functionality, FunctionalityDTO, FunctionalityMapper> {

    @Autowired
    private FunctionalityService service;

    @Autowired
    private FunctionalityMapper mapper;

    @Override
    public TTriageService<Functionality> getService() {
        return service;
    }

    @Override
    public Mapper<Functionality, FunctionalityDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<Functionality, FunctionalityDTO, FunctionalityMapper> getServiceDTO() {
        return this;
    }
    
    public FunctionalityDTO findFunctionalityByExternalId(String externalId) {return convertToDTO(service.findByExternalId(externalId)); }
    
    public List<FunctionalityDTO> searchFunctionality(String name) { return convertToDTO(service.search(name)); }

}
