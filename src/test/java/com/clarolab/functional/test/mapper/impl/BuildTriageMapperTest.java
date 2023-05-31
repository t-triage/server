/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.mapper.impl;

import com.clarolab.dto.BuildTriageDTO;
import com.clarolab.functional.test.mapper.AbstractMapperTest;
import com.clarolab.mapper.impl.BuildTriageMapper;
import com.clarolab.mapper.impl.UserMapper;
import com.clarolab.model.BuildTriage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BuildTriageMapperTest extends AbstractMapperTest<BuildTriage, BuildTriageDTO> {

    @Autowired
    private BuildTriageMapper mapper;

    @Autowired
    UserMapper userMapper;

    @Test
    public void testEntityToDTOConversion() {
        BuildTriage buildTriage = getEntity();
        BuildTriageDTO buildTriageDTO = mapper.convertToDTO(buildTriage);
        this.assertConversion(buildTriage, buildTriageDTO);
    }

    @Test
    public void testDTOToEntityConversion() {
        BuildTriageDTO buildTriageDTO = getDTO();

        buildTriageDTO.setCurrentState("FAIL");

        BuildTriage buildTriage = mapper.convertToEntity(buildTriageDTO);
        this.assertConversion(buildTriage, buildTriageDTO);
    }

    @Override
    public void assertConversion(BuildTriage buildTriage, BuildTriageDTO buildTriageDTO) {
        super.assertConversion(buildTriage, buildTriageDTO);
    }

    public BuildTriageMapperTest() {
        super(BuildTriage.class, BuildTriageDTO.class);
    }

    public BuildTriageDTO getDTO() {
        BuildTriageDTO dto = super.getDTO();

        dto.setTriager(userMapper.convertToDTO(provider.getUser()));
        dto.getTriageSpec().setTriager(userMapper.convertToDTO(provider.getUser()));

        return dto;
    }
}
