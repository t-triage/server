/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.controller.SlackController;
import com.clarolab.dto.SlackSpecDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.SlackSpecServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class SlackControllerImpl extends BaseControllerImpl<SlackSpecDTO> implements SlackController {



    @Autowired
    private SlackSpecServiceDTO slackService;


    @Override
    protected TTriageService<SlackSpecDTO> getService() {
        return slackService;
    }

    @Override
    public ResponseEntity<SlackSpecDTO> container(Long containerId) {
        return ResponseEntity.ok(slackService.findContainer(containerId));
    }

    @Override
    public ResponseEntity<SlackSpecDTO> executor(Long executorId) {
        return ResponseEntity.ok(slackService.findExecutor(executorId));
    }

    @Override
    public ResponseEntity<String> test(Long containerId) {
        return ResponseEntity.ok(slackService.sendTestMessage(containerId));
    }

    @Override
    public ResponseEntity<SlackSpecDTO> save(SlackSpecDTO entity) {
        if (entity.getExecutorId() == null) {
            // ok, we are just updating a container spec
            return ResponseEntity.ok(getService().save(entity));
        } else {
            SlackSpecDTO dto = slackService.findExecutor(entity.getExecutorId());
            if (dto == null) {
                // create new SlackSpec based on entity
                // SlackSpecMapper convertToEntity y persistirlo
                slackService.save(entity);
            } else {
                // update DTO based on entity
                entity.setId(dto.getId());
                return ResponseEntity.ok(getService().save(entity));
            }
        }
        return ResponseEntity.ok(getService().save(entity));
    }

    @Override
    public ResponseEntity<SlackSpecDTO> update(SlackSpecDTO entity){
        return save(entity);
    }


}