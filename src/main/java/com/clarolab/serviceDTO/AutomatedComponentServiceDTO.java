/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.AutomatedComponentDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.AutomatedComponentMapper;
import com.clarolab.model.TestCase;
import com.clarolab.model.component.AutomatedComponent;
import com.clarolab.service.AutomatedComponentService;
import com.clarolab.service.TTriageService;
import com.clarolab.service.TestCaseService;
import com.clarolab.service.TestComponentRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AutomatedComponentServiceDTO implements BaseServiceDTO<AutomatedComponent, AutomatedComponentDTO, AutomatedComponentMapper> {

    @Autowired
    private AutomatedComponentService service;

    @Autowired
    private AutomatedComponentMapper mapper;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestComponentRelationService testComponentRelationService;

    @Override
    public TTriageService<AutomatedComponent> getService() {
        return service;
    }

    @Override
    public Mapper<AutomatedComponent, AutomatedComponentDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<AutomatedComponent, AutomatedComponentDTO, AutomatedComponentMapper> getServiceDTO() {
        return this;
    }

    public List<AutomatedComponentDTO> search(String name) {
        return convertToDTO(service.search(name));
    }

    public List<AutomatedComponentDTO> suggestedDefaultComponents() {
        return convertToDTO(service.suggestedDefaultComponents());
    }

    public List<AutomatedComponentDTO> suggestedComponents(List<Long> automatedComponentIds) {
        return convertToDTO(service.suggestedComponents(automatedComponentIds));
    }

    public AutomatedComponentDTO setComponentToTests(Long automatedComponentId, List<Long> testCaseIds) {
        AutomatedComponent automatedComponent = findEntity(automatedComponentId);
        if (automatedComponent == null) {
            return null;
        }

        service.setComponentToTests(automatedComponent, testCaseIds);
        return convertToDTO(automatedComponent);
    }

    public Long deleteByComponentAndTestCase(Long automatedComponentId, Long testCaseId) {
        AutomatedComponent automatedComponent = findEntity(automatedComponentId);
        TestCase testCase = testCaseService.find(testCaseId);

        if (automatedComponent == null || testCase == null) {
            return 0L;
        }

        return testComponentRelationService.delete(testCase, automatedComponent);
    }
}
