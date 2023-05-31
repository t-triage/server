/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import lombok.*;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_TRIAGE_SPEC;

@Entity
@Table(name = TABLE_TRIAGE_SPEC, indexes = {
        @Index(name = "IDX_TRIAGEFLOWSPEC_PRIORITY", columnList = "priority"),
        @Index(name = "IDX_TRIAGEFLOWSPEC_EXEC_CONT", columnList = "executor_id,container_id,pipeline_id", unique = true),
        @Index(name = "IDX_TRIAGEFLOWSPEC_EXEC", columnList = "executor_id"),
        @Index(name = "IDX_TRIAGEFLOWSPEC_PIPELINE", columnList = "pipeline_id"),
        @Index(name = "IDX_TRIAGEFLOWSPEC_LIST", columnList = "user_id,container_id,lastCalculatedDeadline,priority"),
        @Index(name = "IDX_TRIAGEFLOWSPEC_CONTAINER", columnList = "container_id")
},
        uniqueConstraints = {@UniqueConstraint(columnNames = {"container_id", "executor_id", "pipeline_id"})})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TriageSpec extends Entry{

    @ManyToOne(fetch = FetchType.EAGER) //could be not assigned
    @JoinColumn(name = "user_id")
    private User triager;
    private int priority;

    private int expectedPassRate;
    private int expectedMinAmountOfTests;

    @ManyToOne(fetch = FetchType.EAGER) //could be not assigned
    @JoinColumn(name = "executor_id")
    private Executor executor;

    @ManyToOne(fetch = FetchType.EAGER, optional = false) //must be assigned
    @JoinColumn(name = "container_id")
    private Container container;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_id")
    private Pipeline pipeline;

    // Next deadline fields.
    private String frequencyCron;
    private int everyWeeks;
    private long lastCalculatedDeadline;
    private int lastCalculatedWeek;

    @Builder
    private TriageSpec(Long id, boolean enabled, long updated, long timestamp, User triager, int priority, int expectedPassRate, int expectedMinAmountOfTests, Executor executor, Container container, String frequencyCron, int everyWeeks, Pipeline pipeline) {
        super(id, enabled, updated, timestamp);
        this.triager = triager;
        this.priority = priority;
        this.expectedPassRate = expectedPassRate;
        this.expectedMinAmountOfTests = expectedMinAmountOfTests;
        this.executor = executor;
        this.container = container;
        this.pipeline = pipeline;

        this.frequencyCron = frequencyCron;
        this.everyWeeks = everyWeeks;
        this.lastCalculatedDeadline = 0;
        this.lastCalculatedWeek = 0;
    }

    public Long getExecutorId(){
        if (executor == null) {
            return null;
        }
        return executor.getId();
    }

}
