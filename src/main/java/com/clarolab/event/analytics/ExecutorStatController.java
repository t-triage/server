/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.event.analytics;


import com.clarolab.controller.BaseController;
import com.clarolab.dto.ExecutorStatDTO;
import com.clarolab.util.Constants;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(Constants.API_EXECUTOR_STAT_URI)
@Api(value = "ExecutorStat", description = "Here you will find all those operations related with the statistics", tags = {"Statistics"})
public interface ExecutorStatController extends BaseController<ExecutorStatDTO> {

}
