/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.controller.impl.OptimizedPage;
import com.clarolab.model.Pipeline;
import com.clarolab.model.PipelineTest;
import com.clarolab.model.TestCase;
import com.clarolab.model.User;
import com.clarolab.model.component.AutomatedComponent;
import com.clarolab.model.component.TestComponentRelation;
import com.clarolab.model.types.StateType;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.TestCaseRepository;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.clarolab.util.Constants.MIN_SEARCH_LENGHT_;

@Service
@Log
public class TestCaseService extends BaseService<TestCase> {

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Autowired
    private PipelineTestService pipelineTestService;

    @Autowired
    private PipelineService pipelineService;

    @Autowired
    private UserService userService;

    @Autowired
    private AutomatedComponentService automatedComponentService;

    @Autowired
    private TestComponentRelationService testComponentRelationService;

    private List<TestCase> newTests = new ArrayList<>();


    @Override
    public BaseRepository<TestCase> getRepository() {
        return testCaseRepository;
    }

    public List<TestCase> findAllByComponent(AutomatedComponent component) {
        ArrayList<TestCase> answer = new ArrayList<>();

        for (TestComponentRelation relation : component.getTestComponentRelations()) {
            answer.add(relation.getTestCase());

        }

        return answer.stream().distinct().collect(Collectors.toList());
    }

    public TestCase testCaseLike(TestCase test) {
        List<TestCase> testCases = testCaseRepository.findAllByName(test.getName());

        if (testCases.isEmpty()) {
            return null;
        }

        TestCase bestTest = null;
        int bestMatch = 2;
        for (TestCase dbTest : testCases) {
            int newMatch = matches(test, dbTest);
            if (newMatch >= bestMatch) {
                bestTest = dbTest;
                bestMatch = newMatch;
            }
        }

        return bestTest;
    }

    public TestCase newOrFind(String name, String path) {
        long now = DateUtils.now();
        TestCase testCase = new TestCase().builder()
                .enabled(true)
                .timestamp(now)
                .updated(now)
                .name(name)
                .locationPath(path)
                .build();
        return newOrFind(testCase);
    }

    public synchronized TestCase newOrFind(TestCase testCase) {
        TestCase dbTest = testCaseLike(testCase);
        if (dbTest == null) {
            // if it is not in the DB, it could have been created but still not saved (in transaction)
            TestCase testNotPersistent = findNewTest(newTests, testCase);
            if (testNotPersistent == null) {
                newTests.add(testCase);
                return testCase;
            } else {
                testNotPersistent.setDataProvider(true);
                return testNotPersistent;
            }
        } else {
            return dbTest;
        }
    }

    // Keeps a non persistent object collection
    public TestCase findNewTest(List<TestCase> list, TestCase testCase) {
        TestCase bestTest = null;
        int bestMatch = 2;
        int newMatch = 0;

        for (TestCase newCase : list) {
            if (newCase.getName().equals(testCase.getName())) {
                newMatch = matches(testCase, newCase);
                if (newMatch >= bestMatch) {
                    bestTest = newCase;
                    bestMatch = newMatch;
                }
            }
        }
        return bestTest;
    }

    private int matches(TestCase fromTest, TestCase dbTest) {
        int matches = 0;
        if (fromTest.getName().equals(dbTest.getName())) {
            matches++;
        }

        if (fromTest.getLocationPath() == null && dbTest.getLocationPath() == null) {
            matches++;
        } else if (fromTest.getLocationPath() != null && fromTest.getLocationPath().equals(dbTest.getLocationPath())) {
            matches++;
        }

        return matches;

    }

    public double getUniqueTestsCount() {
        return testCaseRepository.countByEnabled(true);
    }

    public void cleanNewTests() {
        newTests = new ArrayList<>();
    }

    public TestCase find(TestCase test) {
        return testCaseRepository.findTopByNameAndLocationPath(test.getName(), test.getLocationPath());
    }

    public List<TestCase> findAllByLocationPathContains(String locationPath) {
        return testCaseRepository.findAllByLocationPathContains(locationPath);
    }

    public TestCase findTopByLocationPath(String locationPath) {
        return testCaseRepository.findTopByLocationPath(locationPath);
    }

    public OptimizedPage<TestCase> findAllFilteredByAutomatedTest(@Nullable Specification searchSpec, Long lastExecution, String name, String executorName, List<StateType> stateFilter, Boolean hideNoSuite, Pageable pageable, String tag, Long assigneeId, Long pipelineId, List<Long> components) {
        List<Long> testCaseIds = null;
        List<AutomatedComponent> automatedComponents = new ArrayList<>();
        Boolean filter = false;
        List<TestCase> tests = new ArrayList<>();
        String searchName = null;
        String searchExecutorName = null;
        String searchTag = null;
        Sort sort = pageable.getSort();
        OptimizedPage<TestCase> answer;

        boolean hasSuiteFilter = (lastExecution != null && lastExecution > 0) || !StringUtils.isEmpty(executorName) || (stateFilter != null && stateFilter.size() > 0) || (hideNoSuite != null && hideNoSuite) || tag != null || assigneeId != null || pipelineId != null;
        boolean hasFilter = hasSuiteFilter || (!StringUtils.isEmpty(name) && name.length() > MIN_SEARCH_LENGHT_);

        User triager = assigneeId == null ? null : userService.find(assigneeId);
        Pipeline pipeline = pipelineId == null ? null : pipelineService.find(pipelineId);

        if (!components.isEmpty()) {
            for (Long automatedComponentId : components) {
                if (automatedComponentId != null)
                    automatedComponents.add(automatedComponentService.find(automatedComponentId));
            }
        }

        if (name != null && name.length() > MIN_SEARCH_LENGHT_) {
            searchName = StringUtils.prepareStringForSearch(name);
        } else {
            searchName = null;
        }

        if (executorName != null && executorName.length() >= MIN_SEARCH_LENGHT_) {
            searchExecutorName = StringUtils.prepareStringForSearch(executorName);
        } else {
            searchExecutorName = null;
        }
        if (tag != null) {
            searchTag = StringUtils.prepareStringForSearch(tag);
        } else {
            searchTag = null;
        }
        Set<TestCase> testSet = null;
        if (pipeline != null) {
            List<PipelineTest> pipelineTest = pipelineTestService.findAll(pipeline);
            testSet = pipelineTest
                    .stream()
                    .map(mt -> mt.getTest())
                    .collect(Collectors.toSet());
        }

        if (!automatedComponents.isEmpty()) {
            List<TestComponentRelation> testComponentRelations = testComponentRelationService.findAllByComponentIsIn(automatedComponents);

            for (TestComponentRelation relation : testComponentRelations) {
                if (relation.getTestCase().getAutomatedComponents().containsAll(automatedComponents))
                    tests.add(relation.getTestCase());
            }

            testSet = testSet == null
                    ? (new HashSet<>(tests))
                    : (testSet.stream()
                    .filter(tests::contains)
                    .collect(Collectors.toSet()));
        }

        // Look for all possible test cases
        Boolean ignoreCurrentState = true;
        if(stateFilter!= null){
            ignoreCurrentState = false;
        }

        List<TestCase> triage = testCaseRepository.findAllByAutomatedTestFilter(searchName, lastExecution, stateFilter, searchExecutorName, ignoreCurrentState, triager, hideNoSuite, searchTag);
        testSet = testSet == null ? (triage
                .stream()
                .collect(Collectors.toSet()))
                : (testSet.stream()
                        .filter(t -> triage.contains(t))
                        .collect(Collectors.toSet()));

        List<TestCase> sortedList = testSet.stream().sorted(Comparator.comparing(TestCase::getName)).collect(Collectors.toList());
        answer = OptimizedPage.getPageable(pageable, sortedList);
        
        return answer;

    }
}
