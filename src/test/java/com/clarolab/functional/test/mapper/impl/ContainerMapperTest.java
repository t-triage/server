/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.mapper.impl;

import com.clarolab.dto.ContainerDTO;
import com.clarolab.functional.test.mapper.AbstractMapperTest;
import com.clarolab.mapper.impl.ContainerMapper;
import com.clarolab.mapper.impl.UserMapper;
import com.clarolab.model.Container;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ContainerMapperTest extends AbstractMapperTest<Container, ContainerDTO> {

    @Autowired
    private ContainerMapper mapper;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testEntityToDTOConversion() {
        Container container = provider.getContainer();
        provider.getExecutor();
        ContainerDTO containerDTO = mapper.convertToDTO(container);
        this.assertConversion(container, containerDTO);
    }

    @Test
    public void testDTOToEntityConversion() {
        ContainerDTO containerDTO = getDTO();

        containerDTO.setType("VIEW");
        containerDTO.setPopulateMode("UNDEFINED");
        containerDTO.setReportType("UNKNOWN");

        Container connector = mapper.convertToEntity(containerDTO);
        this.assertConversion(connector, containerDTO);
    }

    @Override
    public void assertConversion(Container container, ContainerDTO dto) {
        super.assertConversion(container, dto);

//        Assert.assertEquals(container.getConnector().getId(), dto.getConnector());
        Assert.assertEquals(container.getDescription(), dto.getDescription());
        Assert.assertEquals(container.getName(), dto.getName());
//        Assert.assertEquals(container.getProduct().getId(), dto.getProduct());
        Assert.assertEquals(container.getUrl(), dto.getUrl());

    }

    @Before
    public void clearProvider() {
        provider.clear();
    }

    public ContainerMapperTest() {
        super(Container.class, ContainerDTO.class);
    }

    public ContainerDTO getDTO() {
        ContainerDTO dto = super.getDTO();

        dto.getTriageSpec().setTriager(userMapper.convertToDTO(provider.getUser()));

        return dto;
    }
}
