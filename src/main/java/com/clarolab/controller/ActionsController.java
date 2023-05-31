/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;

import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.AutomatedTestIssueDTO;
import com.clarolab.dto.BuildTriageDTO;
import com.clarolab.dto.TestTriageDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RequestMapping(API_ACTIONS_URI)
@Api(value = "Actions", description = "Here you will find all those operations related with the actions from the UI", tags = {"Actions"})
public interface ActionsController extends SecuredController{

    @ApiOperation(value = "", notes = "Assign one suite to an user")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "User assigned successfully to the Build Triage", response = BuildTriageDTO.class),
                    @ApiResponse(code = 422, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = ACTION_ASSIGN_JOB, method = POST, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<BuildTriageDTO> setAssigneeToBuild(@RequestParam(value = "userid", required = true) Long userId, @RequestParam(value = "buildid", required = true) Long buildId);

    @ApiOperation(value = "", notes = "Assign one test to an user")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "User assigned successfully to the Test Triage", response = TestTriageDTO.class),
                    @ApiResponse(code = 422, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = ACTION_ASSIGN_TEST, method = POST, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<TestTriageDTO> setAssigneeToTest(@RequestParam(value = "userid", required = true) Long userId, @RequestParam(value = "testid", required = true) Long testId);

    @ApiOperation(value = "", notes = "Mark a Suite as Triaged")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Suite triaged successfully", response = BuildTriageDTO.class),
                    @ApiResponse(code = 422, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = ACTION_TRIAGED_JOB, method = POST, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<BuildTriageDTO> markJobAsTriaged(@RequestParam(value = "buildid", required = true) Long buildId, @RequestParam(value = "note", required = false) String notes);

    @ApiOperation(value = "", notes = "Mark a Test as Triaged")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Test triaged successfully", response = TestTriageDTO.class),
                    @ApiResponse(code = 422, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = ACTION_TRIAGED_TEST, method = POST, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<TestTriageDTO> markTestAsTriaged(@RequestParam(value = "userid", required = true) Long userId, @RequestParam(value = "testid", required = true) Long testId);

    @ApiOperation(value = "", notes = "Mark a Suite as Invalid")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Suite Marked as invalid successfully", response = BuildTriageDTO.class),
                    @ApiResponse(code = 422, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = ACTION_INVALIDATE_JOB, method = POST, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<BuildTriageDTO> markJobAsInvalid(@RequestParam(value = "buildid", required = true) Long buildId, @RequestParam(value = "note", required = false) String notes);

    @ApiOperation(value = "", notes = "Mark a Suite as Disabled, it won'+t be triaged anymore")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Suite disabled successfully", response = BuildTriageDTO.class),
                    @ApiResponse(code = 422, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = ACTION_DISABLE_JOB, method = POST, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<BuildTriageDTO> markJobAsDisabled(@RequestParam(value = "buildid", required = true) Long buildId, @RequestParam(value = "note", required = false) String notes);

    @ApiOperation(value = "", notes = "Approve Automatic Triage")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Automatic Triage Approved successfully", response = Boolean.class),
                    @ApiResponse(code = 422, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = ACTION_APPROVE_JOB, method = POST, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> approveAutomaticTriage(@RequestParam(value = "buildid", required = true) Long buildId);


    @ApiOperation(value = "", notes = "Process recently imported data from CI tool")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Automatic Triage Approved successfully", response = Boolean.class),
                    @ApiResponse(code = 422, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = PROCESS, method = POST, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> processImportedSuites();

    @ApiOperation(value = "", notes = "Assign one automation issue to an user")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "User assigned successfully to the Automation Issue", response = AutomatedTestIssueDTO.class),
                    @ApiResponse(code = 422, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = ACTION_ASSIGN_AUTOMATION_ISSUE, method = POST, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<AutomatedTestIssueDTO> setAssigneeToAutomationIssue(@RequestParam(value = "userid", required = true) Long userId, @RequestParam(value = "issueid", required = true) Long issueId);


    @ApiOperation(value = "", notes = "Mark a Suite as Enabled, it will be triaged again")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Suite enabled successfully", response = BuildTriageDTO.class),
                    @ApiResponse(code = 422, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = ACTION_ENABLE_JOB, method = POST, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<BuildTriageDTO> markJobAsEnabled(@RequestParam(value = "buildid", required = true) Long buildId);
}
