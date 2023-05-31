/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.dto.DeadlineDTO;
import io.swagger.annotations.Api;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.clarolab.util.Constants.API_DEADLINE_URI;
import static com.clarolab.util.Constants.ROLE_ADMIN;

@RequestMapping(API_DEADLINE_URI)
@Api(value = "Deadlines", description = "Here you will find all those operations related with Deadlines entities", tags = {"Deadlines"})
@Secured(value = ROLE_ADMIN)
public interface DeadlineController extends BaseController<DeadlineDTO> {


}
