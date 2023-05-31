/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.ContainerDTO;
import com.clarolab.view.KeyValuePair;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping(API_CONTAINER_URI)
@Api(value = "Containers", description = "Here you will find all those operations related with Container entities", tags = {"Containers"})
@Secured(value = ROLE_ADMIN)
public interface ContainerController extends BaseController<ContainerDTO> {

    @ApiOperation(value = "", notes = "Load all the executors to the specific container id.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Container populated successfully", response = Boolean.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = POPULATE_BY_ID_PATH, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> populate(@PathVariable Long id);

    @ApiOperation(value = "", notes = "Load all the executors to the specific container name.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Container populated successfully", response = Boolean.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = POPULATE_BY_NAME_PATH, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> populateByName(@PathVariable String name);

    @ApiOperation(value = "", notes = "List the containers for the logged user.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Containers answered successfully", response = Page.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = SUGGESTED_URI, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<Page<ContainerDTO>> suggested();

    @ApiOperation(value = "", notes = "Validate that the container exists")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Container is valid", response = Boolean.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = VALIDATE_BY_ID_PATH, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> validate(@PathVariable Long id);

    @ApiOperation(value = "", notes = "Return a list of container names according to the product id")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "The list of container names", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = NAMES + ID,  method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_ADMIN, ROLE_SERVICE})
    ResponseEntity<List<KeyValuePair>> getContainersNames(@PathVariable Long id);
}
