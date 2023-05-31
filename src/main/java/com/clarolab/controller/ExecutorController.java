/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.ExecutorDTO;
import com.clarolab.dto.ExecutorStatChartDTO;
import com.clarolab.model.types.ReportType;
import com.clarolab.view.KeyValuePair;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RequestMapping(API_EXECUTOR_URI)
@Api(value = "Executors", description = "Here you will find all those operations related with Executors entities", tags = {"Executors"})
public interface ExecutorController extends BaseController<ExecutorDTO> {

    @ApiOperation(value = "", notes = "Load all the test suite from the CI tool.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Test suite populated successfully", response = Boolean.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = POPULATE, method = POST, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> populate(@RequestParam(value = "executorid", required = true) Long executorid);

    @ApiOperation(value = "", notes = "Search executor by name")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Executor search completed", response = Page.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = SEARCH, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Page<String>> search(@RequestParam(value = "name", required = true) String name);

    @ApiOperation(value = "", notes = "Return a list of container names according to the product id")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "The list of container names", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = NAMES + ID,  method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_ADMIN, ROLE_SERVICE})
    ResponseEntity<List<KeyValuePair>> getExecutorNames(@PathVariable Long id);


    @ApiOperation(value = "", notes = "Uploads a Json file to the executor")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Tests uploaded successfully", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = PUSH_PATH,  method = POST, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_ADMIN, ROLE_SERVICE})
    ResponseEntity<KeyValuePair> upload(@RequestParam(value = "executorid", required = true) Long executorid, @RequestBody String content, @RequestParam(value = "reportType", required = true)  ReportType reportType);

    @ApiOperation(value = "", notes = "Import CSV with Executors, Build and Test Cases")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Import complete", response = Page.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not complete")
            })
    @RequestMapping(value = IMPORT_REPORT, method = POST, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<String> importReport(@RequestBody String file);

    @ApiOperation(value = "", notes = "Import CSV with Executors, Build and Test Cases")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Import complete", response = Page.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not complete")
            })
    @RequestMapping(value = CSV, method = POST, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<String> importCSVReport(@RequestBody String csvContent);

    @ApiOperation(value = "", notes = "Get the growth in tests for a particular Executor")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "EvolutionStats loaded successfully", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = "/getGrowthStats", method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_ADMIN, ROLE_SERVICE})
    ResponseEntity<List<ExecutorStatChartDTO>> getGrowthStats(@RequestParam(value = "executorid", required = true) Long executorid, @RequestParam(value = "from", required = false) Long from, @RequestParam(value = "to", required = false) Long to);

    @ApiOperation(value = "", notes = "Get the stability index for a particular Executor")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "EvolutionStats loaded successfully", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = "/getStabilityStats", method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_ADMIN, ROLE_SERVICE})
    ResponseEntity<List<ExecutorStatChartDTO>> getStabilityStats(@RequestParam(value = "executorid", required = true) Long executorid, @RequestParam(value = "from", required = false) Long from, @RequestParam(value = "to", required = false) Long to);

    @ApiOperation(value = "", notes = "Get the commits for a particular Executor")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "EvolutionStats loaded successfully", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = "/getCommitsStats", method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_ADMIN, ROLE_SERVICE})
    ResponseEntity<List<ExecutorStatChartDTO>> getCommitsStats(@RequestParam(value = "executorid", required = true) Long executorid, @RequestParam(value = "from", required = false) Long from, @RequestParam(value = "to", required = false) Long to);

    @ApiOperation(value = "", notes = "Get the passing tests for a particular Executor")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "EvolutionStats loaded successfully", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = "/getPassingStats", method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_ADMIN, ROLE_SERVICE})
    ResponseEntity<List<ExecutorStatChartDTO>> getPassingStats(@RequestParam(value = "executorid", required = true) Long executorid, @RequestParam(value = "from", required = false) Long from, @RequestParam(value = "to", required = false) Long to);

    @ApiOperation(value = "", notes = "Get the triage done stat for a particular Executor")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "EvolutionStats loaded successfullyEvolutionStats loaded successfully", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = "/getTriageDoneStats", method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_ADMIN, ROLE_SERVICE})
    ResponseEntity<List<ExecutorStatChartDTO>> getTriageDoneStats(@RequestParam(value = "executorid", required = true) Long executorid, @RequestParam(value = "from", required = false) Long from, @RequestParam(value = "to", required = false) Long to);

    @RequestMapping(value = LIST_PATH + "Enabled", method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN})
    ResponseEntity<List<ExecutorDTO>> executorEnabled();
}
