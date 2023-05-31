package com.clarolab.jira.controller.impl;

import com.clarolab.controller.impl.BaseControllerImpl;
import com.clarolab.jira.dto.JiraConfigDTO;
import com.clarolab.jira.repository.JiraConfigRepository;
import com.clarolab.jira.service.JiraConfigService;
import com.clarolab.jira.serviceDTO.JiraConfigServiceDTO;
import com.clarolab.service.TTriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class JiraConfigurationControllerImpl extends BaseControllerImpl<JiraConfigDTO> implements JiraConfigurationController {

    @Autowired
    private JiraConfigServiceDTO jiraConfigServiceDTO;

    @Autowired
    private JiraConfigService jiraConfigService;

    @Autowired
    private JiraConfigRepository jiraConfigRepository;

    @Override
    public ResponseEntity<JiraConfigDTO> getJiraConfig(Long productId) {

        return ResponseEntity.ok(jiraConfigServiceDTO.getJiraConfig(productId));
    }

    @Override
    protected TTriageService<JiraConfigDTO> getService() {
        return jiraConfigServiceDTO;
    }

    @Override
    public ResponseEntity<JiraConfigDTO> findFirstByProductId(Long productId){
        return ResponseEntity.ok(jiraConfigServiceDTO.findLastByProductId(productId));
    }

    @Override
    public ResponseEntity<JiraConfigDTO> saveJiraConfig(JiraConfigDTO jiraConfigDTO, Long productId){
        return ResponseEntity.ok(jiraConfigServiceDTO.saveJiraConfig(jiraConfigDTO, productId));
    }

}
