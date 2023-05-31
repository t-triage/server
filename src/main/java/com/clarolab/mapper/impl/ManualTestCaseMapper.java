/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.ManualTestCaseDTO;
import com.clarolab.dto.ManualTestStepDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.manual.ManualTestCase;
import com.clarolab.model.manual.instruction.ManualTestRequirement;
import com.clarolab.model.manual.instruction.ManualTestStep;
import com.clarolab.model.manual.service.ManualTestCaseService;
import com.clarolab.model.manual.service.ManualTestExecutionService;
import com.clarolab.model.manual.service.ManualTestRequirementService;
import com.clarolab.model.manual.service.ProductComponentService;
import com.clarolab.model.manual.types.AutomationStatusType;
import com.clarolab.model.manual.types.SuiteType;
import com.clarolab.model.manual.types.TechniqueType;
import com.clarolab.model.manual.types.TestPriorityType;
import com.clarolab.service.FunctionalityService;
import com.clarolab.service.ProductService;
import com.clarolab.service.TestCaseService;
import com.clarolab.service.UserService;
import com.clarolab.serviceDTO.ManualTestStepServiceDTO;
import com.clarolab.util.StringUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.clarolab.mapper.MapperHelper.getNullableByID;
import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class ManualTestCaseMapper implements Mapper<ManualTestCase, ManualTestCaseDTO> {

    @Autowired
    private UserService userService;

    @Autowired
    private ManualTestCaseService manualTestCaseService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private FunctionalityService functionalityService;

    @Autowired
    private ProductComponentService productComponentService;

    @Autowired
    private ManualTestExecutionService manualTestExecutionService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private ManualTestRequirementService manualTestRequirementService;

    @Autowired
    private ManualTestStepServiceDTO manualTestStepServiceDTO;

    @Autowired
    private NoteMapper noteMapper;
    
    @Autowired
    private FunctionalityMapper functionalityMapper;

    @Override
    public ManualTestCaseDTO convertToDTO(ManualTestCase manualTestCase) {
        /* El manualTestCase en esta capa NO deberia ser null. Si llega null es porque hay algo mal*/
        /*if (manualTestCase == null) {
            return null;
        }*/
        ManualTestCaseDTO manualTestCaseDTO = new ManualTestCaseDTO();

        setEntryFields(manualTestCase, manualTestCaseDTO);

        manualTestCaseDTO.setName(manualTestCase.getName());
        manualTestCaseDTO.setRequirement(manualTestCase.getRequirement() == null ? null : manualTestCase.getRequirement().getName());
        manualTestCaseDTO.setRequirementId(manualTestCase.getRequirement() == null ? 0L : manualTestCase.getRequirement().getId());
        manualTestCaseDTO.setNeedsUpdate(manualTestCase.isNeedsUpdate());
        manualTestCaseDTO.setSteps(getStepsDTO(manualTestCase));
        manualTestCaseDTO.setMainStepId(manualTestCase.getMainStep() == null ? 0L : manualTestCase.getMainStep().getId());
        manualTestCaseDTO.setPriority(manualTestCase.getPriority() == null ? TestPriorityType.UNDEFINED.name() : manualTestCase.getPriority().name());
        manualTestCaseDTO.setProductId(manualTestCase.getProduct() == null ? 0L : manualTestCase.getProduct().getId());
        manualTestCaseDTO.setProductName(manualTestCase.getProduct() == null ? null : manualTestCase.getProduct().getName());
        manualTestCaseDTO.setSuite(manualTestCase.getSuite() == null ? SuiteType.UNDEFINED.name() : manualTestCase.getSuite().name());
        manualTestCaseDTO.setComponent1Id(manualTestCase.getComponent1() == null ? 0L : manualTestCase.getComponent1().getId());
        manualTestCaseDTO.setComponent1Name(manualTestCase.getComponent1() == null ? null : manualTestCase.getComponent1().getName());
        manualTestCaseDTO.setComponent2Id(manualTestCase.getComponent2() == null ? 0L : manualTestCase.getComponent2().getId());
        manualTestCaseDTO.setComponent2Name(manualTestCase.getComponent2() == null ? null : manualTestCase.getComponent2().getName());
        manualTestCaseDTO.setComponent3Id(manualTestCase.getComponent3() == null ? 0L : manualTestCase.getComponent3().getId());
        manualTestCaseDTO.setComponent3Name(manualTestCase.getComponent3() == null ? null : manualTestCase.getComponent3().getName());
        manualTestCaseDTO.setFunctionality(manualTestCase.getFunctionality());
        manualTestCaseDTO.setFunctionalityEntity(manualTestCase.getFunctionalityEntity() == null ? null : functionalityMapper.convertToDTO(manualTestCase.getFunctionalityEntity()));
        manualTestCaseDTO.setOwner(manualTestCase.getOwner() == null ? null : userMapper.convertToDTO(manualTestCase.getOwner()));
        manualTestCaseDTO.setLastUpdater(manualTestCase.getLastUpdater() == null ? null : userMapper.convertToDTO(manualTestCase.getLastUpdater()));
        manualTestCaseDTO.setAutomationAssignee(manualTestCase.getAutomationAssignee() == null ? null : userMapper.convertToDTO(manualTestCase.getAutomationAssignee()));
        manualTestCaseDTO.setAutomationStatus(manualTestCase.getAutomationStatus() == null ? AutomationStatusType.UNDEFINED.name() : manualTestCase.getAutomationStatus().name());
        manualTestCaseDTO.setLastExecutionId(manualTestCase.getLastExecution() == null ? 0L : manualTestCase.getLastExecution().getId());
        if (manualTestCase.getLastExecution() != null) {
            manualTestCaseDTO.setLastExecutionDate(manualTestCase.getLastExecution().getUpdated());
            manualTestCaseDTO.setLastExecutionStatus(manualTestCase.getLastExecution().getStatus().name());
            manualTestCaseDTO.setLastExecutionPlan(manualTestCase.getLastExecution().getTestPlan().getName());
            if (manualTestCase.getLastExecution().getAssignee() != null) {
                manualTestCaseDTO.setLastExecutionAssignee(manualTestCase.getLastExecution().getAssignee().getDisplayName());
            }
        }
        manualTestCaseDTO.setAutomatedTestCaseId(manualTestCase.getAutomatedTestCase() == null ? 0L : manualTestCase.getAutomatedTestCase().getId());
        manualTestCaseDTO.setNote(manualTestCase.getNote() == null ? null : noteMapper.convertToDTO(manualTestCase.getNote()));
        manualTestCaseDTO.setExternalId(manualTestCase.getExternalId());
        manualTestCaseDTO.setAutomationExternalId(manualTestCase.getAutomationExternalId());
        List<String> techniquesNames = Lists.newArrayList();

        Collection<TechniqueType> techniques = manualTestCase.getTechniques();
        if (techniques != null)
            techniques.forEach(techniqueType -> techniquesNames.add(techniqueType.name()));
        manualTestCaseDTO.setTechniques(techniquesNames);


        return manualTestCaseDTO;
    }

    @Override
    public ManualTestCase convertToEntity(ManualTestCaseDTO dto) {
        if (dto == null) {
            return null;
        }

        List<TechniqueType> techniques = Lists.newArrayList();
        if (dto.getTechniques() != null) {
            List<String> techniquesNames = dto.getTechniques();
            techniquesNames.forEach(name -> {
                if (!StringUtils.isEmpty(name))
                    techniques.add(TechniqueType.valueOf(name));
            });

        }

        ManualTestCase manualTestCase;
        if (dto.getId() == null || dto.getId() < 1) {
            manualTestCase = ManualTestCase.builder()
                    .id(null)
                    .name(dto.getName())
                    .requirement(getNullableByID(dto.getRequirementId(), id -> manualTestRequirementService.find(id)))
                    .needsUpdate(dto.isNeedsUpdate())
                    .steps(manualTestStepServiceDTO.convertToEntity(dto.getSteps()))
                    .priority(StringUtils.isEmpty(dto.getPriority()) ? null : TestPriorityType.valueOf(dto.getPriority()))
                    .techniques(techniques)
                    .product(getNullableByID(dto.getProductId(), id -> productService.find(id)))
                    .suite(StringUtils.isEmpty(dto.getSuite())  ? null : SuiteType.valueOf(dto.getSuite()))
                    .component1(getNullableByID(dto.getComponent1Id(), id -> productComponentService.find(id)))
                    .component2(getNullableByID(dto.getComponent2Id(), id -> productComponentService.find(id)))
                    .component3(getNullableByID(dto.getComponent3Id(), id -> productComponentService.find(id)))
                    .functionality(dto.getFunctionality())
                    .functionalityEntity(dto.getFunctionalityEntity() == null ? null : functionalityService.find(dto.getFunctionalityEntity().getId()))
                    .owner(dto.getOwner() == null ? null : userService.find(dto.getOwner().getId()))
                    .lastUpdater(dto.getLastUpdater() == null ? null : userService.find(dto.getLastUpdater().getId()))
                    .automationAssignee(dto.getAutomationAssignee() == null ? null : userService.find(dto.getAutomationAssignee().getId()))
                    .automationStatus(StringUtils.isEmpty(dto.getAutomationStatus()) ? null : AutomationStatusType.valueOf(dto.getAutomationStatus()))
                    .lastExecution(getNullableByID(dto.getLastExecutionId(), id -> manualTestExecutionService.find(id)))
                    .automatedTestCase(getNullableByID(dto.getAutomatedTestCaseId(), id -> testCaseService.find(id)))
                    .note(noteMapper.convertToEntity(dto.getNote()))
                    .externalId(dto.getExternalId())
                    .automationExternalId(dto.getAutomationExternalId() == null ? null : dto.getAutomationExternalId())
                    .build();


//            manualTestCase.getSteps().addAll(manualTestStepServiceDTO.convertToEntity(dto.getSteps()));
            assignMainStep(dto, manualTestCase);

        } else {
            manualTestCase = manualTestCaseService.find(dto.getId());
//            manualTestCase.setId(); Don't allow to update this.
            manualTestCase.setEnabled(dto.getEnabled());
//            manualTestCase.setTimestamp(dto.getTimestamp()); Don't allow to update this.
//            manualTestCase.setUpdated(dto.getUpdated()); Don't allow to update this.
            manualTestCase.setName(dto.getName());
            manualTestCase.setRequirement(getNullableByID(dto.getRequirementId(), id -> manualTestRequirementService.find(id)));
            manualTestCase.setNeedsUpdate(dto.isNeedsUpdate());
            manualTestCase.getSteps().clear();
            manualTestCase.getSteps().addAll(manualTestStepServiceDTO.convertToEntity(dto.getSteps()));
            manualTestCase.setPriority(TestPriorityType.valueOf(dto.getPriority()));
            manualTestCase.setTechniques(techniques);
            manualTestCase.setProduct(getNullableByID(dto.getProductId(), id -> productService.find(id)));
            manualTestCase.setSuite(SuiteType.valueOf(dto.getSuite()));
            manualTestCase.setComponent1(getNullableByID(dto.getComponent1Id(), id -> productComponentService.find(id)));
            manualTestCase.setComponent2(getNullableByID(dto.getComponent2Id(), id -> productComponentService.find(id)));
            manualTestCase.setComponent3(getNullableByID(dto.getComponent3Id(), id -> productComponentService.find(id)));
            manualTestCase.setFunctionality(dto.getFunctionality());
            manualTestCase.setFunctionalityEntity(dto.getFunctionalityEntity() == null ? null : functionalityService.find(dto.getFunctionalityEntity().getId()));
            manualTestCase.setOwner(dto.getOwner() == null ? null : userService.find(dto.getOwner().getId()));
            manualTestCase.setLastUpdater(dto.getLastUpdater() == null ? null : userService.find(dto.getLastUpdater().getId()));
            manualTestCase.setAutomationAssignee(dto.getAutomationAssignee() == null ? null : userService.find(dto.getAutomationAssignee().getId()));
            manualTestCase.setAutomationStatus(AutomationStatusType.valueOf(dto.getAutomationStatus()));
            manualTestCase.setLastExecution(getNullableByID(dto.getLastExecutionId(), id -> manualTestExecutionService.find(id)));
            manualTestCase.setAutomatedTestCase(getNullableByID(dto.getAutomatedTestCaseId(), id -> testCaseService.find(id)));
            manualTestCase.setNote(dto.getNote() == null ? null : noteMapper.convertToEntity(dto.getNote()));
            manualTestCase.setAutomationExternalId(manualTestCaseService.find(dto.getId()).getAutomationExternalId() == null ? null : manualTestCaseService.find(dto.getId()).getAutomationExternalId());
            assignMainStep(dto, manualTestCase);

        }

        for (ManualTestStep step : manualTestCase.getSteps()) {
            step.setTestCase(manualTestCase);
        }
        
        if (!StringUtils.isEmpty(dto.getRequirement())) {
            if (manualTestCase.getRequirement() == null) {
                ManualTestRequirement requirement = ManualTestRequirement.builder().name(dto.getRequirement()).build();
                requirement = manualTestRequirementService.save(requirement);
                manualTestCase.setRequirement(requirement);
            } else {
                manualTestCase.getRequirement().setName(dto.getRequirement());
                manualTestCaseService.update(manualTestCase);
            }
        }

        manualTestCase.getSteps().sort(Comparator.comparing(ManualTestStep::getStepOrder));

        return manualTestCase;
    }

    private void assignMainStep(ManualTestCaseDTO dto, ManualTestCase manualTestCase) {
        if (dto.getSteps() == null) return;

        Optional<ManualTestStepDTO> match = dto.getSteps()
                .stream()
                .filter(ManualTestStepDTO::isMain)
                .findFirst();

        ManualTestStepDTO step = match.orElseGet(() -> dto.getSteps().stream().reduce((first, second) -> second).orElse(null));
        ManualTestStep mainStep = manualTestStepServiceDTO.convertToEntity(step);
        manualTestCase.setMainStep(mainStep);

    }
    
    private List<ManualTestStepDTO> getStepsDTO(ManualTestCase entity) {
        if (entity.getSteps() == null) {
            return null;
        }
        if (entity.getSteps().isEmpty()) {
            return new ArrayList<>(0);
        }
        
        List<ManualTestStepDTO> dtos = new ArrayList<>(entity.getSteps().size()); 

        int minStep = entity.getSteps().get(0).getStepOrder();
        ManualTestStepDTO stepDTO = null;
        for (ManualTestStep step : entity.getSteps()) {
            stepDTO = manualTestStepServiceDTO.convertToDTO(step);
            stepDTO.setStepOrder(stepDTO.getStepOrder() - minStep);
            dtos.add(stepDTO);
        }
        
        return dtos;
    }
    
    

}
