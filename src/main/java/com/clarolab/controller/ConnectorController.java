/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.ConnectorDTO;
import com.clarolab.dto.ContainerDTO;
import com.clarolab.dto.push.ServiceAuthDTO;
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

@RequestMapping(API_CONNECTOR_URI)
@Api(value = "Connectors", description = "Here you will find all those operations related with Connectors entities", tags = {"Connectors"})
@Secured(value = {ROLE_ADMIN})
public interface ConnectorController extends BaseController<ConnectorDTO> {

    @ApiOperation(value = "", notes = "Return all containers from the CI tool according to the connector id.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "A list of all containers according to the connector id", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = ID + CONTAINERS, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN})
    ResponseEntity<List<ContainerDTO>> getAllContainers(@PathVariable Long id);

    @ApiOperation(value = "", notes = "Return all containers from each CI tool according to all connectors.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "A list of all containers of all connectors", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = CONTAINERS, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN})
    ResponseEntity<List<ContainerDTO>> getAllContainers();

    @ApiOperation(value = "", notes = "Load all the executors inside the container by to the specific connector id.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Container populated successfully", response = Boolean.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = POPULATE_BY_ID_PATH, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = ROLE_ADMIN)
    ResponseEntity<Boolean> populate(@PathVariable Long id);

    @ApiOperation(value = "", notes = "Load all the executors inside the container to all connectors.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Containers populated successfully", response = Boolean.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = POPULATE_PATH, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = ROLE_ADMIN)
    ResponseEntity<Boolean> populate();

    @ApiOperation(value = "", notes = "Validate that the connector exists")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Connector is valid", response = Boolean.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = VALIDATE_BY_ID_PATH, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = ROLE_ADMIN)
    ResponseEntity<Boolean> validate(@PathVariable Long id);

    @ApiOperation(value = "", notes = "Generate a new ClientID and SecretID to be used by external services")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Generated ClientID and SecretID", response = ServiceAuthDTO.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = NEW_TOKEN_PATH, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = ROLE_ADMIN)
    ResponseEntity<ServiceAuthDTO> newServiceAuth(@PathVariable Long id);

    @ApiOperation(value = "", notes = "Return the ClientID and SecretID to be used by external services")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "The ClientID and SecretID", response = ServiceAuthDTO.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = GET_TOKEN_PATH, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = ROLE_ADMIN)
    ResponseEntity<ServiceAuthDTO> getServiceAuth(@PathVariable Long id);



}
