/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;

import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.AutomatedComponentDTO;
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
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RequestMapping(API_AUTOMATED_COMPONENT_URI)
@Api(value = "AutomatedComponent", description = "Here you will find all the operations related with AutomatedComponent entities", tags = {"AutomatedComponent"})
public interface AutomatedComponentController extends BaseController<AutomatedComponentDTO>{

    @ApiOperation(value = "", notes = "Search Automated Component by name")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Automated Component search completed", response = Page.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = SEARCH, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value =  {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<Page<AutomatedComponentDTO>> search(@RequestParam(value = "name", required = false) String name);

    @ApiOperation(value = "", notes = "Search first Automated Component for each automated test")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Automated Component search completed", response = Page.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value =SUGGESTED_URI, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value =  {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<Page<AutomatedComponentDTO>> suggestedDefaultComponents();

    @ApiOperation(value = "", notes = "Search Automated Component by name in automated tests")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Automated Component search completed", response = Page.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = SEARCH + SUGGESTED_URI, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value =  {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<Page<AutomatedComponentDTO>> suggestedComponents(@RequestParam(value = "automatedComponentsIds", required = true) List<Long> automatedComponentIds);

    @ApiOperation(value = "", notes = "Assign a component to multiple TestCases")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Operation complete", response = Page.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not complete")
            })
    @RequestMapping(value = ASSIGN, method = POST, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<AutomatedComponentDTO> setComponentToTests(@RequestParam(value = "productComponentId", required = true) Long productComponentId, @RequestBody List<Long> testCaseIds);

    @ApiOperation(value = "", notes = "Given a automated component and test case, delete the relation")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Automated component - test relation deleted successfully.", response = Long.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = DELETE_BY_AUTOMATED_COMPONENT_AND_TEST, method = DELETE, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN})
    ResponseEntity<Long> deleteByComponentAndTestCase(@RequestParam(value = "automatedComponentId", required = true) Long automatedComponentId, @RequestParam(value = "testCaseId", required = true) Long testCaseId);

}
