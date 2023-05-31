package com.clarolab.logtriage.controller.impl;

import com.clarolab.controller.impl.BaseControllerImpl;
import com.clarolab.logtriage.controller.SearchExecutorController;
import com.clarolab.logtriage.dto.SearchExecutorDTO;
import com.clarolab.logtriage.serviceDTO.SearchExecutorServiceDTO;
import com.clarolab.service.TTriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class SearchExecutorControllerImpl extends BaseControllerImpl<SearchExecutorDTO> implements SearchExecutorController {

    @Autowired
    private SearchExecutorServiceDTO serviceDTO;

    @Override
    protected TTriageService<SearchExecutorDTO> getService() {
        return serviceDTO;
    }
}
