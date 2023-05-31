/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.mapper.impl;

import com.clarolab.dto.TriageSpecDTO;
import com.clarolab.functional.test.mapper.AbstractMapperTest;
import com.clarolab.mapper.impl.TriageSpecMapper;
import com.clarolab.mapper.impl.UserMapper;
import com.clarolab.model.TriageSpec;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TriageSpecMapperTest extends AbstractMapperTest<TriageSpec, TriageSpecDTO> {

    @Autowired
    private TriageSpecMapper mapper;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testEntityToDTOConversion() {
        TriageSpec user = getEntity();
        TriageSpecDTO userDTO = mapper.convertToDTO(user);
        this.assertConversion(user, userDTO);
    }

    @Test
    public void testDTOToEntityConversion() {
        TriageSpecDTO userDTO = getDTO();
        TriageSpec user = mapper.convertToEntity(userDTO);
        this.assertConversion(user, userDTO);
    }

    @Override
    public void assertConversion(TriageSpec triageSpec, TriageSpecDTO dto) {
        super.assertConversion(triageSpec, dto);
    }

    public TriageSpecMapperTest() {
        super(TriageSpec.class, TriageSpecDTO.class);
    }

    public TriageSpecDTO getDTO() {
        TriageSpecDTO dto = super.getDTO();

        dto.setTriager(userMapper.convertToDTO(provider.getUser()));

        return dto;
    }

}
