/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;


import com.clarolab.model.TestExecution;
import com.clarolab.model.TestExecutionStep;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.TestExecutionStepRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;

@Service
@Log
public class TestExecutionStepService extends BaseService<TestExecutionStep> {

    @Autowired
    private TestExecutionStepRepository repository;

    @Override
    public BaseRepository<TestExecutionStep> getRepository() {
        return repository;
    }

    @Autowired
    private TestExecutionService testExecutionService;

    public List<TestExecutionStep> getTestSteps(Long id) {
        TestExecution testExecution = testExecutionService.find(id);
        return testExecution.getTestExecutionSteps();
    }

    public List<TestExecutionStep> getTestSteps(TestExecution testExecution) {
        return  repository.findAllByTestExecutionAndEnabled(testExecution, true);
    }

    public int deleteOld(long timestamp) {
        return repository.deleteTestExecutionStepsByTime(timestamp);
    }


}
