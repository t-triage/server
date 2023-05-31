/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.IssueTicketDTO;
import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping(API_ISSUETICKET_URI)
@Api(value = "IssueTicket", description = "Here you will find all those operations related with IssueTicket entities", tags = {"Issues"})
public interface IssueTicketController extends BaseController<IssueTicketDTO> {

    @ApiOperation(value = "", notes = "Get an issue ticket list")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "A list returned", response = Page.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "Results page you want to retrieve (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "Number of records per page."),
           })

    @RequestMapping(value = USER + ID + LIST_PATH, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Page<IssueTicketDTO>> list(@PathVariable Long id, Pageable pageable);
}
