/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.push.ServiceAuthDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.ServiceAuthMapper;
import com.clarolab.model.Connector;
import com.clarolab.model.auth.ServiceAuth;
import com.clarolab.service.ConnectorService;
import com.clarolab.service.ServiceAuthService;
import com.clarolab.service.TTriageService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log
public class ServiceAuthServiceDTO implements BaseServiceDTO<ServiceAuth, ServiceAuthDTO, ServiceAuthMapper> {

    @Autowired
    private ServiceAuthService service;

    @Autowired
    private ServiceAuthMapper mapper;

    @Autowired
    private ConnectorService connectorService;

    @Override
    public TTriageService<ServiceAuth> getService() {
        return service;
    }

    @Override
    public Mapper<ServiceAuth, ServiceAuthDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<ServiceAuth, ServiceAuthDTO, ServiceAuthMapper> getServiceDTO() {
        return this;
    }

    public ServiceAuthDTO newServiceAuth(Long id) {
        Connector connector = connectorService.find(id);
        return mapper.convertFullToDTO(service.newServiceAuth(connector));
    }

    public ServiceAuthDTO getServiceAuth(Long id) {
        Connector connector = connectorService.find(id);
        return convertToDTO(service.getServiceAuth(connector));
    }
}
