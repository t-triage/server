/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl.build;

import com.clarolab.controller.BuildController;
import com.clarolab.controller.impl.BaseControllerImpl;
import com.clarolab.dto.BuildDTO;
import com.clarolab.dto.push.DataDTO;
import com.clarolab.service.PushService;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.BuildServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@CrossOrigin
@RestController
public class BuildControllerImpl extends BaseControllerImpl<BuildDTO> implements BuildController {

    @Autowired
    private BuildServiceDTO buildService;

    @Autowired
    private PushService pushService;

    @Override
    protected TTriageService<BuildDTO> getService() {
        return buildService;
    }

    @Override
    @ApiIgnore
    public ResponseEntity<BuildDTO> save(BuildDTO dto) {
        return super.save(dto);
    }

    @Override
    @ApiIgnore
    public ResponseEntity<Long> delete(Long id) {
        return super.delete(id);
    }

    @Override
    @ApiIgnore
    public ResponseEntity<BuildDTO> update(BuildDTO dto) {
        return super.update(dto);
    }

    @Override
    public ResponseEntity<BuildDTO> push(DataDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(buildService.convertToDTO(pushService.push(dto)));
    }
}
