/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.mapper.impl;

import com.clarolab.dto.UserDTO;
import com.clarolab.functional.test.mapper.AbstractMapperTest;
import com.clarolab.mapper.impl.UserMapper;
import com.clarolab.model.User;
import com.clarolab.model.types.RoleType;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserMapperTest extends AbstractMapperTest<User, UserDTO> {

    @Autowired
    private UserMapper mapper;

    @Test
    public void testEntityToDTOConversion() {
        User user = getEntity();
        UserDTO userDTO = mapper.convertToDTO(user);
        this.assertConversion(user, userDTO);
    }

    @Test
    public void testDTOToEntityConversion() {
        UserDTO userDTO = getDTO();
        User user = mapper.convertToEntity(userDTO);
        this.assertConversion(user, userDTO);
    }

    @Override
    public void assertConversion(User user, UserDTO userDTO) {
        super.assertConversion(user, userDTO);

        Assert.assertEquals(user.getRealname(), userDTO.getRealname());
        Assert.assertNotNull(user.getAvatar().getData());
        Assert.assertEquals(user.getUsername(), userDTO.getUsername());

    }

    public UserMapperTest() {
        super(User.class, UserDTO.class);
    }

    public UserDTO getDTO() {
        UserDTO dto = super.getDTO();

        dto.setRoleType(RoleType.ROLE_USER.name());

        return dto;
    }

}
