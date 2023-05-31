/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.view;

import com.clarolab.model.User;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@ApiModel
public class GroupedStatView implements View {

    private String name;
    private String shortName;
    private long timestamp;
    private String date;

    private User assignee;

    private String productName;
    private String containerName;
    private String executorName;
    private String buildName;

    private long amountOfJobs;
    private long amountOfBuilds;

    private long total;
    private long totalUnique;
    private long passed;
    private long fails;
    private long skip;
    private long triaged;
    private long newFails;
    private long nowPassing;
    private long toTriage;
    private long permanent;
    private long flaky;

    private long newFixes;
    private long newTests;
    private long pending; //pending to fix issues

    private long passingIssues;
    private long openIssues;
    private long reopenIssues;

    private List<String> testsPass;
    private List<String> testsFail;
    private List<String> testsSkip;
    private List<String> testsToTriage;
    private List<String> testsTriaged;

    private List<String> filedProductBugs;
    private List<String> filedAutomations;


    @Builder
    private GroupedStatView(String name, String shortName, long timestamp, String date, User assignee, String productName, String containerName, String executorName, String buildName, long amountOfJobs, long amountOfBuilds, long total, long totalUnique, long passed, long fails, long flaky, long skip, long triaged, long newFails, long nowPassing, long toTriage, long permanent, long newFixes, long newTests, long pending, long passingIssues, long openIssues, long reopenIssues, List<String> testsPass, List<String> testsFail, List<String> testsSkip, List<String> testsToTriage, List<String> testsTriaged, List<String> filedProductBugs, List<String> filedAutomations) {
        this.name = name;
        this.shortName = shortName;
        this.timestamp = timestamp;
        this.date = date;
        this.assignee = assignee;
        this.productName = productName;
        this.containerName = containerName;
        this.executorName = executorName;
        this.buildName = buildName;
        this.amountOfJobs = amountOfJobs;
        this.amountOfBuilds = amountOfBuilds;
        this.total = total;
        this.totalUnique = totalUnique;
        this.passed = passed;
        this.fails = fails;
        this.flaky = flaky;
        this.skip = skip;
        this.triaged = triaged;
        this.newFails = newFails;
        this.nowPassing = nowPassing;
        this.toTriage = toTriage;
        this.permanent = permanent;
        this.newFixes = newFixes;
        this.newTests = newTests;
        this.pending = pending;
        this.passingIssues = passingIssues;
        this.openIssues = openIssues;
        this.reopenIssues = reopenIssues;
        this.testsPass = testsPass;
        this.testsFail = testsFail;
        this.testsSkip = testsSkip;
        this.testsToTriage = testsToTriage;
        this.testsTriaged = testsTriaged;
        this.filedProductBugs = filedProductBugs;
        this.filedAutomations = filedAutomations;
    }
}
