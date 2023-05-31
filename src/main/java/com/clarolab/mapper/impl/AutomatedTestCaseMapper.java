/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.AutomatedTestCaseDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.TestCase;
import com.clarolab.model.TestTriage;
import com.clarolab.model.component.TestComponentRelation;
import com.clarolab.service.TestCaseService;
import com.clarolab.service.TestTriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AutomatedTestCaseMapper implements Mapper<TestCase, AutomatedTestCaseDTO> {

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private TestTriageMapper testTriageMapper;

    @Autowired
    private AutomatedComponentMapper automatedComponentMapper;

    @Override
    public AutomatedTestCaseDTO convertToDTO(TestCase entity){
        AutomatedTestCaseDTO dto = new AutomatedTestCaseDTO();

        dto.setId(entity.getId());
        dto.setUpdated(entity.getUpdated());
        dto.setEnabled(entity.isEnabled());

        dto.setName(entity.getName());
        dto.setLocationPath(entity.getLocationPath());
        dto.setPin(entity.getPin() != null);

        for (TestTriage testTriage : testTriageService.findAllOngoingTests(entity)) {
            dto.getTestTriageDTOList().add(testTriageMapper.convertToRepository(testTriage));
        }

        for (TestComponentRelation testComponentRelation : entity.getTestComponentRelations()) {
            dto.getAutomatedComponentDTOList().add(automatedComponentMapper.convertToDTO(testComponentRelation.getComponent()));
        }

        return dto;
    }

    @Override
    public TestCase convertToEntity(AutomatedTestCaseDTO dto) {
        if (dto == null) {
            return null;
        }
        TestCase entity;
        if (dto.getId() == null || dto.getId() < 1) {
            entity = TestCase.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .name(dto.getName())
                    .locationPath(dto.getLocationPath())
                    .build();
        } else {
            entity = testCaseService.find(dto.getId());
            entity.setEnabled(true);
            entity.setName(dto.getName());
            entity.setLocationPath(dto.getLocationPath());
        }
        return entity;
    }


}
