/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.TrendGoalDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RequestMapping(API_TREND_GOAL_URI)
@Api(value = "Executors", description = "Here you will find all those operations related with Executors goals", tags = {"Executors"})
public interface TrendGoalController extends BaseController<TrendGoalDTO> {
    @ApiOperation(value = "", notes = "Uploads a Json file to the executor")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Tests uploaded successfully", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = "/newGoal",  method = POST, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_ADMIN, ROLE_SERVICE})
    ResponseEntity<TrendGoalDTO> createNewGoal(@RequestParam(value = "executorid", required = true) Long executorid, @RequestBody TrendGoalDTO entity);

}
