/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;

import org.springframework.security.access.annotation.Secured;

import static com.clarolab.util.Constants.ROLE_ADMIN;
import static com.clarolab.util.Constants.ROLE_USER;

@Secured(value = {ROLE_USER, ROLE_ADMIN})
public interface SecuredController {
}
