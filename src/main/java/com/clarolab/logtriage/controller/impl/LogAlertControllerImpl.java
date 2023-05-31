package com.clarolab.logtriage.controller.impl;

import com.clarolab.controller.impl.BaseControllerImpl;
import com.clarolab.logtriage.controller.LogAlertController;
import com.clarolab.logtriage.dto.LogAlertDTO;
import com.clarolab.logtriage.dto.SplunkAlertDTO;
import com.clarolab.logtriage.service.LogAlertService;
import com.clarolab.logtriage.serviceDTO.LogAlertServiceDTO;
import com.clarolab.service.TTriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class LogAlertControllerImpl extends BaseControllerImpl<LogAlertDTO> implements LogAlertController {

    @Autowired
    private LogAlertService logAlertService;

    @Autowired
    private LogAlertServiceDTO serviceDTO;

    @Override
    public ResponseEntity<Boolean> push(SplunkAlertDTO entity) {
        return ResponseEntity.ok(logAlertService.push(entity));
    }

    @Override
    protected TTriageService<LogAlertDTO> getService() {
        return this.serviceDTO;
    }

}
