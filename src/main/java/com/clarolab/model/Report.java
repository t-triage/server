/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import com.clarolab.model.types.ReportType;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.DateUtils;
import com.google.common.collect.Lists;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.clarolab.util.Constants.TABLE_REPORT;

@Entity
@Table(name = TABLE_REPORT, indexes = {
        @Index(name = "IDX_REPORT_TYPE", columnList = "type")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Report extends Entry {

    @Enumerated
    @Column(columnDefinition = "smallint")
    private ReportType type;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private StatusType status;

    private int passCount;
    private int failCount;
    private int warningCount;
    private int skipCount;
    private int totalTest;
    private double duration;
    private long executiondate;

    @Nullable
    private String productVersion;

    @Column
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Nullable
    private List<TestExecution> testExecutions;

    @Column
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Nullable
    private List<CVSLog> logs;

    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @Column(name = "fail_reason")
    @Type(type = "org.hibernate.type.TextType")
    private String failReason;

    /**
     * Golden: tests that have failed just in the last build (it has passed in all the previous 19 builds).
     * Flaky: tests that have failed more than once in the last 20 builds but not in all of them.
     * Old: tests that have consistently failed in all of the last 20 builds (it's considered as a known/old issue).
     * Blocked: tests that have a Blocked-By tag.
     */

    @Builder
    private Report(Long id, boolean enabled, long updated, long timestamp, ReportType type, StatusType status, int passCount, int failCount, int warningCount, int skipCount, double duration, long executiondate, @Nullable List<TestExecution> testExecutions, @Nullable List<CVSLog> logs, String description, String failReason, String productVersion) {
        super(id, enabled, updated, timestamp);
        this.type = type;
        this.status = status;
        this.passCount = passCount;
        this.failCount = failCount;
        this.warningCount = warningCount;
        this.skipCount = skipCount;
        this.duration = duration;
        this.executiondate = executiondate;
        this.testExecutions = testExecutions == null ? Lists.newArrayList() : testExecutions;
        this.logs = logs == null ? Lists.newArrayList() : logs;
        this.description = description;
        this.failReason = failReason;
        this.productVersion = productVersion;

        initTestCount();
    }

  public static Report getDefault(){
      return Report.builder().type(ReportType.UNKNOWN).status(StatusType.UNKNOWN).duration(0L).passCount(0).failCount(0).skipCount(0).enabled(false).timestamp(DateUtils.now()).build();
  }

    /**
     * The Stability Index tries to help focusing on the most unstable job first.
     * The math behind it is: ((failCount + skipCount) * 100 / totalTests); (it can be > 100 since skipCount is not included in totalTests)
     */

    public double getStabilityIndex() {
        if(getTotalTest() == 0) return 0;
        return ((failCount + skipCount) * 100) / getTotalTest();
    }

   public void add(TestExecution testExecution) {
        initTestExecutions();
        testExecution.setReport(this);
        this.getTestExecutions().add(testExecution);
       initTestCount();
    }

    public void add(List<TestExecution> testExecutions) {
        initTestExecutions();
        testExecutions.forEach(testCase ->this.add(testCase));
    }

    public void addLogs(CVSLog log) {
        if (this.logs == null)
            this.setLogs(new ArrayList<>());
        log.setReport(this);
        this.getLogs().add(log);
    }

    public void addLogs(List<CVSLog> logs) {
        logs.forEach(this::addLogs);
    }

    public long getPassPercentage() {
        if(getTotalTest() == 0) return 0;
        return (passCount * 100) / getTotalTest();
    }

    public long getFailPercentage() {
        if(getTotalTest() == 0) return 0;
        return (failCount * 100) / getTotalTest();
    }

    public long getSkipPercentage() {
        if(getTotalTest() == 0) return 0;
        return (skipCount * 100) / getTotalTest();
    }

  public void updateExecutionDateIfIsNeeded(long date){
      if(this.executiondate == 0L)
          this.setExecutiondate(date);
  }

  private void initTestExecutions(){
      if (getTestExecutions() == null) {
          this.setTestExecutions(Lists.newArrayList());
          totalTest = 0;
      }
  }

  private void initTestCount() {
      if (testExecutions != null) {
          totalTest = testExecutions.size();
      }
  }

  // This is only for data created using demo data, still this works
  public void updateStats() {
      failCount = (int) getTestExecutions().stream().filter(TestExecution::isFailed).count();
      passCount = (int) getTestExecutions().stream().filter(TestExecution::isPassed).count();
      skipCount = (int) getTestExecutions().stream().filter(TestExecution::isSkipped).count();
  }

    public void setTotalTests() {
        totalTest = passCount + failCount + skipCount;
    }

}
