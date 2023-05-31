/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.model.TestCase;
import com.clarolab.model.component.AutomatedComponent;
import com.clarolab.model.component.TestComponentRelation;
import com.clarolab.repository.AutomatedComponentRepository;
import com.clarolab.repository.BaseRepository;
import com.clarolab.util.StringUtils;
import lombok.extern.java.Log;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.clarolab.util.Constants.MAX_AUTOMATED_COMPONENTS_ALLOWED;
import static com.clarolab.util.Constants.MIN_SEARCH_LENGHT_;

@Service
@Log
public class AutomatedComponentService extends BaseService<AutomatedComponent> {

    @Autowired
    AutomatedComponentRepository automatedComponentRepository;

    @Autowired
    TestComponentRelationService testComponentRelationService;

    @Autowired
    TestCaseService testCaseService;

    @Override
    protected BaseRepository<AutomatedComponent> getRepository() {
        return automatedComponentRepository;
    }

    public List<AutomatedComponent> findAll() {
        return automatedComponentRepository.findAllByEnabled(true);
    }

    public List<TestComponentRelation> findAllByComponent(AutomatedComponent automatedComponent) {
        return testComponentRelationService.findAll(automatedComponent);
    }

    public List<AutomatedComponent> search(String name) {
        if (name == null || name.length() < MIN_SEARCH_LENGHT_) {
            return Lists.newArrayList();
        }

        name = StringUtils.prepareStringForSearch(name);
        return automatedComponentRepository.search(name, true);
    }

    public void setComponentToTests(AutomatedComponent automatedComponent, List<Long> testCaseIds) {
        for (Long testCaseID : testCaseIds) {
            TestCase testCase = testCaseService.find(testCaseID);
            TestComponentRelation testComponentRelation = testComponentRelationService.find(automatedComponent, testCase);

            long countRelations = testComponentRelationService.countByTestCase(testCase);

            if (testComponentRelation == null && countRelations < MAX_AUTOMATED_COMPONENTS_ALLOWED) {
                TestComponentRelation relation = new TestComponentRelation();
                relation.setComponent(automatedComponent);
                relation.setTestCase(testCase);
                testCase.add(relation);
                testComponentRelationService.save(relation);
            } else {
                // Nothing to do. Test has already the selected component or has reached the max. amount of components.
                if (testComponentRelation != null)
                    log.info("Automated test case: " + testCase.getId() + " already has component: " + automatedComponent.getId());
                if (countRelations >= MAX_AUTOMATED_COMPONENTS_ALLOWED)
                    log.info("Automated test case: " + testCase.getId() + " has reached the max. amount of components");
            }
        }
    }

    public List<AutomatedComponent> suggestedDefaultComponents() {
        List<TestComponentRelation> relations = testComponentRelationService.findAll();
        Set<TestCase> testCases = new HashSet<>();
        Set<AutomatedComponent> answer = new HashSet<>();

        for (TestComponentRelation relation : relations) {
            testCases.add(relation.getTestCase());
        }

        for (TestCase testcase : testCases) {
            TestComponentRelation relationFound = testComponentRelationService.findFirstByTestCase(testcase);
            answer.add(relationFound.getComponent());
        }

        List<AutomatedComponent> sortedAnswer = new ArrayList<>(answer);
        sortedAnswer.sort(Comparator.comparing(AutomatedComponent::getName));

        return sortedAnswer;
    }

    public List<AutomatedComponent> suggestedComponents(List<Long> automatedComponentIds) {
        List<AutomatedComponent> automatedComponents = new ArrayList<>();

        for (Long automatedComponentId : automatedComponentIds) {
            automatedComponents.add(this.find(automatedComponentId));
        }

        // All relations which contains components passed by parameters
         List<TestComponentRelation> relations = testComponentRelationService.findAllByComponentIsIn(automatedComponents);

        List<TestCase> testCases = new ArrayList<>();
        for (TestComponentRelation relation : relations) {
            testCases.add(relation.getTestCase());
        }
        testCases = testCases.stream().distinct().collect(Collectors.toList());

        Set<AutomatedComponent> answer = new HashSet<>();
        for (TestCase testCase : testCases) {
            if (!testCase.getAutomatedComponents().isEmpty()) {
                Set<AutomatedComponent> components = testCase.getAutomatedComponents().stream().filter(automatedComponents::contains).collect(Collectors.toSet());
                if (automatedComponents.size() == components.size()) {
                    answer.addAll(testCase.getAutomatedComponents());
                }
            }
        }

        // Don't suggest components passed by parameters
        automatedComponents.forEach(answer::remove);

        List<AutomatedComponent> sortedAnswer = new ArrayList<>(answer);
        sortedAnswer.sort(Comparator.comparing(AutomatedComponent::getName));

        return sortedAnswer;

    }
}
