/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.Executor;
import com.clarolab.model.TestCase;
import com.clarolab.model.TestTriage;
import com.clarolab.model.User;
import com.clarolab.populate.RealDataProvider;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.StatsService;
import com.clarolab.service.TestExecutionService;
import com.clarolab.service.TestTriageService;
import com.clarolab.view.GroupedStatView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.List;

public class HistoricDataFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private RealDataProvider realDataProvider;

    @Autowired
    private TestExecutionService testExecutionService;

    @Autowired
    private StatsService statsService;

    @Autowired
    private TestTriageService testTriageService;

    private long amount = 10;


    @Test
    public void historicNoFailOneNewTest() {
        provider.getTestExecutionFail();
        provider.getBuildTriage();

        List<GroupedStatView> globalBurndownFailNewFixes = statsService.getGlobalBurndownFailNewFixes();

        long fails = 0;
        long tests = 0;
        long fixes = 0;

        for(GroupedStatView gsv : globalBurndownFailNewFixes){
            fails += gsv.getFails();
            tests += gsv.getNewTests();
            fixes += gsv.getNewFixes();
        }

        Assert.assertEquals(0, fails);
        Assert.assertEquals(1, tests);
        Assert.assertEquals(0, fixes);
    }

    @Test
    public void historicOneFailOneNewTest() {
        provider.getTestExecutionFail();
        provider.getBuildTriage();

        markAllAsTriaged(provider.getExecutor(), provider.getTestCase());

        List<GroupedStatView> globalBurndownFailNewFixes = statsService.getGlobalBurndownFailNewFixes();

        long fails = 0;
        long tests = 0;
        long fixes = 0;

        for(GroupedStatView gsv : globalBurndownFailNewFixes){
            fails += gsv.getFails();
            tests += gsv.getNewTests();
            fixes += gsv.getNewFixes();
        }

        Assert.assertEquals(1, fails);
        Assert.assertEquals(1, tests);
        Assert.assertEquals(0, fixes);
    }

    @Test
    public void historicTenFailsTemNewTests() {

        Executor executor = provider.getExecutor();
        for(int x=1; x < amount+1 ; x++) {
            provider.clear();
            provider.setExecutor(executor);
            provider.getBuild(x);
            provider.getTestExecutionFail();
            provider.getAutomatedTestIssue();
            provider.getBuildTriage();
            markAllAsTriaged(executor, provider.getTestCase());
        }

        List<GroupedStatView> globalBurndownFailNewFixes = statsService.getGlobalBurndownFailNewFixes();

        long fails = 0;
        long tests = 0;
        long fixes = 0;

        for(GroupedStatView gsv : globalBurndownFailNewFixes){
            fails += gsv.getFails();
            tests += gsv.getNewTests();
            fixes += gsv.getNewFixes();
        }

        Assert.assertEquals(amount, fails);
        Assert.assertEquals(amount, tests);
        Assert.assertEquals(0, fixes);
    }

    @Test
    public void historicAMonthAgoOneFailOneNewTest() {
        provider.getTestExecutionFail();
        provider.getBuildTriage();

        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MONTH, -2);

        markAllAsTriaged(provider.getExecutor(), provider.getTestCase(), instance.getTimeInMillis());

        List<GroupedStatView> globalBurndownFailNewFixes = statsService.getGlobalBurndownFailNewFixes();

        GroupedStatView groupedStatView = globalBurndownFailNewFixes.get(1);
        Assert.assertEquals(1, groupedStatView.getFails());
    }

    @Test
    public void historicAMonthAgoTenFailOneNewTest() {
        Executor executor = provider.getExecutor();

        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MONTH, -2);

        for(int x=1; x < amount+1 ; x++) {
            provider.clear();
            provider.setExecutor(executor);
            provider.getBuild(x);
            provider.getTestExecutionFail();
            provider.getBuildTriage();
            markAllAsTriaged(executor, provider.getTestCase(), instance.getTimeInMillis());
        }


        List<GroupedStatView> globalBurndownFailNewFixes = statsService.getGlobalBurndownFailNewFixes();

        GroupedStatView groupedStatView = globalBurndownFailNewFixes.get(1);
        Assert.assertEquals(10, groupedStatView.getFails());
    }

    private void markAllAsTriaged(Executor executor, TestCase testCase) {
        markAllAsTriaged(executor, testCase, System.currentTimeMillis());
    }

    private void markAllAsTriaged(Executor executor, TestCase testCase, long milliseconds) {
        User user = provider.getUser();

        List<TestTriage> all = testTriageService.findAll(executor, testCase);
        for(TestTriage tt :all){
            testTriageService.markTestAsTriaged(user, tt.getId());
            tt.setTimestamp(milliseconds);
            testTriageService.update(tt);
        }
    }

    @Before
    public void clearProvider(){
        provider.clear();
    }
}
