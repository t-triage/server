package com.clarolab.connectors.impl.utils.report.python.json.entity;

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
public class PythonTestCase extends AbstractTestCreator {

    private String title;
    private double duration;
    private String status;
    //    private String screenshotURL;
//    private String videoURL;
    private String err;

    private String stackTrace;
//    private List<String> steps;

    @Builder
    private PythonTestCase(String title, double duration, String status, String err, String stackTrace) {
        this.title = title;
        this.duration = duration;
        this.status = status;
//        this.screenshotURL = screenshotURL;
//        this.videoURL = videoURL;
        this.err = err;
        this.stackTrace = stackTrace;
//        this.steps = steps;
    }

    public StatusType getStatus() {
        if (isPass())
            return StatusType.PASS;
        if (isFail())
            return StatusType.FAIL;

        return StatusType.UNKNOWN;
    }

    public boolean isPass() {
        return status.equals("passed");
    }

    public boolean isFail() {
        return status.equals("failed");
    }


    public TestExecution getTest(PythonSuite jestSuite, boolean isForDebug) {
        return TestExecution.builder()
                .testCase(getTestCase(this.title, jestSuite.getUri(), isForDebug))
                .duration(this.getDuration())
                .suiteName(jestSuite.getTitle())
                .status(this.getStatus())
                .errorDetails(this.err != null ? this.err : null)
                .errorStackTrace(this.getStackTrace())
                .enabled(true)
                .timestamp(DateUtils.now())
                .build();
    }

//    public List<TestExecutionStep> getSteps() {
//        List<TestExecutionStep> testExecutionSteps = Lists.newArrayList();
//        steps.forEach(step ->
//                testExecutionSteps.add(TestExecutionStep.builder().name(step).build()));
//
//        return testExecutionSteps;
//    }
}

