/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.jira.controller.impl;

import com.clarolab.jira.model.JiraConfig;
import com.clarolab.jira.model.JiraObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;

import static com.clarolab.jira.util.Constants.*;
import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping(API_JIRA_CODE)
@Secured(value = {ROLE_ANONYMOUS, ROLE_ADMIN, ROLE_USER})
public interface JiraOAuthController {

    //this is not going to be used.
    @RequestMapping(value = "/getJiraCode", method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<String> getFirstLoginCode();

    //This has to be completed on the JiraOAuthService
    @GetMapping(value = "/Auth", produces = APPLICATION_JSON_VALUE )
    ResponseEntity<JiraObject>  getRefreshCode(@RequestParam(value = "code", required = true) String code, @RequestParam(value = "state", required = true) Long productId);

    @PostMapping(value = TEST)
    ResponseEntity<JiraObject> refreshJiraToken(JiraConfig jiraConfig);

    @CrossOrigin
    @GetMapping(value = PROJECT_KEYS, produces = APPLICATION_JSON_VALUE)
//    @Secured(value = {ROLE_ANONYMOUS, ROLE_ADMIN, ROLE_USER})
    ResponseEntity<ArrayList> getProjectKeys(@RequestParam(value = "productId", required = true) Long productId);

    @CrossOrigin
    @GetMapping(value = PROJECT_STATES, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<ArrayList> getProjectStates(@RequestParam(value = "productId", required = true) Long productId, @RequestParam(value = "projectKey", required = true) String projectKey);

    //    @GetMapping(value = "/done", produces = APPLICATION_JSON_VALUE)
//    ResponseEntity<String>
}
