/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.event.analytics;

import com.clarolab.model.BuildTriage;
import com.clarolab.model.Entry;
import com.clarolab.model.Executor;
import com.clarolab.model.Product;
import com.clarolab.model.types.StateType;
import lombok.*;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_STAT_EXECUTOR;

@Entity
@Table(name = TABLE_STAT_EXECUTOR, indexes = {
        @Index(name = "IDX_EXECUTOR_DATE", columnList = "actualDate"),
        @Index(name = "IDX_EXECUTOR_ID", columnList = "executor_id")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExecutorStat extends Entry {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executor_id")
    private Executor executor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buildTriage_id")
    private BuildTriage lastBuildTriage;
    private Integer buildNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private String actualDate;
    private StateType state;
    private String tags;

    private long pass;
    private long skip;
    private long newFails;
    private long fails;
    private long nowPassing;
    private long toTriage;
    private Long totalTests;
    private Long autoTriaged;

    private double duration;
    private double stabilityIndex;
    private String executionDate;
    private String assignee;
    private int priority;
    private String productName;
    private String suiteName;
    private String containerName;
    private int defaultPriority;


    private String deadline;
    private int daysToDeadline;
    private int deadlinePriority;

    private int evolutionPass;
    private int evolutionSkip;
    private int evolutionNewFails;
    private int evolutionFails;
    private int evolutionNowPassing;
    private int evolutionToTriage;

    private long maxExecutedTest;

    @Builder
    private ExecutorStat(Long id, boolean enabled, long updated, long timestamp, long totalTests, int buildNumber, Executor executor, BuildTriage lastBuildTriage, String actualDate, StateType state, String tags, long pass, long skip, long newFails, long fails, long nowPassing, long toTriage, double duration, double stabilityIndex, String executionDate, String assignee, int priority, String productName, String suiteName, String containerName, int defaultPriority, String deadline, int daysToDeadline, int deadlinePriority, int evolutionPass, int evolutionSkip, int evolutionNewFails, int evolutionFails, int evolutionNowPassing, int evolutionToTriage, long maxExecutedTest, long autoTriaged, Product product) {
        super(id, enabled, updated, timestamp);
        this.executor = executor;
        this.lastBuildTriage = lastBuildTriage;
        this.actualDate = actualDate;
        this.state = state;
        this.tags = tags;
        this.pass = pass;
        this.skip = skip;
        this.newFails = newFails;
        this.fails = fails;
        this.nowPassing = nowPassing;
        this.toTriage = toTriage;
        this.duration = duration;
        this.stabilityIndex = stabilityIndex;
        this.executionDate = executionDate;
        this.assignee = assignee;
        this.priority = priority;
        this.productName = productName;
        this.suiteName = suiteName;
        this.containerName = containerName;
        this.defaultPriority = defaultPriority;
        this.deadline = deadline;
        this.daysToDeadline = daysToDeadline;
        this.deadlinePriority = deadlinePriority;
        this.evolutionPass = evolutionPass;
        this.evolutionSkip = evolutionSkip;
        this.evolutionNewFails = evolutionNewFails;
        this.evolutionFails = evolutionFails;
        this.evolutionNowPassing = evolutionNowPassing;
        this.evolutionToTriage = evolutionToTriage;
        this.buildNumber = buildNumber;
        this.totalTests = totalTests;
        this.maxExecutedTest = maxExecutedTest;
        this.autoTriaged = autoTriaged;
        this.product = product;
    }

    public int getEvolutionTotal() {
        return evolutionPass + evolutionSkip + evolutionNewFails + evolutionFails + evolutionNowPassing;
    }

    public long getTotalPass() {
        return pass + nowPassing;
    }

    public long getTotalFails() {
        return fails + newFails;
    }

}
