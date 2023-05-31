/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.BuildDTO;
import com.clarolab.dto.TestTriageDTO;
import com.clarolab.dto.UpdateTriageDTO;
import com.clarolab.dto.UserDTO;
import com.clarolab.dto.db.TestTriageHistoryDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.TestTriageMapper;
import com.clarolab.model.TestTriage;
import com.clarolab.model.User;
import com.clarolab.model.types.StateType;
import com.clarolab.service.IssueTicketService;
import com.clarolab.service.TTriageService;
import com.clarolab.service.TestTriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestTriageServiceDTO implements BaseServiceDTO<TestTriage, TestTriageDTO, TestTriageMapper> {

    @Autowired
    private TestTriageService service;

    @Autowired
    private TestTriageMapper mapper;

    @Autowired
    private UserServiceDTO userServiceDTO;

    @Autowired
    private BuildServiceDTO buildServiceDTO;

    @Autowired
    private IssueTicketService issueTicketService;

    @Override
    public TTriageService<TestTriage> getService() {
        return service;
    }

    @Override
    public Mapper<TestTriage, TestTriageDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<TestTriage, TestTriageDTO, TestTriageMapper> getServiceDTO() {
        return this;
    }

    public TestTriageDTO setAssigneeToTest(UserDTO user, Long testId) {
        return convertToDTO(service.setAssigneeToTest(user.getId(), testId));
    }

    public TestTriageDTO markTestAsTriaged(UserDTO user, Long testId) {
        return convertToDTO(service.markTestAsTriaged(userServiceDTO.convertToEntity(user), testId));
    }

    public List<TestTriageDTO> findAllByBuildAndStateNot(BuildDTO lastExecutedBuild, StateType pass) {
        return convertToDTO(service.findAllByBuildAndStateNot(buildServiceDTO.convertToEntity(lastExecutedBuild), pass));
    }

    public TestTriageDTO pin(TestTriage testTriage, User user) {
        return convertToDTO(service.switchPin(testTriage, user));
    }

    public List<TestTriageHistoryDTO> getTestHistory(TestTriage testTriage) {
        return service.getTestHistory(testTriage);
    }



    public TestTriageDTO triage(UpdateTriageDTO updateTriageDTO, TestTriageDTO dto) {
        TestTriage testTriage = findEntity(dto.getId());

        // Assign the triage to the logged user
        if (dto.getTriager() == null) {
            dto.setTriager(updateTriageDTO.getLoggedUserDTO());
        }

        dto.setProductId(updateTriageDTO.getTestTriageDTO() == null ? null : updateTriageDTO.getTestTriageDTO().getProductId());
        // See if the triage state has changed
        boolean isNowTriaged = dto.isTriaged();
        boolean wasTriaged = testTriage.isTriaged();

        boolean hadProductBug = testTriage.hasProductBug();

        TestTriageDTO newTriageDTO = update(dto);

        TestTriage newTriage = findEntity(dto.getId());

        boolean hasNowProductBug = newTriage.hasProductBug();

        service.triageAttempt(newTriage, isNowTriaged, wasTriaged);

        issueTicketService.updateProductIssue(newTriage, hasNowProductBug, hadProductBug);

        return newTriageDTO;
    }

    public TestTriageDTO convertToKanban(TestTriage entity){
        return mapper.convertToKanban(entity);
    }
}
