/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import com.clarolab.model.types.ArtifactType;
import com.clarolab.model.types.PopulateMode;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.clarolab.util.Constants.TABLE_BUILD;

@Entity
@Table(name = TABLE_BUILD, indexes = {
        @Index(name = "IDX_BUILD_NUMBER", columnList = "number"),
        @Index(name = "IDX_BUILD_ENABLED", columnList = "enabled"),
        @Index(name = "IDX_BUILD_EXECUTOR", columnList = "executor_id"),
        @Index(name = "IDX_BUILD_ONGOING", columnList = "executor_id,enabled,id"),
        @Index(name = "IDX_BUILD_AGENT", columnList = "processed,enabled,number,executor_id"),
        @Index(name = "IDX_BUILD_NUMBER_EXECUTOR", columnList = "executor_id, number", unique = true)
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Build extends Entry implements Comparable<Build> {

    private int number;
    private long executedDate;
    private boolean processed;
    private String buildId;
    private String displayName;

    @Type(type = "org.hibernate.type.TextType")
    private String url;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private StatusType status;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Report report;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "executor_id")
    private Executor executor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "container_id")
    private Container container;

    @OneToMany(mappedBy = "build", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Artifact> artifacts;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private PopulateMode populateMode;

    @Builder
    private Build(Long id, boolean enabled, long updated, long timestamp, int number, long executedDate, boolean processed, String buildId, String displayName, StatusType status, Report report, Executor executor, String url, List<Artifact> artifacts, PopulateMode populateMode, Container container) {
        super(id, enabled, updated, timestamp);
        this.number = number;
        this.executedDate = executedDate;
        this.processed = processed;
        this.buildId = buildId;
        this.displayName = displayName;
        this.status = status;
        this.report = report;
        this.executor = executor;
        this.url = url;
        this.populateMode = populateMode;
        this.container = container;
        this.artifacts = Lists.newArrayList();

        if (CollectionUtils.isNotEmpty(artifacts)) {
            for (Artifact artifact : artifacts) {
                this.add(artifact);
            }
        }

        if (this.container == null) {
            this.container = getExecutorContainer();
        }
    }

    public Long getExecutorId() {
        return executor.getId();
    }

    public Long getReportId() {
        return report.getId();
    }

    public Executor getExecutor() {
        return executor;
    }

    public Container getExecutorContainer() {
        if (getExecutor() == null) {
            return null;
        } else {
            return getExecutor().getContainer();
        }
    }

    public Container getContainer() {
        if (container == null) {
            container = getExecutorContainer();
        }

        return container;
    }

    public Report getReport() {
        return report;
    }

    public List<TestExecution> getTestCases() {
        return report.getTestExecutions();
    }

    public List<TestExecution> getTestCasesUnique(boolean unique) {
        List<TestExecution> tests = getTestCases();
        if (!unique) {
            return tests;
        }

        // If the test is duplicate, the final one will be the PASS or the last one
        Map<TestCase, TestExecution> uniqueTests = new HashMap<>();
        for (TestExecution testCase : tests) {
            TestCase key = testCase.getTestCase();
            TestExecution existingTest = uniqueTests.getOrDefault(key, null);
            if (existingTest == null) {
                uniqueTests.put(key, testCase);
            } else {
                if (!existingTest.isPassed()) {
                    uniqueTests.put(key, testCase);
                }
            }
        }
        return new ArrayList<>(uniqueTests.values());
    }

    public String getExecutorName() {
        return executor.getName();
    }

    public String getExecutorULR() {
        return executor.getUrl();
    }

    public String getUrl() {
        if (url == null) {
            return getExecutorULR();
        }
        return url;
    }

    public void add(List<Artifact> artifacts) {
        //initArtifacts();
        artifacts.forEach(artifact -> this.add(artifact));
    }

    public void add(Artifact artifact) {
        initArtifacts();
        artifact.setBuild(this);
        this.getArtifacts().add(artifact);
    }

    public String getStandardOutputUrl() {
        Artifact standardOutput = this.artifacts.stream().filter(artifact -> artifact.getArtifactType().equals(ArtifactType.STANDARD_OUTPUT)).findFirst().orElse(null);
        if (standardOutput != null)
            return standardOutput.getUrl();
        return StringUtils.getEmpty();
    }

    @Override
    // Compare by number If build number exist
    // If not compares with executionDate
    public int compareTo(Build otherBuild) {
        if (otherBuild == null) {
            return 1;
        }
        if (number > 0 && otherBuild.getNumber() > 0) {
            return number - otherBuild.getNumber();
        }

        return (int) (this.getExecutedDate() - otherBuild.getExecutedDate());
    }

    public boolean isHierarchicalyEnabled() {
        return isEnabled() && executor.isHierarchicalyEnabled();
    }

    private void initArtifacts() {
        if (getArtifacts() == null)
            this.setArtifacts(Lists.newArrayList());
    }

    public void setExecutor(Executor newExecutor) {
        executor = newExecutor;

        container = getExecutorContainer();
    }
    
    public String getProductVersion() {
        return getReport().getProductVersion();
    }
}
