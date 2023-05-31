/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.jira.dto;

import com.clarolab.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JiraConfigDTO extends BaseDTO {
    private String jiraUrl;
    private String projectKey;
    private String reporterEmail;
    private Long product;
    private String jiraVersion;
    private String initialStateId;
    private String resolvedStateId;
    private String closedStateId;
    private String reopenStateId;
    private String clientID;
    private String clientSecret;
    private String cloudId;
    private String issueType;
    private String defaultFieldsValues;
    private Boolean isFetchedToken;
    private Boolean isValidToken;

}
