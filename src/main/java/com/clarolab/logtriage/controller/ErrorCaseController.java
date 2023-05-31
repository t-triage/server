package com.clarolab.logtriage.controller;

import com.clarolab.controller.BaseController;
import com.clarolab.logtriage.dto.ErrorCaseDTO;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.clarolab.util.Constants.API_ERROR_CASE;

@RequestMapping(API_ERROR_CASE)
@Api(value = "ErrorCase", tags = {"ErrorCase"})
public interface ErrorCaseController extends BaseController<ErrorCaseDTO> {
}
