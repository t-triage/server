/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManualTestCaseDTO extends BaseDTO{
    private String name;
    private String requirement;
    private Long requirementId;
    private boolean needsUpdate;
    private List<ManualTestStepDTO> steps;
    private Long mainStepId;
    private String priority;
    private List<String> techniques;
    private Long productId;
    private String productName;
    private String suite;
    private NoteDTO note;
    private Long component1Id;
    private String component1Name;
    private Long component2Id;
    private String component2Name;
    private Long component3Id;
    private String component3Name;
    private String functionality;
    private FunctionalityDTO functionalityEntity;
    private UserDTO owner;
    private UserDTO lastUpdater;
    private UserDTO automationAssignee;
    private String automationStatus;
    private Long lastExecutionId;
    private Long lastExecutionDate;
    private String lastExecutionStatus;
    private String lastExecutionPlan;
    private String lastExecutionAssignee;
    private Long automatedTestCaseId;
    private String externalId;
    private String automationExternalId;
}
