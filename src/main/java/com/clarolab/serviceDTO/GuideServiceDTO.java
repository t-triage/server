/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.GuideDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.GuideMapper;
import com.clarolab.model.User;
import com.clarolab.model.onboard.Guide;
import com.clarolab.model.onboard.GuideAnswer;
import com.clarolab.service.GuideService;
import com.clarolab.service.TTriageService;
import com.clarolab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GuideServiceDTO implements BaseServiceDTO<Guide, GuideDTO, GuideMapper> {

    @Autowired
    private GuideService service;

    @Autowired
    private GuideMapper mapper;

    @Autowired
    private UserService userService;

    @Override
    public TTriageService<Guide> getService() {
        return service;
    }

    @Override
    public Mapper<Guide, GuideDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<Guide, GuideDTO, GuideMapper> getServiceDTO() {
        return this;
    }

    public List<GuideDTO> list(String page, User user) {
        List<Guide> list = service.list(page, user);
        List<GuideDTO> answer = convertToDTO(list);

        return answer;
    }

    public Boolean assignAnswer(Long guideid, int answerType, String text, User user) {
        Guide guide = service.find(guideid);
        GuideAnswer type = GuideAnswer.withType(answerType);
        Boolean success = false;

        if (user == null || type == null || guide == null) {
            return false;
        } else {
            success = service.assignAnswer(guide, type, text, user);
        }

        return success;
    }

}
