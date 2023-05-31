/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.mapper.impl;

import com.clarolab.dto.UserPreferenceDTO;
import com.clarolab.functional.test.mapper.AbstractMapperTest;
import com.clarolab.mapper.impl.UserMapper;
import com.clarolab.mapper.impl.UserPreferenceMapper;
import com.clarolab.model.UserPreference;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserPreferenceMapperTest extends AbstractMapperTest<UserPreference, UserPreferenceDTO> {

    @Autowired
    private UserPreferenceMapper mapper;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testDTOToEntityConversion() {
        UserPreferenceDTO userPreferenceDTO = getDTO();
        UserPreference userPreference = mapper.convertToEntity(userPreferenceDTO);
        this.assertConversion(userPreference, userPreferenceDTO);
    }

    @Override
    public void assertConversion(UserPreference userPreference, UserPreferenceDTO dto) {
        super.assertConversion(userPreference, dto);
        Assert.assertEquals(userPreference.getCurrentContainer(), dto.getCurrentContainer());
        Assert.assertEquals(userPreference.getCurrentPageNUmber(), dto.getCurrentPageNUmber());
        Assert.assertEquals(userPreference.getRowPerPage(), dto.getRowPerPage());
    }

    public UserPreferenceMapperTest() {
        super(UserPreference.class, UserPreferenceDTO.class);
    }

    public UserPreferenceDTO getDTO() {
        UserPreferenceDTO dto = super.getDTO();

        dto.setUser(userMapper.convertToDTO(provider.getUser()));

        return dto;
    }


}
