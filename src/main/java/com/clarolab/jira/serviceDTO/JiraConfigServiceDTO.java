/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.jira.serviceDTO;

import com.clarolab.jira.dto.JiraConfigDTO;
import com.clarolab.jira.mapper.JiraConfigMapper;
import com.clarolab.jira.model.JiraConfig;
import com.clarolab.jira.service.JiraConfigService;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.BuildTriage;
import com.clarolab.model.User;
import com.clarolab.service.BuildTriageService;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.BaseServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JiraConfigServiceDTO implements BaseServiceDTO<JiraConfig, JiraConfigDTO, JiraConfigMapper> {

    @Autowired
    private JiraConfigService jiraConfigService;

    @Autowired
    private JiraConfigMapper jiraConfigMapper;

    @Override
    public TTriageService<JiraConfig> getService() {return jiraConfigService;}

    @Override
    public Mapper<JiraConfig, JiraConfigDTO> getMapper(){return jiraConfigMapper;}

    @Override
    public BaseServiceDTO<JiraConfig, JiraConfigDTO, JiraConfigMapper> getServiceDTO() {
        return this;
    }

    public JiraConfigDTO findLastByProductId(Long productId) {

        JiraConfig jiraConfig = jiraConfigService.findLastByProductId(productId);
        if (jiraConfig == null){
            return null;
        }
        return convertToDTO(jiraConfig);
    }

    public JiraConfigDTO saveJiraConfig(JiraConfigDTO jiraConfigDTO, Long productId) {
        if (jiraConfigDTO.getProduct() == null || jiraConfigDTO.getProduct() == 0l) {
            jiraConfigDTO.setProduct(productId);
        }
        JiraConfig entity = jiraConfigMapper.convertToEntity(jiraConfigDTO);
        return convertToDTO(jiraConfigService.save(entity));
    }

    public JiraConfigDTO getJiraConfig(Long productId) {

        return convertToDTO(jiraConfigService.findLastByProductId(productId));
    }

}
