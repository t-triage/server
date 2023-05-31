/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.view;

import com.clarolab.dto.TestTriageDTO;
import com.clarolab.dto.UserDTO;
import com.clarolab.model.Pipeline;
import com.clarolab.model.TriageDeadline;
import com.clarolab.model.TriageSpec;
import com.clarolab.model.types.StateType;
import com.clarolab.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

import static com.clarolab.model.helper.PriorityHelper.convertPriority;
import static com.clarolab.util.StringUtils.containsIgnoreCase;


/* This is wrapper of Executor in order to be used in th  UI */
@Setter
@Getter
@NoArgsConstructor
@ApiModel
public class PipelineView implements View, SuiteView {

    @JsonIgnore
    private Pipeline pipeline;

    @JsonIgnore
    private TriageSpec triageSpec;

    @JsonIgnore
    private Map<String, List<TestTriageDTO>> allTestTriages;

    @JsonIgnore
    private TriageDeadline triageDeadline;

    @JsonIgnore
    private UserDTO triagger;

    private long toTriage;

    private long autoTriaged;

    //
    private long totalNewFails;
    private long totalNewPass;
    private long totalPass;
    private long totalSkip;
    private long totalTriageDone;
    private long totalNotExecuted;
    private long totalFails;
    private long totalTests;
    //

    @Builder
    private PipelineView(Pipeline pipeline, TriageSpec triageSpec, Map<String, List<TestTriageDTO>> allTestTriages, long toTriage, long autoTriaged, UserDTO triagger, long deadline,
                         long totalNewFails, long totalNewPass, long totalTriageDone, long totalNotExecuted, long totalFails, long totalTests, long totalPass, long totalSkip) {
        this.pipeline = pipeline;
        this.allTestTriages = allTestTriages;
        this.toTriage = toTriage;
        this.totalPass = totalPass;
        this.triagger = triagger;
        this.autoTriaged = autoTriaged;
        this.triageSpec = triageSpec;
        this.triageDeadline = this.rebuildTriageDeadline(deadline, triageSpec, toTriage);

        this.totalNewFails = totalNewFails;
        this.totalNewPass = totalNewPass;
        this.totalTriageDone = totalTriageDone;
        this.totalNotExecuted = totalNotExecuted;
        this.totalFails = totalFails;
        this.totalTests = totalTests;
        this.totalSkip = totalSkip;

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
        return pipeline.getName();
    }

    public String getBuildStandardOutputUrl() {
        return null;
    }
/*
    public String getProductName() {
        return pipeline.getProduct().getName();
    }
*/

    public int getBuildNumber() {
        return 0;
    }

    public boolean isTriaged() {
        return getToTriage() <= 0;
    }

    public boolean isExpired() {
        return false;
    }

    public String getContainerName() {
        return triageSpec.getContainer().getName();
    }

    public long getContainerId() {
        return triageSpec.getContainer().getId();
    }

    public String getConnectorName() {
        return triageSpec.getContainer().getConnectorName();
    }

    public String getExecutorDescription() {
        return pipeline.getDescription();
    }

    public String getExecutorUrl() {
        return null;
    }

    public String getExternalBuildURL() {
        return null;
    }

    public StateType getState() {
        return null;
    }

    @JsonIgnore
    private String getTags() {
        return null;
    }

    public long getTotalTestsToTriage() {
        return toTriage;
    }

    public long getTotalTriageDone() {
        return this.totalTriageDone;
        //return getTotalTests(TRIAGEDONE);
    }

    public long getTotalNotExecuted() {
        return this.totalNotExecuted;
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
        return totalPass;
    }

    public long getFailCount() {
        return totalFails;
    }

    public long getSkipCount() {
        return totalSkip;
    }

    public double getDuration() {
        return 0d;
    }

    public double getStabilityIndex() {
        return 100 - (double) getPriority();
    }

    public long getExecutiondate() {
        return DateUtils.now();
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
        return pipeline.getId();
    }

    public Long getExecutorId() {
        return pipeline.getId();
    }

    public Long getBuildTriageId() {
        if (pipeline == null) {
            return 0l;
        } else {
            return pipeline.getId();
        }
    }

    public String getShortPriority() {
        return convertPriority(getTriageSpec().getPriority());
    }

    public int getDefaultPriority() {
        return triageSpec.getPriority();
    }

    public long getDeadline() {
        return triageDeadline.getDeadline();
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

    public int getBarNotExecuted() {
        return getBarSize(getTotalNotExecuted());
    }

    public int getManualTriaged() {
        return (int) (getTotalTriageDone() - getAutoTriaged());
    }

    public long getMaxExecutedTest(){
        return totalTests;
    }

    public boolean getEnabled() {return pipeline.isEnabled(); }

    public boolean containsSuite(String suiteName) {
        suiteName = suiteName.contains(" ") ? suiteName.replaceAll(" ","") : suiteName;
        return containsIgnoreCase(getExecutorName(), suiteName);
    }
    
    public String getProductVersion() {
        return "";
    }

    @JsonIgnore
    private int getBarSize(long testAmount) {
        if (testAmount == 0) {
            return 0;
        }

        double total = getTotalFails() + getTotalNewFails() + getTotalNotExecuted() + (double) getTotalTriageDone();

        if (total == 0) {
            return 0;
        }
        double lineSize = (testAmount / total) * 100;

        return Boolean.logicalAnd(lineSize > 0, lineSize <= 1) ? 1 : Double.valueOf(lineSize).intValue();
    }

    public Map<String, List<TestTriageDTO>> getTestTriages() {
        return allTestTriages;
    }


    public boolean isEnabled() {
        return pipeline.isEnabled();
    }
}
