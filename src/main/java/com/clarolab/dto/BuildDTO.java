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
public class BuildDTO extends BaseDTO {

    private int number;
    private long executedDate;
    private boolean processed;
    private String buildId;
    private String displayName;
    private String populateMode;

    private String status;
    private Long reportId;
    private Long executorId;
}
