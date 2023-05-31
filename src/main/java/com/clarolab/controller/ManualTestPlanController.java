/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.ManualTestExecutionDTO;
import com.clarolab.dto.ManualTestPlanDTO;
import com.clarolab.dto.ManualTestPlanStatDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RequestMapping(API_MANUAL_PLAN_URI)
@Api(value = "ManualTestPlan", description = "Here you will find all the operations related with ManualTestPlan entities", tags = {"ManualTestPlan"})
public interface ManualTestPlanController extends BaseController<ManualTestPlanDTO> {

    @ApiOperation(value = "", notes = "Assign multiple ManualTestCases to a ManualTestPlan")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Operation complete", response = Page.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not complete")
            })
    @RequestMapping(value = ASSIGN_TO_PLAN, method = POST, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<Page<ManualTestExecutionDTO>> assignToTestPlan(@RequestParam(value = "manualTestPlanId", required = true) Long manualTestPlanId, @RequestBody List<Long> manualTestCaseIds);


    @ApiOperation(value = "", notes = "Return all ongoing test plan with totalized test executions")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = API_MANUAL_PLAN, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<ManualTestPlanStatDTO>> getOngoingManualTestPlans();
}
