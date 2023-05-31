/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.dto.ReportDTO;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.clarolab.util.Constants.API_REPORT_URI;

@RequestMapping(API_REPORT_URI)
@Api(value = "Reports", description = "Here you will find all those operations related with Report entities", tags = {"Reports"})
public interface ReportController extends BaseController<ReportDTO> {


}
