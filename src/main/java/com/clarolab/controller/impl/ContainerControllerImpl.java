/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.controller.ContainerController;
import com.clarolab.dto.ContainerDTO;
import com.clarolab.service.GoogleAnalyticsService;
import com.clarolab.service.TTriageService;
import com.clarolab.service.UserService;
import com.clarolab.serviceDTO.ContainerServiceDTO;
import com.clarolab.serviceDTO.TriageSpecServiceDTO;
import com.clarolab.view.KeyValuePair;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@Log
public class ContainerControllerImpl extends BaseControllerImpl<ContainerDTO> implements ContainerController {

    @Autowired
    private ContainerServiceDTO containerService;

    @Autowired
    private TriageSpecServiceDTO triageSpecServiceDTO;

    @Autowired
    private ActionsControllerImpl actionsController;

    @Autowired
    private GoogleAnalyticsService gaService;

    @Autowired
    private UserService userService;

    @Override
    protected TTriageService<ContainerDTO> getService() {
        return containerService;
    }

    @Override
    public ResponseEntity<Boolean> populate(Long id) {
    /*    if (!LicenceValidator.isValid()){
            return ResponseEntity.ok(false);
        }*/

        gaService.sendEvent(GoogleAnalyticsService.GACategory.POPULATE, GoogleAnalyticsService.GAEvent.POPULATE_MANUAL_EVENT);
        boolean populate = containerService.populate(id);
        return populate ? actionsController.processImportedSuites() : ResponseEntity.ok(false);
    }

    @Override
    public ResponseEntity<Boolean> populateByName(String name) {
        boolean populate = containerService.populate(name);
        return populate ? actionsController.processImportedSuites() : ResponseEntity.ok(false);
    }

    @Override
    public ResponseEntity<Page<ContainerDTO>> suggested() {
        return ResponseEntity.ok(new PageImpl<>(containerService.suggested()));
    }

    @Override
    public ResponseEntity<Boolean> validate(Long id) {
        return ResponseEntity.ok(containerService.isValid(id));
    }

    @Override
    public ResponseEntity<ContainerDTO> save(ContainerDTO dto) {
        ResponseEntity<ContainerDTO> containerDTO = super.save(dto);
        triageSpecServiceDTO.save(dto.getTriageSpec(), containerDTO.getBody());
        return containerDTO;
    }

    @Override
    public ResponseEntity<List<KeyValuePair>> getContainersNames(Long id) {
        return  ResponseEntity.ok(containerService.getContainersNames(id));
    }
}
