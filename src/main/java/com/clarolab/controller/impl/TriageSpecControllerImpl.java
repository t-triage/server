/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.controller.TriageSpecController;
import com.clarolab.dto.TriageSpecDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.TriageSpecServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class TriageSpecControllerImpl extends BaseControllerImpl<TriageSpecDTO> implements TriageSpecController {

    @Autowired
    private TriageSpecServiceDTO triageSpecService;

    @Override
    protected TTriageService<TriageSpecDTO> getService() {
        return triageSpecService;
    }

}
