/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.model;

import com.clarolab.agents.EventTimeAgent;
import com.clarolab.agents.TriageAgent;
import com.clarolab.event.analytics.AnalyticsDailyEventHandler;
import com.clarolab.event.analytics.UserStat;
import com.clarolab.event.analytics.UserStatService;
import com.clarolab.event.process.*;
import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.*;
import com.clarolab.populate.DataProvider;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.*;
import com.clarolab.util.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.clarolab.util.Constants.*;

public class ApplicationEventFunctionalTest extends BaseFunctionalTest {
    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private ApplicationEventService applicationEventService;

    @Autowired
    private ApplicationEventBuilder applicationEventBuilder;

    @Autowired
    private ApplicationEventProcessor processor;

    @Autowired
    private CleanupEventsEventHandler cleanupEventsEventHandler;

    @Autowired
    private AnalyticsDailyEventHandler analyticsDailyEventHandler;

    @Autowired
    private TriageAgent triageAgent;

    @Autowired
    private EventTimeAgent eventTimeAgent;

    @Autowired
    protected PropertyService propertyService;
    
    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private ReadCVSRepositoryEventHandler readCVSRepositoryEventHandler;

    @Autowired
    private CVSRepositoryService cvsRepositoryService;

    @Autowired
    private LogService logService;

    @Autowired
    private UserStatService userStatService;

    @Before
    public void clearProvider() {
        provider.clear();
    }


    @Test
    public void testProcessed() {
        int limit = 40;
        int oldPendingEvents = applicationEventService.getCountPendingEvents();

        for (int i = 0; i < limit; i++) {
            provider.setApplicationEvent(null);
            provider.getApplicationEvent();
        }

        processor.process();

        int pendingEvents = applicationEventService.getCountPendingEvents();

        Assert.assertEquals("Pending events should have been processed", oldPendingEvents + 40 - getMaxEventsToProcess(), pendingEvents);
    }

    @Test
    public void testTriageExecutor() {
        Property slackEnabled = propertyService.findByName(SLACK_ENABLED);
        if (slackEnabled != null) {
            boolean slackWasEnabled = Boolean.valueOf(slackEnabled.getValue());
            slackEnabled.setValue("true");
            propertyService.update(slackEnabled);
            propertyService.valueOf(SLACK_ENABLED, DEFAULT_SLACK_ENABLED);
        }

        int pendingEvents = applicationEventService.getCountPendingEvents();
        triageAgent.containerProcessed(provider.getContainer().getId());

        // Only one should be saved
        triageAgent.containerProcessed(provider.getContainer().getId());
        triageAgent.containerProcessed(provider.getContainer().getId());

        if (slackEnabled != null) {
            slackEnabled.setValue(String.valueOf(slackEnabled));
            propertyService.update(slackEnabled);
        }

        Assert.assertEquals(pendingEvents, applicationEventService.getCountPendingEvents());
    }

    @Test
    public void testTriageAgentDone() {
        List<Build> builds = new ArrayList<>(3);
        provider.setName("TriageAgent");
        
        builds.add(provider.getBuild(1));
        provider.getTestExecutionFail();
        provider.getTestCaseTriage();
        
        provider.clearForNewBuild();
        provider.setExecutor(null);

        builds.add(provider.getBuild(1));
        provider.getTestExecutionFail();
        provider.getTestCaseTriage();

        provider.clearForNewBuild();
        provider.setExecutor(null);
        provider.setContainer(null);

        builds.add(provider.getBuild(1));
        provider.getTestExecutionFail();
        provider.getTestCaseTriage();


        triageAgent.triageAgentExecuted(builds);
    }

    @Test
    public void testUnknownEventsNotSaved() {
        int pendingEvents = applicationEventService.getCountPendingEvents();
        applicationEventBuilder.unknownEvent();

        Assert.assertEquals(pendingEvents, applicationEventService.getCountPendingEvents());
    }

    @Test
    public void testProcessingOrder() {
        // Creates a very old event
        provider.setTimestamp(DataProvider.getTimeAdd(-40));
        ApplicationEvent eventOld = provider.getApplicationEvent();
        eventOld.setDisplayName(DataProvider.getRandomName("The Oldest "));
        applicationEventService.update(eventOld);

        // Create a future event
        provider.setApplicationEvent(null);
        provider.setTimestamp(DataProvider.getTimeAdd(5));
        ApplicationEvent eventNewer = provider.getApplicationEvent();
        eventNewer.setDisplayName(DataProvider.getRandomName("The Newest "));
        applicationEventService.update(eventNewer);

        ApplicationEvent firstEvent = applicationEventService.getNextPendingEvent();
        processor.process(firstEvent);
        ApplicationEvent lastEvent = firstEvent;
        ApplicationEvent nextEvent = applicationEventService.getNextPendingEvent();
        while (nextEvent != null) {
            lastEvent = nextEvent;
            processor.process(nextEvent);
            nextEvent = applicationEventService.getNextPendingEvent();
        }

        Assert.assertEquals("The sort accurate, the oldest first", eventOld, firstEvent);
        Assert.assertEquals("The sort accurate, the newest last", eventNewer, lastEvent);
    }

    @Test
    public void testCleanupEvents() {
        int cleanupAmount = 10;
        int newerAmount = 10;
        int oldPendingEvents = applicationEventService.getCountPendingEvents();
        ApplicationEvent eventOld = null;


        for (int i = 0; i < newerAmount; i++) {
            provider.setApplicationEvent(null);
            provider.setTimestamp(DataProvider.getTimeAdd(-1 * i));
            provider.setName("Keep");
            eventOld = provider.getApplicationEvent();
            applicationEventService.update(eventOld);
        }

        for (int i = 0; i < cleanupAmount; i++) {
            provider.setApplicationEvent(null);
            provider.setTimestamp(DataProvider.getTimeAdd(-1 * (40 + i)));
            provider.setName("Delete");
            eventOld = provider.getApplicationEvent();
            applicationEventService.update(eventOld);
        }

        cleanupEventsEventHandler.cleanupOneByOne(DateUtils.now());

        int newPendingEvents = applicationEventService.getCountPendingEvents();

        Assert.assertEquals("Old Events were not properly deleted", 0, newPendingEvents);
    }

    @Test
    public void testCleanupTestTriages() {

        provider.getTestExecutionFail();
        provider.getBuildTriage();
        
        provider.clear();
        provider.getTestExecutionFail();
        provider.getAutomatedTestIssue();

        provider.clear();
        provider.getTestExecutionFail();
        provider.getIssueTicket();
        provider.getBuildTriage();

        cleanupEventsEventHandler.cleanupOneByOne(DateUtils.now());

        long countTriages = testTriageService.count();

        Assert.assertEquals("Test Triages were not properly deleted", 1, countTriages);
    }

    @Test
    public void testCleanupRepositoryLog() {
        int logsAmount = 15;
        int logsOffSet = 0;
        int cleanupOffSet = 8;

        // Create some CSVLog
        for (int i=logsOffSet; i<logsAmount+logsOffSet; i++) {
            CVSLog log = new CVSLog();
            logService.save(log);

            log.setTimestamp(DateUtils.offSetDays(-i));
            logService.update(log);
        }

        cleanupEventsEventHandler.cleanupOneByOne(DateUtils.offSetDays(-cleanupOffSet));

        // Query to see how much now there are long countTriages = testTriageService.count();
        long countLogs = logService.count();

        // Verify there are 0 or as many as expected. Assert.assertEquals("Test Triages were not properly deleted", 2, countTriages);
        Assert.assertEquals("CVSLogs were not properly deleted", cleanupOffSet-logsOffSet, countLogs);
    }

    @Test
    public void testCleanupDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, DEFAULT_OLD_EVENTS_TO_DELETE_DAYS);

        long previousCount = testTriageService.count();

        cleanupEventsEventHandler.cleanupOneByOne(cal.getTime().getTime());

        // Query to see how much now there are long countTriages = testTriageService.count();
        long currentCount = testTriageService.count();

        // Verify there are 0 or as many as expected. Assert.assertEquals("Test Triages were not properly deleted", 2, countTriages);

        Assert.assertTrue("The amount of test triages didnt decrease", previousCount == currentCount);
    }

    @Test
    public void testAnalyticsDailyUserStat() {
        User user = provider.getUser();
        long date = DateUtils.now();

        int logsAmount = 10;
        int changeIndex = 5;

        for (int x=0; x<logsAmount; x++) {
            if (x < changeIndex)
                provider.setTestExecution(null);
            CVSLog log = CVSLog.builder()
                    .author(user)
                    .authorRealname(user.getRealname())
                    .authorText(user.getUsername())
                    .test(provider.getTestCase())
                    .updatedLines((int) DataProvider.getRandomNumber(0, 2))
                    .commitHash(DataProvider.getRandomHash())
                    .commitDate(date)
                    .build();
            logService.save(log);
        }

        clearProvider();

        user = provider.getUser();
        for (int x=0; x<logsAmount; x++) {
            CVSLog log = CVSLog.builder()
                    .author(user)
                    .authorRealname(user.getRealname())
                    .authorText(user.getUsername())
                    .test(provider.getTestCase())
                    .commitHash(x < changeIndex ? "b236b7d6" : DataProvider.getRandomHash())
                    .commitDate(x < changeIndex ? date : DateUtils.offSetDays(-2))
                    .updatedLines((int) DataProvider.getRandomNumber(0, 2))
                    .build();
            logService.save(log);
        }

        ApplicationEvent event = provider.getApplicationEvent();
        event.setType(ApplicationEventType.TIME_NEW_DAY);
        applicationEventService.update(event);
        analyticsDailyEventHandler.process(event);

        List<UserStat> userStats = userStatService.findAll();

        Assert.assertNotEquals(null, userStats);
        Assert.assertEquals(false, userStats.isEmpty());
        Assert.assertEquals((Integer) 1, userStats.get(1).getCommits());
        Assert.assertEquals((Integer) 5, userStats.get(0).getTestsUpdated());
    }

    @Test
    public void createDailyEvent() {
        ApplicationEvent event = provider.getApplicationEvent();
        event.setType(ApplicationEventType.TIME_NEW_DAY);
        applicationEventService.update(event);

        eventTimeAgent.createDailyEvent();
    }

    @Test
    public void createMonthlyEvent() {
        ApplicationEvent event = provider.getApplicationEvent();
        event.setType(ApplicationEventType.TIME_NEW_MONTH);
        applicationEventService.update(event);


        eventTimeAgent.createMonthlyEvent();
    }

    private int getMaxEventsToProcess() {
        return propertyService.valueOf(MAX_EVENTS_TO_PROCESS, DEFAULT_MAX_EVENTS_TO_PROCESS);
    }

    @Test
    public void readCVSRepository(){
        CVSRepository cvsRepository = provider.getCvsRepository();
        cvsRepository.setLastRead(DateUtils.beginDay(-5));
        cvsRepositoryService.update(cvsRepository);

        // Creates an event to process read
        ApplicationEvent event = provider.getApplicationEvent();
        event.setType(ApplicationEventType.TIME_NEW_DAY);
        event = applicationEventService.update(event);
        provider.setApplicationEvent(event);

        readCVSRepositoryEventHandler.process(event);
    }
    

}
