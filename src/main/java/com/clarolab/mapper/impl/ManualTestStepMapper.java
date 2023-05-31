/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.ManualTestStepDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.manual.ManualTestCase;
import com.clarolab.model.manual.instruction.ManualTestStep;
import com.clarolab.model.manual.service.ManualTestCaseService;
import com.clarolab.model.manual.service.ManualTestStepService;
import com.clarolab.service.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.getNullableByID;
import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
@Log
public class ManualTestStepMapper implements Mapper<ManualTestStep, ManualTestStepDTO> {

    @Autowired
    private UserService userService;

    @Autowired
    private ManualTestStepService manualTestStepService;

    @Autowired
    private ManualTestCaseService manualTestCaseService;

    @Override
    public ManualTestStepDTO convertToDTO(ManualTestStep manualTestStep) {

        ManualTestStepDTO manualTestStepDTO = new ManualTestStepDTO();

        setEntryFields(manualTestStep, manualTestStepDTO);

        ManualTestCase testCase = manualTestStep.getTestCase();

        if (testCase != null && testCase.getMainStep().getId() != null) {
            if (manualTestStep.getId().equals(testCase.getMainStep().getId())) {
                manualTestStepDTO.setMain(true);
            } else {
                manualTestStepDTO.setMain(false);
            }
        }

        manualTestStepDTO.setStep(manualTestStep.getStep());
        manualTestStepDTO.setExpectedResult(manualTestStep.getExpectedResult());
        manualTestStepDTO.setData(manualTestStep.getData());
        manualTestStepDTO.setStepOrder(manualTestStep.getStepOrder());
        manualTestStepDTO.setTestCaseId(testCase == null ? 0 : manualTestStep.getTestCase().getId());
        manualTestStepDTO.setExternalId(manualTestStep.getExternalId());
        return manualTestStepDTO;
    }

    @Override
    public ManualTestStep convertToEntity(ManualTestStepDTO dto) {
        if (dto == null) {
            return null;
        }
        ManualTestStep manualTestStep;
        if (dto.getId() == null || dto.getId() < 1) {
            manualTestStep = ManualTestStep.builder()
                    .id(null)
                    .testCase(getNullableByID(dto.getTestCaseId(), id -> manualTestCaseService.find(id)))
                    .step(dto.getStep())
                    .expectedResult(dto.getExpectedResult())
                    .data(dto.getData())
                    .stepOrder(dto.getStepOrder())
                    .externalId(dto.getExternalId())
                    .build();

        } else {
            manualTestStep = manualTestStepService.find(dto.getId());
            if (manualTestStep == null) {
                log.severe(String.format("ManualTestStep contains an id: %n but is not at DB for test id: %d", dto.getId(), dto.getTestCaseId()));
            }
//            manualTestStep.setId(); Don't allow to update this.
            manualTestStep.setEnabled(dto.getEnabled());
//            manualTestStep.setTimestamp(dto.getTimestamp()); Don't allow to update this.
//            manualTestStep.setUpdated(dto.getUpdated()); Don't allow to update this.
            manualTestStep.setTestCase(getNullableByID(dto.getTestCaseId(), id -> manualTestCaseService.find(id)));
            manualTestStep.setStep(dto.getStep());
            manualTestStep.setExpectedResult(dto.getExpectedResult());
            manualTestStep.setData(dto.getData());
            manualTestStep.setStepOrder(dto.getStepOrder());
            manualTestStep.setExternalId(dto.getExternalId());
        }
        return manualTestStep;
    }
}
