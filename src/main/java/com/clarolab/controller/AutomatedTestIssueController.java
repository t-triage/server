/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;

import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.AutomatedTestIssueDTO;
import com.clarolab.dto.NewsBoardDTO;
import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


@RequestMapping(API_AUTOMATED_TEST_URI)
@Api(value = "AutomatedTestIssue", description = "Here you will find all those operations related with issues related entities", tags = {"Issues"})
public interface AutomatedTestIssueController extends BaseController<AutomatedTestIssueDTO> {

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
    ResponseEntity<Page<AutomatedTestIssueDTO>> list(@RequestParam(value = "query", required = false) String[] criteria, Pageable pageable, @RequestParam(value = "filter", required = true) String filter) throws IOException;

    @ApiOperation(value = "", notes = "Automated test pending to be fixed ")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Number of Automation Issues pending to be fixed", response = Long.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = API_AUTOMATED_TEST_PENDING, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Long> automationIssuesPendingToFix();

    
    @ApiOperation(value = "", notes = "Latest events")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Events answered", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not complete")
            })
    @RequestMapping(value = SUGGESTED_URI, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<NewsBoardDTO>> latestNews();


}
