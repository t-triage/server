package com.clarolab.jira.controller.impl;

import com.clarolab.jira.model.JiraConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import static com.clarolab.jira.util.Constants.API_JIRA;
import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(API_JIRA)
public interface JiraObjectController {


    @RequestMapping(value = GET, method = RequestMethod.GET)
    @Secured(value = {ROLE_USER, ROLE_ADMIN, ROLE_SERVICE})
    ResponseEntity<String> getJiraObjects(String issueID);

    @PostMapping(value = CREATE_NEW_ISSUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN, ROLE_SERVICE})
    ResponseEntity<String> createJiraIssueTest(@RequestParam(value = "summary", required = true) String summary, @RequestParam(value = "description", required = true) String description);

    @PostMapping(value = ADD_COMMENT)
    @Secured(value = {ROLE_USER, ROLE_ADMIN, ROLE_SERVICE})
    ResponseEntity<String> addJiraCommentTest(@RequestParam(value = "issueID", required = true) String issueID, @RequestParam(value = "comment", required = true) String comment);

    @RequestMapping(value = "/status", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN, ROLE_SERVICE})
    ResponseEntity<String> getJiraStatus(JiraConfig jira);

    @RequestMapping(value = "/projectList", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN, ROLE_SERVICE})
    ResponseEntity<String> getProjectList(@RequestParam(value = "productId") Long productId);

    @RequestMapping(value = "/projectStatus", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN, ROLE_SERVICE})
    ResponseEntity<String> getProjectStatus(@RequestParam(value = "productId") Long productId ,@RequestParam(value = "projectKey", required = true) String projectKey);

    @RequestMapping(value = "/searchIssuetype", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Secured(value = {ROLE_USER, ROLE_ADMIN, ROLE_SERVICE})
    ResponseEntity<String> searchIssueType(@RequestParam(value = "productId") Long productId ,@RequestParam(value = "projectId", required = true) String projectId);
}
