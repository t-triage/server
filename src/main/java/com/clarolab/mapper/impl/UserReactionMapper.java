/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.UserReactionDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.onboard.UserReaction;
import com.clarolab.service.UserReactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class UserReactionMapper implements Mapper<UserReaction, UserReactionDTO> {

    @Autowired
    private UserReactionService userReactionService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GuideMapper guideMapper;

    @Override
    public UserReactionDTO convertToDTO(UserReaction entity) {
        UserReactionDTO dto = new UserReactionDTO();

        setEntryFields(entity, dto);

        if (entity.getUser() != null) {
            dto.setUser(userMapper.convertToDTO(entity.getUser()));
        }

        dto.setGuide(guideMapper.convertToDTO(entity.getGuide()));
        dto.setAnswer(entity.getAnswer());
        dto.setAnswerType(entity.getAnswerType().getType());

        return dto;
    }

    @Override
    public UserReaction convertToEntity(UserReactionDTO dto) {
        UserReaction entity;
        if (dto.getId() == null || dto.getId() < 1) {
            entity = UserReaction.builder()
                    .build();
        } else {
            entity = userReactionService.find(dto.getId());
        }
        return entity;
    }
}
