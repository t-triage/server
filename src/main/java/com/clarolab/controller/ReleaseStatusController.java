/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;

import com.clarolab.controller.error.ErrorInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping(API_RELASE_STATUS_URI)
@Api(value = "Status", description = "Here you will find all the information about the release statuis", tags = {"Status"})
@Secured(value = {ROLE_USER, ROLE_ADMIN, ROLE_SERVICE})
public interface ReleaseStatusController extends SecuredController{

    @ApiOperation(value = "", notes = "get the triage status by product")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Product status info returned", response = Boolean.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = PRODUCT + ID, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> getProductStatus(@PathVariable Long id);

    @ApiOperation(value = "", notes = "get the triage status by container")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Container status info returned", response = Boolean.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = CONTAINER + ID, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> getContainerStatus(@PathVariable Long id);

    @ApiOperation(value = "", notes = "get the triage status by executor")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Executor status info returned", response = Boolean.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = EXECUTOR + ID, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> getExecutorStatus(@PathVariable Long id);

    @ApiOperation(value = "", notes = "get the triage status by pipeline")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Pipeline status info returned", response = Boolean.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = PIPELINE + ID, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> getPipelineStatus(@PathVariable Long id);

    @RequestMapping(value = PIPELINE + HELP + ID, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<String> getPipelineHelp(@PathVariable Long id);
}
