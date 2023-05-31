package com.clarolab.logtriage.controller;

import com.clarolab.controller.BaseController;
import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.logtriage.dto.LogAlertDTO;
import com.clarolab.logtriage.dto.SplunkAlertDTO;
import com.clarolab.logtriage.model.LogAlert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RequestMapping(API_LOG_ALERT)
@Api(value = "LogAlert", tags = {"LogAlert"})
public interface LogAlertController extends BaseController<LogAlertDTO> {

    @ApiOperation(value = "", notes = "Alert t-Triage that there are new logs to retrieve.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "Entry added successfully.", response = LogAlert.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = PUSH_PATH, method = POST, produces = APPLICATION_JSON_VALUE)
    @Secured(value = ROLE_SERVICE)
    ResponseEntity<Boolean> push(@RequestBody SplunkAlertDTO entity);

}