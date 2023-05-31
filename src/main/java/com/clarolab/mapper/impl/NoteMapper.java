/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.NoteDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.Note;
import com.clarolab.service.NoteService;
import com.clarolab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.getNullableByID;
import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class NoteMapper implements Mapper<Note, NoteDTO> {

    @Autowired
    private UserService userService;
    @Autowired
    private NoteService noteService;

    @Override
    public NoteDTO convertToDTO(Note note) {
        /* El note en esta capa NO deberia ser null. Si llega null es porque hay algo mal
       /* if (note == null) {
            return null;
        }*/
        NoteDTO noteDTO = new NoteDTO();

        setEntryFields(note, noteDTO);

        noteDTO.setDescription(note.getDescription());
        noteDTO.setName(note.getName());
        noteDTO.setAuthor(note.getAuthor() == null ? null : note.getAuthor().getId());
        return noteDTO;
    }

    @Override
    public Note convertToEntity(NoteDTO dto) {
        if (dto == null) {
            return null;
        }
        Note note;
        if (dto.getId() == null || dto.getId() < 1) {
            note = Note.builder()
                    .id(null)
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .author(getNullableByID(dto.getAuthor(), id -> userService.find(id)))
                    .build();

        } else {
            note = noteService.find(dto.getId());
//            note.setId(); Don't allow to update this.
            note.setEnabled(dto.getEnabled());
//            note.setTimestamp(dto.getTimestamp()); Don't allow to update this.
//            note.setUpdated(dto.getUpdated()); Don't allow to update this.
            note.setAuthor(getNullableByID(dto.getAuthor(), id -> userService.find(id)));
            note.setName(dto.getName());
            note.setDescription(dto.getDescription());
        }
        return note;
    }

}
