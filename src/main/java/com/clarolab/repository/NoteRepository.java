/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.model.Note;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends BaseRepository<Note> {

    Note findByName(String name);


}
