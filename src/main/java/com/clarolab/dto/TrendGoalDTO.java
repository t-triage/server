/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendGoalDTO extends BaseDTO {

    private Long expectedGrowth;
    private Long requiredGrowth;

    private Long expectedTriageDone;
    private Long requiredTriageDone;

    private Long expectedPassing;
    private Long requiredPassing;

    private Long expectedStability;
    private Long requiredStability;

    private Long expectedCommits;
    private Long requiredCommits;
}
