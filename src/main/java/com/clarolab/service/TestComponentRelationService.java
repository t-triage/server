/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.model.TestCase;
import com.clarolab.model.component.AutomatedComponent;
import com.clarolab.model.component.TestComponentRelation;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.TestComponentRelationRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log
public class TestComponentRelationService extends BaseService<TestComponentRelation> {

    @Autowired
    private TestComponentRelationRepository testComponentRelationRepository;


    @Override
    public BaseRepository<TestComponentRelation> getRepository() {
        return testComponentRelationRepository;
    }

    public TestComponentRelation find(AutomatedComponent automatedComponent, TestCase testCase) {
        return testComponentRelationRepository.findFirstByComponentAndTestCase(automatedComponent, testCase);
    }

    public List<TestComponentRelation> findAll(AutomatedComponent automatedComponent) {
        return testComponentRelationRepository.findAllByComponent(automatedComponent);
    }

    public TestComponentRelation findFirstByTestCase(TestCase testCase) {
        return testComponentRelationRepository.findFirstByTestCase(testCase);
    }

    public long countByTestCase(TestCase testCase) {
        return testComponentRelationRepository.countByTestCase(testCase);
    }

    public List<TestComponentRelation> findAllByComponentIsIn(List<AutomatedComponent> automatedComponents) {
        return testComponentRelationRepository.findAllByComponentIsIn(automatedComponents);
    }

    public Long delete(TestCase testCase, AutomatedComponent automatedComponent) {
        Long deletedId = 0L;
        TestComponentRelation testComponentRelation = find(automatedComponent, testCase);
        if (testComponentRelation != null) {
            deletedId = testComponentRelation.getId();
            delete(deletedId);
        }

        return deletedId;
    }
}
