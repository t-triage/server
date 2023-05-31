/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;


import com.clarolab.model.Note;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.NoteRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log
public class NoteService extends BaseService<Note> {

    @Autowired
    private NoteRepository noteRepository;

    @Override
    public BaseRepository<Note> getRepository() {
        return noteRepository;
    }
}
