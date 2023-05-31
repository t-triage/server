/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test;

import com.clarolab.agents.errors.FailErrorProcessor;
import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.TestExecution;
import com.clarolab.model.TestTriage;
import com.clarolab.model.helper.exparser.StackTrace;
import com.clarolab.model.types.ErrorType;
import com.clarolab.model.types.StatusType;
import com.clarolab.populate.RealDataProvider;
import com.clarolab.populate.TestTriagePopulate;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.ErrorDetailService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ErrorLogsFunctionalTest extends BaseFunctionalTest {

    @Autowired
    RealDataProvider realDataProvider;

    @Autowired
    ErrorDetailService errorDetailService;

    @Autowired
    FailErrorProcessor failErrorProcessor;

    @Autowired
    private UseCaseDataProvider provider;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void errorNotEqualTest() {

        TestTriagePopulate test = realDataProvider.getTest();
        String errorStackTrace = test.getErrorStackTrace();
        String details = test.getErrorDetails();

        Assert.assertNotNull(errorStackTrace);
        Assert.assertNotNull(details);

        ErrorType errorType = failErrorProcessor.process(errorStackTrace, details, "", "");
        Assert.assertEquals(ErrorType.NOT_EQUAL, errorType);

    }

    @Test
    public void errorEqualTest() {

        TestTriagePopulate test = realDataProvider.getTest();
        String errorStackTrace = test.getErrorStackTrace();
        String details = test.getErrorDetails();

        Assert.assertNotNull(errorStackTrace);
        Assert.assertNotNull(details);

        ErrorType errorType = failErrorProcessor.process(errorStackTrace, details, errorStackTrace, details);
        Assert.assertEquals(ErrorType.EQUAL, errorType);

    }

    @Test
    public void errorVerySimilarTest() {

        TestTriagePopulate test = realDataProvider.getTest();
        String errorStackTrace = test.getErrorStackTrace();
        String details = test.getErrorDetails();

        Assert.assertNotNull(errorStackTrace);
        Assert.assertNotNull(details);

        ErrorType errorType = failErrorProcessor.process(errorStackTrace, details, "", details);
        Assert.assertEquals(ErrorType.VERY_SIMILAR, errorType);

    }

    @Test
    public void errorSimilarTest() {

        TestTriagePopulate test = realDataProvider.getTest();
        String errorStackTrace = test.getErrorStackTrace();
        String details = test.getErrorDetails();

        Assert.assertNotNull(errorStackTrace);
        Assert.assertNotNull(details);

        ErrorType errorType = failErrorProcessor.process(errorStackTrace, details, errorStackTrace, "");
        Assert.assertEquals( ErrorType.SIMILAR, errorType);

    }

    @Test
    public void errorNotEquealNoStackTest() {

        TestTriagePopulate test = realDataProvider.getTest();
        String errorStackTrace = test.getErrorStackTrace();
        String details = test.getErrorDetails();

        Assert.assertNotNull(errorStackTrace);
        Assert.assertNotNull(details);

        ErrorType errorType = failErrorProcessor.process(null, null, null, null);
        Assert.assertEquals(ErrorType.NOT_EQUAL, errorType);

    }

    @Test
    public void errorParseAllExceptionTypesTest() {

        ErrorType errorType = failErrorProcessor.process("Detail", "SomeNameException", "", "");
        Assert.assertNotEquals(errorType, ErrorType.UNDEFINED);
        Assert.assertEquals(ErrorType.NOT_EQUAL, errorType);

        errorType = failErrorProcessor.process("Detail", "SomeNameError", "", "");
        Assert.assertNotEquals(errorType, ErrorType.UNDEFINED);
        Assert.assertEquals(ErrorType.NOT_EQUAL, errorType);

        errorType = failErrorProcessor.process("Detail", "SomeNameFailure", "", "");
        Assert.assertNotEquals(errorType, ErrorType.UNDEFINED);
        Assert.assertEquals(ErrorType.NOT_EQUAL, errorType);
    }

    @Test
    public void errorNotEquealNoStackFormatTest() {
        List<StackTrace> stackTracesOne = failErrorProcessor.analyzeStackTrace("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. ");
        List<StackTrace> stackTracesTwo = failErrorProcessor.analyzeStackTrace("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. ");

        Assert.assertTrue(stackTracesOne.isEmpty());
        Assert.assertTrue(stackTracesTwo.isEmpty());

    }

    @Test
    public void errorTestExecutionNullServiceTest() {
        TestTriagePopulate test = realDataProvider.getTest();
        TestTriage testTriage = test.getPreviousTestTriage();

        ErrorType errorType = errorDetailService.processErrorStack(StatusType.FAIL, null, testTriage);
        Assert.assertEquals(ErrorType.NOT_EQUAL, errorType);

    }

    @Test
    public void errorTestExecutionNotNullAndEqualsServiceTest() {
        String sufix = "123123";

        TestTriagePopulate testSample = realDataProvider.getTest();
        testSample.setTestCaseName("Test" + sufix);
        testSample.setSuiteName("Suite" + sufix);
        testSample.setPath("Path" + sufix);

        provider.setName("SameTest1");
        provider.getBuild(1);

        TestExecution testExecution = provider.getTestExecution(testSample);
        TestTriage testTriage = provider.getTestCaseTriage();

        ErrorType errorType = errorDetailService.processErrorStack(StatusType.FAIL, testExecution, testTriage);
        Assert.assertEquals(ErrorType.EQUAL, errorType);

    }

    @Ignore //Test is not valid since there are empty examples
    public void errorStackParseTests() {
        List<TestTriagePopulate> tests = realDataProvider.getAllTests();

        for(TestTriagePopulate testTriagePopulate : tests){
            String errorStackTrace = testTriagePopulate.getErrorStackTrace();
            List<StackTrace> stackTraces = failErrorProcessor.analyzeStackTrace(errorStackTrace);
            Assert.assertFalse(String.format("Couldn't interpret the stacktrace %s", testTriagePopulate.getErrorStackTrace()), stackTraces.isEmpty());
        }
    }

    @Ignore //Test is not valid since there are stack traces in detail errors
    public void errorDetailParseTests() {
        List<TestTriagePopulate> tests = realDataProvider.getAllTests();

        for(TestTriagePopulate testTriagePopulate : tests){
            //Detail should be empty since is not a Trace
            String errorDetails = testTriagePopulate.getErrorDetails();
            List<StackTrace> stackDetails = failErrorProcessor.analyzeStackTrace(errorDetails);
            Assert.assertTrue(String.format("Couldn't interpret the error detail %s", testTriagePopulate.getErrorDetails()), stackDetails.isEmpty());
        }
    }

    @Test
    public void compareIgnoreClassIdentificationTextError() {
        String sufix = "ignoreClassId";
        provider.setName(sufix);

        String errorDetail = "java.lang.AssertionError - Expected exception: com.clarolab.service.exception.InvalidDataException";
        String stackTraceBase = "CREATED: com.clarolab.model.User@%s";
        String stackTrace1 = String.format(stackTraceBase,"6abf901");
        String stackTrace2 = String.format(stackTraceBase,"7abf902");

        TestTriagePopulate testSample = realDataProvider.getTest();
        testSample.setErrorDetails(stackTrace1);
        testSample.setErrorStackTrace(stackTrace1);

        provider.getBuild(1);
        provider.getTestExecution(testSample);
        TestTriage previousTriage = provider.getTestCaseTriage();

        // 2nd build with very similar error
        provider.clearForNewBuild();
        provider.getBuild(2);
        testSample.setErrorDetails(stackTrace2);
        testSample.setErrorStackTrace(stackTrace2);
        provider.getTestExecution(testSample);
        TestTriage lastTriage = provider.getTestCaseTriage();

        ErrorType errorType = errorDetailService.processErrorStack(StatusType.FAIL, lastTriage.getTestExecution(), previousTriage);
        Assert.assertEquals("The error is the same except for the object id", ErrorType.EQUAL, errorType);
    }

    @Test
    public void compareIgnoreClassIdentificationStackError() {
        String sufix = "ignoreClassId";
        provider.setName(sufix);

        String errorBase = "java.lang.AssertionError: Condition com.clarolab.model.User@%s.    at org.awaitility.core.ConditionAwaiter.await(ConditionAwaiter.java:136)    at org.awaitility.core.CallableCondition.await(CallableCondition.java:79)";
        String stackTrace1 = String.format(errorBase,"6abf901");
        String stackTrace2 = String.format(errorBase,"7abf902");

        TestTriagePopulate testSample = realDataProvider.getTest();
        testSample.setErrorDetails(stackTrace1);
        testSample.setErrorStackTrace(stackTrace1);

        provider.getBuild(1);
        provider.getTestExecution(testSample);
        TestTriage previousTriage = provider.getTestCaseTriage();

        // 2nd build with very similar error
        provider.clearForNewBuild();
        provider.getBuild(2);
        testSample.setErrorDetails(stackTrace2);
        testSample.setErrorStackTrace(stackTrace2);
        provider.getTestExecution(testSample);
        TestTriage lastTriage = provider.getTestCaseTriage();

        ErrorType errorType = errorDetailService.processErrorStack(StatusType.FAIL, lastTriage.getTestExecution(), previousTriage);
        Assert.assertEquals("The error is the same except for the object id", ErrorType.EQUAL, errorType);
    }

    private String stackTrace(int i) {
        return realDataProvider.getTest(i).getErrorStackTrace();
    }

    private String errorDetail(int i) {
        return realDataProvider.getTest(i).getErrorDetails();
    }

}
