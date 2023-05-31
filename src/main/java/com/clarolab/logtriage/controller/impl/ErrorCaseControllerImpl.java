package com.clarolab.logtriage.controller.impl;

import com.clarolab.controller.impl.BaseControllerImpl;
import com.clarolab.logtriage.controller.ErrorCaseController;
import com.clarolab.logtriage.dto.ErrorCaseDTO;
import com.clarolab.logtriage.serviceDTO.ErrorCaseServiceDTO;
import com.clarolab.service.TTriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class ErrorCaseControllerImpl extends BaseControllerImpl<ErrorCaseDTO> implements ErrorCaseController {

    @Autowired
    private ErrorCaseServiceDTO serviceDTO;

    @Override
    protected TTriageService<ErrorCaseDTO> getService() {
        return serviceDTO;
    }
}
