package com.clarolab.connectors.impl.qtest;

import com.clarolab.connectors.impl.AbstractTestCreator;

//import com.clarolab.qtest.client.QTestApiClient;
//import com.clarolab.qtest.client.QTestTestCycleClient;
//import com.clarolab.qtest.entities.QTestTestSuite;

public class QTestTSService extends AbstractTestCreator {

//    private QTestTestCycleClient qTestTestCycleClient;
//
//    @Builder
//    private QTestTSService(QTestApiClient qTestApiClient){
//        qTestTestCycleClient = QTestTestCycleClient.builder().qTestApiClient(qTestApiClient).build();
//    }
//
//    public List<Executor> getAllTestSuiteForCycleLifeAsExecutors(String cycleLifeName) throws ExecutorServiceException {
//        try {
//            return createExecutors(qTestTestCycleClient.findTestCycleCrossAllProjects(cycleLifeName).getTestSuites());
//        } catch (Exception e) {
//            throw new ExecutorServiceException(String.format("[getAllTestSuiteForCycleLifeAsExecutors] : An error occurred trying to get executors for " + cycleLifeName), e);
//        }
//    }
//
//    public Executor getTestSuiteOnCycleLifeAsExecutor(String cycleLifeName, String testSuiteName) throws ExecutorServiceException {
//        try {
//            return createExecutor(qTestTestCycleClient.findTestCycleCrossAllProjects(cycleLifeName).getTestSuites().stream().filter(ts -> ts.getName().equals(testSuiteName)).findFirst().orElse(null));
//        } catch (Exception e) {
//            throw new ExecutorServiceException(String.format("[getAllTestSuiteForCycleLifeAsExecutors] : An error occurred trying to get executors for " + cycleLifeName), e);
//        }
//    }
//
//    private Executor createExecutor(QTestTestSuite testSuite){
//
//        List<TestExecution> tests = Lists.newArrayList();
//        testSuite.getTestCases().forEach(tc -> {
//            TestExecution t = TestExecution.builder()
//                    .status(StatusType.getStatus(tc.getStatus()))
//                    .errorDetails(tc.getFail())
//                    .errorStackTrace(tc.getFullError())
//                    .testCase(createTestCase(tc.getName(), null))
//                    .enabled(true)
//                    .timestamp(DateUtils.now())
//                    .build();
//            tests.add(t);
//        });
//
//        Report report = Report.builder()
//                .failCount(testSuite.getFailedTestCases().size())
//                .passCount(testSuite.getPassedTestCases().size())
//                //.executiondate(DateUtils.convertDate(testSuite.getCreatedDate()))
//                .executiondate(DateUtils.now())
//                .status(StatusType.getStatus(testSuite.getStatus()))
//                .build();
//        report.add(tests);
//
//        Build build = Build.builder()
//                .url(testSuite.getUrl())
//                .displayName(testSuite.getName())
//                .status(StatusType.getStatus(testSuite.getStatus()))
////                .executedDate(DateUtils.convertDate(testSuite.getCreatedDate()))
//                .executedDate(DateUtils.now())
//                .buildId(testSuite.getPid())
//                .enabled(true)
//                .timestamp(DateUtils.now())
//                .build();
//        build.setReport(report);
//
//        Executor executor = Executor.builder()
//                .name(testSuite.getName())
//                .url(testSuite.getUrl())
//                .enabled(true)
//                .timestamp(DateUtils.now())
//                .build();
//        executor.add(build);
//
//        return executor;
//    }
//
//    private List<Executor> createExecutors(List<QTestTestSuite> testSuites){
//        List<Executor> executors = Lists.newArrayList();
//        testSuites.stream().filter(ts -> ts.getName().equals("UI Automation - 2019-09-02 41.8-test-2126")).forEach(ts -> executors.add(createExecutor(ts)));
//        return executors;
//    }
}
