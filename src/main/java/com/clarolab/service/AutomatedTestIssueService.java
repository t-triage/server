/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.aaa.util.AuthContextHelper;
import com.clarolab.event.process.ApplicationEventBuilder;
import com.clarolab.event.process.ApplicationEventType;
import com.clarolab.jira.service.JiraAutomationService;
import com.clarolab.jira.service.JiraConfigService;
import com.clarolab.jira.service.JiraObjectService;
import com.clarolab.model.*;
import com.clarolab.model.helper.NewsBoardHelper;
import com.clarolab.model.types.IssueType;
import com.clarolab.model.types.TestFailType;
import com.clarolab.repository.AutomatedTestIssueRepository;
import com.clarolab.repository.BaseRepository;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import com.clarolab.view.KeyValuePair;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.clarolab.util.Constants.*;
import static com.clarolab.util.StringUtils.containsIgnoreCase;

@Service
@Log
public class AutomatedTestIssueService extends BaseService<AutomatedTestIssue> {

    @Autowired
    private JiraAutomationService jiraAutomationService;

    @Autowired
    private JiraConfigService jiraConfigService;

    @Autowired
    private JiraObjectService jiraObjectService;

    @Autowired
    private AutomatedTestIssueRepository automatedTestIssueRepository;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private UserService userService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private AuthContextHelper authContextHelper;

    @Autowired
    private ApplicationEventBuilder applicationEventBuilder;

    @Override
    protected BaseRepository<AutomatedTestIssue> getRepository() {
        return automatedTestIssueRepository;
    }

    public AutomatedTestIssue get(TestTriage testTriage) {
        if (testTriage == null) {
            return null;
        }
        TestCase test = testTriage.getTestCase();
        return getAutomatedTestIssue(test);
    }

    public List<AutomatedTestIssue> findAll(TestTriage testTriage) {
        return automatedTestIssueRepository.findByTestTriage(testTriage);
    }

    public AutomatedTestIssue getAutomatedTestIssue(TestCase testCase) {
        return automatedTestIssueRepository.findByTestCaseAndEnabled(testCase, true);
    }

    public boolean testTriageCreated(TestTriage testTriage) {
        boolean dbUpdate = false;
        // Checks if there is an automatic issue ticket filed previously
        // AutomatedTestIssue issue = automatedTestIssueService.get(testTriage);
        AutomatedTestIssue issue = testTriage.getTestCase().getAutomatedTestIssue();
        if (issue == null && testTriage.hasTestBug() && testTriage.isFailed()) {
            issue = get(testTriage);
        }
        if (issue != null) {
            updateAutomationIssue(testTriage, issue);
            dbUpdate = true;
        }

        return dbUpdate;
    }

    public void updateAutomationIssue(TestTriage test, AutomatedTestIssue automatedTestIssue) {

        boolean dbTriageUpdate = false;
        boolean dbTestCaseUpdate = false;

        if (automatedTestIssue == null) {
            return;
        }
        TestCase testCase = test.getTestCase();

        boolean areOtherPass = true;
        if (testCase.getAutomatedTestIssue() != null) {
            areOtherPass = !testTriageService.areOtherTestWithAutomationError(test);
        }

        if (testCase.getAutomatedTestIssue() == null) {
            // join again both
            testCase.setAutomatedTestIssue(automatedTestIssue);
            try {
                notifyEvent(null, NewsBoardHelper.newAutomationEvent(automatedTestIssue.getTestCase(), automatedTestIssue.getTriager()));
            } catch (Exception ex) {
                log.log(Level.SEVERE, "Couldn't generate board", ex);
            }
            dbTestCaseUpdate = true;
        }

        automatedTestIssue.addTrend(test);

        if (test.isTestWontFix() && areOtherPass) {
            // User choose not fix the ticket
            automatedTestIssue.setIssueType(IssueType.WONT_FIX);
        }

        // Conditions for test passing
        if (test.isPassed()) {

            if (automatedTestIssue.getCalculatedPriority() >= PASSING_PERMANENT_PRIORITY) {
                automatedTestIssue.setCalculatedPriority(Math.max(0, automatedTestIssue.getCalculatedPriority() - PASSING_PERMANENT_PRIORITY));
            }

            if (areOtherPass && test.isPassed() && (automatedTestIssue.isReOpen() || automatedTestIssue.isOpen())) {
                 automatedTestIssue.setIssueType(IssueType.PASSING);
                 automatedTestIssue.setConsecutivePasses(0);

                 notifyEvent(automatedTestIssue, NewsBoardHelper.passingEvent(automatedTestIssue.getTestCase(), automatedTestIssue.getTriager()));
            }
            automatedTestIssue.setConsecutivePasses(automatedTestIssue.getConsecutivePasses() + 1);

            if (areOtherPass && automatedTestIssue.getConsecutivePasses() >= PASSING_TO_FIX_AMOUNT && automatedTestIssue.isPassing()) {
                automatedTestIssue.setIssueType(IssueType.FIXED);
                test.setTestFailType(TestFailType.UNDEFINED);
                dbTriageUpdate = true;

                notifyEvent(automatedTestIssue, NewsBoardHelper.fixedEvent(automatedTestIssue.getTestCase(), automatedTestIssue.getTriager()));
            }

            if (areOtherPass && automatedTestIssue.getConsecutivePasses() >= getSolidLimit() && automatedTestIssue.isFixed()) {
                // After 30 passes, we consider this is working fine
                // automatedTestIssue.setUserFixPriorityType(UserFixPriorityType.AUTOMATIC);
                automatedTestIssue.setCalculatedPriority(0);
                automatedTestIssue.setIssueType(IssueType.FIXED);
                if (automatedTestIssue.getTestTriage() != null && automatedTestIssue.getTestTriage().getId().equals(test.getId())) {
                    automatedTestIssue.setTestTriage(null);
                }
                dbTestCaseUpdate = true;
            }
        }

        if (test.isFailed()) {
            automatedTestIssue.setConsecutivePasses(0);
            if (automatedTestIssue.getTestTriage() == null) {
                automatedTestIssue.setTestTriage(test);
            }
        }

        // if the ticket is in to fix state
        if (test.hasTestBug() || test.isTestUndefined()) {
            if (automatedTestIssue.getId() == null) {
                // New issue
                automatedTestIssue.setCalculatedPriority(INITIAL_PRIORITY);

                if (automatedTestIssue.isBlocker()) {
                    automatedTestIssue.setCalculatedPriority(automatedTestIssue.getCalculatedPriority() + BLOCKER_PRIORITY);
                }

                if (automatedTestIssue.isHigh()) {
                    automatedTestIssue.setCalculatedPriority(automatedTestIssue.getCalculatedPriority() + HIGH_PRIORITY);
                }

                if (automatedTestIssue.isMedium()) {
                    automatedTestIssue.setCalculatedPriority(automatedTestIssue.getCalculatedPriority() + MEDIUM_PRIORITY);
                }
                automatedTestIssue.setFailTimes(automatedTestIssue.getFailTimes() + 1);
            } else {
                // The issue was filed in a previous build
                if (test.isFailed() || test.isPermanent()) {
                    if (test.isFailed()) {
                        automatedTestIssue.setCalculatedPriority(automatedTestIssue.getCalculatedPriority() + FAIL_AGAIN_PRIORITY);
                    }
                    if (test.isPermanent()) {
                        automatedTestIssue.setCalculatedPriority(automatedTestIssue.getCalculatedPriority() + PASSING_PERMANENT_PRIORITY);
                    }
                    // rule for if the ticket is closed and fail again so the ticket is updated to REOPEN state and incrementing the priority +1
                    if (automatedTestIssue.isFixed()) {
                        automatedTestIssue.setIssueType(IssueType.REOPEN);
                        automatedTestIssue.setReopenTimes(automatedTestIssue.getReopenTimes() + 1);
                        if (automatedTestIssue.getTestTriage() == null) {
                            automatedTestIssue.setTestTriage(test);
                        }
                        test.setTestFailType(TestFailType.TEST_ASSIGNED_TO_FIX);
                        dbTriageUpdate = true;
                    }

                    if (automatedTestIssue.isPassing()) {
                        // The test was set as passed but it failed again too soon
                        automatedTestIssue.setIssueType(IssueType.OPEN);
                    }
                    automatedTestIssue.setFailTimes(automatedTestIssue.getFailTimes() + 1);

                }
            }
        }

        // Update references
        if (dbTriageUpdate) {
            testTriageService.update(test);
        }
        if (dbTestCaseUpdate) {
            testCaseService.update(testCase);
        }

        update(automatedTestIssue);

        jiraAutomationService.checkJiraConfig(automatedTestIssue);


 //       jiraConfig = jiraConfigService.findByProduct(productID);
 //       if (jiraConfig != null){
 //           String response = jiraObjectService.createJiraIssue(jiraConfig,"Automation issue", "ERROR creado desde automation");
 //           log.log(Level.INFO,"Jira Ticket created, id: "+ response);
 //           automatedTestIssue.setRelatedIssueID(response);
 //           update(automatedTestIssue);
 //       }


    }

    private void notifyEvent(AutomatedTestIssue test, String text) {
        ApplicationEvent event = applicationEventBuilder.newEvent();
        event.setType(ApplicationEventType.AUTOMATION_TEST_CHANGED);
        event.setProcessingMessage(text);
        event.setSource(test);
        applicationEventBuilder.saveUnique(event, false);
    }

    public AutomatedTestIssue setAssigneeToAutomationIssue(Long userId, Long issueId) {
        User user = userService.find(userId);
        return setAssigneeToAutomationIssue(user, issueId);
    }

    public AutomatedTestIssue setAssigneeToAutomationIssue(User user, Long issueId) {
        AutomatedTestIssue automatedTestIssue = find(issueId);
        automatedTestIssue.setTriager(user);
        return update(automatedTestIssue);
    }

    public Long countAllButFixed(Container container) {
        if (container == null) {
            return automatedTestIssueRepository.countAllButFixed();
        }
        return automatedTestIssueRepository.countAllButFixed(container);
    }

    public Long countAllButFixedAfter(Container container, long date) {
        return automatedTestIssueRepository.countAllByIssueTypeInAndTestTriageContainerAndEnabledAndTimestampGreaterThanEqual(IssueType.getFixed(), container, true, date);
    }

    public List<AutomatedTestIssue> findAllButFixed(@Nullable Specification spec, @Nullable Sort sort, String executorName, boolean assignee, boolean pin, boolean passingIssues, boolean hideOld) {
        Stream<AutomatedTestIssue> stream = automatedTestIssueRepository.findAllButFixed(spec, sort).stream();

        stream = stream.filter(AutomatedTestIssue::isHierarchicalyEnabled);
        Long startDate = DateUtils.beginDay(-15);

        //Olds are not hidden by default. if hideOld = true --> are shown. If hideOld = false --> olds are hidden.
        if (!hideOld)
            stream = stream.filter(automatedTestIssue -> automatedTestIssue.getTestTriage().getExecutionDate() >= startDate);
        // The passing issues are not shown by default
        if (!passingIssues)
            stream = stream.filter(automatedTestIssue -> automatedTestIssue.isOpen() || automatedTestIssue.isReOpen());

        if (!StringUtils.isEmpty(executorName))
            stream = stream.filter(automatedTestIssue -> automatedTestIssue.relatedWithExecutor(executorName));

        if (assignee)
            stream = stream.filter(automatedTestIssue -> authContextHelper.getCurrentUser().equals(automatedTestIssue.getTriager()));

        if (pin)
            stream = stream.filter(automatedTestIssue -> automatedTestIssue.getTestTriage().getTestExecution().isPin());

        if(sort==null || sort.isUnsorted() || containsIgnoreCase(sort.toString(),"executorName"))
            stream = stream.sorted(Comparator.comparing(AutomatedTestIssue::getExecutorName, String.CASE_INSENSITIVE_ORDER));

        return stream.collect(Collectors.toList());
    }

    private int getSolidLimit() {
        // return propertyService.valueOf(CONSECUTIVE_PASS_COUNT, DEFAULT_CONSECUTIVE_PASS_COUNT); it can lead to
        
        return DEFAULT_CONSECUTIVE_PASS_COUNT;
    }

    public void updateTriageError(AutomatedTestIssue automatedTestIssue, TestTriage testTriage) {
        if (testTriage == null) {
            return;
        }

        if (testTriage.hasTestBug()) {
            automatedTestIssue.setTestTriage(testTriage);
        }
    }

    public Long countAllButFixed(User user) {
        return automatedTestIssueRepository.countAllButFixed(user);
    }

    public Long countAllButFixed(User user, Long prev, Long now) {
        return automatedTestIssueRepository.countAllButFixedAndTimestamp(user, prev, now);
    }    

    public Long countAllFixed(User user) {
        return automatedTestIssueRepository.countAllFixed(user);
    }

    public List<KeyValuePair> findAutomationIssuesGroupedByUser(Product product) {
        List<Object[]> list = automatedTestIssueRepository.findAutomationIssuesGroupedByUser(product);
        return StringUtils.getKeyValuePairList(list);
    }

    public List<KeyValuePair> findAutomationFixedGroupedByUser(Product product) {
        List<Object[]> list = automatedTestIssueRepository.findAutomationFixedGroupedByUser(product);
        return StringUtils.getKeyValuePairList(list);
    }

    public List<KeyValuePair> findAutomationIssuesGroupedByUser() {
        List<Object[]> list = automatedTestIssueRepository.findAutomationIssuesGroupedByUser();
        return StringUtils.getKeyValuePairList(list);
    }

    public List<KeyValuePair> findAutomationFixedGroupedByUser() {
        List<Object[]> list = automatedTestIssueRepository.findAutomationFixedGroupedByUser();
        return StringUtils.getKeyValuePairList(list);
    }

    public List<AutomatedTestIssue> getAutomationIssues(Product product) {
        return automatedTestIssueRepository.findAllByProduct(product);
    }

    public List<AutomatedTestIssue> getAutomationIssues(Product product, Long startDate,  Long endDate) {
        return automatedTestIssueRepository.findAllByProductAndTimestamp(product, startDate, endDate);
    }
    
    public List<AutomatedTestIssue> getAutomationIssues(User user, Long startDate,  Long endDate) {
        return automatedTestIssueRepository.findAllByUserAndTimestamp(user, startDate, endDate);
    }
    
    public List<User> getAllAssignedUsers() {
        return automatedTestIssueRepository.findAllUsers();
    }
}
