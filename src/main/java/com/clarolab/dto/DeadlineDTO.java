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
public class DeadlineDTO extends BaseDTO {

    private String name;
    private String description;
    private Long product;
    private Long note;
    private long deadlineDate;
}
