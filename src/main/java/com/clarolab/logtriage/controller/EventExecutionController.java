package com.clarolab.logtriage.controller;

import com.clarolab.controller.BaseController;
import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.logtriage.dto.EventExecutionDTO;
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

import java.io.IOException;
import java.util.List;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RequestMapping(API_EVENT_EXECUTION)
@Api(value = "EventExecution", tags = {"EventExecution"})
public interface EventExecutionController extends BaseController<EventExecutionDTO> {

    @ApiOperation(value = "", notes = "Get a list of Event Execution filtered by Error Case ID")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "EventExecutions loaded successfully", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = LIST_BY_ERROR_CASE, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_ADMIN, ROLE_SERVICE})
    ResponseEntity<List<EventExecutionDTO>> getEventsFromErrorCase(@RequestParam(value = "errorCaseId") Long errorCaseId);

    @ApiOperation(value = "", notes = "Import events from a log file")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Import complete", response = Page.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not complete")
            })
    @RequestMapping(value = IMPORT_EVENTS_LOG, method = POST, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<Boolean> importEventsLog(@RequestBody String file, @RequestParam(value = "productId") Long productId) throws IOException;

}
