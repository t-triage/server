package com.clarolab.connectors.impl.utils.report.protractor.json.entity.v2;

import com.clarolab.connectors.impl.AbstractTestCreator;
import com.clarolab.model.TestExecution;
import com.clarolab.model.TestExecutionStep;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
public class ProtractorTestCaseV2 extends AbstractTestCreator {

    private int steps;
    private int failedSteps;
    private int errorsSteps;
    private int skippedSteps;
    private String time;
    private String name;
    private String timestamp;
    private List<ProtractorTestCaseStepV2> stepList;
    private Map<String, String> screenshots;

    public StatusType getStatus() {
        if (getFailedSteps() > 0)
            return StatusType.FAIL;
        if (skippedSteps > 0)
            return StatusType.SKIP;
        return StatusType.PASS;
    }

    public int getPassedSteps() {
        return steps - (errorsSteps + failedSteps + skippedSteps);
    }

    public int getFailedSteps() {
        return failedSteps + errorsSteps;
    }


    public long getDuration() {
        return getTime(time);
    }

    public TestExecution getTest() {
        return getTestCase(false);
    }

    public String getError() {
        StringBuilder error = new StringBuilder();
        stepList.stream().filter(step -> step.isFailedStep()).forEach(failure -> {
            error.append(String.format("On step: '%s' \n", failure.getTesCaseStepName()));
            error.append(failure.getError());
            error.append(StringUtils.getLineSeparator());
        });
        return error.toString();
    }

    public String getDetailedError() {
        StringBuilder error = new StringBuilder();
        error.append("ERROR DETAIL");
        error.append("\n=========================\n");
        error.append(getError());
        error.append("\nERROR STACKTRACE");
        error.append("\n=========================\n");
        stepList.stream().filter(step -> step.isFailedStep()).forEach(failure -> {
            error.append(String.format("On step: '%s' \n", failure.getTesCaseStepName()));
            error.append(failure.getDetailedError());
            error.append(StringUtils.getLineSeparator());
        });
        return error.toString();
    }

    public TestExecution getTestCase(boolean isForDebug) {
        TestExecution test = TestExecution.builder()
                .testCase(getTestCase(getTesCaseName(), getSuiteName(), isForDebug))
                .suiteName(getSuiteName())
                .duration(getTime(time))
                .status(getStatus())
                .errorDetails(getError())
                .errorStackTrace(getDetailedError())
                .hasSteps(true)
                .enabled(true)
                .timestamp(DateUtils.now())
                .build();
        test.add(getTesCaseSteps());
        return test;
    }

    private String getSuiteName() {
        if (name == null) {
            return name;
        }
        if (name.contains(">")) {
            return name.substring(0, name.indexOf(">")).trim();
        }
        if (name.contains(":")) {
            return name.substring(0, name.indexOf(":")).trim();
        }
        if (name.contains("-")) {
            return name.substring(0, name.indexOf("-")).trim();
        }

        return name;
    }

    private String getTesCaseName() {
        String suite = getSuiteName();
        if (suite == null) {
            return name;
        }
        if (name.startsWith(suite) && !name.equals(suite)) {
            return name.substring(suite.length() + 2).trim();
        }
        return name;
    }

    private List<TestExecutionStep> getTesCaseSteps() {
        List<TestExecutionStep> steps = Lists.newArrayList();
        stepList.forEach(step -> {
            TestExecutionStep s = TestExecutionStep.builder()
                    .name(step.getTesCaseStepName())
                    .output(step.isFailedStep() ? step.getOutput() : StringUtils.getEmpty())
                    .build();
            steps.add(s);
        });
        setScreenshotURL(steps);
        return steps;
    }

    // Sample URL: http://server/artifact/QAZOOM-TPZ/shared/build-73/screenshots/tests-reporter/screenshots/Webinars%3A%20Past%20Zoom%20webinar%20test%20Click%20the%20Zoom%20webinar%20%22Don%27t%20use%3A%20Past%20Webinar%20Test%20Protractor%22%20in%20Past%20tab.png
    private String setScreenshotURL(List<TestExecutionStep> steps) {
        if (screenshots == null) {
            return null;
        }
        if (failedSteps > 0) {
            int index = 1;
            for (TestExecutionStep step : steps) {
                for (Map.Entry<String, String> testScreen : screenshots.entrySet()) {
                    if (testScreen.getKey().startsWith(getTesCaseName())) {
                        String stepIdentifier = testScreen.getKey().substring(getTesCaseName().length());
                        if (StringUtils.isEmpty(stepIdentifier)) {
                            step.setScreenshotURL(testScreen.getValue());
                        }
                        if (step.getName().equalsIgnoreCase(stepIdentifier)) {
                            step.setScreenshotURL(testScreen.getValue());
                        }
                        if (stepIdentifier.equals(String.valueOf(index))) {
                            step.setScreenshotURL(testScreen.getValue());
                        }
                    }
                }
                index += 1;
            }

        }

        return null;
    }

}
