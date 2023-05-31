/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl.user;

import com.clarolab.controller.UserPreferenceController;
import com.clarolab.controller.impl.BaseControllerImpl;
import com.clarolab.dto.UserPreferenceDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.UserPreferenceServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class UserPreferenceControllerImpl extends BaseControllerImpl<UserPreferenceDTO> implements UserPreferenceController {

    @Autowired
    private UserPreferenceServiceDTO userPreferenceService;

    @Override
    protected TTriageService<UserPreferenceDTO> getService() {
        return userPreferenceService;
    }

}
