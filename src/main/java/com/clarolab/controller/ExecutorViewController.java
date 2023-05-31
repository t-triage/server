/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;

import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.ReportDTO;
import com.clarolab.dto.UserDTO;
import com.clarolab.dto.db.TestTriagePassedDTO;
import com.clarolab.view.ExecutorView;
import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RequestMapping(API_VIEW_EXECUTORS_URI)
@Api(value = "Views", description = "Here you will find all those operations related with Views", tags = {"Views"})
public interface ExecutorViewController extends SecuredController{

    @ApiOperation(value = "", notes = "Return an Executor View.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Executor View returned successfully.", response = ExecutorView.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = READ_PATH, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<ExecutorView> find(@PathVariable Long id);

    @ApiOperation(value = "", notes = "Return an Executor View.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Executor View returned successfully.", response = ExecutorView.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = "/get/passed/{id}", method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<TestTriagePassedDTO>> findTestPass(@PathVariable Long id);

    @ApiOperation(value = "", notes = "List all Executor Views sorted by the page criteria.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Filtered list.", response = Page.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "Results page you want to retrieve (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "Number of records per page."),
            @ApiImplicitParam(name = "filter", dataType = "string", paramType = "query", required = true,
                    value = "Filters."),
            @ApiImplicitParam(name = "sort", allowMultiple = false, dataType = "string", paramType = "query",
                    value = "Sorting criteria in the format: <b>'ShortPriority(,asc|desc)'</b>. or <b>'ExecutorName(,asc|desc)'</b> I.e: ExecutorName,ASC. " +
                            "Default sort order is ascending. " +
                            "Only a single sort criteria are supported.")})
    @RequestMapping(value = LIST_PATH, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_ADMIN, ROLE_SERVICE, ROLE_USER})
    ResponseEntity<Page<ExecutorView>> list(Pageable page, @RequestParam(value = "filter", required = true) String filter) throws IOException;

    @ApiOperation(value = "", notes = "List all suggested Executor Views")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "A suggested list.", response = List.class),
            })
    @RequestMapping(value = SUGGESTED_URI, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<ExecutorView>> suggested();

    @ApiOperation(value = "", notes = "List all suggested Executor Views by assignee")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "A suggested list.", response = LinkedHashSet.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = USER + SUGGESTED_URI, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<LinkedHashSet<UserDTO>> suggestedAssignee(@RequestParam(value = "buildid", required = true) Long buildId);

    @ApiOperation(value = "", notes = "List all suggested Executor Views by assignee")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "A priority.", response = String.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = PRIORITY + ASSIGN, method = POST, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<String> assignPriority(@RequestParam(value = "buildid", required = true) Long buildId, @RequestParam(value = "priority", required = true) String priority);



    @ApiOperation(value = "", notes = "Return an List of Reports.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Executor View returned successfully.", response = ExecutorView.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = HISTORY + ID, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<ReportDTO>> history(@PathVariable Long id);

}
