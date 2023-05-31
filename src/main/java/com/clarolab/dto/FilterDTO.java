package com.clarolab.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class FilterDTO {

    //ManualTestCase Filters
    private String lastExecution;
    /*private Long lastUpdate;*/
    private Long owner;
    private Long testPlan;
    private Long excludeTestPlan;
    private Integer techniques;
    private Boolean needsUpdate;
    private String requirement;
    private String name;
    private String suite;
    private Long component1;
    private Long component2;
    private Long component3;
    private Long component4;
    private Long component5;
    private Long component6;
    private List<Long> components;
    private String functionality;
    private Long functionalityEntity;
    private Integer priority;
    private Integer automationStatus;
    private Long lastUpdater;
    private Long id;
    private String externalId;

    //AutomatedTestCase Filters
   private Boolean hideNoSuite;
   private String currentState;
   private Long AutomatedAssignee;
   private String flakyTest;
    private Long pipeline;
    //ExecutorView Filters
    private Long containerId;
    private Boolean assignee;
    private Boolean failures;
    private Boolean hideDisabled;
    private String search;



    //AutomatedTestIssue Filters
    //private Boolean assignee;
    private Boolean pin;
    private String executorName;
    private Boolean passingIssues;
    private Boolean hideOld;
    private String sortBy;
    private Boolean automatedTriaged;
    private Boolean bugsOnly;


}
