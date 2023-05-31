/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.BuildDTO;
import com.clarolab.dto.push.DataDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RequestMapping(API_BUILD_URI)
@Api(value = "Builds", description = "Here you will find all those operations related with Build entities", tags = {"Builds"})
public interface BuildController extends BaseController<BuildDTO> {

    @ApiOperation(value = "", notes = "Create a new Build from a push.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "Entry added successfully.", response = BuildDTO.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = PUSH_PATH, method = POST, produces = APPLICATION_JSON_VALUE)
    @Secured(value = ROLE_SERVICE)
    ResponseEntity<BuildDTO> push(@RequestBody DataDTO dto);

}
