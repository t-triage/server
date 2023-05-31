/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;


import com.clarolab.model.types.IssueType;
import com.clarolab.model.types.UserFixPriorityType;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.clarolab.util.Constants.AUTOMATION_TREND_SIZE;
import static com.clarolab.util.Constants.TABLE_AUTOMATED_TEST_ISSUE;

@Entity
@Table(name = TABLE_AUTOMATED_TEST_ISSUE, indexes = {
        @Index(name = "IDX_AUTOMATED_USER_TYPE", columnList = "user_id,issueType"),
        @Index(name = "IDX_AUTOMATED_CASE", columnList = "testCase_id"),
        @Index(name = "IDX_AUTOMATED_USER_ISSUE", columnList = "issueType")
})

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AutomatedTestIssue extends Entry {
    @Enumerated
    @Column(columnDefinition = "smallint")
    private IssueType issueType;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "testCase_id")
    private TestCase testCase;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private UserFixPriorityType userFixPriorityType;

    @ManyToOne(fetch = FetchType.EAGER) //could be not assigned
    @JoinColumn(name = "user_id")
    private User triager;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "testTriage_id")
    private TestTriage testTriage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private long calculatedPriority;
    private int reopenTimes;
    private int failTimes;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "note_id")
    private Note note;

    private int consecutivePasses = 0;

    // representation of binary executions. 1 pass, 0 fail. First char is the newest
    private String trend = "";

    @Builder
    private AutomatedTestIssue(Long id, boolean enabled, long updated, long timestamp, IssueType issueType, TestCase testCase, UserFixPriorityType userFixPriorityType, User triager, int reopenTimes, long calculatedPriority, TestTriage testTriage, Note note, int failTimes, Product product) {
        super(id, enabled, updated, timestamp);
        this.issueType = issueType;
        this.testCase = testCase;
        this.userFixPriorityType = userFixPriorityType;
        this.triager = triager;
        this.calculatedPriority = calculatedPriority;
        this.testTriage = testTriage;
        this.note = note;
        this.reopenTimes = reopenTimes;
        this.failTimes = failTimes;
        this.product = product;
    }

    public boolean isHierarchicalyEnabled() {
        if (testTriage == null) {
            return isEnabled();
        } else {
            return isEnabled() && testTriage.isHierarchicalyEnabled();
        }
    }

    public boolean isResolved() {
        return issueType.isResolved();
    }

    public boolean isOpen() {
        return issueType.isOpen();
    }

    public boolean isReOpen() {
        return issueType.isReOpen();
    }

    public boolean isStillOpen() {
        return isOpen() || isReOpen();
    }

    public boolean isWontFix() {
        return issueType.isWontFix();
    }

    // If this status is still valid for new triages
    public boolean shouldPropagateStatus() {
        return isWontFix() || !isResolved();
    }

    public boolean relatedWithExecutor(String executorName) {
        return getTestTriage().relatedWithExecutor(executorName);
    }

    public String getExecutorName() {
        if (getTestTriage() == null) {
            return null;
        } else {
            return getTestTriage().getExecutorName();
        }
    }

    public boolean isPassing() {
        return issueType.isPassing();
    }

    public boolean isFixed() {
        return issueType.isFixed();
    }

    public void addTrend(TestTriage test) {
        String nextBit = test.isPassed() ? "1" : "0";

        trend = trend + nextBit;

        if (trend.length() > AUTOMATION_TREND_SIZE) {
            trend = trend.substring(1);
        }
    }

    // First item is the oldest
    // true == pass, false == fail
    public List<Boolean> getSuccessTrend() {
        List<Boolean> answer = new ArrayList<>(trend.length());

        for (int i = 0; i < trend.length(); i++) {
            answer.add(i, (trend.substring(i, i + 1).equals("1")));
        }

        return answer;
    }

    public boolean isBlocker() {
        return userFixPriorityType.equals(UserFixPriorityType.BLOCKER);
    }

    public boolean isHigh() {
        return userFixPriorityType.equals(UserFixPriorityType.HIGH);
    }

    public boolean isMedium() {
        return userFixPriorityType.equals(UserFixPriorityType.MEDIUM);
    }

    //public Product getProduct(){
    //    return getTestCase().getProduct();
    //}

    public String getRelatedIssueID() { return getTestCase().getRelatedIssueID(); }

    public Product findProduct(){
        return getTestCase().getProduct();
    }

    public String getTestCaseName(){
        return getTestCase().getName();
    }
}
