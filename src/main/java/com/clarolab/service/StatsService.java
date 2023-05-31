/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;


import com.clarolab.aaa.util.AuthContextHelper;
import com.clarolab.dto.LogCommitsPerDayDTO;
import com.clarolab.dto.LogCommitsPerPersonDTO;
import com.clarolab.dto.chart.ChartSerieDTO;
import com.clarolab.event.analytics.ProductStatService;
import com.clarolab.model.*;
import com.clarolab.model.component.AutomatedComponent;
import com.clarolab.model.helper.tag.TagHelper;
import com.clarolab.model.types.IssueType;
import com.clarolab.model.types.StateType;
import com.clarolab.service.filter.FilterSpecificationsBuilder;
import com.clarolab.serviceDTO.LogServiceDTO;
import com.clarolab.util.DateUtils;
import com.clarolab.view.GroupedStatView;
import com.clarolab.view.KeyValuePair;
import com.google.common.collect.Lists;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.clarolab.model.types.StateType.NEWFAIL;
import static com.clarolab.util.Constants.*;
import static com.clarolab.util.LogicalCondition.not;

@Service
@Log
public class StatsService {

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private BuildTriageService buildTriageService;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private TestExecutionService testExecutionService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private AutomatedTestIssueService automatedTestIssueService;

    @Autowired
    private IssueTicketService issueTicketService;

    @Autowired
    private ErrorDetailService errorDetailService;

    @Autowired
    private ProductService productService;

    @Autowired
    private AuthContextHelper authContextHelper;

    @Autowired
    private ProductStatService productStatService;

    @Autowired
    private TriageSpecService triageSpecService;

    @Autowired
    private LogServiceDTO logServiceDTO;

    @Autowired
    private LogService logService;

    @Autowired
    private AutomatedComponentService automatedComponentService;

    public List<GroupedStatView> getComponentBasedTestTriages(){
        List<GroupedStatView> answer = Lists.newArrayList();
        for (AutomatedComponent component : automatedComponentService.findAll()) {
            GroupedStatView view;
            if (component.isEnabled()) {
                view = getTriageStats(component);
                if (view.getTotal() > 0)
                    answer.add(view);
            }
        }
        return answer;
    }

    public GroupedStatView getTriageStats(AutomatedComponent component) {
        long passed = 0;
        long fails = 0;
        long flaky = 0;

        for (TestCase testCase : testCaseService.findAllByComponent(component)){
            TestTriage testTriage = testTriageService.findLastByTestCase(testCase);
            if (testTriage != null) {
                if (testTriage.containTag("FLAKY")) {
                    flaky += 1;
                } else {
                    if (testTriage.getCurrentState() == StateType.FAIL) {
                        fails += 1;
                    }
                    if (testTriage.getCurrentState() == StateType.PASS) {
                        passed += 1;
                    }
                }
            }
        }

        return GroupedStatView.builder()
                .name(component.getName())
                .timestamp(component.getTimestamp())
                .fails(fails)
                .passed(passed)
                .flaky(flaky)
                .total(fails + passed + flaky)
                .build();
    }

    public double getTriagedFails() {
        FilterSpecificationsBuilder filterFail = filter()
                .with("triaged", ":", true)
                .with("currentState", ":", StateType.FAIL);
        double fails = testTriageService.count(filterFail.build());

        FilterSpecificationsBuilder filterNewFail = filter()
                .with("triaged", ":", true)
                .with("currentState", ":", NEWFAIL);
        double newFails = testTriageService.count(filterNewFail.build());

        return newFails + fails;
    }

    //Unique Tests
    public double getUniqueTests() {
        return testCaseService.getUniqueTestsCount();
    }

    //Saved Time
    public double getTotalSavedTime() {
        Integer min = propertyService.valueOf(SAVED_TIME_MIN, DEFAULT_SAVED_TIME_MIN);
        Integer period = propertyService.valueOf(SAVED_TIME_PERIOD, DEFAULT_SAVED_TIME_PERIOD);

        FilterSpecificationsBuilder filter = filter()
                .with("triaged", ":", true)
                .with("tags", ":", TagHelper.AUTO_TRIAGED)
                .with("currentState", "!=", StateType.PASS);
        return (testTriageService.count(filter.build()) * min) / period;
    }

    //Triaged Fails
    public double getTotalTriagedFails() {
        FilterSpecificationsBuilder filterFail = filter()
                .with("triaged", ":", true)
                .with("currentState", ":", StateType.FAIL);
        double fails = testTriageService.count(filterFail.build());

        FilterSpecificationsBuilder filterNewFail = filter()
                .with("triaged", ":", true)
                .with("currentState", ":", NEWFAIL);
        double newFails = testTriageService.count(filterNewFail.build());

        return newFails + fails;
    }

    //Automation fixes
    public double getTotalAutomationFixes() {
        return automatedTestIssueService.count(getFixedFilter());
    }

    //Achieved Deadlines
    public double getDeadlinesCompleted() {
        //select * from qa_build_triage where enabled = true and expired = false and triaged = true
        return buildTriageService.countTriagesCompleted();
    }

    //Product Summary
    public List<GroupedStatView> getProductSummary() {
        List<GroupedStatView> answer = Lists.newArrayList();
        GroupedStatView view;
        for (Product product : productService.findAll()) {
            if (product.isEnabled()) {
                view = getProductStat(product);
                if (view.getTotal() > 0)
                    answer.add(view);
            }
        }
        return answer;
    }

    public GroupedStatView getProductStat(Product product) {
        long newFail = 0;
        long fails = 0;
        long permanent = 0;
        long nowPass = 0;
        long triaged = 0;

        for (Container container : product.getContainers()) {
            GroupedStatView stat = getPendingToTriage(container);
            newFail += stat.getNewFails();
            fails += stat.getFails();
            permanent += stat.getPermanent();
            nowPass += stat.getNowPassing();
            triaged += stat.getTriaged();
        }

        long toTriage = fails + newFail + nowPass + permanent;
        long total = fails + newFail + nowPass + triaged;

        return GroupedStatView.builder()
                .name(product.getName())
                .productName(product.getName())
                .timestamp(product.getTimestamp())
                .fails(fails + permanent)
                .newFails(newFail)
                .nowPassing(nowPass)
                .triaged(triaged)
                .toTriage(toTriage)
                .permanent(permanent)
                //.total(toTriage + pass + permanent + triaged)
                .total(total)
                .build();
    }

    public GroupedStatView getProductStat(Product product, Long prev, Long now) {
        long newFail = 0;
        long fails = 0;
        long permanent = 0;
        long nowPass = 0;
        long triaged = 0;

        for (Container container : product.getContainers()) {
            GroupedStatView stat = getPendingToTriage(container, prev, now);
            newFail += stat.getNewFails();
            fails += stat.getFails();
            permanent += stat.getPermanent();
            nowPass += stat.getNowPassing();
            triaged += stat.getTriaged();
        }

        long toTriage = fails + newFail + nowPass + permanent;
        long total = fails + newFail + nowPass + triaged;

        return GroupedStatView.builder()
                .name(product.getName())
                .productName(product.getName())
                .timestamp(product.getTimestamp())
                .fails(fails + permanent)
                .newFails(newFail)
                .nowPassing(nowPass)
                .triaged(triaged)
                .toTriage(toTriage)
                .permanent(permanent)
                //.total(toTriage + pass + permanent + triaged)
                .total(total)
                .build();
    }

    public List<KeyValuePair> getAllAutomationIssuesPendingByUsers() {
        return automatedTestIssueService.findAutomationIssuesGroupedByUser();
    }

    public List<KeyValuePair> getAllAutomationIssuesFixedByUsers() {
        return automatedTestIssueService.findAutomationFixedGroupedByUser();
    }

    public List<GroupedStatView> getAllAutomationIssuesOutgoingByUsers() {
        List<GroupedStatView> stats = Lists.newArrayList();

        List<User> allAssignedUsers = automatedTestIssueService.getAllAssignedUsers();
        allAssignedUsers.forEach(user -> {
            long tests = countAllButFixed(user);
            long fixes = countAllFixed(user);
            GroupedStatView view = GroupedStatView
                    .builder()
                    .pending(tests)
                    .passingIssues(fixes)
                    .name(user.getRealname())
                    .build();

            stats.add(view);
        });

        return stats;
    }

    public HashMap<String, List<Integer>> getEngineerEffort(Long id) {
        List<KeyValuePair> testCases = testTriageService.findTestCasesGroupedByUser(productService.find(id));
        List<KeyValuePair> executors = executorService.findExecutorsGroupedByUser(productService.find(id));
        List<KeyValuePair> automationIssues = automatedTestIssueService.findAutomationIssuesGroupedByUser(productService.find(id));
        List<KeyValuePair> manualTriages = testTriageService.findManualTriagesGroupedByUser(productService.find(id));
        List<KeyValuePair> autoTriages = testTriageService.findAutoTriagesGroupedByUser(productService.find(id));
        List<KeyValuePair> automationIssuesFixed = automatedTestIssueService.findAutomationFixedGroupedByUser(productService.find(id));

        HashMap<String, List<Integer>> stats = new HashMap<>();

        testCases.forEach(keyValuePair -> {
            List<Integer> list = Arrays.asList(0, 0, 0, 0, 0, 0);
            list.set(0, ((Long) keyValuePair.getValue()).intValue());
            stats.put(keyValuePair.getKey(), list);
        });

        executors.forEach(keyValuePair -> {
            List<Integer> list = Arrays.asList(0, 0, 0, 0, 0, 0);
            if (stats.containsKey(keyValuePair.getKey())) {
                list = stats.get(keyValuePair.getKey());
            }

            list.set(1, ((Long) keyValuePair.getValue()).intValue());
            stats.put(keyValuePair.getKey(), list);
        });

        automationIssues.forEach(keyValuePair -> {
            List<Integer> list = Arrays.asList(0, 0, 0, 0, 0, 0);
            if (stats.containsKey(keyValuePair.getKey())) {
                list = stats.get(keyValuePair.getKey());
            }

            list.set(2, ((Long) keyValuePair.getValue()).intValue());
            stats.put(keyValuePair.getKey(), list);
        });

        manualTriages.forEach(keyValuePair -> {
            List<Integer> list = Arrays.asList(0, 0, 0, 0, 0, 0);
            if (stats.containsKey(keyValuePair.getKey())) {
                list = stats.get(keyValuePair.getKey());
            }

            list.set(3, ((Long) keyValuePair.getValue()).intValue());
            stats.put(keyValuePair.getKey(), list);
        });

        autoTriages.forEach(keyValuePair -> {
            List<Integer> list = Arrays.asList(0, 0, 0, 0, 0, 0);
            if (stats.containsKey(keyValuePair.getKey())) {
                list = stats.get(keyValuePair.getKey());
            }

            list.set(4, ((Long) keyValuePair.getValue()).intValue());
            stats.put(keyValuePair.getKey(), list);
        });

        automationIssuesFixed.forEach(keyValuePair -> {
            List<Integer> list = Arrays.asList(0, 0, 0, 0, 0, 0);
            if (stats.containsKey(keyValuePair.getKey())) {
                list = stats.get(keyValuePair.getKey());
            }

            list.set(5, ((Long) keyValuePair.getValue()).intValue());
            stats.put(keyValuePair.getKey(), list);
        });

        return stats;
    }

    //PENDING TO TRIAGE
    public List<GroupedStatView> getPendingToTriage() {
        List<GroupedStatView> answer = Lists.newArrayList();
        GroupedStatView view;
        for (Container container : containerService.findAll()) {
            if (container.getConnector().isEnabled()) {
                view = getPendingToTriage(container);
                if (view.getTotal() > 0)
                    answer.add(view);
            }
        }
        return answer;
    }

    /**
     * PENDING TRIAGE BY CONTAINER
     */
    public GroupedStatView getPendingToTriage(Container container) {
        List<BuildTriage> triages = buildTriageService.getPendingTriage(container);
        return getContainerStat(container, triages);
    }

    public GroupedStatView getPendingToTriage(Container container, Long prev, Long now) {
        List<BuildTriage> triages = buildTriageService.getPendingTriage(container, prev, now);
        return getContainerStat(container, triages);
    }

    /**
     * PENDING TRIAGE SUITES BY CONTAINER AND USER
     */
    public List<GroupedStatView> getPendingToTriageSuites(Container container, User user) {
        List<BuildTriage> triages = buildTriageService.getPendingTriage(container);
        if (user != null)
            triages = triages.stream().filter(bt -> bt.getTriager().equals(user)).collect(Collectors.toList());
        return getPendingTriageSuites(container, triages);
    }

    /**
     * PENDING TRIAGE SUITES BY CONTAINER, USER AND TIMESTAMP
     */
    public List<GroupedStatView> getPendingToTriageSuites(Container container, User user, Long prev, Long now) {
        List<BuildTriage> triages = buildTriageService.getPendingTriage(container, prev, now);
        if (user != null)
            triages = triages.stream().filter(bt -> bt.getTriager().equals(user)).collect(Collectors.toList());
        return getPendingTriageSuites(container, triages);
    }

    /**
     * PENDING TRIAGE SUITES BY CONTAINER
     */
    public List<GroupedStatView> getPendingToTriageSuites(Container container) {
        List<BuildTriage> triages = buildTriageService.getPendingTriage(container);
        return getPendingTriageSuites(container, triages);
    }

    /**
     * PENDING TRIAGE SUITES BY CONTAINER AND TIMESTAMP
     */
    public List<GroupedStatView> getPendingToTriageSuites(Container container, Long prev, Long now) {
        List<BuildTriage> triages = buildTriageService.getPendingTriage(container,prev, now);
        return getPendingTriageSuites(container, triages);
    }    


    //SUM BY BUILDTRIAGE
    private GroupedStatView getContainerStat(Container container, List<BuildTriage> triages) {

        long newFail = 0;
        long fails = 0;
        long permanent = 0;
        long nowPass = 0;
        long pass = 0;
        long triaged = 0;

        for (BuildTriage buildTriage : triages) {

            FilterSpecificationsBuilder newFailfilter = filter()
                    .with("build", ":", buildTriage.getBuild())
                    .with("triaged", ":", false)
                    .with("currentState", ":", NEWFAIL);

            FilterSpecificationsBuilder newPermanentfilter = filter()
                    .with("build", ":", buildTriage.getBuild())
                    .with("triaged", ":", false)
                    .with("currentState", ":", StateType.PERMANENT);

            FilterSpecificationsBuilder failFilter = filter()
                    .with("build", ":", buildTriage.getBuild())
                    .with("triaged", ":", false)
                    .with("currentState", ":", StateType.FAIL);

            FilterSpecificationsBuilder nowPassfilter = filter()
                    .with("build", ":", buildTriage.getBuild())
                    .with("triaged", ":", false)
                    .with("currentState", ":", StateType.NEWPASS);

            FilterSpecificationsBuilder passfilter = filter()
                    .with("build", ":", buildTriage.getBuild())
                    .with("triaged", ":", true)
                    .with("currentState", ":", StateType.PASS);

            FilterSpecificationsBuilder triagedfilter = filter()
                    .with("build", ":", buildTriage.getBuild())
                    .with("triaged", ":", true);


            newFail += testTriageService.count(newFailfilter.build());
            fails += testTriageService.count(failFilter.build());
            permanent += testTriageService.count(newPermanentfilter.build());
            nowPass += testTriageService.count(nowPassfilter.build());
            pass += testTriageService.count(passfilter.build());
            triaged += testTriageService.count(triagedfilter.build());
        }

        long toTriage = fails + newFail + nowPass + permanent;
        long total = fails + newFail + nowPass + triaged;
        return GroupedStatView.builder()
                .name(container.getName())
                .assignee(triageSpecService.geTriageFlowSpecByContainer(container).getTriager())
                .productName(container.getProductName())
                .containerName(container.getName())
                .timestamp(container.getTimestamp())
                .fails(fails + permanent)
                .newFails(newFail)
                .nowPassing(nowPass)
                .triaged(triaged)
                .passed(pass)
                .toTriage(toTriage)
                .permanent(permanent)
                //.total(toTriage + pass + permanent + triaged)
                .total(total)
                .build();
    }

    //LIST BY SUITES
    public List<GroupedStatView> getPendingTriageSuites(Container container, List<BuildTriage> triages) {

        long newFail = 0;
        long fails = 0;
        long permanent = 0;
        long nowPass = 0;
        long pass = 0;
        long triaged = 0;
        List<GroupedStatView> result = Lists.newArrayList();


        for (BuildTriage buildTriage : triages) {

            FilterSpecificationsBuilder newFailfilter = filter()
                    .with("build", ":", buildTriage.getBuild())
                    .with("triaged", ":", false)
                    .with("currentState", ":", NEWFAIL);

            FilterSpecificationsBuilder newPermanentfilter = filter()
                    .with("build", ":", buildTriage.getBuild())
                    .with("triaged", ":", false)
                    .with("currentState", ":", StateType.PERMANENT);

            FilterSpecificationsBuilder failFilter = filter()
                    .with("build", ":", buildTriage.getBuild())
                    .with("triaged", ":", false)
                    .with("currentState", ":", StateType.FAIL);

            FilterSpecificationsBuilder nowPassfilter = filter()
                    .with("build", ":", buildTriage.getBuild())
                    .with("triaged", ":", false)
                    .with("currentState", ":", StateType.NEWPASS);

            FilterSpecificationsBuilder passfilter = filter()
                    .with("build", ":", buildTriage.getBuild())
                    .with("triaged", ":", true)
                    .with("currentState", ":", StateType.PASS);

            FilterSpecificationsBuilder triagedfilter = filter()
                    .with("build", ":", buildTriage.getBuild())
                    .with("triaged", ":", true);


            newFail = testTriageService.count(newFailfilter.build());
            fails = testTriageService.count(failFilter.build());
            permanent = testTriageService.count(newPermanentfilter.build());
            nowPass = testTriageService.count(nowPassfilter.build());
            pass = testTriageService.count(passfilter.build());
            triaged = testTriageService.count(triagedfilter.build());


            long toTriage = fails + newFail + nowPass + permanent;
            long total = fails + newFail + nowPass + triaged;
            GroupedStatView statView = GroupedStatView.builder()
                    .name(buildTriage.getExecutorName())
                    .productName(container.getProductName())
                    .containerName(container.getName())
                    .timestamp(buildTriage.getDeadline())
                    .fails(fails + permanent)
                    .newFails(newFail)
                    .nowPassing(nowPass)
                    .triaged(triaged)
                    .passed(pass)
                    .toTriage(toTriage)
                    .permanent(permanent)
                    //.total(toTriage + pass + permanent + triaged)
                    .total(total)
                    .build();

            result.add(statView);
        }

        return result;
    }


    //ONGOING TESTS
    public GroupedStatView getCountTestsSummary() {
        return getCountTestsSummary(null);//Any user
    }

    public GroupedStatView getCountTestsSummary(User user) {

        List<TestTriage> allOngoingTests = testTriageService.findAllOngoingTests();
        if (user != null)
            allOngoingTests = allOngoingTests
                    .stream()
                    .filter(testTriage -> testTriage.getTriager().equals(user))
                    .collect(Collectors.toList());

        long fails = allOngoingTests
                .stream()
                .filter(testTriage -> isEnabled(testTriage) && !testTriage.isTriaged() && (testTriage.isFailed() || testTriage.isPermanent()))
                .count();

        long nowPass = allOngoingTests
                .stream()
                .filter(testTriage -> isEnabled(testTriage) && !testTriage.isTriaged() && testTriage.isNewPass())
                .count();

        long triaged = allOngoingTests
                .stream()
                .filter(testTriage -> isEnabled(testTriage) && testTriage.isTriaged() && !testTriage.isPass())
                .count();

        return GroupedStatView.builder()
                .name("")
                .timestamp(0)
                .triaged(triaged)
                .fails(fails)
                // .passed(pass)
                .nowPassing(nowPass)
                .total(fails + nowPass + triaged)
                .build();
    }

    public GroupedStatView getCountTestsSummary(User user, Long prev, Long now) {

        List<TestTriage> allOngoingTests = testTriageService.findAllOngoingTests();
        if (user != null)
            allOngoingTests = allOngoingTests
                    .stream()
                    .filter(testTriage -> testTriage.getTriager().equals(user) && testTriage.getTimestamp() > prev && testTriage.getTimestamp() < now)
                    .collect(Collectors.toList());

        long fails = allOngoingTests
                .stream()
                .filter(testTriage -> isEnabled(testTriage) && !testTriage.isTriaged() && (testTriage.isFailed() || testTriage.isPermanent())&& testTriage.getTimestamp() > prev && testTriage.getTimestamp() < now)
                .count();

        long nowPass = allOngoingTests
                .stream()
                .filter(testTriage -> isEnabled(testTriage) && !testTriage.isTriaged() && testTriage.isNewPass()&& testTriage.getTimestamp() > prev && testTriage.getTimestamp() < now)
                .count();

        long triaged = allOngoingTests
                .stream()
                .filter(testTriage -> isEnabled(testTriage) && testTriage.isTriaged() && !testTriage.isPass() && testTriage.getTimestamp() > prev && testTriage.getTimestamp() < now)
                .count();

        return GroupedStatView.builder()
                .name("")
                .timestamp(0)
                .triaged(triaged)
                .fails(fails)
                // .passed(pass)
                .nowPassing(nowPass)
                .total(fails + nowPass + triaged)
                .build();
    }
    
    private boolean isEnabled(TestTriage testTriage) {
        return testTriage.isHierarchicalyEnabled();
    }

    //BURNDOWN: FAILS VS. NEW TESTS AND FIXES
    public List<GroupedStatView> getGlobalBurndownFailNewFixes() {
        List<GroupedStatView> answer = Lists.newArrayList();
        GroupedStatView view;

        long now = DateUtils.now();
        HashMap<Integer, Long> timeSlices = DateUtils.getTimeSlices();
        for (Long prev : timeSlices.values()) {

            long fails = testTriageService.findAll(getFailFilter(now, prev), Sort.unsorted())
                    .stream()
                    .filter(TestTriage::isHierarchicalyEnabled)
                    .count();
            //testTriageService.count(getFailFilter(now, prev));

            long fixes = automatedTestIssueService.findAll(getFixedFilter(now, prev), Sort.unsorted())
                    .stream()
                    .filter(AutomatedTestIssue::isHierarchicalyEnabled)
                    .count();
            //automatedTestIssueService.count(getFixedFilter(now, prev));

            long newTests = testCaseService.findAll(getNewTestsFilter(now, prev), Sort.unsorted())
                    .stream()
                    .filter(TestCase::isEnabled)
                    .count();
            //testCaseService.count(getNewTestsFilter(now, prev));

            view = GroupedStatView.builder()
                    .timestamp(now)
                    .name(DateUtils.getMonthName(now, false))
                    .shortName(DateUtils.getMonthName(now, true))
                    .fails(fails)
                    .newFixes(fixes)
                    .newTests(newTests)
                    .build();
            answer.add(view);

            now = prev;
        }
        return answer;
    }

    //BUGS FILED
    public List<GroupedStatView> getBugsFiled() {
        List<GroupedStatView> answer = Lists.newArrayList();
        GroupedStatView view;

        long now = DateUtils.now();
        HashMap<Integer, Long> timeSlices = DateUtils.getTimeSlices();
        for (Long prev : timeSlices.values()) {

            long total = automatedTestIssueService.findAll(getFiledFilter(now, prev), Sort.unsorted())
                    .stream()
                    .filter(AutomatedTestIssue::isHierarchicalyEnabled)
                    .count();

            long bugsFixed = automatedTestIssueService.findAll(getFixedFilter(now, prev), Sort.unsorted())
                    .stream()
                    .filter(AutomatedTestIssue::isHierarchicalyEnabled)
                    .count();

            long bugsOpen = automatedTestIssueService.findAll(getOpenIssuesFilter(now, prev), Sort.unsorted())
                    .stream()
                    .filter(AutomatedTestIssue::isHierarchicalyEnabled)
                    .count();

            long bugsPassing = automatedTestIssueService.findAll(getPassingIssuesFilter(now, prev), Sort.unsorted())
                    .stream()
                    .filter(AutomatedTestIssue::isHierarchicalyEnabled)
                    .count();

            long bugsReopen = automatedTestIssueService.findAll(getReopenIssuesFilter(now, prev), Sort.unsorted())
                    .stream()
                    .filter(AutomatedTestIssue::isHierarchicalyEnabled)
                    .count();

            view = GroupedStatView.builder()
                    .timestamp(now)
                    .name(DateUtils.getMonthName(now, false))
                    .shortName(DateUtils.getMonthName(now, true))
                    .total(total)
                    .newFixes(bugsFixed)
                    .reopenIssues(bugsReopen)
                    .passingIssues(bugsPassing)
                    .openIssues(bugsOpen)
                    .build();
            answer.add(view);

            now = prev;
        }
        return answer;
    }

    //MISSING DEADLINES
    public List<GroupedStatView> getMissingDeadlines() {
        List<GroupedStatView> answer = Lists.newArrayList();
        GroupedStatView view;

        long now = DateUtils.now();
        HashMap<Integer, Long> timeSlices = DateUtils.getTimeSlices();
        for (Long prev : timeSlices.values()) {
            long total = buildTriageService.findAll(getMissingDeadlinesFilter(now, prev), Sort.unsorted())
                    .stream()
                    .filter(not(BuildTriage::isAutomatedTriaged)).count();

            view = GroupedStatView.builder()
                    .timestamp(now)
                    .name(DateUtils.getMonthName(now, false))
                    .shortName(DateUtils.getMonthName(now, true))
                    .total(total)
                    .build();
            answer.add(view);

            now = prev;
        }
        return answer;
    }


    //Automation pending and fixes for the logged user
    public GroupedStatView getAutomationPendingsAndFixesForUser() {
        User user = authContextHelper.getCurrentUser();

        long fixes = automatedTestIssueService.count(getFixedFilterForUser(user));
        long pending = automatedTestIssueService.count(getPendingFilterForUser(user));

        return GroupedStatView.builder()
                .name("")
                .timestamp(DateUtils.now())
                .total(fixes + pending)
                .newFixes(fixes)
                .pending(pending)
                .build();
    }

    //Triages for day
    public List<GroupedStatView> getTriagesForDayForUser() {
        User user = authContextHelper.getCurrentUser();

        List<GroupedStatView> answer = Lists.newArrayList();
        GroupedStatView view;

        long now = DateUtils.now();
        HashMap<Integer, Long> timeSlices = DateUtils.getTimeForDaySlices();
        for (Long prev : timeSlices.values()) {
            long triaged = testTriageService.findAll(getTriagesDoneForDay(user, now, prev), Sort.unsorted())
                    .stream()
                    .filter(testTriage -> testTriage.isHierarchicalyEnabled() && !testTriage.isAutomatedTriaged())
                    .count();

            long total = testTriageService.findAll(getTriagesForDay(user, now, prev), Sort.unsorted())
                    .stream()
                    .filter(testTriage -> testTriage.isHierarchicalyEnabled() && !testTriage.isAutomatedTriaged())
                    .count();

            view = GroupedStatView.builder()
                    .timestamp(now)
                    .name(DateUtils.getDayName(now, false))
                    .shortName(DateUtils.getDayName(now, true))
                    .triaged(triaged)
                    .total(total)
                    .build();

            answer.add(view);

            now = prev;
        }

        return answer;
    }

    public List<GroupedStatView> getSuiteEvolutionForWeek(Long id) {
        List<GroupedStatView> answer = Lists.newArrayList();
        GroupedStatView view;

        long now = DateUtils.now();
        HashMap<Integer, Long> timeSlices = DateUtils.getTimeForWeekSlices();
        for (Long prev : timeSlices.values()) {
            Long total = executorService.sumTestExecutedByProduct(productService.find(id), prev, now);

            Date date = new Date(now);
            Format format = new SimpleDateFormat("MMMM dd");

            view = GroupedStatView.builder()
                    .timestamp(now)
                    .name(format.format(date))
                    .shortName(format.format(date))
                    .total(total == null ? 0 : total)
                    .build();

            answer.add(view);

            now = prev;
        }

        return answer;
    }

    public List<GroupedStatView> getFailedTestsForWeek(Long id) {
        List<GroupedStatView> answer = Lists.newArrayList();
        GroupedStatView view;

        long now = DateUtils.now();
        HashMap<Integer, Long> timeSlices = DateUtils.getTimeForWeekSlices();
        for (Long prev : timeSlices.values()) {
            Long total = productStatService.getFailsByProduct(productService.find(id), prev, now);

            Date date = new Date(now);
            Format format = new SimpleDateFormat("MMMM dd");

            view = GroupedStatView.builder()
                    .timestamp(now)
                    .name(format.format(date))
                    .shortName(format.format(date))
                    .total(total == null ? 0 : total)
                    .build();

            answer.add(view);

            now = prev;
        }

        return answer;
    }

    //FAIL EXCEPTIONS
    public List<KeyValuePair> getFailExceptions() {
        return errorDetailService.getAllGroupedBy();
    }

    public List<LogCommitsPerPersonDTO> getCommitsPerPerson() {
        return logServiceDTO.getCommitsPerPerson();
    }

    public List<LogCommitsPerDayDTO> getCommitsPerDay() {
        return logServiceDTO.getCommitsPerDay();
    }

    public List<ChartSerieDTO> getCommitsPerPersonAndPerDay() {
        return logServiceDTO.getCommitsPerPersonAndPerDay();
    }

    public List<CVSLog> getCommits() {
        return logService.findAll();
    }
    
    public long countCommitsPerPerson(User user, long now, Long prev) {
        return logService.count(getCommitsForDay(user, now, prev));
    }


    // --------------------------- Begin Filters ---------------------------

    public Specification getNewTestsFilter(long now, Long prev) {
        return filter()
                .with("timestamp", "<", now)
                .with("timestamp", ">", prev)
                .build();
    }

    public Specification getFixedFilter(long now, Long prev) {
        return filter()
                .with("timestamp", "<", now)
                .with("timestamp", ">", prev)
                .with("issueType", ":", IssueType.FIXED)
                .build();
    }

    public Specification getFixedFilter() {
        return filter()
                .with("issueType", ":", IssueType.FIXED)
                .build();
    }

    public Specification getOpenIssuesFilter(long now, Long prev) {
        return filter()
                .with("issueType", ":", IssueType.OPEN)
                .with("timestamp", "<", now)
                .with("timestamp", ">", prev)
                .build();
    }

    public Specification getReopenIssuesFilter(long now, Long prev) {
        return filter()
                .with("issueType", ":", IssueType.REOPEN)
                .with("timestamp", "<", now)
                .with("timestamp", ">", prev)
                .build();
    }

    public Specification getPassingIssuesFilter(long now, Long prev) {
        return filter()
                .with("issueType", ":", IssueType.PASSING)
                .with("timestamp", "<", now)
                .with("timestamp", ">", prev)
                .build();
    }

    public Specification getFiledFilter(long now, Long prev) {
        return filter()
                .with("timestamp", "<", now)
                .with("timestamp", ">", prev)
                .build();
    }

    public Specification getMissingDeadlinesFilter(long now, Long prev) {
        //select * from qa_build_triage where enabled = false AND triaged = true
        return new FilterSpecificationsBuilder<>()
                .with("triaged", ":", false)
                .with("enabled", ":", false)
                .with("deadline", "<", now)
                .with("deadline", ">", prev)
                .build();
    }

    public Specification getFailFilter(long now, Long prev) {
        return filter()
                .with("triaged", ":", true)
                .with("timestamp", "<", now)
                .with("timestamp", ">", prev)
                .with("currentState", ":", StateType.FAIL)
                .build();
    }

    public Specification getFixedFilterForUser(User user) {
        return filter()
                .with("issueType", ":", IssueType.FIXED)
                .with("triager", ":", user)
                .build();
    }

    public Specification getPendingFilterForUser(User user) {
        return filter()
                .with("issueType", ":", IssueType.OPEN)
                .with("triager", ":", user.getId())
                .build();
    }

    public Specification getTriagesDoneForDay(User user, long now, Long prev) {
        return filter()
                .with("timestamp", "<", now)
                .with("timestamp", ">", prev)
                .with("triager", ":", user)
                .with("triaged", ":", true)
                .build();
    }

    public Specification getTriagesForDay(User user, long now, Long prev) {
        return filter()
                .with("timestamp", "<", now)
                .with("timestamp", ">", prev)
                .with("triager", ":", user)
                .build();
    }

    private Specification getFilterBy(User user) {
        return filter()
                .with("triager", ":", user)
                .build();
    }

    private Specification getFilterBy(Product product) {
        return filter()
                .with("product", ":", product)
                .build();
    }

    private Specification getFilterPendingToTriageBy(Product product) {
        return filter()
                .with("triaged", ":", false)
                .with("product", ":", product)
                .build();
    }

    public Specification getCommitsForDay(User user, long now, Long prev) {
        return filter()
                .with("timestamp", "<", now)
                .with("timestamp", ">", prev)                
                .with("author", ":", user)
                .build();
    }

    private FilterSpecificationsBuilder filter() {
        return new FilterSpecificationsBuilder<>().with("enabled", ":", true);
    }


    //Utility function
    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    // --------------------------- End Filters ---------------------------

    public long testCountAssignedTo(User user) {
        return testTriageService.count(getFilterBy(user));
    }

    public long testCountAssignedTo(User user, Long prev, Long now) {
        return testTriageService.count(getTriagesForDay(user, now, prev));
    }    

    public long testCountAssignedTo(Product product) {
        return testTriageService.count(getFilterBy(product));
    }

    public long testPendingToTriage(Product product) {
        return testTriageService.count(getFilterPendingToTriageBy(product));
    }

    public long countAllButFixed(User user) {
        return automatedTestIssueService.countAllButFixed(user);
    }

    public long countAllButFixed(User user, Long prev, Long now) {
        return automatedTestIssueService.countAllButFixed(user, prev, now);
    }

    public long countAllFixed(User user) {
        return automatedTestIssueService.countAllFixed(user);
    }


    //SUITES PENDING TRIAGED (Used in PDF report)
    public List<GroupedStatView> getSuitesPendingToTriaged(User user) {
        List<GroupedStatView> answer = Lists.newArrayList();
        for (Container container : containerService.findAll()) {
            if (container.getConnector().isEnabled()) {
                List<GroupedStatView> suites = getPendingToTriageSuites(container, user);
                answer.addAll(suites);
            }
        }
        return answer;
    }

    //SUITES PENDING TRIAGED (Used in PDF report)
    public List<GroupedStatView> getSuitesPendingToTriaged(User user, Long prev, Long now) {
        List<GroupedStatView> answer = Lists.newArrayList();
        for (Container container : containerService.findAll()) {
            if (container.getConnector().isEnabled()) {
                List<GroupedStatView> suites = getPendingToTriageSuites(container, user, prev, now);
                answer.addAll(suites);
            }
        }
        return answer;
    }

    //SUITES PENDING TRIAGED (Used in PDF report)
    public List<GroupedStatView> getSuitesPendingToTriaged(Product product) {
        List<GroupedStatView> answer = Lists.newArrayList();
        for (Container container : containerService.findAll(getFilterBy(product), Sort.by("name"))) {
            if (container.getConnector().isEnabled()) {
                List<GroupedStatView> suites = getPendingToTriageSuites(container);
                answer.addAll(suites);
            }
        }
        return answer;
    }

    public List<GroupedStatView> getSuitesPendingToTriaged(Product product, Long prev, Long now) {
        List<GroupedStatView> answer = Lists.newArrayList();
        for (Container container : containerService.findAll(getFilterBy(product), Sort.by("name"))) {
            if (container.getConnector().isEnabled()) {
                List<GroupedStatView> suites = getPendingToTriageSuites(container, prev, now);
                answer.addAll(suites);
            }
        }
        return answer;
    }    

    //SUITES PENDING TRIAGED (Used in PDF report)
    public List<GroupedStatView> getSuitesPendingToTriaged(Product product, User user) {
        List<GroupedStatView> answer = Lists.newArrayList();
        for (Container container : containerService.findAll(getFilterBy(product), Sort.by("name"))) {
            if (container.getConnector().isEnabled()) {
                List<GroupedStatView> suites = getPendingToTriageSuites(container, user);
                answer.addAll(suites);
            }
        }
        return answer;
    }

    public List<GroupedStatView> getSuitesPendingToTriaged(Product product, User user, Long prev, Long now) {
        List<GroupedStatView> answer = Lists.newArrayList();
        for (Container container : containerService.findAll(getFilterBy(product), Sort.by("name"))) {
            if (container.getConnector().isEnabled()) {
                List<GroupedStatView> suites = getPendingToTriageSuites(container, user, prev, now);
                answer.addAll(suites);
            }
        }
        return answer;
    }    

}
