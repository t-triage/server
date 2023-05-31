/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.detail;

import com.clarolab.model.Entry;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

// TODO This will be transient for now until the the daily summary is done.

@Setter
@Getter
@NoArgsConstructor
public class TestDetail extends Entry {

    private long consecutiveFails;
    private long consecutivePasses;
    private long historicFails;
    private long historicPasses;
    private long failsSince;
    private long passSince;
    private Set<ErrorOccurrence> errorOccurrences;
    private Set<TestOccurrence> testOccurrences;

    @Builder
    public TestDetail(Long id, boolean enabled, long updated, long timestamp, long consecutiveFails, long consecutivePasses, long historicFails, long historicPasses, long failsSince, long passSince, Set<ErrorOccurrence> errorOccurrences, Set<TestOccurrence> testOccurrences) {
        super(id, enabled, updated, timestamp);
        this.consecutiveFails = consecutiveFails;
        this.consecutivePasses = consecutivePasses;
        this.historicFails = historicFails;
        this.historicPasses = historicPasses;
        this.failsSince = failsSince;
        this.passSince = passSince;
        this.errorOccurrences = errorOccurrences;
        this.testOccurrences = testOccurrences;
    }
}
