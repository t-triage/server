/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.NoteDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.NoteMapper;
import com.clarolab.model.Note;
import com.clarolab.service.NoteService;
import com.clarolab.service.TTriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NoteServiceDTO implements BaseServiceDTO<Note, NoteDTO, NoteMapper> {

    @Autowired
    private NoteService service;

    @Autowired
    private NoteMapper mapper;

    @Override
    public TTriageService<Note> getService() {
        return service;
    }

    @Override
    public Mapper<Note, NoteDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<Note, NoteDTO, NoteMapper> getServiceDTO() {
        return this;
    }
}
