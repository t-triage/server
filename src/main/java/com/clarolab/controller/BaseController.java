/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;

import com.clarolab.controller.error.ErrorInfo;
import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.*;

public interface BaseController<T> extends SecuredController{

    @ApiOperation(value = "", notes = "Create a new Entry.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "Entry added successfully."),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = CREATE_PATH, method = POST, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<T> save(@RequestBody T entity);

    /*@ApiOperation(value = "", notes = "Create a new Entry.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "Lis of Entries added successfully."),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = Constants.CREATEALL_PATH, method = POST, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> saveAll(@RequestBody List<T> entities);*/


    @ApiOperation(value = "", notes = "Return an Entry.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Entry returned successfully."),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = READ_PATH, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<T> find(@PathVariable Long id);


    @ApiOperation(value = "", notes = "Update an Entry.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Entry updated successfully."),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = UPDATE_PATH, method = PUT, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<T> update(@RequestBody T entity);


    @ApiOperation(value = "", notes = "Delete an Entry.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 202, message = "Entry deleted successfully.", response = Long.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = DELETE_PATH, method = DELETE, produces = APPLICATION_JSON_VALUE)
    @Secured(value = ROLE_ADMIN)
    ResponseEntity<Long> delete(@PathVariable Long id);


    //"Use ':' for equals. Use '%' for like. Use '>' for greater than. Use '<' for less than. I.e. /list?q=username:John"
    @ApiOperation(value = "", notes = "List all Entries filtered by query and sorted by the page criteria.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Filtered list.", response = Page.class)
            })

    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "Query criteria in the format: <b>'property(:|&#60;|&#62;)value'</b>. I.e: propname:value. " +
                            "Default filter is empty. " +
                            ">Multiple query criteria are supported."),
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "Results page you want to retrieve (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "Number of records per page."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "Sorting criteria in the format: <b>'property(,asc|desc)'</b>. I.e: propname,asc. " +
                            "Default sort order is ascending. " +
                            "Multiple sort criteria are supported.")})
    @RequestMapping(value = LIST_PATH, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Page<T>> list(@RequestParam(value = "query", required = false) String[] criteria, Pageable page);


}
