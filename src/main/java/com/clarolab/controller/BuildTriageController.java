/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.BuildTriageDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(API_BUILDREPORT_URI)
@Api(value = "Triage", description = "Here you will find all those operations related with Triage entities", tags = {"Triage"})
public interface BuildTriageController extends BaseController<BuildTriageDTO> {

    @ApiOperation(value = "", notes = "Return the build triage status")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Build Detail returned successfully", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = DETAIL + ID, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN})
    ResponseEntity<String> getTextDetail(@PathVariable Long id);
}
