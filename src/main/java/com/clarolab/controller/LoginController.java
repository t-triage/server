/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;

import com.clarolab.aaa.internal.ClientSecret;
import com.clarolab.aaa.internal.LoginRequest;
import com.clarolab.aaa.internal.RegistrationRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;

@Api(value = "Internal Login Controller", description = "Here you will find all those operations related with authentication", tags = {"Auth"})
public interface LoginController {

    @ApiOperation(value = "", notes = "User log-in")
    ResponseEntity<?> authenticate(LoginRequest loginRequest);

    @ApiOperation(value = "", notes = "Service Authentication")
    ResponseEntity<?> authenticate(ClientSecret loginRequest);

    @ApiOperation(value = "", notes = "Register new user")
    ResponseEntity<?> register(RegistrationRequest registrationRequest);

}
