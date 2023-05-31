/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.model;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.Container;
import com.clarolab.model.Executor;
import com.clarolab.populate.DataProvider;
import com.clarolab.populate.RealDataProvider;
import com.clarolab.populate.TestTriagePopulate;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.ExecutorService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ExecutorFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private RealDataProvider realDataProvider;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void searchTestInOneExecutor() {
        String prefix = "searchTestInOneExecutor";

        TestTriagePopulate test = realDataProvider.getTest();
        test.setTestCaseName(DataProvider.getRandomName(prefix));

        provider.setName(prefix);
        provider.getExecutor();
        provider.getBuild(1);
        provider.getTestExecution(test);

        provider.getTestCaseTriage();

        List<Executor> executors = executorService.findAllWithTestAndExecutorName(prefix);

        Assert.assertEquals("It should find 1 executor", 1, executors.size());

    }

    @Test
    public void searchTestInSeveralExecutor() {
        String prefix = "searchTestInSeveralExecutor";

        TestTriagePopulate test = realDataProvider.getTest();
        test.setTestCaseName(DataProvider.getRandomName(prefix));

        provider.setName(prefix);
        provider.getExecutor();
        provider.getBuild(1);
        provider.getTestExecution(test);
        provider.getTestCaseTriage();

        provider.setExecutor(null);
        provider.clearForNewBuild();
        provider.getExecutor();
        provider.getBuild(1);
        provider.getTestExecution(test);
        provider.getTestCaseTriage();

        provider.setName("otherother");
        provider.setExecutor(null);
        provider.clearForNewBuild();
        provider.getExecutor();
        provider.getBuild(1);
        provider.getTestExecution();
        provider.getTestCaseTriage();

        List<Executor> executors = executorService.findAllWithTestAndExecutorName(prefix);

        Assert.assertEquals("It should find 2 executor", 2, executors.size());

    }

    @Test
    public void searchTestWithSpaces() {
        String prefix = "search test";

        TestTriagePopulate test = realDataProvider.getTest();
        test.setTestCaseName(DataProvider.getRandomName(prefix));

        provider.getExecutor();
        provider.getBuild(1);
        provider.getTestExecution(test);

        provider.getTestCaseTriage();

        List<Executor> executors = executorService.findAllWithTestAndExecutorName(prefix);

        Assert.assertEquals("It should find 1 executor", 1, executors.size());

    }

    @Test
    public void searchTestInContainer() {
        String prefix = "searchTestInContainer";

        TestTriagePopulate test = realDataProvider.getTest();
        test.setTestCaseName(DataProvider.getRandomName(prefix));

        provider.setName(prefix);
        provider.getExecutor();
        provider.getBuild(1);
        provider.getTestExecution(test);
        provider.getTestCaseTriage();

        Container container = provider.getContainer();

        provider.setName("otherother");
        provider.clear();
        provider.getExecutor();
        provider.getBuild(1);
        provider.getTestExecution();
        provider.getTestCaseTriage();

        List<Executor> executors = executorService.findAllWithTestAndExecutorName(prefix, container);

        Assert.assertEquals("It should find 1 executor", 1, executors.size());

    }


}
