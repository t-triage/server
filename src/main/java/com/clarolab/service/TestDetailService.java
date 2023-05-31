/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.model.TestTriage;
import com.clarolab.model.detail.ErrorOccurrence;
import com.clarolab.model.detail.TestDetail;
import com.clarolab.model.detail.TestOccurrence;
import com.clarolab.repository.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@Component
public class TestDetailService extends BaseService<TestDetail> implements TTriageService<TestDetail> {

    @Autowired
    private TestExecutionService testExecutionService;

    @Autowired
    private TestTriageService testTriageService;

    private static Predicate<TestTriage> isPass = TestTriage::isPass;
    private static Predicate<TestTriage> isNewPass = TestTriage::isNewPass;

    //Identifies all test passed states
    private static Predicate<TestTriage> countsAsPass = TestDetailService.isPass
            .or(TestDetailService.isNewPass);

    // Possible shortcut cases:
    // TODO If new fail -> consecutive: 0, since: this test
    // TODO If new pass / pass -> consecutive: 0, since: none
    // If fail the current calculation below
    public TestDetail calculateTestDetails(Long testTriageID) {
        TestTriage testTriage = testTriageService.find(testTriageID);
        if (testTriage == null) {
            // nothing to calculate really
            return null;
        }
        return calculateTestDetails(testTriage);
    }

    public TestDetail calculateTestDetails(TestTriage testTriage) {
        TestDetail.TestDetailBuilder builder = TestDetail.builder().id(0L).enabled(true);

        // TODO - get details and calculate diff ?
        // TODO - OR calculate diff on each test triage creation and here just get.

        this.calculateHistoricPasses(testTriage, builder);
        this.calculateHistoricFails(testTriage, builder);
        this.calculateConsecutivePasses(testTriage, builder);
        this.calculateConsecutiveFails(testTriage, builder);
        this.calculateSameErrorAt(testTriage, builder);
        this.calculateSameTestAt(testTriage, builder);

        // TODO - save details

        return builder.build();
    }

    private void calculateHistoricPasses(final TestTriage testTriage, TestDetail.TestDetailBuilder builder) {
        final long historicOccurrences = testTriageService.countTestWith(testTriage.getTestCase(), TestTriage.passStates());
        builder.historicPasses(Math.max(0,historicOccurrences - (testTriage.isPassed() ? 1 : 0 )));
    }

    private void calculateHistoricFails(final TestTriage testTriage, TestDetail.TestDetailBuilder builder) {
        final long historicOccurrences = testTriageService.countTestWith(testTriage.getTestCase(), TestTriage.failStates());
        builder.historicFails(Math.max(0,historicOccurrences - (testTriage.isFailed() ? 1 : 0 )));
    }

    private void calculateConsecutivePasses(final TestTriage testTriage, TestDetail.TestDetailBuilder builder) {
        TestTriage passTriage = testTriageService.lastTestWithoutStates(testTriage, TestTriage.passStates());

        if (passTriage == null) {
            builder.consecutivePasses(testTriage.getBuildNumber());
            builder.failsSince(0);
        } else {
            builder.consecutivePasses(testTriage.getBuildNumber() - passTriage.getBuildNumber());
            builder.passSince(passTriage.getExecutionDate());
        }
    }

    private void calculateConsecutiveFails(final TestTriage testTriage, TestDetail.TestDetailBuilder builder) {
        TestTriage passTriage = testTriageService.lastTestWithoutStates(testTriage, TestTriage.failStates());

        if (passTriage == null) {
            builder.consecutiveFails(testTriage.getBuildNumber());
            builder.failsSince(0);
        } else {
            builder.consecutiveFails(testTriage.getBuildNumber() - passTriage.getBuildNumber());
            builder.failsSince(passTriage.getExecutionDate());
        }
    }


    private void calculateSameErrorAt(final TestTriage testTriage, TestDetail.TestDetailBuilder builder) {
        Set<ErrorOccurrence> sameErrors = new HashSet<>();

        if (testTriage.getTextExecutionErrorDetails() == null || testTriage.getTextExecutionErrorDetails().isEmpty()) {
            builder.errorOccurrences(sameErrors);
            return;
        }

        List<TestTriage> tests = testTriageService.findAllSameError(testTriage);
        for (TestTriage test: tests) {
            if (!test.getId().equals(testTriage.getId())) {
                ErrorOccurrence.ErrorOccurrenceBuilder occurrenceBuilder = ErrorOccurrence.builder();
                occurrenceBuilder.suiteID(test.getExecutorId()); // it should be executor
                occurrenceBuilder.suiteName(test.getExecutorName());
                occurrenceBuilder.testID(test.getId());
                occurrenceBuilder.testName(test.getTestName());
                occurrenceBuilder.testTriage(test);

                sameErrors.add(occurrenceBuilder.build());
            }
        }

        builder.errorOccurrences(sameErrors);
    }


    private void calculateSameTestAt(final TestTriage testTriage, TestDetail.TestDetailBuilder builder) {
        List<TestTriage> tests = testTriageService.findLastSameTests(testTriage);

        Set<TestOccurrence> sameTest = new HashSet<>();

        for (TestTriage test: tests) {
            if (!test.getId().equals(testTriage.getId())) {
                if (testTriage.getProduct().getId().equals(test.getProduct().getId())) {
                    TestOccurrence.TestOccurrenceBuilder occurrenceBuilder = TestOccurrence.builder();
                    occurrenceBuilder.suiteID(test.getExecutorId());
                    occurrenceBuilder.suiteName(test.getExecutorName());
                    occurrenceBuilder.testTriage(test);

                    sameTest.add(occurrenceBuilder.build());
                }
            }
        }

        builder.testOccurrences(sameTest);
    }


    @Override
    protected BaseRepository<TestDetail> getRepository() {
        return null;
    }
}
