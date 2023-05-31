/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;


import com.clarolab.dto.db.TestTriageHistoryDTO;
import com.clarolab.event.process.ApplicationEventBuilder;
import com.clarolab.event.process.ApplicationEventType;
import com.clarolab.model.*;
import com.clarolab.model.helper.tag.TagHelper;
import com.clarolab.model.types.StateType;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.TestTriageRepository;
import com.clarolab.startup.LicenceValidator;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import com.clarolab.view.KeyValuePair;
import com.google.common.collect.Lists;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static com.clarolab.util.Constants.*;

@Service
@Log
public class TestTriageService extends BaseService<TestTriage> {

    @Autowired
    private TestTriageRepository testTriageRepository;

    @Autowired
    private BuildTriageService buildTriageService;

    @Autowired
    private ApplicationEventBuilder applicationEventBuilder;

    @Autowired
    private UserService userService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestPinService testPinService;

    @Autowired
    protected PropertyService propertyService;

    @Autowired
    private ErrorDetailService errorDetailService;

    @Autowired
    private AutomatedTestIssueService automatedTestIssueService;

    @Autowired
    private TestExecutionService testExecutionService;

    @Autowired
    private TestExecutionStepService testExecutionStepService;

    @Autowired
    private LicenceValidator licenceValidator;

    @Autowired
    private NotificationService notificationService;

    @Override
    public BaseRepository<TestTriage> getRepository() {
        return testTriageRepository;
    }

    public List<TestTriage> findAll(Executor executor, TestCase testCase) {
        // return testTriageRepository.findAllByBuild_ExecutorAndTestExecution_TestCase(executor, testCase);
        return testTriageRepository.findAllByExecutorAndTestCaseAndEnabledOrderByIdDesc(executor, testCase, true);
    }

    public TestTriage findLastByTestCase(TestCase testCase){
        return testTriageRepository.findFirstByTestCaseOrderByBuildDesc(testCase);
    }

    public List<TestTriage> findAllByLastExecution(Long lastExecution) {
        return testTriageRepository.findByExecutionDateGreaterThanEqualOrderByExecutionDateDesc(lastExecution);
    }
    public List<TestTriage> findAllBySuiteName(String executorName) {
        if (executorName == null || executorName.length() < MIN_SEARCH_LENGHT_) {
            return Lists.newArrayList();
        }
        executorName = StringUtils.prepareStringForSearch(executorName);
        return testTriageRepository.findAllByExecutorNameIgnoreCaseLike(executorName);
    }

    public List<TestTriage> findAllByCurrentState(StateType[] currentState) {
        return testTriageRepository.findAllByCurrentStateIn(currentState);
    }

    public List<TestTriage> findAllByTestCaseIsNotNull(){
        return testTriageRepository.findAllByTestCaseIsNotNull();
    }

   public TestTriage findPreviousTriage(TestTriage testTriage) {
        Optional<TestTriage> answer = findPreviousTriage(testTriage.getExecutor(), testTriage.getTestCase(), testTriage.getBuildNumber());

        return answer.isPresent() ? answer.get() : null;
    }

    public Optional<TestTriage> findPreviousTriage(List<TestTriage> previousTriages, TestCase testCase) {

        for (TestTriage test : previousTriages) {
            if (test.getTestCase().equals(testCase)) {
                return Optional.of(test);
            }
        }

        return Optional.empty();
    }

    public List<TestTriage> findPreviousTriages(List<TestTriage> previousTriages, TestCase testCase) {
        List<TestTriage> answer = new ArrayList<>();

        for (TestTriage test : previousTriages) {
            if (test.getTestCase().equals(testCase)) {
                answer.add(test);
            }
        }

        return answer;
    }


    public TestTriage findLastTriage(TestExecution testExecution, Build build) {
        return testTriageRepository.findFirstByTestExecutionAndBuild(testExecution, build);
    }

    public TestTriage findLastTriage(TestCase test, Build build) {
        return testTriageRepository.findFirstByTestCaseAndBuild(test, build);
    }

    public Optional<TestTriage> findPreviousTriage(Executor executor, TestCase testCase, int buildNumber) {
        // return testTriageRepository.findAllByBuild_ExecutorAndTestExecution_TestCase(executor, testCase);
        List<TestTriage> triages = testTriageRepository.findTopByExecutorAndTriagedAndExpiredAndEnabledAndTestCaseAndBuildNumberLessThanOrderByIdDesc(executor, true, false, true, testCase, buildNumber);
        if (triages.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(triages.get(0));
        }
    }

    public List<TestTriage> findPreviousTriages(Executor executor, int buildNumber) {
        List<TestTriage> triages = testTriageRepository.findAllByExecutorAndTriagedAndExpiredAndEnabledAndBuildNumberLessThanOrderByIdDesc(executor, true, false, true, buildNumber);

        return triages;
    }

    // It will answer the previous triage only if it not too old
    public TestTriage findValidPreviousTriage(TestTriage testTriage) {
        long from = dayFromValidInformation();

        TestTriage previousTriage = findPreviousTriage(testTriage);

        if (previousTriage == null || previousTriage.getExecutionDate() < from) {
            return null;
        } else {
            return previousTriage;
        }
    }

    // It will answer the previous triage with a note only if it not too old
    public TestTriage findValidPreviousTriageWithNote(TestTriage testTriage) {
        long from = dayFromValidInformation();

        List<TestTriage> previousTriages = testTriageRepository.findAllByExecutorAndTestCaseAndEnabledAndTriagedAndExecutionDateGreaterThanOrderByIdDesc(testTriage.getExecutor(), testTriage.getTestCase(), true, true, from);

        for (TestTriage triage : previousTriages) {
            if (triage.hasNote()) {
                return triage;
            }
        }

        return null;
    }

    public List<TestTriage> findAllByBuild(Build build) {
        return testTriageRepository.findAllByBuild(build, Sort.by(Sort.Direction.DESC, "id"));
    }

    public List<TestTriage> findAllPendingByBuild(Build build) {
        return testTriageRepository.findAllByBuildAndTriagedAndExpiredAndEnabled(build, false, false, true);
    }

    public long countPendingToTriage(BuildTriage build) {
        return testTriageRepository.countByBuildTriageAndTriagedAndExpiredAndEnabled(build, false, false, true);
    }

    public long countPendingToTriage() {
        StateType[] allowedStates = { StateType.FAIL, StateType.NEWFAIL };
        return findAllOngoingTests(allowedStates).size();
    }

    public User getBestTriager(long fromDate, long toDate) {
        List<User> triagers = testTriageRepository.findMoreTriagedUsers(fromDate, toDate);
        if (triagers.isEmpty()) {
            return null;
        } else {
            return triagers.get(0);
        }
    }

    public List<TestTriage> find(BuildTriage build) {
        return testTriageRepository.findAllByBuildTriage(build);
    }

    public List<TestTriage> findAllByBuildAndState(Build build, StateType state) {
        return testTriageRepository.findAllByBuildAndCurrentState(build, Sort.by(Sort.Direction.DESC, "rank"), state);
    }

    public List<TestTriage> findAllByBuildAndStateNot(Build build, StateType state) {
        return testTriageRepository.findAllByBuildAndCurrentStateNot(build, Sort.by(Sort.Direction.DESC, "rank"), state);
    }

    public long countTestWith(TestCase testCase, StateType[] states) {
        return testTriageRepository.countByTestCaseAndCurrentStateInAndEnabled(testCase, states, true);
    }

    public long count(TestCase testCase) {
        return testTriageRepository.countByTestCaseAndEnabled(testCase, true);
    }

    public TestTriage lastTestWithoutStates(TestTriage test, StateType[] states) {
        return testTriageRepository.findFirstByExecutorAndTestCaseAndCurrentStateNotInOrderByBuildNumberDesc(test.getExecutor(), test.getTestCase(), states);
    }

    public int consecutiveTestsWithoutStates(TestTriage test, StateType[] states) {
        int answer = 0;
        TestTriage passTriage = lastTestWithoutStates(test, states);
        if (passTriage == null) {
            answer = 0;
        } else {
            answer = test.getBuildNumber() - passTriage.getBuildNumber();
        }
        return answer;
    }

    public long countTestsBetweenBuilds(TestTriage testTriage, int startBuild, int endBuild) {
        return testTriageRepository.countByExecutorAndBuildNumberGreaterThanAndBuildNumberLessThan(testTriage.getExecutor(), startBuild, endBuild);
    }

    public void triageAttempt(TestTriage testTriage, boolean isNowTriaged, boolean wasTriaged) {
        if (isNowTriaged && !wasTriaged) {
            testIsTriaged(testTriage);
        }
    }


    // The test is now triaged, lets start the other flows.
    public void testIsTriaged(TestTriage test) {
        notifyTestTriaged(test);
        triageBuildIfProper(test);
        //  updateAutomationIssue(test);
        updateIssueTicket(test);
        buildTriageService.testWasTriaged(test);
    }

    // The test is going to be updated in the DB
    public void preUpdate(TestTriage newTest) {
        // The user has accepted new status, let's add them in the DB
        newTest.addTag(TagHelper.TRIAGE_UPDATED);
    }

    private void updateIssueTicket(TestTriage test) {
        // TODO
    }

    /*public long testCountAssignedTo(User user){
        return testTriageRepository.countByTriager(user);
    }

    public long testCountAssignedTo(Product product){
        return testTriageRepository.countByProduct(product);
    }


    public long testPendingToTriage(Product product) {
        return testTriageRepository.countByProductAndTriaged(product, false);
    }*/

    // The test is now triaged, lets verify this build if automatically triaged.
    private void triageBuildIfProper(TestTriage test) {
        long notTriaged = testTriageRepository.countByBuildAndTriagedAndExpiredAndEnabledAndIdNot(test.getBuild(), false, false, true, test.getId());

        if (notTriaged == 0) {
            BuildTriage buildTriage = test.getBuildTriage();
            buildTriageService.markBuildAsTriaged(test.getTriager(), buildTriage, null);
        }
    }

    private void notifyTestTriaged(TestTriage test) {
        ApplicationEvent event = applicationEventBuilder.newEvent();
        event.setType(ApplicationEventType.TEST_TRIAGED);
        event.setExtraParameter(String.valueOf(test.getId()));
        applicationEventBuilder.saveUnique(event, true);
    }

    public TestTriage markTestAsTriaged(User user, Long testId) {
        TestTriage testTriage = find(testId);
        testTriage.setTriager(user);
        testTriage.setTriaged();
        return update(testTriage);
    }

    public TestTriage setAssigneeToTest(Long userId, Long testId) {
        User user = userService.find(userId);
        return setAssigneeToTest(user, testId);
    }

    public TestTriage setAssigneeToTest(User user, Long testId) {
        TestTriage testTriage = find(testId);
        testTriage.setTriager(user);
        return update(testTriage);
    }

    public List<TestTriage> findLastSameTests(TestTriage test) {
        return testTriageRepository.findAllSameTriage(test.getTestCase());
    }

    public List<TestTriage> findAllSameError(TestTriage testTriage) {
        TestExecution testExecution = testTriage.getTestExecution();
        String errorDetail = testExecution.getErrorDetails();
        if (errorDetail == null) {
            errorDetail = "";
        }
        String errorNoNumber = errorDetail;
        if (!errorDetail.isEmpty()) {
            errorNoNumber = testExecution.getErrorDetails().replaceAll("\\d", "");
        }

        if (StringUtils.isEmpty(testExecution.getErrorStackTrace())) {
            return testTriageRepository.findAllSameError(testExecution.getErrorDetails(), errorNoNumber);
        }

        return testTriageRepository.findAllSameError(testExecution.getErrorDetails(), testExecution.getErrorStackTrace(), errorNoNumber);
    }


    // The amount of days we estimate an information may be very important for the user
    private long dayFromValidInformation() {
        return DateUtils.beginDay(-1 * propertyService.valueOf(MAX_TESTCASES_PER_DAY, DEFAULT_MAX_TESTCASES_PER_DAY));
    }

    public long countByStateAndTriaged(Build build, StateType state, boolean triaged) {
        return testTriageRepository.countByBuildAndCurrentStateAndTriaged(build, state, triaged);
    }

    public long countByStateAndTriaged(List<TestTriage> triages, StateType state, boolean triaged) {
        long count = triages.stream().filter(testTriage -> testTriage.isTriaged() == triaged && state.equals(testTriage.getCurrentState()))
                .count();
        return count;
    }

    public long countByStateNotAndTriaged(List<TestTriage> triages, StateType state, boolean triaged) {
        long count = triages.stream().filter(testTriage -> testTriage.isTriaged() == triaged && !state.equals(testTriage.getCurrentState()))
                .count();
        return count;
    }

    public long countByStateNotAndTag(List<TestTriage> triages, StateType state, String tag) {
        long count = triages.stream().filter(testTriage -> StringUtils.contains(testTriage.getTags(), tag) && !state.equals(testTriage.getCurrentState()))
                .count();
        return count;
    }

    public long countBy(Build build) {
        return testTriageRepository.countByBuild(build);
    }

    public long countBy(Build build, boolean triaged) {
        return testTriageRepository.countByBuildAndTriaged(build, triaged);
    }

    public long countByStateNotAndTriaged(Build build, StateType state, boolean triaged) {
        return testTriageRepository.countByBuildAndCurrentStateNotAndTriaged(build, state, triaged);
    }

    public long countByStateNotAndTag(Build build, StateType state, String tag) {
        return testTriageRepository.countByBuildAndCurrentStateNotAndTagsContains(build, state, tag);
    }

    public TestTriage switchPin(TestTriage testTriage, User user) {
        TestPin pin = testTriage.getPin();
        TestCase testCase = testTriage.getTestCase();
        if (pin == null) {
            pin = TestPin.builder()
                    .author(user)
                    .reason(null)
                    .createDate(DateUtils.now())
                    .build();

            pin = testPinService.save(pin);
            testCase.setPin(pin);
            testCaseService.update(testCase);
        } else {
            testCase.setPin(null);
            testCaseService.update(testCase);
            testPinService.delete(pin.getId());
        }

        return testTriage;
    }

    public List<Long> getPreviousTriageIds(TestTriage testTriage) {
        // List<TestTriage> previousTriage = findAll(testTriage.getExecutor(), testTriage.getTestCase());
        List<Long> previousTriage = testTriageRepository.findAllIdsByExecutorAndTestCaseOrderByIdDesc(testTriage.getExecutor(), testTriage.getTestCase());
        previousTriage.remove(testTriage.getId());
        try {
            return previousTriage
                    .stream()
                    .sorted(Comparator.naturalOrder())
                    .collect(Collectors.toList());
        } catch (NullPointerException e) {
            return Lists.newArrayList();
        }
    }

    public TestTriage getLastTriage(TestTriage testTriage) {
        List<TestTriage> previousTriages = testTriageRepository.findFirstByExecutorAndTestCaseAndEnabledAndExpiredOrderByIdDesc(testTriage.getExecutor(), testTriage.getTestCase(), true, false);
        if (previousTriages.isEmpty()) {
            return null;
        } else {
            return previousTriages.get(0);
        }
    }

    public List<TestTriage> findAllOngoingTests() {
        return testTriageRepository.findAllOngoingTests(StateType.PASS);
    }

    public List<TestTriage> findAllOngoingTests(StateType[] states) {
        return testTriageRepository.findAllOngoingTests(states);
    }

    public List<TestTriage> findAllOngoingTests(TestCase testCase) {
        return testTriageRepository.findAllOngoingTests(testCase);
    }

    public List<TestTriage> findAllOngoingTests(List<TestCase> testCases) {
        if (testCases == null || testCases.isEmpty()) {
            return new ArrayList<>();
        }
        return testTriageRepository.findAllOngoingTestCases(testCases);
    }

    public void activateTests(BuildTriage buildTriage) {
        List<TestTriage> testTriages = findAllByBuildAndStateNot(buildTriage.getBuild(), StateType.PASS);

        for (TestTriage testTriage : testTriages) {
            if (testTriage.isFailed()) {
                testTriage.activate();
                update(testTriage);
            }
        }
    }

    public List<TestTriageHistoryDTO> getTestHistory(TestTriage test) {
        List<TestTriageHistoryDTO> list = testTriageRepository.getTestHistory(test.getExecutor(), test.getTestCase(), PageRequest.of(0, UI_GRAPH_BUILD_SIZE));
        Collections.reverse(list);
        list.forEach(TestTriageHistoryDTO::initialize);
        return list;
    }

    public boolean areOtherTestWithAutomationError(TestTriage test) {
        // List<Long> tests = testTriageRepository.findLatestSameTestWithStatusfindLatestSameTestWithStatus(test.getTestCase(), dayFromValidInformation(), TestTriage.failStates(), TestFailType.WONT_FILE, test.getExecutor());
        List<Long> testIds = testTriageRepository.findSameOngoingTests(test.getTestCase(), dayFromValidInformation(), test.getExecutor());
        List<TestTriage> tests = testTriageRepository.findAllByIdIn(testIds);

        // removing itself from the list
        if (tests.indexOf(test) > 0) {
            return tests.size() > 1;
        }

        boolean allPass = tests.stream().allMatch(t -> (t.isPassed() || t.isTestWontFix()));

        return !allPass;
    }

    public List<KeyValuePair> findTestCasesGroupedByUser(Product product) {
        List<Object[]> list = testTriageRepository.findTestCasesGroupedByUser(product);
        return StringUtils.getKeyValuePairList(list);
    }

    public List<KeyValuePair> findManualTriagesGroupedByUser(Product product) {
        List<Object[]> list = testTriageRepository.findManualTriagesGroupedByUser(product);
        return StringUtils.getKeyValuePairList(list);
    }

    public List<KeyValuePair> findAutoTriagesGroupedByUser(Product product) {
        List<Object[]> list = testTriageRepository.findAutoTriagesGroupedByUser(product);
        return StringUtils.getKeyValuePairList(list);
    }

    public boolean isFlaky(TestTriage testTriage) {
        List<StateType> states = testTriageRepository.findAllCurrentStateByExecutorAndTestCaseAndBuildNumberGreaterThanOrderByIdAsc(testTriage.getExecutor(), testTriage.getTestCase(), testTriage.getBuildNumber() - DEFAULT_MAX_TESTCASES_TO_PROCESS);

        int switches = 0;
        StateType previous = null;
        for (StateType state : states) {
            if (previous == null) {
                previous = state;
            }
            if (previous.isPass() != state.isPass()) {
                switches = switches + 1;
            }
            previous = state;
        }
        return switches >= DEFAULT_MAX_FLAKY_CHANGES;
    }

    public void deleteOld(long timestamp) {

        log.log(Level.INFO, String.format("TestTriage: Remaining tests %d", testTriageRepository.count()));
        log.log(Level.INFO, String.format("TestExecution: Remaining tests %d", testExecutionService.count()));

        callCleanupProcedure(timestamp);

        log.log(Level.INFO, String.format("TestTriage: %d", testTriageRepository.count()));
        log.log(Level.INFO, String.format("TestExecution: %d", testExecutionService.count()));
    }

    public void callCleanupProcedure(long timestamp){
        testTriageRepository.cleanup(timestamp);
    }

    public void deleteOldOneByOne(long timestamp) {
        int i = 0;
        log.log(Level.INFO, String.format("TestTriage: Remaining tests %d", testTriageRepository.count()));
        log.log(Level.INFO, String.format("TestExecution: Remaining tests %d", testExecutionService.count()));
        int iterations = 20;
        long lastProcessed = timestamp;
        for (int j = 0; j < iterations; j++) {
            List<TestTriage> tests = testTriageRepository.findFirst500ByTimestampLessThanOrderByIdAsc(timestamp);
            if (tests.size() > 2) {
                log.log(Level.INFO, String.format("TestTriage: %d - Start to delete %d tests. First: %s Last: %s", iterations, tests.size(), DateUtils.covertToString(tests.get(0).getTimestamp(), null), DateUtils.covertToString(tests.get(tests.size() -1).getTimestamp(), null)));
            }
            int totalTests = tests.size();

            for (TestTriage test : tests) {
                try {
                    delete(test);
                } catch (PersistenceException ex) {
                    log.log(Level.SEVERE, "Could not delete test: " + test.getId(), ex);
                }
                if (i % 10 == 0) {
                    log.log(Level.INFO, String.format("TestTriage: Deleting %d / %d", i, totalTests));
                }
                i = i + 1;
                lastProcessed = test.getTimestamp();
                if (i % 100 == 0) {
                    try {
                        Thread.sleep(10l);
                    } catch (InterruptedException e) {
                    }
                }
            }
            log.log(Level.INFO, String.format("TestTriage: Finish deleting %d tests", i));
        }
        
        long deletedTestExecutions = testExecutionService.deleteOldExecutions(lastProcessed);
        log.log(Level.INFO, String.format("TestExecution: Finish deleting %d tests", deletedTestExecutions));
        log.log(Level.INFO, String.format("TestTriage: Remaining tests %d", testTriageRepository.count()));
        log.log(Level.INFO, String.format("TestExecution: Remaining tests %d", testExecutionService.count()));
    }

    public void delete(TestTriage testTriage) {
        List<AutomatedTestIssue> automatedTestIssues = automatedTestIssueService.findAll(testTriage);
        if (!automatedTestIssues.isEmpty()) {
            return;
        }
        errorDetailService.deleteBy(testTriage);
        testTriageRepository.clearPreviousTriage(testTriage);
        List<TestTriage> triages = testTriageRepository.findAllByPreviousTriage(testTriage);
        for (TestTriage reference : triages) {
            reference.setPreviousTriage(null);
            update(reference);
            if (testTriage.getAutomationIssue() != null && testTriage.getAutomationIssue().getTestTriage() == testTriage) {
                testTriage.getAutomationIssue().setTestTriage(reference);
                automatedTestIssueService.update(testTriage.getAutomationIssue());
            }
        }
        testTriageRepository.deleteTestTriageById(testTriage.getId());
        testExecutionService.delete(testTriage.getTestExecution().getId());
    }
    public long countByEnabledToday() {
        long today = DateUtils.beginDay(0);

        return testTriageRepository.countByEnabledAndTimestampGreaterThanEqual(true, today);
    }

}
