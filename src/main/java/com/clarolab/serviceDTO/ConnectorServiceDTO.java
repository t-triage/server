/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.ConnectorDTO;
import com.clarolab.dto.ContainerDTO;
import com.clarolab.dto.push.ServiceAuthDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.ConnectorMapper;
import com.clarolab.model.Connector;
import com.clarolab.service.ConnectorService;
import com.clarolab.service.TTriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConnectorServiceDTO implements BaseServiceDTO<Connector, ConnectorDTO, ConnectorMapper> {

    @Autowired
    private ConnectorService service;

    @Autowired
    private ConnectorMapper mapper;

    @Autowired
    private ContainerServiceDTO containerServiceDTO;

    @Autowired
    private ServiceAuthServiceDTO serviceAuthServiceDTO;

    @Override
    public TTriageService<Connector> getService() {
        return service;
    }

    @Override
    public Mapper<Connector, ConnectorDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<Connector, ConnectorDTO, ConnectorMapper> getServiceDTO() {
        return this;
    }

    public List<ContainerDTO> getAllContainers(Long id){
        return containerServiceDTO.convertToDTO(service.getAllContainers(id));
    }

    public List<ContainerDTO> getAllContainers(){
        return containerServiceDTO.convertToDTO(service.getAllContainers());
    }

    public Boolean populate(Long id){
        return service.populate(id);
    }

    public Boolean populate(){
        return service.populateAll();
    }

    public Boolean isValid(Long id) {
        return service.isValid(id);
    }

    public ServiceAuthDTO newServiceAuth(Long id) {
        return serviceAuthServiceDTO.newServiceAuth(id);
    }

    public ServiceAuthDTO getServiceAuth(Long id) {
        return serviceAuthServiceDTO.getServiceAuth(id);
    }
}
