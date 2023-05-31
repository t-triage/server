/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.mapper.impl;

import com.clarolab.dto.ExecutorDTO;
import com.clarolab.functional.test.mapper.AbstractMapperTest;
import com.clarolab.mapper.impl.ExecutorMapper;
import com.clarolab.model.Executor;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ExecutorMapperTest extends AbstractMapperTest<Executor, ExecutorDTO> {

    @Autowired
    private ExecutorMapper mapper;

    @Test
    public void testEntityToDTOConversion() {
        Executor executor = getEntity();
        ExecutorDTO executorDTO = mapper.convertToDTO(executor);
        this.assertConversion(executor, executorDTO);
    }

    @Test
    public void testDTOToEntityConversion() {
        ExecutorDTO executorDTO = getDTO();
        executorDTO.getTriageSpec().getTriager().setRoleType("ROLE_ADMIN");
        Executor executor = mapper.convertToEntity(executorDTO);
        this.assertConversion(executor, executorDTO);
    }

    @Override
    public void assertConversion(Executor executor, ExecutorDTO executorDTO) {
        super.assertConversion(executor, executorDTO);

        Assert.assertEquals(executor.getName(), executorDTO.getName());
        Assert.assertEquals(executor.getUrl(), executorDTO.getUrl());
        Assert.assertEquals(executor.getDescription(), executorDTO.getDescription());

    }

    public ExecutorMapperTest() {
        super(Executor.class, ExecutorDTO.class);
    }

}
