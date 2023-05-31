/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.ConnectorDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.Connector;
import com.clarolab.model.types.ConnectorType;
import com.clarolab.service.ConnectorService;
import com.clarolab.service.ContainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.*;

@Component
public class ConnectorMapper implements Mapper<Connector, ConnectorDTO> {

    @Autowired
    private ConnectorService connectorService;
    @Autowired
    private ContainerService containerService;

    @Override
    public ConnectorDTO convertToDTO(Connector connector) {
        ConnectorDTO connectorDTO = new ConnectorDTO();

        setEntryFields(connector, connectorDTO);

        connectorDTO.setName(connector.getName());
        connectorDTO.setUrl(connector.getUrl());
        connectorDTO.setUserName(connector.getUserName());
        connectorDTO.setUserToken(connector.getUserToken());
        connectorDTO.setType(getEnumName(connector.getType()));
        connectorDTO.setContainers(getIDList(connector.getContainers()));

        return connectorDTO;
    }

    @Override
    public Connector convertToEntity(ConnectorDTO dto) {
        Connector connector;
        if (dto.getId() == null || dto.getId() < 1) {
            connector = Connector.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .name(dto.getName())
                    .url(dto.getUrl())
                    .userName(dto.getUserName())
                    .userToken(dto.getUserToken())
                    .type(ConnectorType.valueOf(dto.getType()))
                    .build();
        } else {
            connector = connectorService.find(dto.getId());
            connector.setEnabled(dto.getEnabled());
            connector.setName(dto.getName());
            connector.setUrl(dto.getUrl());
            connector.setUserName(dto.getUserName());
            connector.setUserToken(dto.getUserToken());
            connector.setType(ConnectorType.valueOf(dto.getType()));

            /*Don't allow to update these.*/
            /*connector.setId(dto.getId());
            connector.setServiceToken(dto.getServiceToken());
            connector.setTimestamp(dto.getTimestamp());
            connector.setUpdated(dto.getUpdated()); */
        }

        return connector;
    }
}
