/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.manual.service;


import com.clarolab.model.Entry;
import com.clarolab.model.User;
import com.clarolab.model.manual.ManualTestCase;
import com.clarolab.model.manual.ManualTestExecution;
import com.clarolab.model.manual.ManualTestPlan;
import com.clarolab.model.manual.repository.ManualTestExecutionRepository;
import com.clarolab.repository.BaseRepository;
import com.clarolab.service.BaseService;
import com.clarolab.util.DateUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log
public class ManualTestExecutionService extends BaseService<ManualTestExecution> {

    @Autowired
    private ManualTestExecutionRepository manualTestExecutionRepository;

    @Autowired
    private ManualTestCaseService manualTestCaseService;

    @Autowired
    private ManualTestPlanService manualTestPlanService;

    @Autowired
    private ManualTestExecutionService manualTestExecutionService;

    @Override
    public BaseRepository<ManualTestExecution> getRepository() {
        return manualTestExecutionRepository;
    }

    public List<ManualTestExecution> findAllByAssignee(User user) {
        return manualTestExecutionRepository.findAllByAssignee(user);
    }

    public List<ManualTestExecution> findAllByPlanId(long id) {
        ManualTestPlan manualTestPlan = manualTestPlanService.find(id);
        List<ManualTestExecution> byPlan = findByPlan(manualTestPlan);
        return byPlan.stream().filter(Entry::isEnabled).sorted().collect(Collectors.toList());
    }

    public List<ManualTestExecution> findByPlan(ManualTestPlan manualTestPlan) {
        return manualTestExecutionRepository.findByTestPlanOrderById(manualTestPlan);
    }

    public Optional<ManualTestExecution> findByPlanAndCase(ManualTestPlan manualTestPlan, ManualTestCase manualTestCase) {
        return manualTestExecutionRepository.findByTestPlanAndTestCaseAndEnabledIsTrue(manualTestPlan, manualTestCase);
    }

    public List<ManualTestExecution> findByLastExecutionTime(Long timeValue) {
        return manualTestExecutionRepository.findByLastExecutionTimeIsGreaterThanEqual(timeValue);
    }

    public Long deleteByPlanAndCase(Long manualTestPlanId, Long manualTestCaseId) {
        Long deletedId = 0L;
        ManualTestCase manualTestCase = manualTestCaseService.find(manualTestCaseId);
        ManualTestPlan manualTestPlan = manualTestPlanService.find(manualTestPlanId);
        Optional<ManualTestExecution> byPlanAndCase = findByPlanAndCase(manualTestPlan, manualTestCase);
        if (byPlanAndCase.isPresent())
            deletedId = byPlanAndCase.get().getId();
        byPlanAndCase.ifPresent(manualTestExecution -> manualTestExecutionService.disable(manualTestExecution.getId()));
        return deletedId;
    }

    public List<ManualTestExecution> findManualTestExecutionSince(long timestamp){
        return manualTestExecutionRepository.findManualTestExecutionsByTimestampIsGreaterThanEqual(timestamp);
    }
}
