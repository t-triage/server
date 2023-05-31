/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.model.User;
import com.clarolab.model.onboard.Guide;
import com.clarolab.model.onboard.UserReaction;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.UserReactionRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log
public class UserReactionService extends BaseService<UserReaction> {

    @Autowired
    private UserReactionRepository userReactionRepository;

    @Override
    public BaseRepository<UserReaction> getRepository() {
        return userReactionRepository;
    }

    public UserReaction find(Guide guide, User user) {
        return userReactionRepository.findFirstByGuideAndUserAndEnabled(guide, user, true);
    }

    public List<UserReaction> findAll(List<Guide> guides, User user) {
        return userReactionRepository.findAllByGuideInAndUser(guides, user);
    }
}
