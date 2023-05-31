/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import com.clarolab.model.types.StatusType;
import com.clarolab.model.types.UserFixPriorityType;
import com.clarolab.util.LogicalCondition;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

import static com.clarolab.util.Constants.TABLE_TEST_EXECUTION;


@Entity
@Table(name = TABLE_TEST_EXECUTION, indexes = {
        @Index(name = "IDX_TESTEXECUTION_TEST", columnList = "testCase_id"),
        @Index(name = "IDX_TESTEXECUTION_CLEAN_TEST", columnList = "timestamp"),
        @Index(name = "IDX_TESTEXECUTION_DURATION", columnList = "duration"),
        @Index(name = "IDX_TESTEXECUTION_STATUS", columnList = "status")

})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TestExecution extends Entry {

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "testCase_id")
    private TestCase testCase;

    private double duration;

    private int age;

    @Type(type = "org.hibernate.type.TextType")
    private String errorDetails;

    @Type(type = "org.hibernate.type.TextType")
    private String errorStackTrace;

    @Type(type = "org.hibernate.type.TextType")
    private String standardOutput;

    @Type(type = "org.hibernate.type.TextType")
    private String screenshotURL;

    @Type(type = "org.hibernate.type.TextType")
    private String videoURL;

    @Type(type = "org.hibernate.type.TextType")
    private String skippedMessage;

    @Type(type = "org.hibernate.type.TextType")
    private String suiteName;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private StatusType status;

    /* The last build id*/
    private int failedSince;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "report_id")

    private Report report;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private UserFixPriorityType userFixPriorityType;

    @OneToMany(mappedBy = "testExecution", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("stepOrder ASC")
    private List<TestExecutionStep> testExecutionSteps;

    private boolean hasSteps;

    @Builder
    private TestExecution(Long id, boolean enabled, long updated, long timestamp, double duration, String errorDetails, String errorStackTrace, StatusType status, int failedSince, Report report, TestCase testCase, int age, TestPin pin, String standardOutput, String skippedMessage, String screenshotURL, List<TestExecutionStep> testExecutionSteps, String suiteName, boolean hasSteps, UserFixPriorityType userFixPriorityType) {
        super(id, enabled, updated, timestamp);
        this.testCase = testCase;
        this.duration = duration;
        this.errorDetails = errorDetails;
        this.errorStackTrace = errorStackTrace;
        this.standardOutput = standardOutput;
        this.skippedMessage = skippedMessage;
        this.screenshotURL = screenshotURL;
        this.status = status;
        this.failedSince = failedSince;
        this.report = report;
        this.age = age;
        this.testExecutionSteps = testExecutionSteps;
        this.suiteName = suiteName;
        this.hasSteps = hasSteps;
        this.testExecutionSteps = Lists.newArrayList();
        this.userFixPriorityType = userFixPriorityType;

        if (CollectionUtils.isNotEmpty(testExecutionSteps)) {
            for (TestExecutionStep testExecutionStep : testExecutionSteps) {
                this.add(testExecutionStep);
            }
        }
    }

    public boolean isFailed() {
        return LogicalCondition.OR(status.equals(StatusType.FAIL), status.equals(StatusType.REGRESSION));
    }

    public boolean isPassed() {
        return LogicalCondition.OR(status.equals(StatusType.PASS), status.equals(StatusType.FIXED));
    }

    public boolean isSkipped() {
        return status.equals(StatusType.SKIP);
    }

    public void setName(String name) {
        getTestCase().setName(name);
    }

    public void setLocationPath(String name) {
        getTestCase().setLocationPath(name);
    }

    public TestCase getTestCase() {
        if (testCase == null) {
            testCase = TestCase.builder().build();
        }

        return testCase;
    }

    public String getName() {
        return getTestCase().getName();
    }

    public String getLocationPath() {
        return getTestCase().getLocationPath();
    }

    public String getDisplayName() {
        return StringUtils.methodToWords(getName());
    }

    public TestPin getPin() {
        return getTestCase().getPin();
    }

    public void add(TestExecutionStep testExecutionStep) {
        if (testExecutionStep == null) {
            return;
        }
        initTestExecutionSteps();
        hasSteps = true;
        if (testExecutionStep.getStepOrder() < 1) {
            testExecutionStep.setStepOrder(getTestExecutionSteps().size() + 1);
        }
        testExecutionStep.setTestExecution(this);
        this.getTestExecutionSteps().add(testExecutionStep);
    }

    public void add(List<TestExecutionStep> testExecutionSteps) {
        //initTestExecutionSteps();
        testExecutionSteps.forEach(step ->this.add(step));
    }

    public void initTestExecutionSteps(){
        if (getTestExecutionSteps() == null)
            this.setTestExecutionSteps(Lists.newArrayList());
    }

    public boolean isPin() {
        return getPin()!=null;
    }

    public String getFirstScreenshot(){
        return screenshotURL != null && screenshotURL.contains(",") ? screenshotURL.split(",")[0] : screenshotURL;
    }

    public boolean isHasMultipleEnvironment() {
        if (getTestCase() == null) {
            return false;
        }
        return getTestCase().isHasMultipleEnvironment();
    }

    public String getProductVersion() {
        return getReport().getProductVersion();
    }

//This is used for 'MainTestNG.getTests()'
//    @Override
//    public boolean equals(final Object obj) {
//        final TestExecution testExecution = (TestExecution) obj;
//        return (this.testCase.getName().equals(testExecution.getTestCase().getName()) && this.testCase.getLocationPath().equals(testExecution.getTestCase().getLocationPath()));
//    }
//
//    @Override
//    public int hashCode() {
//        return (this.testCase.getName() == null && this.testCase.getLocationPath() == null ? 0 : this.testCase.getName().hashCode()+this.testCase.getLocationPath().hashCode());
//    }

}
