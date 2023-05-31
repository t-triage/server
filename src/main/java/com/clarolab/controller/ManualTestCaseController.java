/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.ManualTestCaseDTO;
import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RequestMapping(API_MANUAL_TEST_URI)
@Api(value = "ManualTestCase", description = "Here you will find all the operations related with ManualTestCase entities", tags = {"ManualTestCase"})
public interface ManualTestCaseController extends BaseController<ManualTestCaseDTO> {

    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "Query criteria in the format: <b>'property(:|&#60;|&#62;)value'</b>. I.e: propname:value. " +
                            "Default filter is empty. " +
                            ">Multiple query criteria are supported."),
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "Number of records per page."),
            @ApiImplicitParam(name = "filter", dataType = "string", paramType = "query", required = true,
                    value = "Filters."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "Sorting criteria in the format: <b>'property(,asc|desc)'</b>. I.e: propname,asc. " +
                            "Default sort order is ascending. " +
                            "Multiple sort criteria are supported.")})
    @RequestMapping(value = LIST_PATH + FILTERS, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Page<ManualTestCaseDTO>> list(@RequestParam(value = "query", required = false) String[] criteria, Pageable pageable, @RequestParam(value = "filter", required = true) String filter) throws IOException;

    @ApiOperation(value = "", notes = "Search functionality by name")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "functionality search complete", response = Page.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not complete")
            })
    @RequestMapping(value = SEARCH_FUNCTIONALITY, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value =  {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<Page<String>> searchFunctionality(@RequestParam(value = "functionality", required = true) String name);

    @ApiOperation(value = "", notes = "Search for manualTestCases that need automation")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "search complete", response = Page.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not complete")
            })
    @RequestMapping(value = SEARCH_AUTOMATION, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<Page<ManualTestCaseDTO>> searchAutomation();

    @ApiOperation(value = "", notes = "Import manual test cases report")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "import complete", response = Page.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not complete")
            })
    @RequestMapping(value = IMPORT_REPORT, method = POST, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<Boolean> importReport(@RequestBody String file) throws IOException;

    // v1/manualTest/updateFunctionality
    @ApiOperation(value = "", notes = "Update String functionality to Class")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Update complete", response = Page.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not complete")
            })
    @RequestMapping(value = "/updateFunctionality", method = POST, produces = APPLICATION_JSON_VALUE)
    @Secured(value =  {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<Boolean> updateFunctionality() throws IOException;

    @RequestMapping(value = "/linkManualToAutomated", method = GET )
    ResponseEntity<Boolean> linkManualTestToAutomatedTest(@RequestParam Long manualTestId ,@RequestParam Long automatedTestId);

    @ApiOperation(value = "" , notes = "return ManualTestCases since any timestamp")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics"),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = MANUALTESTCASE_SINCE , method = GET , produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<ManualTestCaseDTO>> getManualTestSince(@RequestParam (value = "timestamp", required = true )long timestamp);
}
