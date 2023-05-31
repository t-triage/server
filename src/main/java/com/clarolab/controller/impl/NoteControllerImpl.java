/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.controller.NoteController;
import com.clarolab.dto.NoteDTO;
import com.clarolab.service.TTriageService;
import com.clarolab.serviceDTO.NoteServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@CrossOrigin
@RestController
public class NoteControllerImpl extends BaseControllerImpl<NoteDTO> implements NoteController {

    @Autowired
    private NoteServiceDTO noteServiceDTO;

    @Override
    protected TTriageService<NoteDTO> getService() {
        return noteServiceDTO;
    }

    @Override
    @ApiIgnore
    public ResponseEntity<NoteDTO> save(NoteDTO dto) {
        return super.save(dto);
    }

    @Override
    @ApiIgnore
    public ResponseEntity<Long> delete(Long id) {
        return super.delete(id);
    }

    @Override
    @ApiIgnore
    public ResponseEntity<NoteDTO> update(NoteDTO dto) {
        return super.update(dto);
    }
}
