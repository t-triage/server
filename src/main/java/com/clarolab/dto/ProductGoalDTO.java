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
public class ProductGoalDTO extends BaseDTO {

    private Long expectedTestCase;
    private Long requiredTestCase;

    private Long expectedPassRate;
    private Long requiredPassRate;
}
