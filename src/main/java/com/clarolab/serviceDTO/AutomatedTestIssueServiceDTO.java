/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.aaa.util.AuthContextHelper;
import com.clarolab.controller.impl.PageableHelper;
import com.clarolab.dto.AutomatedTestIssueDTO;
import com.clarolab.dto.FilterDTO;
import com.clarolab.dto.UpdateTriageDTO;
import com.clarolab.dto.UserDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.AutomatedTestIssueMapper;
import com.clarolab.model.AutomatedTestIssue;
import com.clarolab.service.AutomatedTestIssueService;
import com.clarolab.service.TTriageService;
import com.clarolab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.clarolab.util.SearchSpecificationUtil.getSearchSpec;

@Component
public class AutomatedTestIssueServiceDTO implements BaseServiceDTO<AutomatedTestIssue, AutomatedTestIssueDTO, AutomatedTestIssueMapper> {

    @Autowired
    private AutomatedTestIssueService service;

    @Autowired
    private AutomatedTestIssueMapper mapper;

    @Autowired
    private AuthContextHelper authContextHelper;

    @Autowired
    private AutomatedTestIssueMapper automatedTestIssueMapper;

    @Autowired
    private UserService userService;

    @Override
    public TTriageService<AutomatedTestIssue> getService() {
        return service;
    }

    @Override
    public Mapper<AutomatedTestIssue, AutomatedTestIssueDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<AutomatedTestIssue, AutomatedTestIssueDTO, AutomatedTestIssueMapper> getServiceDTO() {
        return this;
    }

    public void triage(UpdateTriageDTO updateTriageDTO, AutomatedTestIssueDTO dto) {
        if (dto == null) return;

        if (updateTriageDTO.getTestTriageDTO().getAutomatedTestIssueId() == null) {
            dto.setTriager(updateTriageDTO.getTestTriageDTO().getTriager());
        }
        if (updateTriageDTO.getTestTriageDTO().getProductId() != null) {
            dto.setProductId(updateTriageDTO.getTestTriageDTO().getProductId());
        }

        AutomatedTestIssue newAutomationIssue = mapper.convertToEntity(dto);
        service.updateAutomationIssue(newAutomationIssue.getTestTriage(), mapper.convertToEntity(dto));
    }

    //public Page<AutomatedTestIssueDTO> filterList(String[] criteria, Pageable pageable, String executorName, boolean assignee, boolean pin, boolean passingIssues) {
    public Page<AutomatedTestIssueDTO> filterList(String[] criteria, Pageable pageable, FilterDTO filters) {
        List<AutomatedTestIssue> list = service.findAllButFixed(getSearchSpec(criteria), pageable.getSort(), filters.getExecutorName(), filters.getAssignee(), filters.getPin(), filters.getPassingIssues(), filters.getHideOld());
        return PageableHelper.getPageable(pageable, convertToDTO(list));

    }

    public AutomatedTestIssueDTO setAssigneeToAutomationIssue(UserDTO user, Long issueId) {
        return convertToDTO(service.setAssigneeToAutomationIssue(user.getId(), issueId));
    }

    public Long automationIssuesPendingToFix() {
        return service.countAllButFixed(authContextHelper.getCurrentUser());
    }
}
