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
public class AutomatedTestIssueDTO extends BaseDTO {

    private String issueType;
    private String userFixPriority;
    private Long testCaseId;
    private Long productId;
    private String relatedIssueId;
    private UserDTO triager;
    private TestTriageDTO testTriage;
    private Long lastTestTriage;
    private long calculatedPriority;
    private NoteDTO note;
    private Long buildTriageId;
    private int failTimes;
    private int reopenTimes;
    private List<Boolean> successTrend;
    private String trendExplanation;

}
