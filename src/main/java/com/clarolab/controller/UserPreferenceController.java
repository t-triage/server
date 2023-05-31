/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.dto.UserPreferenceDTO;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.clarolab.util.Constants.API_USER_PREFERENCES_URI;

@RequestMapping(API_USER_PREFERENCES_URI)
@Api(value = "Users", description = "Here you will find all those operations related with Users Preferences entities", tags = {"Users"})
public interface UserPreferenceController extends BaseController<UserPreferenceDTO> {


}
