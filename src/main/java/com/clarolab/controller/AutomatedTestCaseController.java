/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.dto.AutomatedTestCaseDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping(API_AUTOMATED_TEST_CASE_URI)
@Api(value = "AutomatedTestCase", description = "Here you will find all those operations related with TestCase entities", tags = {"TestCase"})
public interface AutomatedTestCaseController extends BaseController<AutomatedTestCaseDTO> {

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
    ResponseEntity<Page<AutomatedTestCaseDTO>> list(@RequestParam(value = "query", required = false) String[] criteria, Pageable pageable, @RequestParam(value = "filter", required = true) String filter) throws IOException;


}
