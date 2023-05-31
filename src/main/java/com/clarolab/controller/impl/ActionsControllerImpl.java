/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.aaa.util.AuthContextHelper;
import com.clarolab.agents.TriageAgent;
import com.clarolab.controller.ActionsController;
import com.clarolab.dto.AutomatedTestIssueDTO;
import com.clarolab.dto.BuildTriageDTO;
import com.clarolab.dto.TestTriageDTO;
import com.clarolab.dto.UserDTO;
import com.clarolab.service.exception.NotFoundServiceException;
import com.clarolab.serviceDTO.AutomatedTestIssueServiceDTO;
import com.clarolab.serviceDTO.BuildTriageServiceDTO;
import com.clarolab.serviceDTO.TestTriageServiceDTO;
import com.clarolab.serviceDTO.UserServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class ActionsControllerImpl implements ActionsController {

    @Autowired
    private UserServiceDTO userService;

    @Autowired
    private TestTriageServiceDTO testTriageService;

    @Autowired
    private BuildTriageServiceDTO buildTriageService;

    @Autowired
    TriageAgent triageAgent;

    @Autowired
    private AuthContextHelper authContextHelper;

    @Autowired
    private AutomatedTestIssueServiceDTO automatedTestIssueServiceDTO;

    @Override
    public ResponseEntity<TestTriageDTO> setAssigneeToTest(Long userId, Long testId) {
        UserDTO user = userService.find(userId);
        return ResponseEntity.ok(testTriageService.setAssigneeToTest(user, testId));
    }

    @Override
    public ResponseEntity<BuildTriageDTO> setAssigneeToBuild(Long userId, Long buildId) {
        UserDTO user = userService.find(userId);
        return ResponseEntity.ok(buildTriageService.setAssigneeToBuild(user, buildId));
    }

    @Override
    public ResponseEntity<TestTriageDTO> markTestAsTriaged(Long userId, Long testId) {//TODO IMO its not necessary since we can use the UPDATE in TestTriageController
        UserDTO user = userService.find(userId);
        return ResponseEntity.ok(testTriageService.markTestAsTriaged(user, testId));
    }

    @Override
    public ResponseEntity<BuildTriageDTO> markJobAsTriaged(Long buildId, String note) {//TODO IMO its not necessary since we can use the UPDATE in TestTriageController
        UserDTO user = authContextHelper.getCurrentUserAsDTO();
        return ResponseEntity.ok(buildTriageService.markBuildAsTriaged(user, buildId, note));
    }

    @Override
    public ResponseEntity<BuildTriageDTO> markJobAsInvalid(Long buildId, String note) {
        UserDTO user = authContextHelper.getCurrentUserAsDTO();
        return ResponseEntity.ok(buildTriageService.markBuildAsInvalid(user, buildId, note));
    }

    @Override
    public ResponseEntity<BuildTriageDTO> markJobAsDisabled(Long buildId, String note) {
        UserDTO user = authContextHelper.getCurrentUserAsDTO();
        return ResponseEntity.ok(buildTriageService.markBuildAsDisabled(user, buildId, note));
    }

    @Override
    public ResponseEntity<Boolean> approveAutomaticTriage(Long buildId) {
        throw new NotFoundServiceException("The Service is not implemented yet!!");
    }

    @Override
    public ResponseEntity<Boolean> processImportedSuites() {
        triageAgent.execute();

        return ResponseEntity.ok(true);
    }

    @Override
    public ResponseEntity<AutomatedTestIssueDTO> setAssigneeToAutomationIssue(Long userId, Long issueId) {
        UserDTO user = userService.find(userId);
        return ResponseEntity.ok(automatedTestIssueServiceDTO.setAssigneeToAutomationIssue(user, issueId));
    }

    @Override
    public ResponseEntity<BuildTriageDTO> markJobAsEnabled(Long buildId) {
        UserDTO user = authContextHelper.getCurrentUserAsDTO();
        return ResponseEntity.ok(buildTriageService.markBuildAsEnabled(user, buildId));
    }
}
