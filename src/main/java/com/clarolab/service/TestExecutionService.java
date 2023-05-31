/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;


import com.clarolab.dto.DateStatsDTO;
import com.clarolab.model.TestExecution;
import com.clarolab.model.TestExecutionStep;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.TestExecutionRepository;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static com.clarolab.util.Constants.MIN_SEARCH_LENGHT_;
import static com.clarolab.util.StringUtils.trimAll;

@Service
@Log
public class TestExecutionService extends BaseService<TestExecution> {

    @Autowired
    private TestExecutionRepository testExecutionRepository;

    @Autowired
    private TestExecutionStepService testExecutionStepService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    protected PropertyService propertyService;

    @Override
    public BaseRepository<TestExecution> getRepository() {
        return testExecutionRepository;
    }

    public List<TestExecution> findExecutionsWithSameErrors(TestExecution testExecution) {
        return testExecutionRepository.findAllByErrorDetailsOrErrorStackTrace(testExecution.getErrorDetails(), testExecution.getErrorStackTrace());
    }

    public List<String> searchTestName(String name) {
        if (name == null || name.length() < MIN_SEARCH_LENGHT_) {
            return Lists.newArrayList();
        }

        String nameWithoutSpaces = StringUtils.prepareStringForSearch(trimAll(name));
        name  = StringUtils.prepareStringForSearch(name);
        List<TestExecution> testExecutions = testExecutionRepository.searchTestNames(name, nameWithoutSpaces, true);

        return testExecutions
                .stream()
                .filter(TestExecution::isEnabled)
                .map(TestExecution::getName)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<TestExecutionStep> getTestSteps(Long id) {
        TestExecution testExecution = find(id);
        return testExecution.getTestExecutionSteps();
    }

    public long deleteOldExecutions(long timestamp) {
        long deletedCount = testExecutionStepService.deleteOld(timestamp);
        log.log(Level.INFO, String.format("TestStep: Finish deleting %d tests", deletedCount));
        deletedCount = testExecutionRepository.clearLogs(timestamp);
        log.log(Level.INFO, String.format("TestExecution: Finish clearing logs %d tests", deletedCount));
        return deletedCount;
    }

    public List<DateStatsDTO> getErrorTestStats(){
        List<DateStatsDTO>dateDTO = new ArrayList<>();

        //long cal = Calendar.getInstance().getTimeInMillis()-Calendar.YEAR;
        long current = Calendar.getInstance().getTimeInMillis();

        List<DateStatsDTO> list = testExecutionRepository.findTestWithErrors(current);
        list.stream()
                .sorted(Comparator.comparing(DateStatsDTO::getFailExecutionDate).reversed())
                .collect(Collectors.toList());
        return list;
    }
}
