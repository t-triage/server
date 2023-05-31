/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl.executor;

import com.clarolab.controller.TrendGoalController;
import com.clarolab.controller.impl.BaseControllerImpl;
import com.clarolab.dto.TrendGoalDTO;
import com.clarolab.model.Executor;
import com.clarolab.service.ExecutorService;
import com.clarolab.service.TTriageService;
import com.clarolab.service.UserService;
import com.clarolab.serviceDTO.ExecutorServiceDTO;
import com.clarolab.serviceDTO.TrendGoalServiceDTO;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@CrossOrigin
@RestController
@Log
public class TrendGoalControllerImpl extends BaseControllerImpl<TrendGoalDTO> implements TrendGoalController {

    @Autowired
    private TrendGoalServiceDTO trendGoalServiceDTO;

    @Autowired
    private UserService userService;

    @Autowired
    private ExecutorServiceDTO executorServiceDTO;

    @Autowired
    private ExecutorService executorService;

    @Override
    protected TTriageService<TrendGoalDTO> getService() {
        return trendGoalServiceDTO;
    }
    
    @Override
    @ApiIgnore
    public ResponseEntity<Long> delete(Long id) {
        return super.delete(id);
    }

    @Override
    public ResponseEntity<TrendGoalDTO> createNewGoal(Long executorId, @RequestBody TrendGoalDTO entity) {
        TrendGoalDTO newGoal = getService().save(entity);
        Executor executor = executorServiceDTO.findEntity(executorId);
        executor.setGoal(trendGoalServiceDTO.findEntity(newGoal.getId()));
        executorService.update(executor);
        return ResponseEntity.status(HttpStatus.CREATED).body(newGoal);
    }
}
