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
public class TriageSpecDTO extends BaseDTO {

    private UserDTO triager;

    private int priority;
    private int expectedPassRate;
    private int expectedMinAmountOfTests;

    private Long executor;
    private Long container;
    private Long pipeline;

    private String frequencyCron;
    private int everyWeeks;

}
