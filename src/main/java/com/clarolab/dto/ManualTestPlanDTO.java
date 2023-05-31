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
public class ManualTestPlanDTO extends BaseDTO {
    private String name;
    private String description;
    private String environment;
    private UserDTO assignee;
    private long fromDate;
    private long toDate;
    private String status;
}
