/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.model.User;
import com.clarolab.model.onboard.Guide;
import com.clarolab.model.onboard.GuideAnswer;
import com.clarolab.model.onboard.UserReaction;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.GuideRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log
public class GuideService extends BaseService<Guide> {

    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private UserReactionService userReactionService;

    @Override
    public BaseRepository<Guide> getRepository() {
        return guideRepository;
    }

    public List<Guide> list(String page, User user) {
        List<Guide> guidesInPage = list(page);

        if (guidesInPage.isEmpty()) {
            return guidesInPage;
        }

        List<UserReaction> reactions = userReactionService.findAll(guidesInPage, user);

        for (UserReaction reaction : reactions) {
            if (reaction.isEnabled() && reaction.wasAnswered()) {
                guidesInPage.remove(reaction.getGuide());
            }
        }

        return guidesInPage;
    }

    public List<Guide> list(String page) {
        List<Guide> answer = guideRepository.findAllByPageUrlAndEnabled(page, true);

        return answer;
    }

    public Boolean assignAnswer(Guide guide, GuideAnswer answerType, String text, User user) {
        UserReaction reaction = userReactionService.find(guide, user);

        if (reaction == null) {
            reaction = UserReaction.builder()
                    .answer(text)
                    .answerType(answerType)
                    .user(user)
                    .guide(guide)
                    .build();
            reaction = userReactionService.save(reaction);
        } else {
            reaction.setAnswer(text);
            reaction.setAnswerType(answerType);
            userReactionService.update(reaction);
        }

        return true;
    }


}
