package com.clarolab.connectors.impl.utils.report.protractor.json.entity.v1;

import com.clarolab.connectors.impl.AbstractTestCreator;
import com.clarolab.model.TestExecution;
import com.clarolab.model.types.StatusType;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class ProtractorSuite extends AbstractTestCreator {

    private int tests;
    private int failures;
    private int errors;
    private int skipped;
    private String time;
    private String name;
    private String timestamp;
    private List<ProtractorTestCase> testCases;

    public StatusType getStatus(){
        if(getFailures() > 0 || getErrors() > 0)
            return StatusType.FAIL;
        if(getSkipped() > 0)
            return StatusType.SKIP;
        return StatusType.PASS;
    }

    public int getPassed(){
        return (int) testCases.stream().filter(test -> test.getFailure() == null || test.getFailure().noErrorPresent()).count();
    }

    public int getFailed(){
        return failures + errors;
    }

    public long getDuration() {
        return getTime(time);
    }

    public List<TestExecution> getTestCases(){
        return getTestCases(false);
    }

    public List<TestExecution> getTestCases(boolean isForDebug){
        List<TestExecution> tests = Lists.newArrayList();
        testCases.forEach(test -> test.setContext(getContext()));
        this.testCases.forEach(test -> {
            TestExecution testCase = test.getTest(isForDebug);
            testCase.setSuiteName(name);
            tests.add(testCase);
        });
        return tests;
    }

}
