package com.clarolab.jira.controller.impl;


import com.clarolab.jira.model.JiraConfig;
import com.clarolab.jira.model.JiraObject;
import com.clarolab.jira.service.JiraOAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;

@RestController
public class JiraOAuthControllerImp implements JiraOAuthController{

    @Autowired
    private JiraOAuthService jiraOAuthService;

    @Override
    public ResponseEntity<JiraObject>  getRefreshCode(String code, Long productId){
        return  ResponseEntity.ok(jiraOAuthService.getRefreshCode(code, productId));
    }

    //not going to be used.
    @Override
    public ResponseEntity<String> getFirstLoginCode(){
        return ResponseEntity.ok(jiraOAuthService.getFirstLoginCode());
    }

    @Override
    public ResponseEntity<JiraObject> refreshJiraToken(JiraConfig jiraConfig){
        return ResponseEntity.ok(jiraOAuthService.refreshJiraToken(jiraConfig));
    }

    @Override
    public ResponseEntity<ArrayList> getProjectKeys(Long productId){
        return ResponseEntity.ok(jiraOAuthService.getProjectKeys(productId));
    }

    @Override
    public ResponseEntity<ArrayList> getProjectStates(Long productId, String projectKey){
        return ResponseEntity.ok(jiraOAuthService.getProjectStates(productId, projectKey));
    }
}
