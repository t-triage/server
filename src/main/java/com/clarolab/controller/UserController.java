/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;

import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.UserDTO;
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

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RequestMapping(API_USER_URI)
@Api(value = "Users", description = "Here you will find all those operations related with Users entities", tags = {"Users"})
@Secured(value =  ROLE_ADMIN)
public interface UserController extends BaseController<UserDTO> {

    @ApiOperation(value = "", notes = "Returns logged user")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "User data", response = UserDTO.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = ME, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value =  {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<UserDTO> currentUser();

    @ApiOperation(value = "", notes = "Search user by name")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "User search completed", response = Page.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = SEARCH, method = GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value =  {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<Page<UserDTO>> search(@RequestParam(value = "name", required = true) String name);


    @ApiOperation(value = "", notes = "Get T&C form current user")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "T&C returned", response = String.class),
                    @ApiResponse(code = 422, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = TERMS, method = GET, produces = TEXT_HTML_VALUE)
    @Secured(value =  {ROLE_ADMIN, ROLE_USER})
    ResponseEntity<String> getTerm();

    @ApiOperation(value = "", notes = "Create a new User.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "User added successfully."),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Not Found")
            })
    @RequestMapping(value = CREATE_PATH, method = POST, produces = APPLICATION_JSON_VALUE)
    @Secured(value =  {ROLE_ADMIN})
    ResponseEntity<UserDTO> save(@RequestBody UserDTO userDTO);



}
