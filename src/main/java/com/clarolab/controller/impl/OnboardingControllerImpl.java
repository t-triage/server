/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.aaa.util.AuthContextHelper;
import com.clarolab.controller.OnboardingController;
import com.clarolab.dto.GuideDTO;
import com.clarolab.model.User;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.GuideServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class OnboardingControllerImpl extends BaseControllerImpl<GuideDTO> implements OnboardingController {

    @Autowired
    private GuideServiceDTO service;

    @Autowired
    private AuthContextHelper authContextHelper;

    @Override
    protected TTriageService<GuideDTO> getService() {
        return service;
    }

    @Override
    public ResponseEntity<List<GuideDTO>> list(String page) {
        User user = authContextHelper.getCurrentUser();
        return ResponseEntity.ok(service.list(page, user));
    }

    @Override
    public ResponseEntity<Boolean> assignAnswer(Long guideid, int answerType, String answer) {
        User user = authContextHelper.getCurrentUser();
        return ResponseEntity.ok(service.assignAnswer(guideid, answerType, answer, user));
    }


}
