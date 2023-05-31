/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.PropertyDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping(API_PROPERTY_URI)
@Api(value = "Properties", description = "Here you will find all those operations related with Property entities", tags = {"Properties"})
@Secured(value = ROLE_ADMIN)
public interface PropertyController extends BaseController<PropertyDTO> {


    @ApiOperation(value = "", notes = "Return a list of Properties according to the name parameter")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "A property where the name matchs with the parameter.", response = List.class),
                    @ApiResponse(code = 406, response = ErrorInfo.class, message = "Not Acceptable"),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = FIND_PATH, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value =  {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<List<PropertyDTO>> findAllByName(@PathVariable String name);

    @ApiOperation(value = "", notes = "Return a Property according to the name parameter")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "A property where the name matchs with the parameter.", response = PropertyDTO.class),
                    @ApiResponse(code = 406, response = ErrorInfo.class, message = "Not Acceptable"),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = GET_NAME_PATH, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value =  {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<PropertyDTO> getByName(@PathVariable String name);

    @ApiOperation(value = "", notes = "Return True or False if internal users are enabled")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "A boolean value", response = Boolean.class),
                    @ApiResponse(code = 401, message = "")
            })
    @RequestMapping(value = INTERNAL_PATH, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = ROLE_ANONYMOUS)
    ResponseEntity<Boolean> isInternalUserEnabled();

}
