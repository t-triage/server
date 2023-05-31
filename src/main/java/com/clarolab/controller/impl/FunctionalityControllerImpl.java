/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.controller.FunctionalityController;
import com.clarolab.dto.FunctionalityDTO;
import com.clarolab.model.manual.ManualTestCase;
import com.clarolab.model.manual.service.ManualTestCaseService;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.FunctionalityServiceDTO;
import com.clarolab.serviceDTO.ManualTestCaseServiceDTO;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@CrossOrigin
@RestController
@Log
public class FunctionalityControllerImpl extends BaseControllerImpl<FunctionalityDTO> implements FunctionalityController {

    @Autowired
    private FunctionalityServiceDTO functionalityServiceDTO;

    @Autowired
    private ManualTestCaseServiceDTO manualTestCaseServiceDTO;

    @Autowired
    private ManualTestCaseService manualTestCaseService;

    @Override
    protected TTriageService<FunctionalityDTO> getService() {
        return functionalityServiceDTO;
    }
    
    @Override
    @ApiIgnore
    public ResponseEntity<Long> delete(Long id) {
        return super.delete(id);
    }

    @Override
    public ResponseEntity<FunctionalityDTO> createNewFunctionality(Long manualTestCaseId, @RequestBody FunctionalityDTO entity) {
        FunctionalityDTO newFunctionality = getService().save(entity);
        ManualTestCase manualTestCase = manualTestCaseServiceDTO.findEntity(manualTestCaseId);
        manualTestCase.setFunctionalityEntity(functionalityServiceDTO.findEntity(newFunctionality.getId()));
        manualTestCaseService.update(manualTestCase);
        return ResponseEntity.status(HttpStatus.CREATED).body(newFunctionality);
    }

    @Override
    public ResponseEntity<FunctionalityDTO> findFunctionalityByExternalId(String externalId) {
        return ResponseEntity.ok(functionalityServiceDTO.findFunctionalityByExternalId(externalId));
    }

    @Override
    public ResponseEntity<Page<FunctionalityDTO>> searchFunctionality(String name) {
        return ResponseEntity.ok(new PageImpl<>(functionalityServiceDTO.searchFunctionality(name)));
    }
}
