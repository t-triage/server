/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.controller.DeadlineController;
import com.clarolab.dto.DeadlineDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.DeadlineServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class DeadlineControllerImpl extends BaseControllerImpl<DeadlineDTO> implements DeadlineController {

    @Autowired
    private DeadlineServiceDTO deadlineService;

    @Override
    protected TTriageService<DeadlineDTO> getService() {
        return deadlineService;
    }

}
