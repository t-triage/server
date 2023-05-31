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
public class IssueTicketDTO extends BaseDTO {

    private String summary;
    private String url;
    private String urlKey;
    private String component;
    private String file;
    private int priority;
    private long dueDate;
    private String description;
    private UserDTO assignee;
    private Long note;
    private Long product;
    private String issueType;
    private Long testCaseId;

}
