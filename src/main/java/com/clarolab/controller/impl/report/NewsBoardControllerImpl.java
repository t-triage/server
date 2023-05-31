/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl.report;


import com.clarolab.controller.NewsBoardController;
import com.clarolab.controller.impl.BaseControllerImpl;
import com.clarolab.dto.NewsBoardDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.NewsBoardServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin
@Controller
public class NewsBoardControllerImpl extends BaseControllerImpl<NewsBoardDTO> implements NewsBoardController {


    @Autowired
    private NewsBoardServiceDTO newsBoardServiceDTO;

    @Override
    protected TTriageService<NewsBoardDTO> getService() {
        return newsBoardServiceDTO;
    }

    @Override
    public ResponseEntity<List<NewsBoardDTO>> latestNews() {
        return ResponseEntity.ok(newsBoardServiceDTO.latestNews());
    }

}
