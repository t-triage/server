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
public class ExecutorDTO extends BaseDTO {

    private String name;
    private String description;
    private String url;
    private Long container;
    private Long trendGoal;
    private List<Long> lastBuilds;
    private TriageSpecDTO triageSpec;
    private String reportType;
}
