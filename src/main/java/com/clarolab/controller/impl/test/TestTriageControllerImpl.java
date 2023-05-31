/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl.test;

import com.clarolab.aaa.util.AuthContextHelper;
import com.clarolab.controller.TestTriageController;
import com.clarolab.controller.impl.BaseControllerImpl;
import com.clarolab.dto.TestTriageDTO;
import com.clarolab.dto.UpdateTriageDTO;
import com.clarolab.dto.db.TestTriageHistoryDTO;
import com.clarolab.dto.details.TestDetailDTO;
import com.clarolab.model.TestTriage;
import com.clarolab.model.User;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.AutomatedTestIssueServiceDTO;
import com.clarolab.serviceDTO.IssueTicketServiceDTO;
import com.clarolab.serviceDTO.TestDetailServiceDTO;
import com.clarolab.serviceDTO.TestTriageServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.transaction.Transactional;
import java.util.List;

@CrossOrigin
@RestController
public class TestTriageControllerImpl extends BaseControllerImpl<TestTriageDTO> implements TestTriageController {

    @Autowired
    private TestTriageServiceDTO testTriageServiceDTO;

    @Autowired
    private IssueTicketServiceDTO issueTicketServiceDTO;

    @Autowired
    private AutomatedTestIssueServiceDTO automatedTestIssueServiceDTO;

    @Autowired
    private TestDetailServiceDTO testDetailServiceDTO;

    @Autowired
    private AuthContextHelper authContextHelper;

    @Override
    protected TTriageService<TestTriageDTO> getService() {
        return testTriageServiceDTO;
    }

    @ApiIgnore
    @Override
    public ResponseEntity<TestTriageDTO> save(TestTriageDTO entity) {
        return super.save(entity);
    }

    @ApiIgnore
    @Override
    public ResponseEntity<Long> delete(Long id) {
        return super.delete(id);
    }

    @Override
    public ResponseEntity<TestDetailDTO> getTestDetail(Long id) {
        return ResponseEntity.ok(testDetailServiceDTO.getTestDetail(id));
    }

    @Override
    public ResponseEntity<List<TestTriageHistoryDTO>> getTestHistory(Long id) {
        TestTriage testTriage = testTriageServiceDTO.findEntity(id);
        if (testTriage == null) {
            return null;
        }
        return ResponseEntity.ok(testTriageServiceDTO.getTestHistory(testTriage));
    }

    @Override
    public ResponseEntity<TestTriageDTO> pin(Long id) {
        User user = authContextHelper.getCurrentUser();
        TestTriage testTriage = testTriageServiceDTO.findEntity(id);
        if (testTriage == null || user == null) {
            return null;
        }
        return ResponseEntity.ok(testTriageServiceDTO.pin(testTriage, user));
    }

    @Override
    @Transactional
    public ResponseEntity<TestTriageDTO> draftTriage(UpdateTriageDTO updateTriageDTO, boolean triage) {
        if(triage)  updateTriageDTO.markTriaged();
        TestTriageDTO newTriage = basicTriage(updateTriageDTO);
        return ResponseEntity.ok(newTriage);
    }

    private TestTriageDTO basicTriage(UpdateTriageDTO updateTriageDTO) {
        updateTriageDTO.setLoggedUserDTO(authContextHelper.getCurrentUserAsDTO());

        if(updateTriageDTO.getTestTriageDTO().isTriaged())  updateTriageDTO.markTriaged();

        // TEST TRIAGE UPDATE
        TestTriageDTO newTriage = testTriageServiceDTO.triage(updateTriageDTO, updateTriageDTO.getTestTriageDTO());

        // TICKET UPDATE
        issueTicketServiceDTO.triage(updateTriageDTO, updateTriageDTO.getIssueTicketDTO());

        // AUTOMATION UPDATE
        automatedTestIssueServiceDTO.triage(updateTriageDTO, updateTriageDTO.getAutomatedTestIssueDTO());
        return newTriage;
    }

    @Override
    public ResponseEntity<Boolean> triageAll(List<UpdateTriageDTO> updateTriageDTOList) {
        updateTriageDTOList.forEach(updateTriageDTO -> {
           updateTriageDTO.markTriaged();
           basicTriage(updateTriageDTO);
        });

        return ResponseEntity.ok(true);
    }
}
