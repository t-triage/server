/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.UserPreferenceDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.UserPreferenceMapper;
import com.clarolab.model.UserPreference;
import com.clarolab.service.TTriageService;
import com.clarolab.service.UserPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserPreferenceServiceDTO implements BaseServiceDTO<UserPreference, UserPreferenceDTO, UserPreferenceMapper> {

    @Autowired
    private UserPreferenceService service;

    @Autowired
    private UserPreferenceMapper mapper;

    @Override
    public TTriageService<UserPreference> getService() {
        return service;
    }

    @Override
    public Mapper<UserPreference, UserPreferenceDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<UserPreference, UserPreferenceDTO, UserPreferenceMapper> getServiceDTO() {
        return this;
    }

    

}
