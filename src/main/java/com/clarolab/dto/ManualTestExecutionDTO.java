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
public class ManualTestExecutionDTO extends BaseDTO{
    private ManualTestCaseDTO testCase;
    private ManualTestPlanDTO testPlan;
    private UserDTO assignee;
    private String environment;
    private String comment;
    private String Status;

    private int executionOrder;
}
