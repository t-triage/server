package com.clarolab.logtriage.controller;

import com.clarolab.controller.BaseController;
import com.clarolab.logtriage.dto.SearchExecutorDTO;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.clarolab.util.Constants.API_SEARCH_EXECUTOR;

@RequestMapping(API_SEARCH_EXECUTOR)
@Api(value = "SearchExecutor", tags = {"SearchExecutor"})
public interface SearchExecutorController extends BaseController<SearchExecutorDTO> {
}
