/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.UserPreferenceDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.UserPreference;
import com.clarolab.service.UserPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class UserPreferenceMapper implements Mapper<UserPreference, UserPreferenceDTO> {

    @Autowired
    private UserPreferenceService userPreferenceService;

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserPreferenceDTO convertToDTO(UserPreference userPreference) {
        UserPreferenceDTO userPreferenceDTO = new UserPreferenceDTO();

        setEntryFields(userPreference, userPreferenceDTO);

        userPreferenceDTO.setUser(userPreference.getUser() == null ? null : userMapper.convertToDTO(userPreference.getUser()));
        userPreferenceDTO.setCurrentContainer(userPreference.getCurrentContainer());
        userPreferenceDTO.setCurrentPageNUmber(userPreference.getCurrentPageNUmber());
        userPreferenceDTO.setRowPerPage(userPreference.getRowPerPage());

        return userPreferenceDTO;
    }

    @Override
    public UserPreference convertToEntity(UserPreferenceDTO dto) {
        UserPreference userPreference;
        if (dto.getId() == null || dto.getId() < 1) {
            userPreference = UserPreference.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .currentContainer(dto.getCurrentContainer())
                    .currentPageNUmber(dto.getCurrentPageNUmber())
                    .rowPerPage(dto.getRowPerPage())
                    .user(userMapper.convertToEntity(dto.getUser()))
                    .build();

        } else {
            userPreference = userPreferenceService.find(dto.getId());

//            userPreference.setId(dto.getId());
//            userPreference.setUpdated(dto.getUpdated());
//            userPreference.setTimestamp(dto.getTimestamp());
            userPreference.setEnabled(dto.getEnabled());
            userPreference.setCurrentContainer(dto.getCurrentContainer());
            userPreference.setCurrentPageNUmber(dto.getCurrentPageNUmber());
            userPreference.setRowPerPage(dto.getRowPerPage());
            userPreference.setUser(userMapper.convertToEntity(dto.getUser()));
        }

        return userPreference;
    }
}
