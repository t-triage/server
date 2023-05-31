/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.FunctionalityDTO;
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

@RequestMapping(API_FUNCTIONALITY_URI)
@Api(value = "Functionality", description = "Here you will find all those operations related with Functionality entities", tags = {"Functionality"})
public interface FunctionalityController extends BaseController<FunctionalityDTO> {
    @ApiOperation(value = "", notes = "Uploads a Json file to the executor")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Tests uploaded successfully", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = "/newFunctionality",  method = POST, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_ADMIN, ROLE_SERVICE})
    ResponseEntity<FunctionalityDTO> createNewFunctionality(@RequestParam(value = "manualTestCaseId", required = true) Long manualTestCaseId, @RequestBody FunctionalityDTO entity);

    @ApiOperation(value = "", notes = "Search functionality by external Id")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "functionality search completed", response = Page.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = FIND_FUNCTIONALITY_BY_EXTERNAL_ID, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value =  {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<FunctionalityDTO> findFunctionalityByExternalId(@RequestParam(value = "functionalityExternalId", required = true) String externalId);
    
    
    @ApiOperation(value = "", notes = "Search functionality by name")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "functionality search complete", response = Page.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not complete")
            })
    @RequestMapping(value = SEARCH, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value =  {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<Page<FunctionalityDTO>> searchFunctionality(@RequestParam(value = "functionality", required = true) String name);
}
