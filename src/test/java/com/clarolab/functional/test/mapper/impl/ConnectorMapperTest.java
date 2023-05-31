/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.mapper.impl;

import com.clarolab.dto.ConnectorDTO;
import com.clarolab.functional.test.mapper.AbstractMapperTest;
import com.clarolab.mapper.impl.ConnectorMapper;
import com.clarolab.model.Connector;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ConnectorMapperTest extends AbstractMapperTest<Connector, ConnectorDTO> {

    @Autowired
    private ConnectorMapper mapper;

    @Test
    public void testEntityToDTOConversion() {
       Connector connector = getEntity();
       ConnectorDTO connectorDTO = mapper.convertToDTO(connector);
       this.assertConversion(connector, connectorDTO);
    }

    @Test
    public void testDTOToEntityConversion() {
        ConnectorDTO connectorDTO = getDTO();

        //Override Enums since are not random.
        connectorDTO.setType("JENKINS");

        Connector connector = mapper.convertToEntity(connectorDTO);
        this.assertConversion(connector, connectorDTO);
    }

    @Override
    public void assertConversion(Connector connector, ConnectorDTO connectorDTO) {
        super.assertConversion(connector, connectorDTO);

        Assert.assertEquals(connector.getName(), connectorDTO.getName());
        Assert.assertEquals(connector.getUrl(), connectorDTO.getUrl());
        Assert.assertEquals(connector.getUserName(), connectorDTO.getUserName());
        Assert.assertEquals(connector.getUserToken(), connectorDTO.getUserToken());
        Assert.assertEquals(connector.getType().name(), connectorDTO.getType());

    }

    public ConnectorMapperTest() {
        super(Connector.class, ConnectorDTO.class);
    }

}
