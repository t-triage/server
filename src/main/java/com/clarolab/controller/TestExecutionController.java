/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.TestExecutionDTO;
import com.clarolab.dto.TestExecutionStepDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping(API_TESTCASE_URI)
@Api(value = "TestExecution", description = "Here you will find all those operations related with Test Executions entities", tags = {"TestExecution"})
public interface TestExecutionController extends BaseController<TestExecutionDTO> {

    @ApiOperation(value = "", notes = "Return a list of test names according to the name parameter")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Test name search completed", response = Page.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = SEARCH, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Page<String>> searchTestName(@RequestParam(value = "name", required = true) String name);

    @ApiOperation(value = "", notes = "Return a list of test names according to the name parameter")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Test name search completed", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STEPS + ID, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<TestExecutionStepDTO>> getTestSteps(@PathVariable Long id);

}
