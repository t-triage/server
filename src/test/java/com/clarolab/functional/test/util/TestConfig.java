/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.util;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.types.*;
import com.clarolab.populate.DataProvider;
import com.clarolab.populate.RealDataProvider;
import com.clarolab.populate.TestTriagePopulate;
import com.clarolab.service.ErrorDetailService;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;


@Data
public class TestConfig {

    @Autowired
    private RealDataProvider realDataProvider;

    @Autowired
    private ErrorDetailService errorDetailService;

    StateType newState;  // new test triage result
    int rank;  // new test triage rank

    StatusType currentStatus; // testExecutor
    StatusType previousStatus; // testExecutor
    StateType previousState; // previous test triage
    ErrorType errorDifference;
    ApplicationFailType applicationFailType;
    TestFailType testFailType;
    String tags;
    boolean includeTags = true;
    private DeducedReasonType rule;
    boolean triaged = false;

    String stackTrace1;
    String stackTrace2;
    String errorDetail1;
    String errorDetail2;
    String errorDifferent;

    StatusType firstStatus;
    StateType firstState;
    boolean firstHasTestBug;
    boolean firstHasProductBug;
    ApplicationFailType firstApplicationFailType;
    TestFailType firstTestFailType;

    @Builder
    public TestConfig(RealDataProvider realDataProvider, ErrorDetailService errorDetailService, StateType newState, int rank, StatusType currentStatus, StatusType previousStatus, StateType previousState, ErrorType errorDifference, ApplicationFailType applicationFailType, TestFailType testFailType, String tags, boolean includeTags, DeducedReasonType rule, boolean triaged, String stackTrace1, String stackTrace2, String errorDetail1, String errorDetail2, String errorDifferent, StatusType firstStatus, StateType firstState, boolean firstHasTestBug, boolean firstHasProductBug, ApplicationFailType firstApplicationFailType, TestFailType firstTestFailType) {
        this.realDataProvider = realDataProvider;
        this.errorDetailService = errorDetailService;
        this.newState = newState;
        this.rank = rank;
        this.currentStatus = currentStatus;
        this.previousStatus = previousStatus;
        this.previousState = previousState;
        this.errorDifference = errorDifference;
        this.applicationFailType = applicationFailType;
        this.testFailType = testFailType;
        this.tags = tags;
        this.includeTags = includeTags;
        this.rule = rule;
        this.triaged = triaged;
        this.stackTrace1 = stackTrace1;
        this.stackTrace2 = stackTrace2;
        this.errorDetail1 = errorDetail1;
        this.errorDetail2 = errorDetail2;
        this.errorDifferent = errorDifferent;
        this.firstStatus = firstStatus;
        this.firstState = firstState;
        this.firstHasTestBug = firstHasTestBug;
        this.firstHasProductBug = firstHasProductBug;
        this.firstApplicationFailType = firstApplicationFailType;
        this.firstTestFailType = firstTestFailType;

        initializeErrors();
    }


    public TestTriagePopulate getNewTestPopulate() {
        TestTriagePopulate newTest = new TestTriagePopulate();

        if (errorDifference != null && !errorDifference.equals(ErrorType.NO_ERROR) && !errorDifference.equals(ErrorType.UNDEFINED)) {
            newTest.setErrorDetails(errorDetail1);
            newTest.setErrorStackTrace(stackTrace1);
        } else {
            newTest.setErrorDetails(null);
            newTest.setErrorStackTrace(null);
        }
        newTest.setTestCaseName(DataProvider.getRandomName("state", 6));
        newTest.getBuildSpec().put(1, previousStatus);
        newTest.getBuildSpec().put(2, currentStatus);

        return newTest;
    }

    public TestTriagePopulate updateTestPopulate(TestTriagePopulate theTest) {

        // Update Test Error Populate
        String errorDetail = errorDetail2;
        String stackTrace = stackTrace2;
        switch (errorDifference) {
            case EQUAL:
                break;
            case NO_ERROR:
                theTest.setErrorDetails(null);
                theTest.setErrorStackTrace(null);
                break;
            case NOT_EQUAL:
                theTest.setErrorDetails(errorDifferent);
                theTest.setErrorStackTrace(errorDifferent);
                break;
            case VERY_SIMILAR:
                theTest.setErrorDetails(stackTrace);
                theTest.setErrorStackTrace(stackTrace);
                break;
            case SIMILAR:
                theTest.setErrorDetails(errorDetail);
                theTest.setErrorStackTrace(errorDetail);
                break;
            case UNDEFINED:
                theTest.setErrorDetails(null);
                theTest.setErrorStackTrace(null);
                break;
        }

        return theTest;
    }

    private String stackTrace(int i) {
        return realDataProvider.getTest(i).getErrorStackTrace();
    }

    private String errorDetail(int i) {
        return realDataProvider.getTest(i).getErrorDetails();
    }

    private TestTriagePopulate newTestWithError() {
        TestTriagePopulate error = null;
        TestTriagePopulate newError;

        while (error == null) {
            newError = realDataProvider.getTest();
            if (errorDetailService.getStackTraces(StatusType.FAIL, newError.getErrorDetails()).size() > 0) {
                error = newError;
            }
        }

        return error;
    }

    private void initializeErrors() {
        stackTrace1 = "org.awaitility.core.ConditionTimeoutException: Condition with lambda expression in com.lithium.mineraloil.selenium.elements.ElementImpl was not fulfilled within 60 seconds.    at org.awaitility.core.ConditionAwaiter.await(ConditionAwaiter.java:136)    at org.awaitility.core.CallableCondition.await(CallableCondition.java:79)    at org.awaitility.core.CallableCondition.await(CallableCondition.java:27)    at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:856)    at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:814)    at com.lithium.mineraloil.selenium.elements.ElementImpl.waitUntilDisplayed(ElementImpl.java:493)    at com.lithium.mineraloil.selenium.elements.ElementImpl.waitUntilDisplayed(ElementImpl.java:488)    at com.lithium.mineraloil.selenium.elements.BaseElement.waitUntilDisplayed(BaseElement.java:15)    at com.lithium.mineraloil.lia.community.registration.RegisterUser.registerNewUser(RegisterUser.java:51)    at com.lithium.mineraloil.lia.test.gdpr.ui.CloseAccountTest.userReregisterSameUserName(CloseAccountTest.java:160)    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)    at java.lang.reflect.Method.invoke(Method.java:497)    at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:515)    at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:115)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeTestMethod$6(TestMethodTestDescriptor.java:171)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestMethod(TestMethodTestDescriptor.java:167)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:114)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:59)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:105)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at java.util.ArrayList.forEach(ArrayList.java:1249)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:110)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at java.util.ArrayList.forEach(ArrayList.java:1249)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:110)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:32)    at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)    at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:51)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:170)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:154)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:90)    at org.junit.platform.surefire.provider.JUnitPlatformProvider.invokeAllTests(JUnitPlatformProvider.java:155)    at org.junit.platform.surefire.provider.JUnitPlatformProvider.invoke(JUnitPlatformProvider.java:134)    at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:383)    at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:344)    at org.apache.maven.surefire.booter.ForkedBooter.execute(ForkedBooter.java:125)    at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:417)";
        errorDetail1 = "org.awaitility.core.ConditionTimeoutException: Condition with lambda expression in com.lithium.mineraloil.selenium.elements.ElementImpl was not fulfilled within 60 seconds.    at org.awaitility.core.ConditionAwaiter.await(ConditionAwaiter.java:136)    at org.awaitility.core.CallableCondition.await(CallableCondition.java:79)    at org.awaitility.core.CallableCondition.await(CallableCondition.java:27)    at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:856)    at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:814)    at com.lithium.mineraloil.selenium.elements.ElementImpl.waitUntilDisplayed(ElementImpl.java:493)    at com.lithium.mineraloil.selenium.elements.ElementImpl.waitUntilDisplayed(ElementImpl.java:488)    at com.lithium.mineraloil.selenium.elements.BaseElement.waitUntilDisplayed(BaseElement.java:15)    at com.lithium.mineraloil.lia.community.registration.RegisterUser.registerNewUser(RegisterUser.java:51)    at com.lithium.mineraloil.lia.test.gdpr.ui.CloseAccountTest.userReregisterSameUserName(CloseAccountTest.java:160)    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)    at java.lang.reflect.Method.invoke(Method.java:497)    at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:515)    at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:115)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeTestMethod$6(TestMethodTestDescriptor.java:171)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestMethod(TestMethodTestDescriptor.java:167)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:114)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:59)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:105)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at java.util.ArrayList.forEach(ArrayList.java:1249)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:110)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at java.util.ArrayList.forEach(ArrayList.java:1249)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:110)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:32)    at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)    at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:51)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:170)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:154)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:90)    at org.junit.platform.surefire.provider.JUnitPlatformProvider.invokeAllTests(JUnitPlatformProvider.java:155)    at org.junit.platform.surefire.provider.JUnitPlatformProvider.invoke(JUnitPlatformProvider.java:134)    at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:383)    at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:344)    at org.apache.maven.surefire.booter.ForkedBooter.execute(ForkedBooter.java:125)    at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:417)";
        stackTrace2 = "org.awaitility.core.ConditionTimeoutException: Condition WITHOUT lambda expression in com.lithium.mineraloil.selenium.elements.ElementImpl was not fulfilled within 60 seconds.    at org.awaitility.core.ConditionAwaiter.await(ConditionAwaiter.java:136)    at org.awaitility.core.CallableCondition.await(CallableCondition.java:79)    at org.awaitility.core.CallableCondition.await(CallableCondition.java:27)    at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:856)    at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:814)    at com.lithium.mineraloil.selenium.elements.ElementImpl.waitUntilDisplayed(ElementImpl.java:493)    at com.lithium.mineraloil.selenium.elements.ElementImpl.waitUntilDisplayed(ElementImpl.java:488)    at com.lithium.mineraloil.selenium.elements.BaseElement.waitUntilDisplayed(BaseElement.java:15)    at com.lithium.mineraloil.lia.community.registration.RegisterUser.registerNewUser(RegisterUser.java:51)    at com.lithium.mineraloil.lia.test.gdpr.ui.CloseAccountTest.userReregisterSameUserName(CloseAccountTest.java:160)    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)    at java.lang.reflect.Method.invoke(Method.java:497)    at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:515)    at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:115)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeTestMethod$6(TestMethodTestDescriptor.java:171)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestMethod(TestMethodTestDescriptor.java:167)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:114)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:59)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:105)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at java.util.ArrayList.forEach(ArrayList.java:1249)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:110)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at java.util.ArrayList.forEach(ArrayList.java:1249)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:110)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:32)    at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)    at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:51)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:170)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:154)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:90)    at org.junit.platform.surefire.provider.JUnitPlatformProvider.invokeAllTests(JUnitPlatformProvider.java:155)    at org.junit.platform.surefire.provider.JUnitPlatformProvider.invoke(JUnitPlatformProvider.java:134)    at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:383)    at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:344)    at org.apache.maven.surefire.booter.ForkedBooter.execute(ForkedBooter.java:125)    at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:417)";
        errorDetail2 = "org.awaitility.core.NullPointerException: Condition with lambda expression in com.lithium.mineraloil.selenium.elements.ElementImpl was not fulfilled within 60 seconds.    at org.awaitility.core.ConditionAwaiter.await(ConditionAwaiter.java:136)    at org.awaitility.core.CallableCondition.await(CallableCondition.java:79)    at org.awaitility.core.CallableCondition.await(CallableCondition.java:27)    at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:856)    at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:814)    at com.lithium.mineraloil.selenium.elements.ElementImpl.waitUntilDisplayed(ElementImpl.java:493)    at com.lithium.mineraloil.selenium.elements.ElementImpl.waitUntilDisplayed(ElementImpl.java:488)    at com.lithium.mineraloil.selenium.elements.BaseElement.waitUntilDisplayed(BaseElement.java:15)    at com.lithium.mineraloil.lia.community.registration.RegisterUser.registerNewUser(RegisterUser.java:51)    at com.lithium.mineraloil.lia.test.gdpr.ui.CloseAccountTest.userReregisterSameUserName(CloseAccountTest.java:160)    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)    at java.lang.reflect.Method.invoke(Method.java:497)    at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:515)    at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:115)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeTestMethod$6(TestMethodTestDescriptor.java:171)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestMethod(TestMethodTestDescriptor.java:167)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:114)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:59)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:105)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at java.util.ArrayList.forEach(ArrayList.java:1249)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:110)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at java.util.ArrayList.forEach(ArrayList.java:1249)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:110)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:32)    at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)    at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:51)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:170)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:154)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:90)    at org.junit.platform.surefire.provider.JUnitPlatformProvider.invokeAllTests(JUnitPlatformProvider.java:155)    at org.junit.platform.surefire.provider.JUnitPlatformProvider.invoke(JUnitPlatformProvider.java:134)    at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:383)    at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:344)    at org.apache.maven.surefire.booter.ForkedBooter.execute(ForkedBooter.java:125)    at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:417)";
        errorDifferent = "org.awaitility.core.NullPointerException: Condition WITHOUT lambda expression in com.lithium.mineraloil.selenium.elements.ElementImpl was not fulfilled within 60 seconds.    at org.awaitility.core.ConditionAwaiter.await(ConditionAwaiter.java:136)    at org.awaitility.core.CallableCondition.await(CallableCondition.java:79)    at org.awaitility.core.CallableCondition.await(CallableCondition.java:27)    at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:856)    at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:814)    at com.lithium.mineraloil.selenium.elements.ElementImpl.waitUntilDisplayed(ElementImpl.java:493)    at com.lithium.mineraloil.selenium.elements.ElementImpl.waitUntilDisplayed(ElementImpl.java:488)    at com.lithium.mineraloil.selenium.elements.BaseElement.waitUntilDisplayed(BaseElement.java:15)    at com.lithium.mineraloil.lia.community.registration.RegisterUser.registerNewUser(RegisterUser.java:51)    at com.lithium.mineraloil.lia.test.gdpr.ui.CloseAccountTest.userReregisterSameUserName(CloseAccountTest.java:160)    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)    at java.lang.reflect.Method.invoke(Method.java:497)    at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:515)    at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:115)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeTestMethod$6(TestMethodTestDescriptor.java:171)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestMethod(TestMethodTestDescriptor.java:167)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:114)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:59)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:105)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at java.util.ArrayList.forEach(ArrayList.java:1249)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:110)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at java.util.ArrayList.forEach(ArrayList.java:1249)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:110)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:32)    at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)    at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:51)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:170)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:154)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:90)    at org.junit.platform.surefire.provider.JUnitPlatformProvider.invokeAllTests(JUnitPlatformProvider.java:155)    at org.junit.platform.surefire.provider.JUnitPlatformProvider.invoke(JUnitPlatformProvider.java:134)    at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:383)    at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:344)    at org.apache.maven.surefire.booter.ForkedBooter.execute(ForkedBooter.java:125)    at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:417)";

    }

}
