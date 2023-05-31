/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.model.User;
import com.clarolab.model.onboard.Guide;
import com.clarolab.model.onboard.UserReaction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserReactionRepository extends BaseRepository<UserReaction> {
    UserReaction findFirstByGuideAndUserAndEnabled(Guide guide, User user, boolean enabled);

    List<UserReaction> findAllByGuideInAndUser(List<Guide> guides, User user);
}
