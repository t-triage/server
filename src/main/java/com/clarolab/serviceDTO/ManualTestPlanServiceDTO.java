/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.ManualTestExecutionDTO;
import com.clarolab.dto.ManualTestPlanDTO;
import com.clarolab.dto.ManualTestPlanStatDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.ManualTestPlanMapper;
import com.clarolab.mapper.impl.UserMapper;
import com.clarolab.model.manual.ManualTestPlan;
import com.clarolab.model.manual.service.ManualTestPlanService;
import com.clarolab.service.TTriageService;
import com.clarolab.service.exception.InvalidDataException;
import com.clarolab.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.clarolab.util.StringUtils.parseDataError;

@Component
public class ManualTestPlanServiceDTO implements BaseServiceDTO<ManualTestPlan, ManualTestPlanDTO, ManualTestPlanMapper> {

    @Autowired
    private ManualTestPlanService service;

    @Autowired
    private ManualTestExecutionServiceDTO manualTestExecutionServiceDTO;

    @Autowired
    private ManualTestPlanMapper mapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public TTriageService<ManualTestPlan> getService() {
        return service;
    }

    @Override
    public Mapper<ManualTestPlan, ManualTestPlanDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<ManualTestPlan, ManualTestPlanDTO, ManualTestPlanMapper> getServiceDTO() {
        return this;
    }

    public List<ManualTestExecutionDTO> assignToTestPlan(Long manualTestPlanId, List<Long> manualTestCaseIds) {
        return manualTestExecutionServiceDTO.convertToDTO(service.assignToTestPlan(manualTestPlanId, manualTestCaseIds));
    }

    @Override
    public ManualTestPlanDTO save(ManualTestPlanDTO manualTestPlanDto) {
        ManualTestPlan manualTestPlan = convertToEntity(manualTestPlanDto);
        checkIfDateIsNull(manualTestPlan);
        checkIfChronologicallyCorrect(manualTestPlan);
        return convertToDTO(service.save(manualTestPlan));
    }

    @Override
    public ManualTestPlanDTO update(ManualTestPlanDTO manualTestPlanDto) {
        ManualTestPlan manualTestPlan = convertToEntity(manualTestPlanDto);
        checkIfDateIsNull(manualTestPlan);
        checkIfChronologicallyCorrect(manualTestPlan);
        return convertToDTO(service.update(manualTestPlan));
    }

    private void checkIfDateIsNull(ManualTestPlan manualTestPlan) {
        if (manualTestPlan.getFromDate() == 0) {
            manualTestPlan.setFromDate(DateUtils.now());
        }
    }

    private void checkIfChronologicallyCorrect(ManualTestPlan manualTestPlan) {
        if (manualTestPlan.getToDate() < manualTestPlan.getFromDate() && manualTestPlan.getToDate() != 0) {
            throw new InvalidDataException(parseDataError("The end date must be later than the start date", manualTestPlan.getToDate()));
        }
    }

    public List<ManualTestPlanStatDTO> getOngoingManualTestPlans() {
        List<ManualTestPlanStatDTO> answer = service.getOngoingManualTestPlans();
        return answer;
    }
    public List<ManualTestPlanDTO> sortByName() {
        List<ManualTestPlan> original = service.findManualTestPlans();
        original = service.sortByName();

        List<ManualTestPlanDTO> answer = new ArrayList<>();
        for (ManualTestPlan t : original) {
            answer.add(convertToDTO(t));
        }
        return answer;
    }
    public List<ManualTestPlanDTO> findByOrderByStatusAscUpdatedDesc() {
        List<ManualTestPlan> original = service.findManualTestPlans();
        // Sort by status priority asc
        original.sort((o1, o2) -> {
            int o1Prio = o1.getStatus().getPriority();
            int o2Prio = o2.getStatus().getPriority();

            if (o1Prio == o2Prio) return 0;
            return (o1Prio > o2Prio) ? 1 : -1;
        });

        List<ManualTestPlanDTO> answer = new ArrayList<>();
        for (ManualTestPlan t : original) {
            answer.add(convertToDTO(t));
        }
        return answer;
    }

}
