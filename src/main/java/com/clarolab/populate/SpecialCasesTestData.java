/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.populate;

import com.clarolab.model.BuildTriage;
import com.clarolab.model.TestExecution;
import com.clarolab.model.TestTriage;
import com.clarolab.model.types.ApplicationFailType;
import com.clarolab.model.types.StateType;
import com.clarolab.model.types.StatusType;
import com.clarolab.model.types.TestFailType;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.clarolab.model.helper.tag.TagHelper.FLAKY_TRIAGE;
import static com.clarolab.util.Constants.CONSECUTIVE_PASS_COUNT;
import static com.clarolab.util.Constants.DEFAULT_CONSECUTIVE_PASS_COUNT;

@Component
@Log
public class SpecialCasesTestData extends AbstractTestData {

    private TestTriagePopulate sameTest;
    private TestTriagePopulate sameTestWithIssues;

    private String stackTrace1 = "org.awaitility.core.ConditionTimeoutException: Condition with lambda expression in com.lithium.mineraloil.selenium.elements.ElementImpl was not fulfilled within 60 seconds.    at org.awaitility.core.ConditionAwaiter.await(ConditionAwaiter.java:136)    at org.awaitility.core.CallableCondition.await(CallableCondition.java:79)    at org.awaitility.core.CallableCondition.await(CallableCondition.java:27)    at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:856)    at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:814)    at com.lithium.mineraloil.selenium.elements.ElementImpl.waitUntilDisplayed(ElementImpl.java:493)    at com.lithium.mineraloil.selenium.elements.ElementImpl.waitUntilDisplayed(ElementImpl.java:488)    at com.lithium.mineraloil.selenium.elements.BaseElement.waitUntilDisplayed(BaseElement.java:15)    at com.lithium.mineraloil.lia.community.registration.RegisterUser.registerNewUser(RegisterUser.java:51)    at com.lithium.mineraloil.lia.test.gdpr.ui.CloseAccountTest.userReregisterSameUserName(CloseAccountTest.java:160)    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)    at java.lang.reflect.Method.invoke(Method.java:497)    at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:515)    at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:115)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeTestMethod$6(TestMethodTestDescriptor.java:171)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestMethod(TestMethodTestDescriptor.java:167)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:114)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:59)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:105)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at java.util.ArrayList.forEach(ArrayList.java:1249)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:110)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at java.util.ArrayList.forEach(ArrayList.java:1249)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:110)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:32)    at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)    at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:51)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:170)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:154)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:90)    at org.junit.platform.surefire.provider.JUnitPlatformProvider.invokeAllTests(JUnitPlatformProvider.java:155)    at org.junit.platform.surefire.provider.JUnitPlatformProvider.invoke(JUnitPlatformProvider.java:134)    at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:383)    at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:344)    at org.apache.maven.surefire.booter.ForkedBooter.execute(ForkedBooter.java:125)    at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:417)";
    private String errorDetail1 = "org.awaitility.core.ConditionTimeoutException: Condition with lambda expression in com.lithium.mineraloil.selenium.elements.ElementImpl was not fulfilled within 60 seconds.    at org.awaitility.core.ConditionAwaiter.await(ConditionAwaiter.java:136)    at org.awaitility.core.CallableCondition.await(CallableCondition.java:79)    at org.awaitility.core.CallableCondition.await(CallableCondition.java:27)    at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:856)    at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:814)    at com.lithium.mineraloil.selenium.elements.ElementImpl.waitUntilDisplayed(ElementImpl.java:493)    at com.lithium.mineraloil.selenium.elements.ElementImpl.waitUntilDisplayed(ElementImpl.java:488)    at com.lithium.mineraloil.selenium.elements.BaseElement.waitUntilDisplayed(BaseElement.java:15)    at com.lithium.mineraloil.lia.community.registration.RegisterUser.registerNewUser(RegisterUser.java:51)    at com.lithium.mineraloil.lia.test.gdpr.ui.CloseAccountTest.userReregisterSameUserName(CloseAccountTest.java:160)    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)    at java.lang.reflect.Method.invoke(Method.java:497)    at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:515)    at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:115)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeTestMethod$6(TestMethodTestDescriptor.java:171)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestMethod(TestMethodTestDescriptor.java:167)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:114)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:59)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:105)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at java.util.ArrayList.forEach(ArrayList.java:1249)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:110)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at java.util.ArrayList.forEach(ArrayList.java:1249)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:110)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:32)    at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)    at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:51)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:170)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:154)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:90)    at org.junit.platform.surefire.provider.JUnitPlatformProvider.invokeAllTests(JUnitPlatformProvider.java:155)    at org.junit.platform.surefire.provider.JUnitPlatformProvider.invoke(JUnitPlatformProvider.java:134)    at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:383)    at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:344)    at org.apache.maven.surefire.booter.ForkedBooter.execute(ForkedBooter.java:125)    at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:417)";
    private String stackTrace2 = "org.awaitility.core.ConditionTimeoutException: Condition WITHOUT lambda expression in com.lithium.mineraloil.selenium.elements.ElementImpl was not fulfilled within 60 seconds.    at org.awaitility.core.ConditionAwaiter.await(ConditionAwaiter.java:136)    at org.awaitility.core.CallableCondition.await(CallableCondition.java:79)    at org.awaitility.core.CallableCondition.await(CallableCondition.java:27)    at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:856)    at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:814)    at com.lithium.mineraloil.selenium.elements.ElementImpl.waitUntilDisplayed(ElementImpl.java:493)    at com.lithium.mineraloil.selenium.elements.ElementImpl.waitUntilDisplayed(ElementImpl.java:488)    at com.lithium.mineraloil.selenium.elements.BaseElement.waitUntilDisplayed(BaseElement.java:15)    at com.lithium.mineraloil.lia.community.registration.RegisterUser.registerNewUser(RegisterUser.java:51)    at com.lithium.mineraloil.lia.test.gdpr.ui.CloseAccountTest.userReregisterSameUserName(CloseAccountTest.java:160)    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)    at java.lang.reflect.Method.invoke(Method.java:497)    at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:515)    at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:115)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeTestMethod$6(TestMethodTestDescriptor.java:171)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestMethod(TestMethodTestDescriptor.java:167)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:114)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:59)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:105)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at java.util.ArrayList.forEach(ArrayList.java:1249)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:110)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at java.util.ArrayList.forEach(ArrayList.java:1249)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:110)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:32)    at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)    at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:51)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:170)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:154)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:90)    at org.junit.platform.surefire.provider.JUnitPlatformProvider.invokeAllTests(JUnitPlatformProvider.java:155)    at org.junit.platform.surefire.provider.JUnitPlatformProvider.invoke(JUnitPlatformProvider.java:134)    at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:383)    at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:344)    at org.apache.maven.surefire.booter.ForkedBooter.execute(ForkedBooter.java:125)    at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:417)";
    private String errorDetail2 = "org.awaitility.core.NullPointerException: Condition with lambda expression in com.lithium.mineraloil.selenium.elements.ElementImpl was not fulfilled within 60 seconds.    at org.awaitility.core.ConditionAwaiter.await(ConditionAwaiter.java:136)    at org.awaitility.core.CallableCondition.await(CallableCondition.java:79)    at org.awaitility.core.CallableCondition.await(CallableCondition.java:27)    at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:856)    at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:814)    at com.lithium.mineraloil.selenium.elements.ElementImpl.waitUntilDisplayed(ElementImpl.java:493)    at com.lithium.mineraloil.selenium.elements.ElementImpl.waitUntilDisplayed(ElementImpl.java:488)    at com.lithium.mineraloil.selenium.elements.BaseElement.waitUntilDisplayed(BaseElement.java:15)    at com.lithium.mineraloil.lia.community.registration.RegisterUser.registerNewUser(RegisterUser.java:51)    at com.lithium.mineraloil.lia.test.gdpr.ui.CloseAccountTest.userReregisterSameUserName(CloseAccountTest.java:160)    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)    at java.lang.reflect.Method.invoke(Method.java:497)    at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:515)    at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:115)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeTestMethod$6(TestMethodTestDescriptor.java:171)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestMethod(TestMethodTestDescriptor.java:167)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:114)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:59)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:105)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at java.util.ArrayList.forEach(ArrayList.java:1249)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:110)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at java.util.ArrayList.forEach(ArrayList.java:1249)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:110)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:32)    at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)    at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:51)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:170)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:154)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:90)    at org.junit.platform.surefire.provider.JUnitPlatformProvider.invokeAllTests(JUnitPlatformProvider.java:155)    at org.junit.platform.surefire.provider.JUnitPlatformProvider.invoke(JUnitPlatformProvider.java:134)    at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:383)    at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:344)    at org.apache.maven.surefire.booter.ForkedBooter.execute(ForkedBooter.java:125)    at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:417)";
    private String errorDifferent = "org.awaitility.core.NullPointerException: Condition WITHOUT lambda expression in com.lithium.mineraloil.selenium.elements.ElementImpl was not fulfilled within 60 seconds.    at org.awaitility.core.ConditionAwaiter.await(ConditionAwaiter.java:136)    at org.awaitility.core.CallableCondition.await(CallableCondition.java:79)    at org.awaitility.core.CallableCondition.await(CallableCondition.java:27)    at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:856)    at org.awaitility.core.ConditionFactory.until(ConditionFactory.java:814)    at com.lithium.mineraloil.selenium.elements.ElementImpl.waitUntilDisplayed(ElementImpl.java:493)    at com.lithium.mineraloil.selenium.elements.ElementImpl.waitUntilDisplayed(ElementImpl.java:488)    at com.lithium.mineraloil.selenium.elements.BaseElement.waitUntilDisplayed(BaseElement.java:15)    at com.lithium.mineraloil.lia.community.registration.RegisterUser.registerNewUser(RegisterUser.java:51)    at com.lithium.mineraloil.lia.test.gdpr.ui.CloseAccountTest.userReregisterSameUserName(CloseAccountTest.java:160)    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)    at java.lang.reflect.Method.invoke(Method.java:497)    at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:515)    at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:115)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeTestMethod$6(TestMethodTestDescriptor.java:171)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestMethod(TestMethodTestDescriptor.java:167)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:114)    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:59)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:105)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at java.util.ArrayList.forEach(ArrayList.java:1249)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:110)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at java.util.ArrayList.forEach(ArrayList.java:1249)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:110)    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:72)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:95)    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:71)    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:32)    at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)    at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:51)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:170)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:154)    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:90)    at org.junit.platform.surefire.provider.JUnitPlatformProvider.invokeAllTests(JUnitPlatformProvider.java:155)    at org.junit.platform.surefire.provider.JUnitPlatformProvider.invoke(JUnitPlatformProvider.java:134)    at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:383)    at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:344)    at org.apache.maven.surefire.booter.ForkedBooter.execute(ForkedBooter.java:125)    at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:417)";
    private String foreignChars = "i18n míñç usernew/míñç üller馬ǎ Yǒu小宝añuet> อดนิยม Николай Юрий Геоий /test6@clarolab.com";

    @Override
    public void populate() {
        clear();
        provider.setName("SpecialTests");
        provider.getContainer();

        initSameTest();

        createChangeContainerCase();
        createI18n();
        automationIssueFixed();
        productIssueFixed();
        createDataProvider();
        createPassingFlaky();
        createRuleDefault();
        createRuleDontFix();
        historyAllStates();
        performanceBuildsToProcess();
        setSameTestWithIssues();
        suiteAutoTriaged();
        previousTriageKanban();
    }

    private void initSameTest() {
        int amount = 50;

        sameTestWithIssues = new TestTriagePopulate();
        sameTestWithIssues.setTestCaseName("TestWithIssues");
        sameTestWithIssues.setAs(StatusType.FAIL, 0, amount + 1);

        sameTest = new TestTriagePopulate();
        sameTest.setTestCaseName("testNameThatIsInOtherSuites");
        Map<Integer, StatusType> buildSpec = new HashMap<>();
        sameTest.setBuildSpec(buildSpec);
        for (int i = 0; i <= amount + 2 ; i++) {
            buildSpec.put(i, StatusType.FAIL);
        }
    }


    private void createPassingFlaky() {
        String prefix = "flakyTest";
        clearNewMethod(prefix);

        int passIterations = propertyService.valueOf(CONSECUTIVE_PASS_COUNT, DEFAULT_CONSECUTIVE_PASS_COUNT);

        TestTriagePopulate test1 = new TestTriagePopulate();
        test1.setTestCaseName(prefix);
        Map<Integer, StatusType> buildSpec1 = new HashMap<>();
        test1.setBuildSpec(buildSpec1);
        test1.setTestCaseName(DataProvider.getRandomName(prefix + "Solid", 1));
        buildSpec1.put(0, StatusType.UNKNOWN); // not used
        buildSpec1.put(1, StatusType.FAIL); // triage product bug
        for (int i = 2; i <= passIterations + 2 ; i++) {
            buildSpec1.put(i, StatusType.PASS); // no triage
        }
        buildSpec1.put(passIterations + 3, StatusType.FAIL);

        TestTriagePopulate test2 = new TestTriagePopulate();
        Map<Integer, StatusType> buildSpec2 = new HashMap<>();
        test2.setBuildSpec(buildSpec2);
        test2.setTestCaseName(DataProvider.getRandomName(prefix + "Fail", 1));
        buildSpec2.put(0, StatusType.UNKNOWN); // not used
        for (int i = 1; i <= passIterations + 3 ; i++) {
            buildSpec2.put(i, StatusType.FAIL); // no triage
        }

        TestTriage triage = null;

        clearBuild();
        provider.getBuild(1);
        provider.getTestExecution(test1);

        provider.setTestExecution(null);
        provider.getTestExecution(test2);

        BuildTriage buildTriage = provider.getBuildTriage();
        List<TestTriage> testsToTriage = testTriageService.find(buildTriage);

        for (TestTriage toTriage: testsToTriage) {
            toTriage.setTags(FLAKY_TRIAGE);
            triage(toTriage);
        }

        for (int i = 2; i <= passIterations + 3; i++) {
            clearBuild();
            provider.getBuild(i);
            provider.getTestExecution(test1);
            provider.setTestExecution(null);
            provider.getTestExecution(test2);

            provider.getBuildTriage();
        }
    }

    private void createDataProvider() {
        String prefix = "createDataProvider";
        clearNewMethod(prefix);

        TestTriagePopulate test = new TestTriagePopulate();
        test.setTestCaseName("createDocumentWith");


        provider.getBuild(1);
        TestExecution testExecution = provider.getTestExecution(test);
        testExecution.getTestCase().setDataProvider(true);
        testCaseService.update(testExecution.getTestCase());

        provider.setTestExecution(null);
        provider.getTestExecution(test);

        provider.getBuildTriage();

        clearBuild();
        provider.getBuild(2);
        provider.getTestExecution(test);
        provider.setTestExecution(null);
        provider.getTestExecution(test);

        provider.getBuildTriage();
    }


    private void createI18n() {
        String prefix = foreignChars;
        clearNewMethod(prefix);

        TestTriagePopulate test1 = new TestTriagePopulate();
        test1.setTestCaseName("i18n míñç usernew/míñç üller馬ǎ Yǒu小宝añuet> อดนิยม Николай Юрий Геоий /test6@clarolab.com");

        TestTriagePopulate test2 = new TestTriagePopulate();
        test2.setTestCaseName("Speed test » Chart » Verify User Adoption");


        provider.getBuild(1);
        provider.getTestExecution(test1);
        provider.setTestExecution(null);
        provider.getTestExecution(test2);
        provider.setTestExecution(null);
        provider.getTestExecution(sameTest);
        provider.setTestExecution(null);
        provider.getTestExecution(sameTestWithIssues);

        provider.getBuildTriage();

        clearBuild();
        provider.getBuild(2);
        provider.getTestExecution(test1);
        provider.setTestExecution(null);
        provider.getTestExecution(test2);
        provider.setTestExecution(null);
        provider.getTestExecution(sameTest);
        provider.setTestExecution(null);
        provider.getTestExecution(sameTestWithIssues);

        provider.setName(foreignChars);
        provider.getManualTestCase(10);

        provider.getBuildTriage();
    }

    private void createChangeContainerCase() {
        String prefix = "createChangeContainerCase";
        clearNewMethod(prefix);

        TestTriagePopulate test = new TestTriagePopulate();
        Map<Integer, StatusType> buildSpec = new HashMap<>();
        test.setBuildSpec(buildSpec);
        test.setTestCaseName(DataProvider.getRandomName(prefix , 1));
        test.setErrorDetails("ErrorDetail1");
        test.setErrorStackTrace(stackTrace1);

        buildSpec.put(0, StatusType.UNKNOWN); // not used
        buildSpec.put(1, StatusType.FAIL); // no triage
        buildSpec.put(2, StatusType.FAIL); // triage bug automation
        buildSpec.put(3, StatusType.FAIL); // no triage
        buildSpec.put(4, StatusType.PASS); // no triage
        buildSpec.put(5, StatusType.FAIL); // no triage
        buildSpec.put(6, StatusType.FAIL);

        TestTriage triage = null;

        clearBuild();
        provider.getBuild(1);
        provider.getTestExecution(test);
        triage = provider.getTestCaseTriage();

        clearBuild();
        provider.getBuild(2);
        provider.getTestExecution(test);
        triage = provider.getTestCaseTriage();
        triage.setApplicationFailType(ApplicationFailType.NO_FAIL);
        createTestIssue(triage);
        triage(triage);

        for (int i = 3; i <= 5; i++) {
            clearBuild();
            provider.getBuild(i);
            provider.getTestExecution(test);
            triage = provider.getTestCaseTriage();
        }
        clearBuild();
        test.setErrorStackTrace(errorDifferent);
        test.setErrorDetails(null);
        provider.getBuild(6);
        provider.getTestExecution(test);
        triage = provider.getTestCaseTriage();

    }

    private void automationIssueFixed() {
        String prefix = "automationIssueFixed";
        clearNewMethod(prefix);

        TestTriagePopulate test = new TestTriagePopulate();
        Map<Integer, StatusType> buildSpec = new HashMap<>();
        test.setBuildSpec(buildSpec);
        test.setTestCaseName(DataProvider.getRandomName(prefix , 1));
        test.setErrorDetails("ErrorDetail2");
        test.setErrorStackTrace(stackTrace2);

        buildSpec.put(0, StatusType.UNKNOWN); // not used
        buildSpec.put(1, StatusType.FAIL); // triage bug automation
        for (int i = 2; i <= 40 ; i++) {
            buildSpec.put(i, StatusType.PASS); // no triage
        }
        buildSpec.put(41, StatusType.FAIL);

        TestTriage triage = null;

        clearBuild();
        provider.getBuild(1);
        provider.getTestExecution(test);
        triage = provider.getTestCaseTriage();
        triage.setApplicationFailType(ApplicationFailType.NO_FAIL);
        createTestIssue(triage);
        triage(triage);

        for (int i = 2; i <= 41; i++) {
            clearBuild();
            provider.getBuild(i);
            provider.getTestExecution(test);
            triage = provider.getTestCaseTriage();
        }
    }

    private void productIssueFixed() {
        String prefix = "productIssueFixed";
        clearNewMethod(prefix);

        TestTriagePopulate test = new TestTriagePopulate();
        Map<Integer, StatusType> buildSpec = new HashMap<>();
        test.setBuildSpec(buildSpec);
        test.setTestCaseName(DataProvider.getRandomName(prefix , 1));
        test.setErrorDetails("ErrorDetail3");
        test.setErrorStackTrace(stackTrace1);

        buildSpec.put(0, StatusType.UNKNOWN); // not used
        buildSpec.put(1, StatusType.FAIL); // triage product bug
        for (int i = 2; i <= 3 ; i++) {
            buildSpec.put(i, StatusType.PASS); // no triage
        }
        buildSpec.put(4, StatusType.FAIL);

        TestTriage triage = null;

        clearBuild();
        provider.getBuild(1);
        provider.getTestExecution(test);
        triage = provider.getTestCaseTriage();
        createProductIssue(triage);
        triage.setTestFailType(TestFailType.NO_FAIL);
        createTestIssue(triage);
        triage(triage);

        for (int i = 2; i <= 4; i++) {
            clearBuild();
            provider.getBuild(i);
            provider.getTestExecution(test);
            triage = provider.getTestCaseTriage();
        }
    }

    private void createRuleDefault() {
        String prefix = "createRuleDefault";
        clearNewMethod(prefix);

        TestTriagePopulate test = new TestTriagePopulate();
        Map<Integer, StatusType> buildSpec = new HashMap<>();
        test.setBuildSpec(buildSpec);
        test.setTestCaseName(DataProvider.getRandomName(prefix , 1));
        test.setErrorDetails(prefix);
        test.setErrorStackTrace(stackTrace1);

        buildSpec.put(0, StatusType.UNKNOWN); // not used
        buildSpec.put(1, StatusType.FAIL);
        buildSpec.put(2, StatusType.PASS);
        buildSpec.put(3, StatusType.PASS);
        buildSpec.put(4, StatusType.FAIL);

        TestTriage triage = null;

        for (int i = 1; i <= 4; i++) {
            clearBuild();
            provider.getBuild(i);
            provider.getTestExecution(test);

            provider.setTestExecution(null);
            provider.getTestExecution(sameTest);

            triage = provider.getTestCaseTriage();
        }
    }

    private void createRuleDontFix() {
        String prefix = "createRuleDontFix";
        clearNewMethod(prefix);

        TestTriagePopulate test = new TestTriagePopulate();
        Map<Integer, StatusType> buildSpec = new HashMap<>();
        test.setBuildSpec(buildSpec);
        test.setTestCaseName(DataProvider.getRandomName(prefix , 1));
        test.setErrorDetails(prefix);
        test.setErrorStackTrace(stackTrace1);

        buildSpec.put(0, StatusType.UNKNOWN); // not used
        buildSpec.put(1, StatusType.FAIL); // triage Test: Dont fix yet
        buildSpec.put(2, StatusType.FAIL); // should fall in an automatic triage under the same error

        TestTriage triage = null;

        clearBuild();
        provider.getBuild(1);
        provider.getTestExecution(test);
        triage = provider.getTestCaseTriage();
        triage.setTestFailType(TestFailType.WONT_FILE);
        triage.setApplicationFailType(ApplicationFailType.UNDEFINED);
        triage(triage);

        clearBuild();
        provider.getBuild(2);
        provider.getTestExecution(test);
        triage = provider.getTestCaseTriage();
    }

    private void historyAllStates() {
        int amount = 100;
        String prefix = "historyAllStates";
        clearNewMethod(prefix);

        TestTriagePopulate lotOfTests = new TestTriagePopulate();
        lotOfTests.setTestCaseName(DataProvider.getRandomName(prefix + "Long"));
        for (int i = 0; i < amount ; i++) {
            lotOfTests.setAs(DataProvider.getRandomStatusType(), i, i);
        }
        for (int i = 1; i < amount ; i++) {
            clearBuild();
            provider.getBuild(i);
            provider.getTestExecution(lotOfTests);
            provider.getTestCaseTriage();
        }

        // Test with several data
        TestTriagePopulate test = new TestTriagePopulate();
        Map<Integer, StatusType> buildSpec = new HashMap<>();
        test.setBuildSpec(buildSpec);
        test.setTestCaseName(DataProvider.getRandomName(prefix , 1));
        test.setErrorDetails(prefix);
        test.setErrorStackTrace(stackTrace1);

        buildSpec.put(amount + 0, StatusType.FAIL); // not used
        buildSpec.put(amount + 1, StatusType.PASS); // pass
        buildSpec.put(amount + 2, StatusType.FAIL); // new fail
        buildSpec.put(amount + 3, StatusType.FAIL); // fail
        buildSpec.put(amount + 4, StatusType.FAIL); // permanent
        buildSpec.put(amount + 5, StatusType.PASS); // new pass
        buildSpec.put(amount + 6, StatusType.FAIL); // fail

        TestTriage triage = null;

        clearBuild();
        provider.getBuild(amount + 1);
        provider.getTestExecution(lotOfTests);
        provider.setTestExecution(null);
        provider.getTestExecution(test);
        triage = provider.getTestCaseTriage();

        clearBuild();
        provider.getBuild(amount + 2);
        provider.getTestExecution(lotOfTests);
        provider.setTestExecution(null);
        provider.getTestExecution(test);
        triage = provider.getTestCaseTriage();
        triage.setTestFailType(TestFailType.WONT_FILE);
        triage.setApplicationFailType(ApplicationFailType.UNDEFINED);
        triage.setCurrentState(StateType.NEWFAIL);
        triage(triage);

        clearBuild();
        provider.getBuild(amount + 3);
        provider.getTestExecution(lotOfTests);
        provider.setTestExecution(null);
        provider.getTestExecution(test);
        triage = provider.getTestCaseTriage();
        triage.setCurrentState(StateType.FAIL);
        triage(triage);

        clearBuild();
        provider.getBuild(amount + 4);
        provider.getTestExecution(lotOfTests);
        provider.setTestExecution(null);
        provider.getTestExecution(test);
        triage = provider.getTestCaseTriage();
        triage.setCurrentState(StateType.PERMANENT);
        triage(triage);

        clearBuild();
        provider.getBuild(amount + 5);
        provider.getTestExecution(lotOfTests);
        provider.setTestExecution(null);
        provider.getTestExecution(test);
        triage = provider.getTestCaseTriage();
        triage.setCurrentState(StateType.NEWPASS);
        triage(triage);

        clearBuild();
        provider.getBuild(amount + 6);
        provider.getTestExecution(lotOfTests);
        provider.setTestExecution(null);
        provider.getTestExecution(test);
        triage = provider.getTestCaseTriage();
        triage.setCurrentState(StateType.FAIL);
    }

    private void setSameTestWithIssues() {
        int amount = 1;
        String prefix = "SameTestWithIssues";
        clearNewMethod(prefix);

        TestTriage triage = null;

        clearBuild();
        provider.getBuild(1);
        provider.getTestExecution(sameTestWithIssues);
        triage = provider.getTestCaseTriage();
        createTestIssue(triage);
        triage.setApplicationFailType(ApplicationFailType.UNDEFINED);
        triage(triage);

    }

    public void performanceBuildsToProcess() {
        String prefix = "performanceBuildsToProcess";
        clearNewMethod(prefix);
        int amountTests = 2; // 500
        int amountBuilds = 4;
        int amountExecutors = 5;

        for (int i = 0; i < amountExecutors; i++) {
            provider.setExecutor(null);
            provider.getExecutor();
            for (int j = 0; j < amountBuilds; j++) {
                provider.clearForNewBuild();
                provider.getBuild(j + 1);
                for (int k = 0; k < amountTests; k++) {
                    provider.setTestExecution(null);
                    provider.getTestExecution();
                }
            }
        }
    }

    private void suiteAutoTriaged() {
        int amount = 2;
        String prefix = "suiteAutoTriaged";
        clearNewMethod(prefix);

        TestTriagePopulate test = realDataProvider.getTest();
        test.setAs(StatusType.FAIL, 0, amount);

        TestTriage triage = null;

        clearBuild();
        provider.getBuild(1);
        provider.getTestExecution(test);
        triage = provider.getTestCaseTriage();
        createTestIssue(triage);
        triage(triage);

        clearBuild();
        provider.getBuild(2);
        provider.getTestExecution(test);
        triage = provider.getTestCaseTriage();

        clearBuild();
        provider.setExecutor(null);
        provider.getBuild(1);
        provider.getTestExecution(test);
        provider.setTestExecution(null);
        provider.getTestExecution(StatusType.PASS);

        provider.getBuildTriage();

    }

    private void previousTriageKanban() {
        int amount = 4;
        String prefix = "previousTriageKanban";
        clearNewMethod(prefix);

        TestTriagePopulate test = realDataProvider.getTest();
        test.setAs(StatusType.FAIL, 0, amount);

        TestTriage triage = null;

        clearBuild();
        provider.getBuild(1);
        provider.getTestExecution(test);
        triage = provider.getTestCaseTriage();
        createTestIssue(triage);
        createProductIssue(triage);
        triage.setNote(provider.getNote());
        triage(triage);

        clearBuild();
        test.setErrorStackTrace("test1");
        test.setErrorDetails("test1");
        provider.getBuild(2);
        provider.getTestExecution(test);
        triage = provider.getTestCaseTriage();

        clearBuild();
        provider.getBuild(3);
        provider.getTestExecution(test);
        triage = provider.getTestCaseTriage();

        provider.getBuildTriage();

    }

    private void clearBuild() {
        provider.clearForNewBuild();
    }

    private void clear() {
        provider.clear();
        provider.setName(foreignChars);
    }

}
