/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.dto.TriageSpecDTO;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.clarolab.util.Constants.API_TRAIGESPECY_URI;

@RequestMapping(API_TRAIGESPECY_URI)
@Api(value = "Triage Spec", description = "Here you will find all those operations related with Triage Spec, entities", tags = {"Triage Spec"})
public interface TriageSpecController extends BaseController<TriageSpecDTO> {


}
