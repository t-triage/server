/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.push.ServiceAuthDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.auth.ServiceAuth;
import com.clarolab.service.ConnectorService;
import com.clarolab.service.ServiceAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.getNullableByID;
import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class ServiceAuthMapper implements Mapper<ServiceAuth, ServiceAuthDTO> {

    @Autowired
    private ServiceAuthService serviceAuthService;

    @Autowired
    private ConnectorService connectorService;


    @Override
    public ServiceAuthDTO convertToDTO(ServiceAuth entity) {
        if(entity == null) return null;
        ServiceAuthDTO dto = new ServiceAuthDTO();
        setEntryFields(entity, dto);

        dto.setConnector(entity.getConnector()==null? null : entity.getConnector().getId());
        dto.setSecretID("HIDDEN");
        dto.setClientID(entity.getClientId());

        return dto;
    }

    public ServiceAuthDTO convertFullToDTO(ServiceAuth entity) {
        ServiceAuthDTO dto = convertToDTO(entity);
        dto.setSecretID(entity.getSecretId());
        return dto;
    }

    @Override
    public ServiceAuth convertToEntity(ServiceAuthDTO dto) {
        ServiceAuth serviceAuth;
        if (dto.getId() == null || dto.getId() < 1) {
            serviceAuth = ServiceAuth.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .clientId(dto.getClientID())
                    .secretId(dto.getSecretID())
                    .connector(getNullableByID(dto.getConnector(), id -> connectorService.find(id)))
                    .build();

        } else {
            serviceAuth = serviceAuthService.find(dto.getId());
            serviceAuth.setEnabled(dto.getEnabled());
            serviceAuth.setClientId(dto.getClientID());
            serviceAuth.setSecretId(dto.getSecretID());
            serviceAuth.setConnector(getNullableByID(dto.getConnector(), id -> connectorService.find(id)));
        }
        return serviceAuth;
    }

}
