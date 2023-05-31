package com.clarolab.controller.impl;

import com.clarolab.controller.LogController;
import com.clarolab.dto.LogDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.LogServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class LogControllerImpl extends BaseControllerImpl<LogDTO> implements LogController {

    @Autowired
    private LogServiceDTO logService;

    @Override
    protected TTriageService<LogDTO> getService() {
        return logService;
    }

}
