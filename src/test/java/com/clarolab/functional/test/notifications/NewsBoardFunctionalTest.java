/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.notifications;

import com.clarolab.event.productivity.NewsBoardEventHandler;
import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.ApplicationEvent;
import com.clarolab.model.AutomatedTestIssue;
import com.clarolab.model.NewsBoard;
import com.clarolab.model.types.StatusType;
import com.clarolab.populate.DataProvider;
import com.clarolab.populate.RealDataProvider;
import com.clarolab.populate.TestTriagePopulate;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.ApplicationEventService;
import com.clarolab.service.NewsBoardService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class NewsBoardFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private NewsBoardService newsBoardService;

    @Autowired
    private NewsBoardEventHandler newsBoardEventHandler;

    @Autowired
    private ApplicationEventService applicationEventService;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void persistence() {
        provider.getNewsBoard(null);
        Assert.assertEquals(1, newsBoardService.findAll().size());
    }

    @Test
    public void test30Passes() {
        int amount = 14;
        RealDataProvider realProvider = new RealDataProvider();
        TestTriagePopulate testSample = realProvider.getTest(); // TestExecution like
        testSample.setTestCaseName(DataProvider.getRandomName("test30Passes"));
        testSample.setAs(StatusType.FAIL, 0, 1);
        testSample.setAs(StatusType.PASS, 2, amount);

        // First Test as Pass
        provider.getBuild(1);
        provider.getTestExecution(testSample);
        provider.getTestCaseTriage();
        AutomatedTestIssue automatedTestIssue = provider.getAutomatedTestIssue();

        for (int i = 2; i <= amount; i++) {
            provider.clearForNewBuild();

            provider.getBuild(i);
            provider.getTestExecution(testSample);

            provider.getBuildTriage();
        }

        ApplicationEvent event = applicationEventService.getLastEvent(automatedTestIssue);

        Assert.assertNotNull("Couldn't find an event for the automation issue", event);

        newsBoardEventHandler.process(event);

        List<NewsBoard> news = newsBoardService.findAll();

        Assert.assertTrue(!news.isEmpty());
    }
}
