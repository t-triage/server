/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestTriageDTO extends BaseDTO {

    private String currentState;
    private String deducedReason;
    private String applicationFailType;
    private String testFailType;
    private String tags;
    private String file;
    private String externalBuildURL;
    private int rank;
    private long snooze;
    private boolean triaged;
    private boolean expired;
    private boolean autoTriaged;
    private boolean flaky;
    private String executorName;
    private String containerName;
    private Long executorId;
    private Long containerId;
    private Long productId;
    private Long buildTriageId;
    private UserDTO triager;
    private NoteDTO note;
    private Long build;
    private Long testExecutionId;
    private TestExecutionDTO testExecution;
    private boolean hasSteps;
    private String productPackages;
    private List<Long> previousTriage;
    private Long automatedTestIssueId;
    private Long issueTicketId;
    private Long testCaseId;

    private Long executionDate;
    private int buildNumber;

    // Week History
    private NoteDTO pastNote;
    private String pastState;
    private String pastApplicationFailType;
    private String pastTestFailType;
    private long pastTriageTimestamp;
    private boolean newInfo;

    private NoteDTO lastNote;

}
