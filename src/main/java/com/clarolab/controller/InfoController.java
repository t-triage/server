/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.jira.model.JiraObject;
import com.clarolab.view.KeyValuePair;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping(API_INFO_URI)
@Api(value = "Info", description = "Here you will find all those operations related with System Info", tags = {"Info"})
@Secured(value = {ROLE_ANONYMOUS, ROLE_ADMIN, ROLE_USER} )
public interface InfoController extends SecuredController{

    @ApiOperation(value = "", notes = "Get the build info")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Build info returned", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = BUILD, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<KeyValuePair>> getBuildInfo();

    @ApiOperation(value = "", notes = "Get the welcome message")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Welcome info returned", response = String.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = WELCOME_MESSAGE, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<String> getWelcomeMessage();

    @ApiOperation(value = "", notes = "Get the Google UA")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Google UA returned", response = String.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = GOOGLE_ANALYTICS_UA, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<String> getGoogleAnalyticsUA();

    @ApiOperation(value = "", notes = "Get the Google UA")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Google UA returned", response = String.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = "health", method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<String> health();

    //This has to be completed on the JiraOAuthService
    @GetMapping(value = "/jiraauth", produces = APPLICATION_JSON_VALUE )
    ResponseEntity<JiraObject>  getRefreshCode(@RequestParam(value = "code", required = true) String code, @RequestParam(value = "state", required = true) Long productId);
}
