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
public class ManualTestStepDTO extends BaseDTO{
    private Long testCaseId;
    private String step;
    private String expectedResult;
    private String data;
    private int stepOrder;
    private boolean main;
    private long externalId;
}
