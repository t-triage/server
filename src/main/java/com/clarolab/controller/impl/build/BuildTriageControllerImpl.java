/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl.build;

import com.clarolab.controller.BuildTriageController;
import com.clarolab.controller.impl.BaseControllerImpl;
import com.clarolab.dto.BuildTriageDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.BuildTriageServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@CrossOrigin
@RestController
public class BuildTriageControllerImpl extends BaseControllerImpl<BuildTriageDTO> implements BuildTriageController {

    @Autowired
    private BuildTriageServiceDTO buildTriageService;

    @Override
    protected TTriageService<BuildTriageDTO> getService() {
        return buildTriageService;
    }

    @ApiIgnore
    @Override
    public ResponseEntity<BuildTriageDTO> save(BuildTriageDTO dto) {
        return super.save(dto);
    }

    @ApiIgnore
    @Override
    public ResponseEntity<Long> delete(Long id) {
        return super.delete(id);
    }

    @ApiIgnore
    @Override
    public ResponseEntity<BuildTriageDTO> update(BuildTriageDTO dto) {
        return super.update(dto);
    }

    @Override
    public ResponseEntity<Page<BuildTriageDTO>> list(@RequestParam(value = "query", required = false) String[] criteria, Pageable pageable) {
        return ResponseEntity.ok(buildTriageService.getPendingBuildTriages(null, false));
    }

    @Override
    public ResponseEntity<String> getTextDetail(Long id) {
        return ResponseEntity.ok(buildTriageService.getTextDetail(id));
    }
}
