/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.view;

import com.clarolab.dto.TestTriageDTO;
import com.clarolab.dto.UserDTO;
import com.clarolab.event.analytics.ExecutorStat;
import com.clarolab.model.*;
import com.clarolab.model.types.StateType;
import com.clarolab.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.clarolab.model.helper.PriorityHelper.convertPriority;
import static com.clarolab.util.DateUtils.BaseDateFormat;
import static com.clarolab.util.StringUtils.containsIgnoreCase;


/* This is wrapper of Executor in order to be used in th  UI */
@Setter
@Getter
@NoArgsConstructor
@ApiModel
public class ExecutorView implements View, SuiteView {

    @JsonIgnore
    private Executor executor;

    @JsonIgnore
    private TriageSpec triageSpec;

    @JsonIgnore
    private BuildTriage buildTriage;

    @JsonIgnore
    private Map<String, List<TestTriageDTO>> allTestTriages;

    @JsonIgnore
    private Report report;

    @JsonIgnore
    private TriageDeadline triageDeadline;

    private long toTriage;

    private long autoTriaged;

    @JsonIgnore
    private UserDTO triagger;


    //
    private long totalNewFails;
    private long totalNewPass;
    private long totalTriageDone;
    private long totalFails;
    private long totalTests;
    //

    @Builder
    private ExecutorView(Executor executor, TriageSpec triageSpec, BuildTriage buildTriage, Map<String, List<TestTriageDTO>> allTestTriages, long toTriage, long autoTriaged, UserDTO triagger,
                         long totalNewFails, long totalNewPass, long totalTriageDone, long totalFails, long totalTests) {
        this.executor = executor;
        this.buildTriage = buildTriage;
        this.allTestTriages = allTestTriages;
        this.report = buildTriage.getReport();
        this.toTriage = toTriage;
        this.triagger = triagger;
        this.autoTriaged = autoTriaged;
        this.triageSpec = triageSpec;
        this.triageDeadline = buildTriage.getTriageDeadline() == null ?
                this.rebuildTriageDeadline(buildTriage.getDeadline(), triageSpec, toTriage) : buildTriage.getTriageDeadline();

        this.totalNewFails = totalNewFails;
        this.totalNewPass = totalNewPass;
        this.totalTriageDone = totalTriageDone;
        this.totalFails = totalFails;
        this.totalTests = totalTests;

    }

    private TriageDeadline rebuildTriageDeadline(long deadline, TriageSpec triageSpec, long toTriage) {
        return TriageDeadline.builder()
                .deadline(deadline)
                .spec(triageSpec)
                .toTriage(toTriage)
                .view(this)
                .build();
    }

    public String getExecutorName() {
        return executor.getName();
    }

    public String getBuildStandardOutputUrl() {
        return getBuildTriage().getStandardOutputUrl();
    }

    public String getProductName() {
        return executor.getProductName();
    }

    public int getBuildNumber() {
        return getBuildTriage().getNumber();
    }

    public boolean isTriaged() {
        return getBuildTriage().isTriaged() && getToTriage() <= 0;
    }

    public boolean isExpired() {
        return getBuildTriage().isExpired();
    }

    public String getContainerName() {
        return executor.getContainerName();
    }

    public long getContainerId() {
        return executor.getContainer().getId();
    }

    public String getConnectorName() {
        return executor.getConnectorName();
    }

    public String getExecutorDescription() {
        return executor.getDescription();
    }

    public String getExecutorUrl() {
        return executor.getUrl();
    }

    public String getExternalBuildURL() {
        return buildTriage.getBuild().getUrl();
    }

    public StateType getState() {
        return buildTriage.getCurrentState();
    }

    @JsonIgnore
    private String getTags() {
        return buildTriage.getTags();
    }

    public long getTotalTestsToTriage() {
        return toTriage;
    }

    public long getTotalTriageDone() {
        return this.totalTriageDone;
        //return getTotalTests(TRIAGEDONE);
    }

    public long getTotalNewFails() {
        //return return getTotalTests(NEWFAIL);
        return totalNewFails;
    }

    public long getTotalFails() {
        //return getTotalTests(FAIL);
        return this.totalFails;
    }

    public long getTotalNowPassing() {
        return this.totalNewPass;
        //return getTotalTests(NOWPASSING);
    }

    private long getTotalTests(String typeKey) {
        if (MapUtils.isEmpty(allTestTriages))
            return 0;

        return allTestTriages.get(typeKey).size();
    }

    @JsonIgnore
    public long getTotalTestsToTriagePercentage() {
        if (getTotalTests() == 0) return 0;
        return (getTotalTestsToTriage() * 100) / getTotalTests();
    }

    public long getPassCount() {
        return report.getPassCount();
    }

    @JsonIgnore
    public long getPassPercentage() {
        return report.getPassPercentage();
    }

    public long getFailCount() {
        return report.getFailCount();
    }

    @JsonIgnore
    public long getFailPercentage() {
        return report.getFailPercentage();
    }

    public long getSkipCount() {
        return report.getSkipCount();
    }

    public long getSkipPercentage() {
        return report.getSkipPercentage();
    }

    public double getDuration() {
        return report.getDuration();
    }

    public double getStabilityIndex() {
        return 100 - (double) getPriority();
    }

    public long getExecutiondate() {
        return buildTriage.getExecutionDate();
    }

    public long getPriority() {
        if (getTotalTests() == 0 || getToTriage() == 0 || getTotalFails() == 0) {
            return 0;
        }
        if (getTotalFails() == getTotalTests() && getTotalTests() > 10) {
            // If the entire suite is failing, we can asume some major error
            return 10;
        }
        return 100 * getToTriage() / getTotalTests();
    }

    public UserDTO getAssignee() {
        return triagger;
    }

    public long getLastBuildTriageId() {
        return buildTriage.getId();
    }

    public Long getExecutorId() {
        return executor.getId();
    }

    public Long getBuildTriageId() {
        if (buildTriage == null) {
            return 0l;
        } else {
            return buildTriage.getId();
        }
    }

    public String getShortPriority() {
        return convertPriority(getTriageSpec().getPriority());
    }

    public int getDefaultPriority() {
        return triageSpec.getPriority();
    }

    public long getDeadline() {
        return buildTriage.getDeadline();
    }

    public int getDaysToDeadline() {
        return triageDeadline.getDaysToDeadline();
    }

    public int getDeadlinePriority() {
        return triageDeadline.getDeadlinePriority();
    }

    public String getDeadlineTooltip() {
        return triageDeadline.getDeadlineTooltip();
    }

    public int getBarNowPassing() {
        return getBarSize(getTotalNowPassing());
    }

    public int getBarFails() {
        return getBarSize(getTotalFails());
    }

    public int getBarNewFails() {
        return getBarSize(getTotalNewFails());
    }

    public int getBarAutoTriaged() {
        return getBarSize(getAutoTriaged());
    }

    public int getBarManualTriaged() {
        return getBarSize(getManualTriaged());
    }

    public int getManualTriaged() {
        return (int) (getTotalTriageDone() - getAutoTriaged());
    }

    public long getMaxExecutedTest(){
        return executor.getMaxTestExecuted();
    }

    public boolean getEnabled() {return executor.isHierarchicalyEnabled(); }

    public boolean containsSuite(String suiteName) {
        suiteName = suiteName.contains(" ") ? suiteName.replaceAll(" ","") : suiteName;
        return containsIgnoreCase(getExecutorName(), suiteName);
    }
    
    public String getProductVersion() {
        return getBuildTriage().getProductBuildVersion();
    }

    @JsonIgnore
    private int getBarSize(long testAmount) {
        if (testAmount == 0) {
            return 0;
        }

        double total = getTotalFails() + getTotalNewFails() + getTotalNowPassing() + (double) getTotalTriageDone();

        if (total == 0) {
            return 0;
        }
        double lineSize = (testAmount / total) * 100;

        return Boolean.logicalAnd(lineSize > 0, lineSize <= 1) ? 1 : Double.valueOf(lineSize).intValue();
    }

    public Map<String, List<TestTriageDTO>> getTestTriages() {
        return allTestTriages;
    }

    @JsonIgnore
    public ExecutorStat getNewExecutorStat() {
        long timestamp = DateUtils.now();

        return ExecutorStat.builder()
                .enabled(true)
                .timestamp(timestamp)
                .updated(timestamp)
                .actualDate(BaseDateFormat.format(new Date()))
                .state(StateType.PASS)
                .tags(getTags())
                .pass(getPassCount())
                .skip(getSkipCount())
                .newFails(getTotalNewFails())
                .fails(getTotalFails())
                .nowPassing(getTotalNowPassing())
                .toTriage(getToTriage())
                .duration(getDuration())
                .stabilityIndex(getStabilityIndex())
                .executionDate(BaseDateFormat.format(timestamp))
                .assignee(getAssignee().getRealname())
                .priority((int) getPriority())
                .productName(getProductName())
                .suiteName(getExecutorName())
                .containerName(getContainerName())
                .defaultPriority(getDefaultPriority())
                .deadline(BaseDateFormat.format(getDeadline()))
                .daysToDeadline(getDaysToDeadline())
                .deadlinePriority(getDeadlinePriority())
                .evolutionPass(0)
                .evolutionSkip(0)
                .evolutionNewFails(0)
                .evolutionFails(0)
                .evolutionNowPassing(0)
                .evolutionToTriage(0)
                .maxExecutedTest(getMaxExecutedTest())
                .buildNumber(getBuildNumber())
                .executor(getExecutor())
                .lastBuildTriage(getBuildTriage())
                .totalTests(getTotalTests())
                .autoTriaged(getAutoTriaged())
                .build();

    }

    public boolean isEnabled() {
        return executor.isHierarchicalyEnabled() && buildTriage.isEnabled();
    }
}
