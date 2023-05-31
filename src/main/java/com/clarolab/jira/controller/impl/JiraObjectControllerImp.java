package com.clarolab.jira.controller.impl;

import com.clarolab.jira.model.JiraConfig;
import com.clarolab.jira.service.JiraObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class JiraObjectControllerImp implements JiraObjectController {

    @Autowired
    private JiraObjectService jiraObjectService;

    @Override
    public ResponseEntity<String> getJiraObjects(String issueID) {
        return ResponseEntity.ok(jiraObjectService.getJiraObjects(issueID));
    }

    @Override
    public ResponseEntity<String> createJiraIssueTest(String summary, String description){
        return ResponseEntity.ok(jiraObjectService.createJiraIssueTest(summary, description));
    }

    @Override
    public ResponseEntity<String> addJiraCommentTest(String issueID, String comment){
        return ResponseEntity.ok(jiraObjectService.addJiraCommentTest(issueID, comment));
    }

    @Override
    public ResponseEntity<String> getJiraStatus(JiraConfig jira) {
        return ResponseEntity.ok(jiraObjectService.jiraStatus(jira));
    }

    @Override
    public ResponseEntity<String> getProjectList(Long productId) {
        return ResponseEntity.ok(jiraObjectService.getProjectList(productId));
    }

    @Override
    public ResponseEntity<String> getProjectStatus(Long productId, String projectKey) {
        return ResponseEntity.ok(jiraObjectService.getProjectStatus(productId, projectKey));
    }

    @Override
    public ResponseEntity<String> searchIssueType(Long productId, String projectId) {
        return ResponseEntity.ok(jiraObjectService.searchIssueType(productId, projectId));
    }
}
