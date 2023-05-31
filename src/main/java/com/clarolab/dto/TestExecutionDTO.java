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
public class TestExecutionDTO extends BaseDTO {


    private double duration;
    private int age;
    private String errorDetails;
    private String errorStackTrace;
    private String status;
    private int failedSince;
    private Long report;
    private String standardOutput;
    private String[] screenshotURLs;
    private String[] videoURLs;
    private String skippedMessage;

    private String name;
    private String suiteName;
    private String path;
    private String groupName;
    private String displayName;
    private String shortName;
    private String[] parameters;

    private boolean pin;
    private UserDTO pinAuthor;
    private long pinDate;

}
