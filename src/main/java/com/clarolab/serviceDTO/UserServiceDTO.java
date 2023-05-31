/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.UserDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.UserMapper;
import com.clarolab.model.User;
import com.clarolab.model.helper.ResourcesHelper;
import com.clarolab.service.PropertyService;
import com.clarolab.service.TTriageService;
import com.clarolab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.clarolab.util.Constants.DEFAULT_TandC_FILE;
import static com.clarolab.util.Constants.TERM_AND_CONDITIONS;

@Component
public class UserServiceDTO implements BaseServiceDTO<User, UserDTO, UserMapper> {

    @Autowired
    private UserService service;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private PropertyService propertyService;

    @Override
    public TTriageService<User> getService() {
        return service;
    }

    @Override
    public Mapper<User, UserDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<User, UserDTO, UserMapper> getServiceDTO() {
        return this;
    }

    public List<UserDTO> search(String name) {
        return convertToDTO(service.search(name));
    }

    public String getTerms() {
        return propertyService.valueOf(TERM_AND_CONDITIONS,  ResourcesHelper.getDefaulTermAndCondition(DEFAULT_TandC_FILE));
    }
}
