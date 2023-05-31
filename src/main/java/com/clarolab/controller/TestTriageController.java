/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.TestTriageDTO;
import com.clarolab.dto.UpdateTriageDTO;
import com.clarolab.dto.db.TestTriageHistoryDTO;
import com.clarolab.dto.details.TestDetailDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(API_TESTCASEEPORT_URI)
@Api(value = "Triage", description = "Here you will find all those operations related with Triage entities", tags = {"Triage"})
public interface TestTriageController extends BaseController<TestTriageDTO> {

    @ApiOperation(value = "", notes = "Return the test triage detail")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Test detail returner successfully", response = TestDetailDTO.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = DETAIL + ID, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<TestDetailDTO> getTestDetail(@PathVariable Long id);


    @ApiOperation(value = "", notes = "Switch the pin state of the test case")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Pin/Unpinned successfully", response = TestTriageDTO.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = PIN, method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<TestTriageDTO> pin(@RequestParam(value = "testid", required = true) Long testId);

    @ApiOperation(value = "", notes = "Update a test triage draft")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Test triage draft updated successfully", response = TestTriageDTO.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = DRAFT, method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<TestTriageDTO> draftTriage(@RequestBody UpdateTriageDTO updateTriageDTO, @RequestParam(value = "triage", required = false) boolean triage);

    @ApiOperation(value = "", notes = "Triage all in a list of test triage")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "All test triaged", response = Boolean.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = TRIAGED_ALL, method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> triageAll(@RequestBody List<UpdateTriageDTO> updateTriageDTOList);


    @ApiOperation(value = "", notes = "Return the test triage history")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Test detail returner successfully", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = HISTORY + ID, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<TestTriageHistoryDTO>> getTestHistory(@PathVariable Long id);
}
