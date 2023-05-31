/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import com.clarolab.model.helper.tag.TagHelper;
import com.clarolab.model.types.*;
import com.clarolab.util.DateUtils;
import com.clarolab.util.LogicalCondition;
import com.clarolab.util.StringUtils;
import lombok.*;
import org.hibernate.annotations.OnDelete;

import javax.persistence.*;

import static com.clarolab.model.helper.tag.TagHelper.AUTO_TRIAGED;
import static com.clarolab.util.Constants.AUTOTRIAGE_SAME_ERROR_TEST;
import static com.clarolab.util.Constants.TABLE_TEST_TRIAGE;
@Entity
@Table(name = TABLE_TEST_TRIAGE, indexes = {
        // @Index(name = "IDX_TESTTRIAGE_TEST_STATE", columnList = "testCase_id,currentState,enabled"),
        @Index(name = "IDX_TESTTRIAGE_TEST_ENABLED", columnList = "testCase_id,enabled"),
        @Index(name = "IDX_TESTTRIAGE_BUILD_SAMETEST", columnList = "build_id,testCase_id,enabled"),
        @Index(name = "IDX_TESTTRIAGE_EXECUTOR_TEST", columnList = "executor_id,testCase_id,enabled,id"),
        @Index(name = "IDX_TESTTRIAGE_EXECUTOR_AUTOMATION", columnList = "executor_id,testCase_id,timestamp"),
        @Index(name = "IDX_TESTTRIAGE_BUILD", columnList = "build_id"),
        @Index(name = "IDX_TESTTRIAGE_KANBAN", columnList = "build_id,currentState,triaged"),
        @Index(name = "IDX_TESTTRIAGE_AUTOTRIAGED", columnList = "build_id,currentState,tags"),
        // @Index(name = "IDX_TESTTRIAGE_RANK", columnList = "build_id,currentState,rank"),
        @Index(name = "IDX_TESTTRIAGE_PREV2", columnList = "executor_id,testCase_id,triaged,expired,enabled,buildNumber,id"),
        @Index(name = "IDX_TESTTRIAGE_PREV", columnList = "executor_id,testCase_id,currentState,buildNumber"),
        @Index(name = "IDX_TESTTRIAGE_PREVNOTE", columnList = "executor_id,testCase_id,enabled,triaged,executionDate,id"),
        @Index(name = "IDX_TESTTRIAGE_ATTEMPT", columnList = "build_id,triaged,expired,enabled"),
        @Index(name = "IDX_TESTTRIAGE_SUMMARY", columnList = "id,triaged,expired,enabled"),
        @Index(name = "IDX_TESTTRIAGE_STAT_ONGOING", columnList = "executor_id,enabled,build_id,currentState"),
        @Index(name = "IDX_TESTTRIAGE_STAT_ENG", columnList = "container_id,user_id")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TestTriage extends Entry {

    @Enumerated
    @Column(columnDefinition = "smallint")
    private StateType currentState;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private DeducedReasonType stateReasonType;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private ApplicationFailType applicationFailType;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private TestFailType testFailType;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private ErrorType previousErrorType;

    private String tags;

    private boolean expired = false;

    private String file;

    private int rank;

    private long snooze;

    private boolean triaged;

    private boolean updatedByUser;

    private String executorName;

    private int buildNumber;

    @ManyToOne(fetch = FetchType.EAGER) //could be not assigned
    @JoinColumn(name = "user_id")
    private User triager;


    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "note_id")
    private Note note;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "build_id")
    private Build build;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "test_id")
    private TestExecution testExecution;

    // Variables that make faster queries, they are private for use
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "buildTriage_id")
    private BuildTriage buildTriage;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "container_id")
    private Container container;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "executor_id")
    private Executor executor;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "testCase_id")
    private TestCase testCase;

    private long executionDate;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "previousTriage_id")
    private TestTriage previousTriage;

    @Builder
    private TestTriage(Long id, boolean enabled, long updated, long timestamp, StateType currentState, ApplicationFailType applicationFailType, TestFailType testFailType, String tags, String file, int rank, long snooze, boolean triaged, String executorName, User triager, Note note, Build buildParent, TestExecution testExecution, DeducedReasonType stateReasonType, long executionDate, ErrorType previousErrorType, TestTriage previousTriage) {
        super(id, enabled, updated, timestamp);
        this.currentState = currentState;
        this.applicationFailType = applicationFailType;
        this.testFailType = testFailType;
        this.tags = tags;
        this.file = file;
        this.rank = rank;
        this.snooze = snooze;
        this.triaged = triaged;
        this.executorName = executorName;
        this.triager = triager;
        this.note = note;
        this.build = buildParent;
        this.testExecution = testExecution;
        this.stateReasonType = stateReasonType;
        this.executionDate = executionDate;
        this.previousErrorType = previousErrorType;
        this.previousTriage = previousTriage;

        newBuildSet();

    }

    public void initialize() {
        newBuildSet();
        newTestExecutionSet();
    }

    public static StateType[] passStates() {
        StateType[] states = {StateType.PASS, StateType.NEWPASS};
        return states;
    }

    public static StateType[] failStates() {
        StateType[] states = {StateType.FAIL, StateType.NEWFAIL, StateType.PERMANENT};
        return states;
    }

    public Build getBuild() {
        return build;
    }

    public Long getBuildId() {
        return build.getId();
    }

    public Boolean isSnoozed() {
        //If snooze time > now then its snoozed
        // System.err.println("This functionality is almost deprecated");
        return snooze > DateUtils.now();
    }

    public boolean containTag(String tag) {
        return StringUtils.contains(tags, tag);
    }

    public boolean isFiled() {
        return LogicalCondition.NOT(StringUtils.isEmpty(getFile()));
    }

    public boolean isFailed() {
        return LogicalCondition.OR(isFail(), isNewFail());
    }

    public boolean isPass(){
        return currentState.equals(StateType.PASS);
    }

    public boolean isPassed(){
        return isPass() || isNewPass();
    }

    public boolean isFail() {
        return currentState.equals(StateType.FAIL);
    }

    public boolean isNewPass(){
        return currentState.equals(StateType.NEWPASS);
    }

    public boolean isSkip(){
        return currentState.equals(StateType.SKIP);
    }

    public boolean isNewFail() {
        return currentState.equals(StateType.NEWFAIL);
    }

    public boolean isPermanent() {
        return currentState.equals(StateType.PERMANENT);
    }

    public boolean isTestUndefined() {
        return testFailType.equals(TestFailType.UNDEFINED);
    }

    public boolean isAutomatedTriaged(){
        return containTag(AUTO_TRIAGED) || containTag(AUTOTRIAGE_SAME_ERROR_TEST);
    }

    public boolean isNotExecuted() {return currentState.equals(StateType.NOT_EXECUTED); }

    public void addTag(String newTag) {
        tags = TagHelper.addNewTag(tags, newTag);
    }

    public String getExecutorURL(){
        return build.getExecutorULR();
    }

    public Executor getExecutor(){
        return build.getExecutor();
    }

    public long getExecutorId(){
        return build.getExecutorId();
    }

    public int getFailedSince(){
        return testExecution.getFailedSince();
    }

    public String getJobName() {
        return build.getExecutorName();
    }

    public String getCurrentStateName(){
        return currentState.name();
    }

    public String getTestName() {
        return getTestCase().getName();
    }

    public Product getProduct() {
        return container.getProduct();
    }

    public String getBuildUrl() {
        return build.getUrl();
    }

    public TestPin getPin() {
        return getTestCase().getPin();
    }

    public void setPin(TestPin pin) {
        getTestCase().setPin(pin);
    }

    public String getTextExecutionErrorDetails(){
        return testExecution.getErrorDetails();
    }

    public String getTextExecutionStackTrace(){
        return testExecution.getErrorStackTrace();
    }

    public boolean isHierarchicalyEnabled(){
        return isEnabled() && build.isHierarchicalyEnabled();
    }

    public boolean isFlaky() {
        return containTag(TagHelper.FLAKY_TRIAGE);
    }

    public boolean isSolid() {
        return containTag(TagHelper.SOLID_TEST);
    }

    public boolean isFirtsTriage() {
        return containTag(TagHelper.FIRST_TRIAGE);
    }

    public void setBuild(Build newBuild) {
        this.build = newBuild;
        this.newBuildSet();
    }

    public void setTestExecution(TestExecution execution) {
        this.testExecution = execution;
        this.newTestExecutionSet();
    }

    public void newBuildSet(){
        if (build != null) {
            this.container = build.getContainer();
            this.executor = build.getExecutor();
            this.buildNumber = build.getNumber();
        }
    }

    public void newTestExecutionSet(){
        if (testExecution != null) {
            testCase = testExecution.getTestCase();
        }
    }

    public boolean hasProductBug() {
        return applicationFailType != null && applicationFailType == ApplicationFailType.FILED_TICKET;
    }

    public boolean isProductWorking() {
        return applicationFailType != null && applicationFailType == ApplicationFailType.NO_FAIL;
    }

    public boolean isProductWorkingWithExternalCause() {
        return applicationFailType != null && applicationFailType == ApplicationFailType.EXTERNAL_CAUSE;
    }

    public boolean isProductSkip() {
        return applicationFailType != null && applicationFailType == ApplicationFailType.UNDEFINED;
    }

    public boolean hasTestBug() {
        boolean hasBug = getTestCase().getAutomatedTestIssue() != null && getTestCase().getAutomatedTestIssue().shouldPropagateStatus();
        return hasBug || testFailType != null && (testFailType == TestFailType.TEST_ASSIGNED_TO_FIX);
    }

    public boolean isTestWorking() {
        return testFailType != null && testFailType == TestFailType.NO_FAIL;
    }

    public boolean isTestWorkingWithExternalCause() {
        return testFailType != null && testFailType == TestFailType.EXTERNAL_CAUSE;
    }

    public boolean isTestSkip() {
        return testFailType != null && testFailType == TestFailType.UNDEFINED;
    }

    public boolean isTestWontFix() {
        return testFailType != null && testFailType == TestFailType.WONT_FILE;
    }

    public Long getAutomationIssueId() {
        return getTestCase().getAutomatedTestIssue() != null ? getTestCase().getAutomatedTestIssue().getId() : null;
    }

    public AutomatedTestIssue getAutomationIssue() {
        return getTestCase().getAutomatedTestIssue() != null ? getTestCase().getAutomatedTestIssue() : null;
    }

    public boolean hasNote() {
        return note != null && note.getDescription() != null && !note.getDescription().isEmpty();
    }

    public void activate() {
        setEnabled(true);
        setExpired(false);
    }

    public void setTriaged() {
        setTriaged(true);
        setEnabled(true);
    }

    public void invalidate() {
        setTriaged(true);
        setEnabled(false);
    }

    public long getExecutionDate() {
        return executionDate;
    }

    public Long getAutomatedTestIssueId() {
        AutomatedTestIssue automation = getTestCase().getAutomatedTestIssue();
        return automation == null ? null : automation.getId();
    }

    public Long getIssueTicketId() {
        IssueTicket ticket = getTestCase().getIssueTicket();
        return ticket == null ? null : ticket.getId();
    }

    public String getIssueTicketName() {
        IssueTicket ticket = getTestCase().getIssueTicket();
        return ticket == null ? null : ticket.getDisplaySummary();
    }

    public boolean relatedWithExecutor(String executorName) {
        return getExecutorName().equals(executorName) || getTestExecution().getName().equals(executorName);

    }

    public String getTestExecutionDisplayName() {
        return getTestExecution().getDisplayName();
    }

    public boolean hasSteps() {
        return getTestExecution().isHasSteps();
    }

    public String getProductBuildVersion() {
        return buildTriage.getProductBuildVersion();
    }

    public String getLatestProductVersion() {
        return getBuild().getProductVersion();
    }
    
    public boolean hasSameProductVersion(String anotherBuildVersion) {
        return StringUtils.containsSameValue(getProductBuildVersion(), anotherBuildVersion);
    }
}
