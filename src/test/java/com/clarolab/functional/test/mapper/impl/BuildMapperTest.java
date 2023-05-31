/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.mapper.impl;

import com.clarolab.dto.BuildDTO;
import com.clarolab.functional.test.mapper.AbstractMapperTest;
import com.clarolab.mapper.impl.BuildMapper;
import com.clarolab.model.Build;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BuildMapperTest extends AbstractMapperTest<Build, BuildDTO> {

    @Autowired
    private BuildMapper mapper;

    @Test
    public void testEntityToDTOConversion() {
       Build build = getEntity();
       BuildDTO buildDTO = mapper.convertToDTO(build);
       this.assertConversion(build, buildDTO);
    }

    @Test
    public void testDTOToEntityConversion() {
       BuildDTO buildDTO = getDTO();

       //Override Enums since are not random.
       buildDTO.setStatus("SUCCESS");

       Build build = mapper.convertToEntity(buildDTO);
       this.assertConversion(build, buildDTO);
    }

    @Override
    public void assertConversion(Build build, BuildDTO buildDTO) {
        super.assertConversion(build, buildDTO);
        Assert.assertEquals(build.getNumber(), buildDTO.getNumber());
        Assert.assertEquals(build.getExecutedDate(), buildDTO.getExecutedDate());
        Assert.assertEquals(build.getDisplayName(), buildDTO.getDisplayName());
        Assert.assertEquals(build.getStatus().name(), buildDTO.getStatus());
    }

    public BuildMapperTest() {
        super(Build.class, BuildDTO.class);
    }

}
