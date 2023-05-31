/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.manual.service;


import com.clarolab.aaa.util.AuthContextHelper;
import com.clarolab.dto.ManualTestPlanStatDTO;
import com.clarolab.model.manual.ManualTestCase;
import com.clarolab.model.manual.ManualTestExecution;
import com.clarolab.model.manual.ManualTestPlan;
import com.clarolab.model.manual.repository.ManualTestPlanRepository;
import com.clarolab.model.manual.types.ExecutionStatusType;
import com.clarolab.repository.BaseRepository;
import com.clarolab.service.BaseService;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.clarolab.util.Constants.MIN_SEARCH_LENGHT_;

@Service
@Log
public class ManualTestPlanService extends BaseService<ManualTestPlan> {

    @Autowired
    private ManualTestPlanRepository manualTestPlanRepository;

    @Autowired
    private ManualTestPlanService manualTestPlanService;

    @Autowired
    private ManualTestCaseService manualTestCaseService;

    @Autowired
    private ManualTestExecutionService manualTestExecutionService;

    @Autowired
    private AuthContextHelper authContextHelper;


    @Override
    public BaseRepository<ManualTestPlan> getRepository() {
        return manualTestPlanRepository;
    }

    public List<ManualTestPlan> searchByName(String name) {
        if (name == null || name.length() < MIN_SEARCH_LENGHT_)
            return Lists.newArrayList();

        name = StringUtils.prepareStringForSearch(name);
        return manualTestPlanRepository.search(name);

    }

    public List<ManualTestExecution> assignToTestPlan(Long testPlanID, List<Long> testCaseIDs) {
        ManualTestPlan manualTestPlan = manualTestPlanService.find(testPlanID);
        for (Long testCaseID : testCaseIDs) {
            ManualTestCase manualTestCase = manualTestCaseService.find(testCaseID);
            Optional<ManualTestExecution> aCase = manualTestExecutionService.findByPlanAndCase(manualTestPlan, manualTestCase);
            if (!aCase.isPresent() || !aCase.get().isEnabled()) {
                ManualTestExecution manualTestExecution = new ManualTestExecution();
                manualTestExecution.setTestCase(manualTestCase);
                manualTestExecution.setTestPlan(manualTestPlan);
                manualTestExecution.setStatus(ExecutionStatusType.PENDING);
                manualTestExecution.setAssignee(manualTestPlan.getAssignee());
                manualTestExecutionService.save(manualTestExecution);
            } else {
                log.info("Manual test case: " + manualTestCase.getId() + " already belongs to manual test plan: " + manualTestPlan.getId());
            }
        }

        return manualTestExecutionService.findByPlan(manualTestPlan);
    }

    public List<ManualTestPlanStatDTO> getOngoingManualTestPlans() {
        long startDate = DateUtils.beginDay(365);
        long endDate = DateUtils.beginDay(-365);
        List<ManualTestPlanStatDTO> allPlans = manualTestPlanRepository.getManualTestPlanStats(startDate, endDate);
        List<ManualTestPlanStatDTO> answer = new ArrayList<>();

        if (allPlans.isEmpty()) {
            return answer;
        }
        ManualTestPlanStatDTO currentPlan = allPlans.get(0);
        answer.add(currentPlan);

        for (ManualTestPlanStatDTO planWithStatus : allPlans) {
            if (currentPlan.getName().equals(planWithStatus.getName())) {
               planWithStatus.assignValueTo(currentPlan);
            } else {
                currentPlan = planWithStatus;
                answer.add(currentPlan);

                planWithStatus.assignValueTo(currentPlan);
            }
        }

        return answer;
    }

    public List<ManualTestPlan> findManualTestPlans() {
        return manualTestPlanRepository.findAll();
    }

    public List<ManualTestPlan> sortByName() {
        List<ManualTestPlan> manualTestPlans = findManualTestPlans();
        Collections.sort(manualTestPlans, new Comparator<ManualTestPlan>() {
            @Override
            public int compare(ManualTestPlan o1, ManualTestPlan o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return manualTestPlans;
    }

}
