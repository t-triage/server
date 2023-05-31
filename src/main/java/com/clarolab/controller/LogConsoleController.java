package com.clarolab.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;

import static com.clarolab.util.Constants.LOG_CONSOLE_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping(LOG_CONSOLE_PATH)
public interface LogConsoleController extends SecuredController{

    @RequestMapping(value = "/get", method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<String> getLogs(@RequestParam(value = "cantLines") Integer cantLines);

    @RequestMapping(value = "/download", method = GET)
    ResponseEntity<Resource> getLogsFile() throws IOException;

}
