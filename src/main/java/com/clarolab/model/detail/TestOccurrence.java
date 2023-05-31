/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.detail;

import com.clarolab.model.Entry;
import com.clarolab.model.TestTriage;
import lombok.*;

// TODO This will be transient for now until the the daily summary is done.

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class TestOccurrence extends Entry {

    private long suiteID;
    private String suiteName;
    private TestTriage testTriage;

    @Builder
    public TestOccurrence(Long id, boolean enabled, long updated, long timestamp, long suiteID, String suiteName, TestTriage testTriage) {
        super(id, enabled, updated, timestamp);
        this.suiteID = suiteID;
        this.suiteName = suiteName;
        this.testTriage = testTriage;
    }
}
