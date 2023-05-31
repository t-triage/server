/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.manual.service;


import com.clarolab.model.Product;
import com.clarolab.model.User;
import com.clarolab.model.manual.Functionality;
import com.clarolab.model.manual.ManualTestCase;
import com.clarolab.model.manual.ManualTestExecution;
import com.clarolab.model.manual.ProductComponent;
import com.clarolab.model.manual.repository.ManualTestCaseRepository;
import com.clarolab.model.manual.types.AutomationStatusType;
import com.clarolab.model.manual.types.TechniqueType;
import com.clarolab.model.manual.types.TestPriorityType;
import com.clarolab.repository.BaseRepository;
import com.clarolab.service.BaseService;
import com.clarolab.service.FunctionalityService;
import com.clarolab.service.UserService;
import com.clarolab.service.exception.ConfigurationError;
import com.clarolab.startup.LicenceValidator;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.clarolab.util.Constants.MIN_SEARCH_LENGHT_;

@Service
@Log
public class ManualTestCaseService extends BaseService<ManualTestCase> {

    @Autowired
    private ManualTestCaseRepository manualTestCaseRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ManualTestCaseService manualTestCaseService;

    @Autowired
    private ManualTestExecutionService manualTestExecutionService;

    @Autowired
    private ProductComponentService productComponentService;

    @Autowired
    private ManualTestPlanService manualTestPlanService;

    @Autowired
    private LicenceValidator licenceValidator;
    
    @Autowired
    private FunctionalityService functionalityService;


    @Override
    public BaseRepository<ManualTestCase> getRepository() {
        return manualTestCaseRepository;
    }

    @Override
    public ManualTestCase save(ManualTestCase entry){
        if (!licenceValidator.validateTestCreation()) {
            log.info("License not valid...");
            throw new ConfigurationError("Unable to create Test, you have reached the limit of 50 test cases");
        }
        return super.save(entry);
    }
    
    public List<ManualTestCase> findAllByProduct(Product product) {
        return manualTestCaseRepository.findAllByProduct(product);
    }

    public List<ManualTestCase> findAllByOwner(User user) {
        return manualTestCaseRepository.findAllByOwner(user);
    }

    public List<ManualTestCase> searchByName(String name) {
        if (name == null || name.length() < MIN_SEARCH_LENGHT_)
            return Lists.newArrayList();

        name = StringUtils.prepareStringForSearch(name);
        return manualTestCaseRepository.search(name);
    }

    public List<ManualTestCase> searchStrictName(String name) {
        return manualTestCaseRepository.findAllByName(name);
    }

    public List<ManualTestCase> findAllFiltered(@Nullable Specification searchSpec, @Nullable Sort sort, Long lastExecution, Long owner, Long testPlan, Integer techniques, Boolean needsUpdate, String requirement, String name, String suite, Long component1, Long component2, Long component3, Long functionality, Integer priority, Integer automationStatus, Long id, String externalId, Long excludeTestPlan) {
        Stream<ManualTestCase> stream = manualTestCaseRepository.findAllFiltered(searchSpec, sort).stream();

        if (id != null || !StringUtils.isEmpty(externalId)) {
            List<ManualTestCase> match = Lists.newArrayList();
            ManualTestCase testWithId = null;
            if (id != null) {
                testWithId = find(id);
            }
            if (testWithId == null && !StringUtils.isEmpty(externalId)) {
                testWithId = findByExternalId(externalId);
                if (testWithId == null) {
                    try {
                        testWithId = find(Long.parseLong(externalId));
                    } catch (NumberFormatException ex) {
                        log.log(Level.INFO, "Repository Filter: Not using externalId as regular Id");
                    }
                }
            }
            if (testWithId == null && id != null) {
                testWithId = findByExternalId(String.valueOf(id));
            }
            if (testWithId != null) {
                match.add(testWithId);
            }
            return match;
        }

        if (lastExecution != null) {
            List<ManualTestExecution> executions = manualTestExecutionService.findByLastExecutionTime(lastExecution);
            if (!executions.isEmpty())
                stream = stream.filter(mt -> executions.contains(mt.getLastExecution()));
        }

        if (owner != null) {
            User user = userService.find(owner);
            if (user != null)
                stream = stream.filter(mt -> mt.getOwner().equals(user) || (mt.getLastUpdater() != null && mt.getLastUpdater().equals(user)));
        }


        if (testPlan != null) {
            List<ManualTestExecution> manualTestExecutions = manualTestExecutionService.findByPlan(manualTestPlanService.find(testPlan));
            List<Long> testCaseIds = manualTestExecutions
                    .stream()
                    .filter(ManualTestExecution::isEnabled)
                    .map(manualTestExecution -> manualTestExecution.getTestCase().getId())
                    .collect(Collectors.toList());
            if (!manualTestExecutions.isEmpty())
                stream = stream.filter(mt -> testCaseIds.contains(mt.getId()));
        }

        if (excludeTestPlan != null) {
            List<ManualTestExecution> manualTestExecutions = manualTestExecutionService.findByPlan(manualTestPlanService.find(excludeTestPlan));
            List<Long> testCaseIds = manualTestExecutions
                    .stream()
                    .filter(ManualTestExecution::isEnabled)
                    .map(manualTestExecution -> manualTestExecution.getTestCase().getId())
                    .collect(Collectors.toList());
            if (!manualTestExecutions.isEmpty())
                stream = stream.filter(mt -> !testCaseIds.contains(mt.getId()));
        }

        if (techniques != null) {
            TechniqueType type = TechniqueType.values()[techniques];
            stream = stream.filter(mt -> mt.getTechniques().contains(type));
        }

        if (needsUpdate != null)
            stream = stream.filter(mt -> mt.isNeedsUpdate() == needsUpdate);

        if (requirement != null) {
            stream = stream.filter(mt -> mt.getRequirement() != null && mt.getRequirement().getName().toLowerCase().contains(requirement.toLowerCase()));
        }

        if (name != null)
            stream = stream.filter(mt -> mt.getName().toLowerCase().contains(name.toLowerCase()));
        

        if (suite != null)
            stream = stream.filter(mt -> mt.getSuite().name().toLowerCase().contains(suite.toLowerCase()));

        if (component1 != null) {
            ProductComponent productComponent = productComponentService.find(component1);
            if (productComponent != null) {
                stream = stream.filter(mt -> productComponent.equals(mt.getComponent1()) || productComponent.equals(mt.getComponent2()) || productComponent.equals(mt.getComponent3()));
            }
        }

        if (component2 != null) {
            ProductComponent productComponent2 = productComponentService.find(component2);
            if (productComponent2 != null) {
                stream = stream.filter(mt -> productComponent2.equals(mt.getComponent1()) || productComponent2.equals(mt.getComponent2()) || productComponent2.equals(mt.getComponent3()));
            }
        }

        if (component3 != null) {
            ProductComponent productComponent3 = productComponentService.find(component3);
            if (productComponent3 != null) {
                stream = stream.filter(mt -> productComponent3.equals(mt.getComponent1())|| productComponent3.equals(mt.getComponent2()) || productComponent3.equals(mt.getComponent3()));
            }
        }

        if (functionality != null) {
            Functionality func = functionalityService.find(functionality);
            if (func != null)
                stream = stream.filter(mt -> mt.getFunctionalityEntity() != null && mt.getFunctionalityEntity().equals(func));
        }
            
        if (priority != null) {
            TestPriorityType type = TestPriorityType.values()[priority];
            stream = stream.filter(mt -> mt.getPriority().equals(type));
        }

        if (automationStatus != null) {
            AutomationStatusType type = AutomationStatusType.values()[automationStatus];
            stream = stream.filter(mt -> mt.getAutomationStatus().equals(type));
        }

        return stream.collect(Collectors.toList());

    }

    public List<String> searchFunctionality(String name) {
        if (name == null || name.length() < MIN_SEARCH_LENGHT_)
            return Lists.newArrayList();

        name = StringUtils.prepareStringForSearch(name);
        List<ManualTestCase> queryResult = manualTestCaseRepository.searchFunctionality(name);
        List<String> queryString = Lists.newArrayList();
        for (ManualTestCase manualTestCase : queryResult) {
            queryString.add(manualTestCase.getFunctionalityEntity().getName());
        }
        return queryString.stream().distinct().collect(Collectors.toList());
    }
    
    public List<ManualTestCase> findAllByFunctionalityNotNull() {
        return manualTestCaseRepository.findAllByFunctionalityNotNull();
    }

    public List<ManualTestCase> findToAutomate() {
        return manualTestCaseRepository.findByAutomationStatusIn(AutomationStatusType.getPendingToAutomate());
    }

    public ManualTestCase findByExternalId(String id) {
        return manualTestCaseRepository.findManualTestCaseByExternalId(id);
    }

    public long countAllPendingToAutomate(Product product) {
        return manualTestCaseRepository.countByProductAndAutomationStatusIn(product, AutomationStatusType.getPendingToAutomate());
    }

    public long countAllPendingToAutomateAfter(Product product, long date) {
        return manualTestCaseRepository.countByProductAndAutomationStatusInAndTimestampGreaterThan(product, AutomationStatusType.getPendingToAutomate(), date);
    }

    public List<ManualTestCase> getManualTestCases (long timestamp){
        return manualTestCaseRepository.findManualTestCasesByTimestampIsGreaterThanEqual(timestamp);
    }
}

