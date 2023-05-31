/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.testng.json.entity;

import com.clarolab.connectors.impl.AbstractTestCreator;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.TestExecution;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.DateUtils;
import com.clarolab.util.LogicalCondition;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Data
public class MainTestNG extends AbstractTestCreator {

    private String reporter_output;
    private int total;
    private int passed;
    private int failed;
    private int skipped;
    private TestNGSuite suite;
    private long skippedByAssumption;
    private boolean isForTestingPurpose;
    List<TestExecution> tests = null;

    @Builder
    private MainTestNG(String reporter_output, int total, int passed, int failed, int skipped, TestNGSuite suite, ApplicationContextService context, boolean isForTestingPurpose){
        this.context = context;
        this.suite = suite;
        this.skippedByAssumption = getSkippedByAssumption();
        this.reporter_output = reporter_output;
        this.total = total;
        this.passed = passed - (int) skippedByAssumption;
        this.failed = failed - (int) skippedByAssumption;
        this.skipped = skipped + (int) skippedByAssumption;
        this.isForTestingPurpose = isForTestingPurpose;
    }

    public StatusType getStatus(){
        if(failed > 0) return StatusType.FAIL;
        if(skipped > 0) return StatusType.SKIP;
        if(LogicalCondition.AND(failed == 0, skipped == 0, passed > 0)) return StatusType.PASS;
        return StatusType.UNKNOWN;
    }

    public long getExecutionDate(){
        return this.getSuite() == null ? 0L : DateUtils.convertDate(this.getSuite().getStarted_at());
    }

    public long getDuration(){
        return this.getSuite() == null ? 0L : this.getSuite().getDuration_ms();
    }

    private long getSkippedByAssumption(){
        long skipped = 0;
        if(suite != null)
            for(TestNGSuiteTest clazz: this.getSuite().getTests()) {
                for(TestNGSuiteTestClass clazzz: clazz.getClazz()){
                    skipped += clazzz.getTest_method().stream().filter(testMethod -> testMethod.isSkippedByAssumption()).count();
                }
            };
        return skipped;
    }

    public List<TestExecution> getTests(){
        if(CollectionUtils.isEmpty(tests)){
            tests = Lists.newArrayList();
            if(suite != null)
                this.getSuite().getTests().forEach(test -> {
                    test.getClazz().forEach(clazz -> {
                        clazz.getTest_method().forEach(testMethod -> {
                            //if(!testMethod.is_config()) {
                            TestExecution testExecution = TestExecution.builder()
                                    .testCase(getTestCase(testMethod.getName(), clazz.getName(), isForTestingPurpose))
                                    .duration(testMethod.getDuration_ms())
                                    .status(testMethod.getStatus())
                                    .suiteName(getSuite().getName())
                                    .errorDetails(testMethod.getError())
                                    .errorStackTrace(testMethod.getErrorDetails())
                                    .build();

                            if(testMethod.getStatus().equals(StatusType.SKIP)){
                                TestNGSuiteTestClassTestMethod failConfig = clazz.getFailedConfig();
                                if(failConfig != null){
                                    testExecution.setErrorDetails("There was a config error on '"+ failConfig.getName() +"':\n" + failConfig.getError());
                                    testExecution.setErrorStackTrace(failConfig.getErrorDetails());
                                }
                            }

                            tests.add(testExecution);
                            //}
                        });
                    });
                });
        }
        return tests;
    }

    public void setPassed(int passed){
        this.passed = passed - (int) skippedByAssumption;
    }

    public void setFailed(int failed){
        this.failed = failed - (int) skippedByAssumption;
    }

    public void setSkipped(int skipped){
        this.skipped = skipped + (int) skippedByAssumption;
    }

    public int getTotalFromTests(){
        return getTests().size();
    }

    public int getPassedFromTests(){
        return (int) getTests().stream().filter(test -> test.isPassed()).count();
    }

    public int getFailedFromTests(){
        return (int) getTests().stream().filter(test -> test.isFailed()).count();
    }

    public int getSkippedFromTests(){
        return (int) getTests().stream().filter(test -> test.isSkipped()).count();
    }

//    public List<TestExecution> getTests(){
//        List<TestExecution> tests = Lists.newArrayList();
//
//        getInternalTests().forEach(test -> {
//            test.setTestCase(getTestCase(test.getTestCase().getName(), test.getTestCase().getLocationPath()));
//            tests.add(test);
//        });
//        return tests;
//    }
//
//    private List<TestExecution> getInternalTests(){
//        List<TestExecution> tests = Lists.newArrayList();
//        if(suite != null)
//            this.getSuite().getTest().getClazz().forEach(clazz -> {
//                clazz.getTest_method().forEach(testMethod -> {
//                    if(!testMethod.is_config()) {
//                        TestExecution testExecution = TestExecution.builder()
//                                .testCase(createTestCase(testMethod.getName(), clazz.getName()))
//                                .duration(testMethod.getDuration_ms())
//                                .status(testMethod.getStatus())
//                                .suiteName(getSuite().getName())
//                                .errorDetails(testMethod.getError())
//                                .errorStackTrace(testMethod.getErrorDetails())
//                                .build();
//
//                        if(testMethod.getStatus().equals(StatusType.SKIP)){
//                            TestNGSuiteTestClassTestMethod failConfig = clazz.getFailedConfig();
//                            if(failConfig != null){
//                                testExecution.setErrorDetails("There was a config error on '"+ failConfig.getName() +"':\n" + failConfig.getError());
//                                testExecution.setErrorStackTrace(failConfig.getErrorDetails());
//                            }
//                        }
//
//                        tests.add(testExecution);
//                    }
//                });
//            });
//
//        Stream<TestExecution> s = tests.stream();
//        log.info("Before remove distinct");
//        s.forEach(t -> log.info(t.getTestCase().getName() + "//" + t.getTestCase().getLocationPath()));
//        List<TestExecution> tests2 = tests.stream().distinct().collect(Collectors.toList());
//        log.info("After remove distinct");
//        tests2.stream().forEach(t -> log.info(t.getTestCase().getName() + "//" + t.getTestCase().getLocationPath()));
//        return  tests2;
//    }

}
