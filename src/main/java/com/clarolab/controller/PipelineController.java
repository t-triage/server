/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.ContainerDTO;
import com.clarolab.dto.PipelineDTO;
import com.clarolab.dto.TestTriageDTO;
import com.clarolab.view.PipelineView;
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

@RequestMapping(API_PIPELINE)
@Api(value = "Pipeline", description = "Here you will find all the operations related with Pipeline entities", tags = {"Executors"})
public interface PipelineController extends BaseController<PipelineDTO> {

    @ApiOperation(value = "", notes = "Return all pipelines enabled:true")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = LIST_PATH + "Enabled", method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN})
    ResponseEntity<List<PipelineDTO>> pipelinesEnabled();

    @ApiOperation(value = "", notes = "Assign multiple TestCases to a Pipeline")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Operation complete", response = Page.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not complete")
            })
    @RequestMapping(value = ASSIGN_TO_PLAN, method = POST, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<PipelineDTO> assignToPipeline(@RequestParam(value = "pipelineId", required = true) Long pipelineId, @RequestBody List<Long> testCaseIds);


    @ApiOperation(value = "", notes = "Return all pipelines with that name")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = SEARCH, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN})
    ResponseEntity<Page<PipelineDTO>> search(@RequestParam(value = "name", required = true) String name);

    @ApiOperation(value = "", notes = "Return all ongoing test triages")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = ONGOING, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN})
    ResponseEntity<Page<TestTriageDTO>> ongoingTestTriageList(@RequestParam(value = "pipelineId", required = true) Long pipelineId);

    @ApiOperation(value = "", notes = "Return all ongoing test triages")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = ONGOING + "List", method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN})
    ResponseEntity<PipelineView> ongoingTestTriage(@RequestParam(value = "pipelineId", required = true) Long pipelineId);

    @ApiOperation(value = "", notes = "Return all containers with pipelines")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = CONTAINERS, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN})
    ResponseEntity<List<ContainerDTO>> containers();

    @ApiOperation(value = "", notes = "Given a pipeline and test case delete the relation")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Pipeline test relation deleted successfully.", response = Long.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = DELETE_BY_PIPELINE_AND_CASE, method = DELETE, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN})
    ResponseEntity<Long> deleteByPipelineAndCase(@RequestParam(value = "pipelineId", required = true) Long pipelineId, @RequestParam(value = "testCaseId", required = true) Long testCaseId);

    @RequestMapping(value = FIND_PIPELINES_BY_CONTAINER, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN})
    ResponseEntity<List<PipelineDTO>> findPipelinesByContainer(@RequestParam(value = "containerId", required = true) Long containerId);
}
