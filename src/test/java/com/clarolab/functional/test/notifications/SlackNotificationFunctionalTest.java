/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.notifications;

import com.clarolab.agents.TriageAgent;
import com.clarolab.event.analytics.ExecutorStat;
import com.clarolab.event.analytics.ProductStat;
import com.clarolab.event.process.ApplicationEventType;
import com.clarolab.event.slack.SlackNotificationEventHandler;
import com.clarolab.event.slack.SlackService;
import com.clarolab.event.slack.SlackSpec;
import com.clarolab.event.slack.SlackSpecService;
import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.*;
import com.clarolab.model.types.ApplicationFailType;
import com.clarolab.model.types.StatusType;
import com.clarolab.model.types.TestFailType;
import com.clarolab.populate.PopulateDemoData;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.*;
import com.clarolab.util.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SlackNotificationFunctionalTest extends BaseFunctionalTest {

    public final String token = "xoxb-546678226724-815665986210-WaHknRamVSIYbVG2FJSmEnbE";

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private TestExecutionService testExecutionService;

    @Autowired
    private SlackSpecService slackSpecService;

    @Autowired
    private ApplicationEventService applicationEventService;

    @Autowired
    private SlackNotificationEventHandler slack;

    @Autowired
    private SlackService slackService;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private UserService userService;

    @Autowired
    private TriageSpecService triageSpecService;

    @Autowired
    private TriageAgent triageAgent;

    @Autowired
    private PopulateDemoData populateDemoData;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void testSendMessageContainer() {
        int amount = 10;
        int suites = 3;

        // Configures a test triage
        provider.setName("testSendMessageContainer");
        configureSpec();
        provider.getDeadline();
        provider.getTriageSpec().getTriager().setUsername("satasuarez@gmail.com");
        userService.update(provider.getTriageSpec().getTriager());
        provider.getTriageSpec().setContainer(provider.getContainer());
        triageSpecService.update(provider.getTriageSpec());

        // Creates suites with tests
        for (int i = 0; i < suites; i++) {
            provider.setExecutor(null);
            provider.clearForNewBuild();

            populateBuilds();
        }


        // Creates an event to process slack
        ApplicationEvent event = provider.getApplicationEvent();
        event.setType(ApplicationEventType.BUILD_TRIAGE_GENERATED_FOR_CONTAINER);
        event.setExtraParameter(String.valueOf(provider.getContainer().getId()));
        event = applicationEventService.update(event);
        provider.setApplicationEvent(event);

        slack.process(event);

    }

    // Configure to send it to the slack t-triage.slack.com / notification
    private void configureSpec() {
        SlackSpec spec = provider.getSlackSpec();
        spec.setContainer(provider.getContainer());
        spec.setSendUserNotification(true);
        spec.setToken(token);
        spec.setChannel("notification");
        spec = slackSpecService.update(spec);
        provider.setSlackSpec(spec);
    }

    @Test
    public void testSendMessageExecutor() {

        // Configures a test triage
        provider.setName("executor");
        provider.getDeadline();
        provider.getTriageSpec().getTriager().setUsername("francisco.vives@clarolab.com");
        provider.getTriageSpec().getTriager().setSlackId("UG22ZDP7A");
        userService.update(provider.getTriageSpec().getTriager());
        provider.getTriageSpec().setContainer(provider.getContainer());
        triageSpecService.update(provider.getTriageSpec());
        configureSpec();

        populateBuilds();

        // Creates an event to process slack
        ApplicationEvent event = provider.getApplicationEvent();
        event.setType(ApplicationEventType.BUILD_TRIAGE_GENERATED_FOR_EXECUTOR);
        event.setExtraParameter(String.valueOf(provider.getBuildTriage().getId()));
        event = applicationEventService.update(event);
        provider.setApplicationEvent(event);

        slack.process(event);

    }

    private void populateBuilds() {
        provider.clearForNewBuild();
        provider.getBuild(1);

        // CREATE EACH TYPE OF TESTS
        provider.setTestExecution(null);
        TestExecution failTest = provider.getTestExecution(StatusType.FAIL); // for fail

        provider.setTestExecution(null);
        TestExecution permanentTest = provider.getTestExecution(StatusType.FAIL); // for permanent

        provider.setTestExecution(null);
        TestExecution passTest = provider.getTestExecution(StatusType.PASS); // for pass

        provider.setTestExecution(null);
        TestExecution newPassTest = provider.getTestExecution(StatusType.FAIL); // for new pass

        // TRIAGE AGENT
        provider.getBuildTriage();

        // PERFORM TRIAGE
        // skip options
        TestTriage failTriage = testTriageService.findLastTriage(failTest, provider.getBuild());
        failTriage.setTriager(provider.getUser());
        failTriage.setTriaged();
        testTriageService.update(failTriage);

        // for permanent triage
        TestTriage permanentTriage = testTriageService.findLastTriage(permanentTest, provider.getBuild());
        permanentTriage.setTriager(provider.getUser());
        permanentTriage.setTriaged();
        permanentTriage.setApplicationFailType(ApplicationFailType.FILED_TICKET);
        permanentTriage.setTestFailType(TestFailType.TEST_ASSIGNED_TO_FIX);
        testTriageService.update(permanentTriage);

        // pass options
        TestTriage passTriage = testTriageService.findLastTriage(passTest, provider.getBuild());
        passTriage.setTriager(provider.getUser());
        passTriage.setTriaged();
        testTriageService.update(passTriage);

        // new pass
        TestTriage newPassTriage = testTriageService.findLastTriage(newPassTest, provider.getBuild());
        newPassTriage.setTriager(provider.getUser());
        newPassTriage.setTriaged();
        testTriageService.update(newPassTriage);


        // Creates Build 2
        provider.clearForNewBuild();
        provider.getBuild(2);

        provider.setTestExecution(null);
        provider.getTestExecution(failTest);

        provider.setTestExecution(null);
        provider.getTestExecution(permanentTest);

        provider.setTestExecution(null);
        passTest.setStatus(StatusType.PASS);
        provider.getTestExecution(passTest);

        provider.setTestExecution(null);
        provider.getTestExecution(newPassTest);


        provider.getBuildTriage();

    }

    @Test
    public void testMessage() {
        // Configures a test triage
        provider.setName("testConfiguration");
        configureSpec();

        slackSpecService.sendTestMessage(provider.getContainer());

    }

    @Test
    public void testDailySummary() {
        int amount = 1;
        User user = provider.getUser();

        for (int i = 0; i < amount; i++) {
            // Configures a test triage
            provider.clear();
            provider.setUser(user);
            provider.setName("DailySummary" + i);
            provider.getDeadline();
            provider.getTriageSpec().getTriager().setUsername("francisco.vives@ttriage.com");
            userService.update(provider.getTriageSpec().getTriager());
            provider.getTriageSpec().setContainer(provider.getContainer());
            triageSpecService.update(provider.getTriageSpec());
            configureSpec();
            SlackSpec spec = provider.getSlackSpec();
            spec.setSendDailyNotification(true);
            spec.setSendUserNotification(false);
            slackSpecService.update(spec);

            provider.getBuild(1);
            provider.getTestExecutionFail();

            // TRIAGE AGENT
            triageAgent.executeByBuildsSync();
        }

        slack.processYesterdayTriageAgent(provider.getApplicationEvent());

    }

    @Test
    public void testSlackIds() {
        provider.setName("slackId");
        provider.getDeadline();
        provider.getTriageSpec().getTriager().setUsername("francisco.vives@ttriage.com");
        User user = userService.update(provider.getTriageSpec().getTriager());
        provider.getTriageSpec().setContainer(provider.getContainer());
        triageSpecService.update(provider.getTriageSpec());
        configureSpec();

        slackService.setSlackUserIds();

        user = userService.find(user.getId());

        Assert.assertNotNull(user.getSlackId());
    }

    @Test
    public void testSendPendingFixes() {
        provider.setName("testSendPendingFixes1");
        provider.getDeadline();
        provider.getTriageSpec().getTriager().setUsername("francisco.vives@ttriage.com");
        User user = userService.update(provider.getTriageSpec().getTriager());
        provider.getTriageSpec().setContainer(provider.getContainer());
        triageSpecService.update(provider.getTriageSpec());
        configureSpec();

        provider.getAutomatedTestIssue();
        provider.getManualTestCase(1);

        provider.clear();
        provider.setName("testSendPendingFixes2");
        provider.setUser(user);
        provider.getDeadline();
        provider.getTriageSpec().setContainer(provider.getContainer());
        triageSpecService.update(provider.getTriageSpec());
        configureSpec();

        provider.getBuild(1);
        provider.getAutomatedTestIssue();
        provider.clearForNewBuild();
        provider.setAutomatedTestIssue(null);
        provider.getBuild(2);
        provider.getAutomatedTestIssue();

        provider.getManualTestCase(1);
        provider.setManualTestCase(null);
        provider.getManualTestCase(1);

        slack.sendAutomationPending();
    }

    @Test
    public void testSendMessageContainerUnique() {
        int amount = 3;

        // Configures a test triage
        provider.setName("testSendMessageContainerUnique");
        configureSpec();
        provider.getSlackSpec().setSendUserNotification(false);
        slackSpecService.update(provider.getSlackSpec());

        // Creates suites with tests
        for (int i = 1; i < amount; i++) {
            provider.clearForNewBuild();
            provider.getBuild(i);
            provider.getTestExecutionFail();
            triageAgent.executeByBuildsSync();
        }

        // Creates an event to process slack
        ApplicationEvent event = applicationEventService.getLastPendingEvent(ApplicationEventType.TRIAGE_AGENT_EXECUTED);

        slack.process(event);

    }

    @Test
    public void testProductChannel() {
        SlackSpec spec = provider.getSlackSpec();
        spec.setToken(token);
        spec.setChannel("product");
        spec = slackSpecService.update(spec);
        provider.setSlackSpec(spec);

        slackSpecService.sendTestMessage(spec);
    }

    @Test
    public void testContainerChannel() {
        SlackSpec specP = provider.getSlackSpec();
        provider.setSlackSpec(null);
        SlackSpec spec = provider.getSlackSpec();
        spec.setParent(specP);
        spec.setContainer(provider.getContainer());
        spec.setToken(token);
        spec.setChannel("container");
        spec = slackSpecService.update(spec);
        provider.setSlackSpec(spec);

        slackSpecService.sendTestMessage(spec);
    }

    @Test
    public void testExecutorChannel() {
        SlackSpec specP = provider.getSlackSpec();
        provider.setSlackSpec(null);
        SlackSpec specC = provider.getSlackSpec();
        specC.setParent(specP);
        specC.setContainer(provider.getContainer());
        specC = slackSpecService.update(specC);
        provider.setSlackSpec(null);
        SlackSpec spec = provider.getSlackSpec();
        spec.setParent(specC);
        spec.setContainer(provider.getContainer());
        spec.setExecutor(provider.getExecutor());
        spec = slackSpecService.update(spec);
        spec.setToken(token);
        spec.setChannel("executor");
        spec = slackSpecService.update(spec);
        provider.setSlackSpec(spec);

        slackSpecService.sendTestMessage(spec);
    }

    // DAILY MESSAGE

    @Test
    public void testDailyProductChannel() {
        getProductSpec();

        SlackSpec spec = slackSpecService.find(provider.getProduct());

        provider.setSlackSpec(spec);

        slackSpecService.sendTestMessage(spec, spec.getFinalDailyChannel(), "Daily Message: testDailyProductChannel");
    }

    @Test
    public void testDailyContainerChannel() {
        getContainerSpec();

        SlackSpec spec = slackSpecService.find(provider.getContainer());

        slackSpecService.sendTestMessage(spec, spec.getFinalDailyChannel(), "Daily Message: testDailyContainerChannel");
    }

    @Test
    public void testDailyExecutorChannel() {
        getExecutorSpec();

        SlackSpec spec = slackSpecService.find(provider.getExecutor());

        slackSpecService.sendTestMessage(spec, spec.getFinalDailyChannel(), "Daily Message: testDailyExecutorChannel");
    }

    private SlackSpec getProductSpec() {
        SlackSpec specP = provider.getSlackSpec();
        specP.setToken(token);
        specP.setChannel("product");
        specP.setDailyChannel("product");
        specP = slackSpecService.update(specP);
        provider.setSlackSpec(specP);

        specP = slackSpecService.find(provider.getProduct());

        return specP;
    }

    private SlackSpec getContainerSpec() {
        SlackSpec specP = getProductSpec();
        provider.setSlackSpec(null);
        SlackSpec spec = provider.getSlackSpec();
        spec.setParent(specP);
        spec.setContainer(provider.getContainer());
        spec.setToken(token);
        spec.setChannel("container");
        spec.setDailyChannel("container");
        spec = slackSpecService.update(spec);
        provider.setSlackSpec(spec);

        spec = slackSpecService.find(provider.getContainer());
        return spec;
    }

    private SlackSpec getExecutorSpec() {
        SlackSpec specC = getContainerSpec();
        provider.setSlackSpec(null);
        SlackSpec spec = provider.getSlackSpec();
        spec.setParent(specC);
        spec.setContainer(provider.getContainer());
        spec.setExecutor(provider.getExecutor());
        spec = slackSpecService.update(spec);
        spec.setToken(token);
        spec.setChannel("executor");
        spec.setDailyChannel("executor");
        spec = slackSpecService.update(spec);
        provider.setSlackSpec(spec);

        spec = slackSpecService.find(provider.getExecutor());

        return spec;
    }

    @Test
    public void testDailyContainerSummary() {
        int amount = 1;
        User user = provider.getUser();

        for (int i = 0; i < amount; i++) {
            // Configures a test triage
            provider.clear();
            provider.setUser(user);
            provider.setName("DailySummary" + i);
            provider.getDeadline();
            provider.getTriageSpec().getTriager().setUsername("francisco.vives@ttriage.com");
            userService.update(provider.getTriageSpec().getTriager());
            provider.getTriageSpec().setContainer(provider.getContainer());
            triageSpecService.update(provider.getTriageSpec());
            configureSpec();
            SlackSpec spec = provider.getSlackSpec();
            spec.setSendDailyNotification(true);
            spec.setSendUserNotification(false);
            slackSpecService.update(spec);

            provider.getBuild(1);
            provider.getTestExecutionFail();

            // TRIAGE AGENT
            triageAgent.executeByBuildsSync();
        }

        slack.processDailyContainer("testDailyContainerSummary", provider.getContainer(), provider.getSlackSpec());

    }

    @Test
    public void testDailyContainerSummaryAllPass() {
        int amount = 1;
        User user = provider.getUser();

        for (int i = 0; i < amount; i++) {
            // Configures a test triage
            provider.clear();
            provider.setUser(user);
            provider.setName("DailySummaryPass" + i);
            provider.getDeadline();
            provider.getTriageSpec().getTriager().setUsername("francisco.vives@ttriage.com");
            userService.update(provider.getTriageSpec().getTriager());
            provider.getTriageSpec().setContainer(provider.getContainer());
            triageSpecService.update(provider.getTriageSpec());
            configureSpec();
            SlackSpec spec = provider.getSlackSpec();
            spec.setSendDailyNotification(true);
            spec.setSendUserNotification(false);
            slackSpecService.update(spec);

            provider.getBuild(1);
            provider.getTestExecution(StatusType.PASS);

            // TRIAGE AGENT
            triageAgent.executeByBuildsSync();
        }

        slack.processDailyContainer("testDailyContainerSummaryAllPass", provider.getContainer(), provider.getSlackSpec());
    }

    @Test
    public void testDailyContainerSummaryTriaged() {
        int amount = 1;
        User user = provider.getUser();

        for (int i = 0; i < amount; i++) {
            // Configures a test triage
            provider.clear();
            provider.setUser(user);
            provider.setName("DailySummaryPass" + i);
            provider.getDeadline();
            provider.getTriageSpec().getTriager().setUsername("francisco.vives@ttriage.com");
            userService.update(provider.getTriageSpec().getTriager());
            provider.getTriageSpec().setContainer(provider.getContainer());
            triageSpecService.update(provider.getTriageSpec());
            configureSpec();
            SlackSpec spec = provider.getSlackSpec();
            spec.setSendDailyNotification(true);
            spec.setSendUserNotification(false);
            slackSpecService.update(spec);

            provider.getBuild(1);
            provider.getTestExecutionFail();

            // TRIAGE AGENT
            TestTriage triage = provider.getTestCaseTriage();
            triage.setTriaged(true);
            testTriageService.update(triage);

        }

        slack.processDailyContainer("testDailyContainerSummaryAllPass", provider.getContainer(), provider.getSlackSpec());
    }

    @Test
    public void testDailyContainerSummaryFail() {
        int amount = 1;
        User user = provider.getUser();

        for (int i = 0; i < amount; i++) {
            // Configures a test triage
            provider.clear();
            provider.setUser(user);
            provider.setName("DailySummaryPass" + i);
            provider.getDeadline();
            provider.getTriageSpec().getTriager().setUsername("francisco.vives@ttriage.com");
            userService.update(provider.getTriageSpec().getTriager());
            provider.getTriageSpec().setContainer(provider.getContainer());
            triageSpecService.update(provider.getTriageSpec());
            configureSpec();
            SlackSpec spec = provider.getSlackSpec();
            spec.setSendDailyNotification(true);
            spec.setSendUserNotification(false);
            slackSpecService.update(spec);

            provider.getBuild(1);

            // TRIAGE AGENT
            triageAgent.executeByBuildsSync();

        }

        slack.processDailyContainer("testDailyContainerSummaryAllPass", provider.getContainer(), provider.getSlackSpec());
    }

    @Test
    public void testDailyContainerSummaryMultiple() {

        User user = provider.getUser();

        provider.setUser(user);
        provider.setName("Multiple");
        provider.getDeadline();
        provider.getTriageSpec().getTriager().setUsername("francisco.vives@ttriage.com");
        userService.update(provider.getTriageSpec().getTriager());
        provider.getTriageSpec().setContainer(provider.getContainer());
        triageSpecService.update(provider.getTriageSpec());
        configureSpec();
        SlackSpec spec = provider.getSlackSpec();
        spec.setSendDailyNotification(true);
        spec.setSendUserNotification(false);
        slackSpecService.update(spec);

        // pass
        provider.getBuild(1);
        provider.getTestExecution(StatusType.PASS);
        triageAgent.executeByBuildsSync();

        // clear
        provider.clearForNewBuild();
        provider.setExecutor(null);

        // empty
        provider.getBuild(1);
        triageAgent.executeByBuildsSync();

        // clear
        provider.clearForNewBuild();
        provider.setExecutor(null);

        // Fail Triaged
        provider.getBuild(1);
        provider.getTestExecutionFail();

        // TRIAGE AGENT
        TestTriage triage = provider.getTestCaseTriage();
        triage.setTriaged(true);
        testTriageService.update(triage);


        slack.processDailyContainer("testDailyContainerSummaryAllPass", provider.getContainer(), provider.getSlackSpec());
    }

    @Test
    public void testWeeklyContainerIndex() {
        User user = provider.getUser();

        provider.setUser(user);

        provider.getDeadline();
        provider.getTriageSpec().getTriager().setUsername("francisco.vives@ttriage.com");
        userService.update(provider.getTriageSpec().getTriager());
        provider.getTriageSpec().setContainer(provider.getContainer());
        triageSpecService.update(provider.getTriageSpec());
        configureSpec();
        SlackSpec spec = provider.getSlackSpec();
        spec.setSendDailyNotification(true);
        spec.setSendUserNotification(false);
        slackSpecService.update(spec);

        // pass
        provider.getBuild(1);
        provider.getTestExecution(StatusType.PASS);
        triageAgent.executeByBuildsSync();

        // clear
        provider.clearForNewBuild();
        provider.setExecutor(null);

        // empty
        provider.getBuild(1);
        triageAgent.executeByBuildsSync();

        // clear
        provider.clearForNewBuild();
        provider.setExecutor(null);

        // Fail Triaged
        provider.getBuild(1);
        provider.getTestExecutionFail();

        // TRIAGE AGENT
        TestTriage triage = provider.getTestCaseTriage();
        triage.setTriaged(true);
        testTriageService.update(triage);

        provider.setTimestamp(DateUtils.beginDay(-7));
        ExecutorStat statPrevious = provider.getExecutorStat();

        provider.setExecutorStat(null);
        provider.setTimestamp(DateUtils.beginDay(-0));
        ExecutorStat statCurrent = provider.getExecutorStat();


        slack.processWeeklyContainer("testWeeklyContainerIndex", provider.getContainer(), provider.getSlackSpec());
    }

    @Test
    public void testWeeklyProductGoalIndex() {
        User user = provider.getUser();

        provider.setUser(user);

        provider.getDeadline();
        provider.getTriageSpec().getTriager().setUsername("francisco.vives@ttriage.com");
        userService.update(provider.getTriageSpec().getTriager());
        provider.getTriageSpec().setContainer(provider.getContainer());
        triageSpecService.update(provider.getTriageSpec());
        configureSpec();
        SlackSpec spec = provider.getSlackSpec();
        spec.setSendDailyNotification(true);
        spec.setSendUserNotification(false);
        slackSpecService.update(spec);

        // pass
        provider.getBuild(1);
        provider.getTestExecution(StatusType.PASS);
        triageAgent.executeByBuildsSync();


        // clear
        provider.clearForNewBuild();
        provider.setProduct(null);


        provider.setTimestamp(DateUtils.beginDay(-7));
        ProductStat statPrevious = provider.getProductStat();

        provider.setProductStat(null);
        provider.setTimestamp(DateUtils.beginDay(-0));
        ProductStat statCurrent = provider.getProductStat();

        slack.processWeeklyProduct("testWeeklyProductIndex", provider.getSlackSpec());
    }
}
