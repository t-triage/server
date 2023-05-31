/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;

import com.clarolab.controller.error.ErrorInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping(API_EXPORT_URI)
@Api(value = "Exports", description = "Here you will find all those operations related with Reports in a few formats", tags = {"Exports"})
@CrossOrigin(origins = "*", allowedHeaders = "*")
public interface ExportController extends SecuredController{

    @ApiOperation(value = "", notes = "Return User Report.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Report returned successfully."),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = USER, method = GET, produces = TEXT_PLAIN_VALUE)
    String downloadUserList(Model model);

    @ApiOperation(value = "", notes = "Return Executors Report.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Report returned successfully."),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = EXECUTOR, method = GET, produces = TEXT_PLAIN_VALUE)
    String downloadExecutorList(Model model);


    @ApiOperation(value = "", notes = "Return User Triage Report.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Report returned successfully."),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = USER_FULL_REPORT, method = GET, produces = TEXT_PLAIN_VALUE)
    String downloadUserReport(Model model);

    @ApiOperation(value = "", notes = "Return Product Triage Report.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Report returned successfully."),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = PRODUCT_FULL_REPORT, method = GET, produces = TEXT_PLAIN_VALUE)
    String downloadProductReport(@RequestParam(value = "productId", required = true) Long productId, Model model);

}
