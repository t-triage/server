package com.clarolab.jira.mapper;

import com.clarolab.jira.dto.JiraConfigDTO;
import com.clarolab.jira.model.JiraConfig;
import com.clarolab.jira.service.JiraConfigService;
import com.clarolab.jira.service.JiraOAuthService;
import com.clarolab.mapper.Mapper;
import com.clarolab.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.clarolab.mapper.MapperHelper.getNullableByID;
import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class JiraConfigMapper implements Mapper<JiraConfig, JiraConfigDTO>{

    @Autowired
    private ProductService productService;

    @Autowired
    private JiraConfigService jiraConfigService;
    @Autowired
    private JiraOAuthService jiraOAuthService;

    @Override
    public JiraConfigDTO convertToDTO(JiraConfig jiraConfig) {
        JiraConfigDTO jiraConfigDTO = new JiraConfigDTO();

        setEntryFields(jiraConfig, jiraConfigDTO);

        jiraConfigDTO.setJiraUrl(jiraConfig.getJiraUrl());
        jiraConfigDTO.setProjectKey(jiraConfig.getProjectKey());
        jiraConfigDTO.setReporterEmail(jiraConfig.getReporterEmail());
        jiraConfigDTO.setProduct(jiraConfig.getProduct().getId());
        jiraConfigDTO.setJiraVersion(jiraConfig.getJiraVersion());
        jiraConfigDTO.setInitialStateId(jiraConfig.getInitialStateId());
        jiraConfigDTO.setResolvedStateId(jiraConfig.getResolvedStateId());
        jiraConfigDTO.setClosedStateId(jiraConfig.getClosedStateId());
        jiraConfigDTO.setReopenStateId(jiraConfig.getReopenStateId());
        jiraConfigDTO.setClientID(jiraConfig.getClientID());
        jiraConfigDTO.setClientSecret(jiraConfig.getClientSecret());
        jiraConfigDTO.setCloudId(jiraConfig.getCloudId());
        jiraConfigDTO.setIssueType(jiraConfig.getIssueType());
        jiraConfigDTO.setDefaultFieldsValues(jiraConfig.getDefaultFieldsValues());
        jiraConfigDTO.setIsFetchedToken(!Objects.isNull(jiraConfig.getFinalToken()));
        jiraConfigDTO.setIsValidToken(jiraOAuthService.validateToken(jiraConfig));
        return jiraConfigDTO;
    }

    @Override
    public JiraConfig convertToEntity(JiraConfigDTO jiraConfigDTO) {
        JiraConfig jiraConfig = jiraConfigService.findLastByProductId(jiraConfigDTO.getProduct());

        if (jiraConfig == null) {
            jiraConfig = JiraConfig.builder()
                    .id(null)
                    .jiraUrl(jiraConfigDTO.getJiraUrl())
                    .reporterEmail(jiraConfigDTO.getReporterEmail())
                    .product(getNullableByID(jiraConfigDTO.getProduct(), id -> productService.find(id)))
                    .jiraVersion(jiraConfigDTO.getJiraVersion())
                    .clientID(jiraConfigDTO.getClientID())
                    .clientSecret(jiraConfigDTO.getClientSecret())
                    .build();
            return jiraConfig;
        }
        if(!jiraConfigDTO.getClientID().equals(jiraConfig.getClientID()) ||
                !jiraConfigDTO.getJiraUrl().equals(jiraConfig.getJiraUrl()) ||
                !jiraConfigDTO.getClientSecret().equals(jiraConfig.getClientSecret())) {
            jiraConfig.setClientID(jiraConfigDTO.getClientID());
            jiraConfig.setClientSecret(jiraConfigDTO.getClientSecret());
            jiraConfig.setJiraUrl(jiraConfigDTO.getJiraUrl());
            jiraConfig.setReporterEmail((jiraConfigDTO.getReporterEmail()));
            jiraConfig.setJiraVersion(jiraConfigDTO.getJiraVersion());
            jiraConfig.setDefaultFieldsValues(jiraConfigDTO.getDefaultFieldsValues());

            jiraConfig.setProjectKey(null);
            jiraConfig.setIssueType(null);
            jiraConfig.setInitialStateId(null);
            jiraConfig.setResolvedStateId(null);
            jiraConfig.setClosedStateId(null);
            jiraConfig.setReopenStateId(null);
            jiraConfig.setFinalToken(null);
            jiraConfig.setRefreshToken(null);
            jiraConfig.setCloudId(null);

            return jiraConfig;
        }
        jiraConfig.setProjectKey(jiraConfigDTO.getProjectKey());
        jiraConfig.setReporterEmail((jiraConfigDTO.getReporterEmail()));
        jiraConfig.setIssueType(jiraConfigDTO.getIssueType());
        jiraConfig.setJiraVersion(jiraConfigDTO.getJiraVersion());
        jiraConfig.setInitialStateId(jiraConfigDTO.getInitialStateId());
        jiraConfig.setResolvedStateId(jiraConfigDTO.getResolvedStateId());
        jiraConfig.setClosedStateId(jiraConfigDTO.getClosedStateId());
        jiraConfig.setReopenStateId(jiraConfigDTO.getReopenStateId());
        jiraConfig.setFinalToken(jiraConfig.getFinalToken());
        jiraConfig.setRefreshToken(jiraConfig.getRefreshToken());
        jiraConfig.setCloudId(jiraConfig.getCloudId());
        jiraConfig.setDefaultFieldsValues(jiraConfigDTO.getDefaultFieldsValues());

        return jiraConfig;
    }


}
