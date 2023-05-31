/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;

import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.NewsBoardDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

import static com.clarolab.util.Constants.API_BOARD_URI;
import static com.clarolab.util.Constants.VIEW;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
@RequestMapping(API_BOARD_URI)
@Api(value = "Statistics", description = "News board messages", tags = {"NewsBoard"})
public interface NewsBoardController extends BaseController<NewsBoardDTO> {

    @ApiOperation(value = "", notes = "Latest events")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Events answered", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not complete")
            })
    @RequestMapping(value = VIEW, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<NewsBoardDTO>> latestNews();
}
