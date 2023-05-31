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
public class ErrorOccurrence extends Entry {

    private long suiteID;
    private String suiteName;
    private long testID;
    private String testName;
    private TestTriage testTriage;

    @Builder
    public ErrorOccurrence(long suiteID, String suiteName, long testID, String testName, TestTriage testTriage) {
        this.suiteID = suiteID;
        this.suiteName = suiteName;
        this.testID = testID;
        this.testName = testName;
        this.testTriage = testTriage;
    }
}

