/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.mapper.impl;

import com.clarolab.dto.NoteDTO;
import com.clarolab.functional.test.mapper.AbstractMapperTest;
import com.clarolab.mapper.impl.NoteMapper;
import com.clarolab.model.Note;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class NoteMapperTest extends AbstractMapperTest<Note, NoteDTO> {

    @Autowired
    private NoteMapper mapper;

    @Test
    public void testEntityToDTOConversion() {
        Note note = getEntity();
        NoteDTO noteDTO = mapper.convertToDTO(note);
        this.assertConversion(note, noteDTO);
    }

    @Test
    public void testDTOToEntityConversion() {
        NoteDTO noteDTO = getDTO();
        Note note = mapper.convertToEntity(noteDTO);
        this.assertConversion(note, noteDTO);
    }

    @Override
    public void assertConversion(Note note, NoteDTO noteDTO) {
        super.assertConversion(note, noteDTO);

        Assert.assertEquals(note.getName(), noteDTO.getName());
        Assert.assertEquals(note.getDescription(), noteDTO.getDescription());

    }

    public NoteMapperTest() {
        super(Note.class, NoteDTO.class);
    }
}
