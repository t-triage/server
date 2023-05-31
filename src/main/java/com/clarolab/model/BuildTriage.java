/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import com.clarolab.model.types.StateType;
import com.clarolab.util.LogicalCondition;
import com.clarolab.util.StringUtils;
import lombok.*;

import javax.persistence.*;

import static com.clarolab.model.helper.tag.TagHelper.AUTO_TRIAGED;
import static com.clarolab.model.helper.tag.TagHelper.NEED_TRIAGE;
import static com.clarolab.util.Constants.TABLE_BUILD_TRIAGE;

@Entity
@Table(name = TABLE_BUILD_TRIAGE, indexes = {
        @Index(name = "IDX_BUILDTRIAGE_LIST", columnList = "triaged,expired,enabled"),
        @Index(name = "IDX_BUILDTRIAGE_RANK", columnList = "rank"),
        @Index(name = "IDX_BUILDTRIAGE_TODAY", columnList = "enabled,user_id,triaged,deadline,rank"),
        @Index(name = "IDX_BUILDTRIAGE_DEADLINE", columnList = "executor_id,number,enabled"),
        @Index(name = "IDX_BUILDTRIAGE_EXPIRE_AGENT", columnList = "executor_id,number,enabled,triaged,expired"),
        @Index(name = "IDX_BUILDTRIAGE_LIST_CONT", columnList = "container_id,triaged,expired,enabled,executor_id"),
        @Index(name = "IDX_BUILDTRIAGE_CANDIDATES_CONT", columnList = "container_id,user_id"),
        @Index(name = "IDX_BUILDTRIAGE_CANDIDATES_EXEC", columnList = "executor_id,user_id"),
        @Index(name = "IDX_BUILDTRIAGE_LIST_EXEC_SEARCH", columnList = "executor_id,triaged,enabled,expired")
    }
)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BuildTriage extends Entry {

    @Enumerated
    @Column(columnDefinition = "smallint")
    private StateType currentState;

    private boolean expired = false;

    private String tags;

    private String file;

    private int rank;

    private boolean triaged;

    @ManyToOne(fetch = FetchType.EAGER) //could be not assigned
    @JoinColumn(name = "user_id")
    private User triager;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "note_id")
    private Note note;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "build_id")
    private Build build;

    private int number;
    private String productBuildVersion;

    // Variables that make faster queries, they are private for use
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "container_id")
    private Container container;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "executor_id")
    private Executor executor;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "spec_id")
    private TriageSpec spec;

    @Transient
    private TriageDeadline triageDeadline;
    private long deadline;

    @Builder
    private BuildTriage(Long id, boolean enabled, long updated, long timestamp, Build lastBuild, StateType currentState, User triager, Note note, String tags, String file, boolean triaged, int rank, TriageSpec triageSpec) {
        super(id, enabled, updated, timestamp);
        this.build = lastBuild;
        this.currentState = currentState;
        this.triager = triager;
        this.note = note;
        this.tags = tags;
        this.file = file;
        this.triaged = triaged;
        this.rank = rank;
        this.spec = triageSpec;
        newBuildSet();
    }

    public boolean isFiled() {
        return LogicalCondition.NOT(StringUtils.isEmpty(file));
    }

    public boolean isTriaggerAssigned() {
        return getTriager() != null;
    }

    public double getStabilityIndex() {
        return getReport().getStabilityIndex();
    }

    public Long getExecutionDate() {
        if (getReport() == null || getReport().getExecutiondate() == 0) {
            return getBuild().getExecutedDate();
        } else {
            return getReport().getExecutiondate();
        }
    }

    public int getTotalTest() {
        return getReport().getTotalTest();
    }

    public Report getReport() {
        return build.getReport();
    }

    public String getExecutorName() {
        return build.getExecutorName();
    }

    public String getTriagerName() {
        return triager.getRealname();
    }

    public String getContainerName() {
        return container.getName();
    }

    public Long getExecutorId() {
        return build.getExecutorId();
    }

    public void setBuild(Build newBuild) {
        this.build = newBuild;
        this.newBuildSet();
    }

    public void newBuildSet() {
        if (build != null) {
            this.container = build.getContainer();
            this.executor = build.getExecutor();
            this.number = build.getNumber();
            this.productBuildVersion = build.getProductVersion();
        }
    }

    public void initialize() {
        newBuildSet();
    }

    public void setTriageDeadline(TriageDeadline triageDeadline) {
        this.triageDeadline = triageDeadline;
        this.deadline = triageDeadline.getDeadline();
    }

    public String getProductName(){
        return container.getProductName();
    }

    public long getBuildId(){
        return build.getId();
    }

    public String getStandardOutputUrl() {
        return build.getStandardOutputUrl();
    }

    public boolean isAutomatedTriaged(){
        return tags.contains(AUTO_TRIAGED);
    }

    public boolean needTriage(){
        return tags.contains(NEED_TRIAGE);
    }

    public void expire() {
        setTriaged(true);
        setExpired(true);
    }

    public void activate() {
        setExpired(false);
        setEnabled(true);
        setTriaged(false);
    }

    public void setTriaged() {
        setTriaged(true);
        setEnabled(true);
    }

    public void invalidate() {
        setTriaged(true);
        setEnabled(false);
    }

    public void disable() {
        setEnabled(false);
    }

    public String toString() {
        return String.format("BuildTriage(Number: %d, executor: %s, container: %s)", getNumber(), getExecutorName(), getContainer().getName());
    }

}
