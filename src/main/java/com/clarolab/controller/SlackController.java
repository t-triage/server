/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.SlackSpecDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping(API_SLACK_URI)
@Api(value = "Slack Spec", description = "Here you will find all those operations related with Setting Up Slack Connection", tags = {"Integrations"})
public interface SlackController extends BaseController<SlackSpecDTO> {


    @ApiOperation(value = "", notes = "Return Slack Spec")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Slack Spec returned", response = SlackSpecDTO.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = CONTAINER, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN})
    ResponseEntity<SlackSpecDTO> container(@RequestParam(value = "containerid", required = true) Long buildId);

    @ApiOperation(value = "", notes = "Return Slack Spec")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Slack Spec returned", response = SlackSpecDTO.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = EXECUTOR, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN})
    ResponseEntity<SlackSpecDTO> executor(@RequestParam(value = "executorid", required = true) Long buildId);

    @ApiOperation(value = "", notes = "Send a slack test message")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Slack message sent", response = SlackSpecDTO.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = TEST, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN})
    ResponseEntity<String> test(@RequestParam(value = "containerid", required = true) Long buildId);

}
