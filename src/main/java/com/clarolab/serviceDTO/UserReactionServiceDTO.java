/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.UserReactionDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.UserReactionMapper;
import com.clarolab.model.onboard.UserReaction;
import com.clarolab.service.TTriageService;
import com.clarolab.service.UserReactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserReactionServiceDTO implements BaseServiceDTO<UserReaction, UserReactionDTO, UserReactionMapper> {

    @Autowired
    private UserReactionService service;

    @Autowired
    private UserReactionMapper mapper;

    @Override
    public TTriageService<UserReaction> getService() {
        return service;
    }

    @Override
    public Mapper<UserReaction, UserReactionDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<UserReaction, UserReactionDTO, UserReactionMapper> getServiceDTO() {
        return this;
    }

}
