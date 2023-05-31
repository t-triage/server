/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.controller.ConnectorController;
import com.clarolab.dto.ConnectorDTO;
import com.clarolab.dto.ContainerDTO;
import com.clarolab.dto.push.ServiceAuthDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.ConnectorServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class ConnectorControllerImpl extends BaseControllerImpl<ConnectorDTO> implements ConnectorController {

    @Autowired
    private ConnectorServiceDTO connectorService;

    @Autowired
    private ActionsControllerImpl actionsController;

    @Override
    protected TTriageService<ConnectorDTO> getService() {
        return connectorService;
    }

    @Override
    public ResponseEntity<List<ContainerDTO>> getAllContainers(Long id) {
        return ResponseEntity.ok(connectorService.getAllContainers(id));
    }

    @Override
    public ResponseEntity<List<ContainerDTO>> getAllContainers() {
        return ResponseEntity.ok(connectorService.getAllContainers());
    }

    @Override
    public ResponseEntity<Boolean> populate(Long id) {
        boolean populate = connectorService.populate(id);
        return populate ? actionsController.processImportedSuites() : ResponseEntity.ok(false);
    }

    @Override
    public ResponseEntity<Boolean> populate() {
        boolean populate = connectorService.populate();
        return populate ? actionsController.processImportedSuites() : ResponseEntity.ok(false);
    }

    @Override
    public ResponseEntity<Boolean> validate(Long id) {
        return ResponseEntity.ok(connectorService.isValid(id));
    }

    @Override
    public ResponseEntity<ServiceAuthDTO> newServiceAuth(Long id) {
        return ResponseEntity.ok(connectorService.newServiceAuth(id));
    }

    @Override
    public ResponseEntity<ServiceAuthDTO> getServiceAuth(Long id) {
        return ResponseEntity.ok(connectorService.getServiceAuth(id));
    }
}