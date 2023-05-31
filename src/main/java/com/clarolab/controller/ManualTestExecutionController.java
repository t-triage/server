/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.ManualTestCaseDTO;
import com.clarolab.dto.ManualTestExecutionDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping(API_MANUAL_EXECUTION_URI)
@Api(value = "ManualTestExecution", description = "Here you will find all the operations related with ManualTestExecution entities", tags = {"ManualTestExecution"})
public interface ManualTestExecutionController extends BaseController<ManualTestExecutionDTO> {
    @ApiOperation(value = "", notes = "Return an List of Executions.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Executions list returned successfully.", response = ManualTestExecutionDTO.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = FIND_ID_PATH, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<ManualTestExecutionDTO>> findByPlanId(@PathVariable Long id);

    @ApiOperation(value = "", notes = "Given a manual test plan and case delete the corresponding manual test execution.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Manual test execution deleted successfully.", response = Long.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = DELETE_BY_PLAN_AND_CASE, method = DELETE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Long> deleteByPlanAndCase(@RequestParam(value = "manualTestPlanId", required = true) Long manualTestPlanId, @RequestParam(value = "manualTestCaseId", required = true) Long manualTestCaseId);

    @ApiOperation(value = "" , notes = "return ManualTestExecution since any timestamp")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics"),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = MANUALTESTEXECUTION_SINCE , method = GET , produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<ManualTestExecutionDTO>> getManualTestExecutionSince(@RequestParam (value = "timestamp", required = true )long timestamp);
}
