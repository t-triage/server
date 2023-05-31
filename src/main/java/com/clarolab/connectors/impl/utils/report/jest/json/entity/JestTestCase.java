package com.clarolab.connectors.impl.utils.report.jest.json.entity;

import com.clarolab.connectors.impl.AbstractTestCreator;
import com.clarolab.model.TestExecution;
import com.clarolab.model.TestExecutionStep;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.DateUtils;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@Log
public class JestTestCase extends AbstractTestCreator {

    private String title;
    private double duration;
    private String status;
//    private String screenshotURL;
//    private String videoURL;
    private JestTestCaseError err;
    private List<String> steps;

    @Builder
    private JestTestCase(String title, double duration, String status, JestTestCaseError err, List<String> steps) {
        this.title = title;
        this.duration = duration;
        this.status = status;
//        this.screenshotURL = screenshotURL;
//        this.videoURL = videoURL;
        this.err = err;
        this.steps = steps;
    }

    public StatusType getStatus() {
        if (isPass())
            return StatusType.PASS;
        if (isFail())
            return StatusType.FAIL;
        if (isSkip())
            return StatusType.SKIP;
        if (isPending())
            return StatusType.PASS;
        return StatusType.UNKNOWN;
    }

    public boolean isPass() {
        return status.equals("passed");
    }

    public boolean isFail() {
        return status.equals("failed");
    }

    public boolean isSkip() {
        return status.equals("skipped");
    }
    public boolean isPending() {
        return status.equals("pending");
    }
    public TestExecution getTest(JestSuite jestSuite, boolean isForDebug) {
        return TestExecution.builder()
                .testCase(getTestCase(this.title, jestSuite.getUri(), isForDebug))
                .duration(this.getDuration())
                .suiteName(jestSuite.getTitle())
                .status(this.getStatus())
                .errorDetails(this.err != null ? this.err.getMessage() : null)
//                .errorStackTrace(this.err != null ? this.err.getPass() : true)
                .enabled(true)
                .timestamp(DateUtils.now())
                .build();
    }

    public List<TestExecutionStep> getSteps() {
        List<TestExecutionStep> testExecutionSteps = Lists.newArrayList();
        steps.forEach(step ->
                testExecutionSteps.add(TestExecutionStep.builder().name(step).build()));

        return testExecutionSteps;
    }
}
