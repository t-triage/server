/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PipelineDTO extends BaseDTO {
    private String name;
    private String description;
    private UserDTO assignee;
    private TriageSpecDTO triageSpec;
    private String status;
    private int buildNumber;

    private long deadline;
    private String deadlinePriority;
    private String deadlineTooltip;
    private boolean triaged;

    private long totalFails;
    private long totalNewFails;
    private long totalNewPass;
    private long totalNowPassing;
    private long totalTests;
    private long totalTestsToTriage;
    private long totalTriageDone;
    private long totalNotExecuted;

    private long toTriage;
    private long barAutoTriaged;
    private long barFails;
    private long barManualTriaged;
    private long barNewFails;
    private long barNowPassing;
    private long barNotExecuted;

    private String productName;
    private int priority;

    //to add
    private int autoTriaged;
    private int passCount;
    private int manualTriaged;
    private long executedDate;
    private int daysToDeadline;

}
