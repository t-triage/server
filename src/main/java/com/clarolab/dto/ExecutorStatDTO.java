/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.dto;

import com.clarolab.model.types.StateType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutorStatDTO extends BaseDTO {

    private Long executorId;
    private Long lastBuildTriageId;

    private String actualDate;
    private StateType state;
    private String tags;

    private long pass;
    private long skip;
    private long newFails;
    private long fails;
    private long nowPassing;
    private long toTriage;
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

    private Long productId;

}
