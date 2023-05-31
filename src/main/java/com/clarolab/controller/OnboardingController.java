/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;

import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.GuideDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@CrossOrigin
@RequestMapping(API_ONBOARDING_URI)
@Api(value = "Statistics", description = "Onboard tooltips, helps, surveys", tags = {"Onboard"})
public interface OnboardingController extends BaseController<GuideDTO> {

    @ApiOperation(value = "", notes = "Available guides")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Guides available for the page and user", response = Long.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = ITEMS, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<GuideDTO>> list(@RequestParam(value = "page", required = true) String page);

    @ApiOperation(value = "", notes = "Switch the pin state of the test case")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Assign the answer", response = Boolean.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = ASSIGN, method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> assignAnswer(@RequestParam(value = "guideid", required = true) Long guideid, @RequestParam(value = "answerType", required = true) int answerType, @RequestParam(value = "answer", required = false) String answer);
}