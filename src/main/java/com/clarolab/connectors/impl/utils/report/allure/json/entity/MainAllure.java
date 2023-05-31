package com.clarolab.connectors.impl.utils.report.allure.json.entity;

import com.clarolab.connectors.impl.AbstractTestCreator;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.model.TestExecution;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.DateUtils;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log
@Data
public class MainAllure extends AbstractTestCreator {

    private List<AllureTestCase> testCaseList;
    private AllureReportSummary summary;
    private List<TestExecution> tests;

    @Builder
    public MainAllure(Map<String, String> tests, String singleTest, String summary, ApplicationContextService context, boolean isForDebug){
        testCaseList = Lists.newArrayList();
        if(tests != null)
            tests.entrySet().forEach(element -> testCaseList.add(new Gson().fromJson(element.getValue(), AllureTestCase.class)));
        else
            testCaseList.add(new Gson().fromJson(singleTest, AllureTestCase.class));
        this.summary = summary != null ? new Gson().fromJson(summary, AllureReportSummary.class) : null;
        super.context = context;
        setTestCases(isForDebug);
    }

    public void setTestCases(boolean isForDebug) {
        this.tests = getTestCasesUnique(isForDebug);
    }

    public StatusType getStatus(){
        if(getOnlyFailed() + getBroken() > 0)
            return StatusType.FAIL;
        if(getOnlyFailed() == 0 && getBroken() > 0)
            return StatusType.BROKEN;
        if(getFailed() == 0 && getSkipped() > 0)
            return StatusType.SKIP;
        if(getTotal() == getPassed())
            return StatusType.PASS;
        return StatusType.UNKNOWN;
    }

    public int getTotal(){
        return  summary != null ? summary.getTotal() : tests.size();
    }

    public int getPassed(){
        return summary != null ? summary.getPassed() : getAmountFromTestCasesWithStatus(StatusType.PASS);
    }

    public int getSkipped(){
        return summary != null ? summary.getSkipped() : getAmountFromTestCasesWithStatus(StatusType.SKIP);
    }

    public int getOnlyFailed(){
        return summary != null ? summary.getFailed() : getAmountFromTestCasesWithStatus(StatusType.FAIL);
    }

    public int getFailed(){
        return getOnlyFailed() + getBroken();
    }

    public int getBroken(){
        return summary != null ? summary.getBroken() : getAmountFromTestCasesWithStatus(StatusType.BROKEN);
    }

    public long getDuration(){
        return summary != null ? summary.getDuration() : getDurationFromTestCases();
    }

    public long getExecutionDate(){
        return summary != null ? summary.getExecutionDate() : 0L;
    }

    private int getAmountFromTestCasesWithStatus(StatusType statusType){
        return this.tests.stream().filter(element -> element.getStatus().equals(statusType)).collect(Collectors.toList()).size();
    }

    private long getDurationFromTestCases(){
        return (long) this.tests.stream().mapToDouble(allureTestCase -> allureTestCase.getDuration()).sum();
    }

//    public List<TestExecution> getTests(){
//        return getTestCases(false);
//    }
//
//    public List<TestExecution> getTests(boolean isForDebug){
//        return getTestCases(isForDebug);
//    }

    private List<TestExecution> getTestCases(boolean isForDebug){
        List<TestExecution> tests = Lists.newArrayList();
        testCaseList.forEach(test -> {
            TestExecution testCase = TestExecution.builder()
                    .testCase(getTestCase(test.getTestCaseName() ,test.getTestCaseSuite(), isForDebug))
                    .duration(test.getDuration())
                    .suiteName(test.getTestCaseSuiteName())
                    .status(test.getStatus())
                    .errorDetails(test.getError())
                    .errorStackTrace(test.getErrorDetail())
                    .skippedMessage(test.skipReason())
                    .screenshotURL(test.getAttachmentsAsString())
                    .hasSteps(CollectionUtils.isNotEmpty(test.getTestCaseSteps()))
                    .enabled(true)
                    .timestamp(DateUtils.now())
                    .build();

            testCase.add(test.getTestCaseSteps());

            tests.add(testCase);
        });
        return tests;
    }

    private List<TestExecution> getTestCasesUnique(boolean isForDebug){
        List<TestExecution> tests = Lists.newArrayList();

        // If the test is duplicate, the final one will be the PASS or the last one
        Map<String, AllureTestCase> uniqueTests = new HashMap<>();
        for (AllureTestCase testCase : testCaseList) {
            String key = testCase.getTestCaseName() + testCase.getTestCaseSuite();
            AllureTestCase existingTest = uniqueTests.getOrDefault(key, null);
            if (existingTest == null) {
                uniqueTests.put(key, testCase);
            } else {
                if (!existingTest.isPass()) {
                    uniqueTests.put(key, testCase);
                }
            }
        }

        uniqueTests.values().forEach(test -> {
            TestExecution testCase = TestExecution.builder()
                    .testCase(getTestCase(test.getTestCaseName() ,test.getTestCaseSuite(), isForDebug))
                    .duration(test.getDuration())
                    .suiteName(test.getTestCaseSuiteName())
                    .status(test.getStatus())
                    .errorDetails(test.getError())
                    .errorStackTrace(test.getErrorDetail())
                    .skippedMessage(test.skipReason())
                    .screenshotURL(test.getAttachmentsAsString())
                    .hasSteps(CollectionUtils.isNotEmpty(test.getTestCaseSteps()))
                    .enabled(true)
                    .timestamp(DateUtils.now())
                    .build();

            testCase.add(test.getTestCaseSteps());

            tests.add(testCase);
        });
        return tests;
    }

    private int getSize(Stream stream){
        return stream.mapToInt(e -> 1).sum();
    }


}
