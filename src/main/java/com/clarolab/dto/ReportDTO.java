/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO extends BaseDTO {

    private String type;
    private String status;
    private int passCount;
    private int failCount;
    private int skipCount;
    private int totalTest;
    private int buildNumber;
    private double duration;
    private long executiondate;
    private List<Long> testExecutions;
    private String description;
    private String productVersion;
}
