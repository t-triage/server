/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.DeadlineDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.Deadline;
import com.clarolab.service.DeadlineService;
import com.clarolab.service.NoteService;
import com.clarolab.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.getNullableByID;
import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class DeadlineMapper implements Mapper<Deadline, DeadlineDTO> {

    @Autowired
    private DeadlineService deadlineService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private ProductService productService;

    @Override
    public DeadlineDTO convertToDTO(Deadline deadline) {
        DeadlineDTO deadlineDTO = new DeadlineDTO();

        setEntryFields(deadline, deadlineDTO);

        deadlineDTO.setName(deadline.getName());
        deadlineDTO.setDeadlineDate(deadline.getDeadlineDate());
        deadlineDTO.setDescription(deadline.getDescription());
        deadlineDTO.setProduct(deadline.getProduct() == null ? null : deadline.getProduct().getId());
        deadlineDTO.setNote(deadline.getNote() == null ? null : deadline.getNote().getId());

        return deadlineDTO;
    }

    @Override
    public Deadline convertToEntity(DeadlineDTO dto) {
        Deadline deadline;
        if (dto.getId() == null || dto.getId() < 1) {
            deadline = Deadline.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .deadlineDate(dto.getDeadlineDate())
                    .product(getNullableByID(dto.getProduct(), id -> productService.find(id)))
                    .note(getNullableByID(dto.getNote(), id -> noteService.find(id)))
                    .build();

        } else {
            deadline = deadlineService.find(dto.getId());
            deadline.setEnabled(dto.getEnabled());
            deadline.setProduct(getNullableByID(dto.getProduct(), id -> productService.find(id)));
            deadline.setNote(getNullableByID(dto.getNote(), id -> noteService.find(id)));
            deadline.setName(dto.getName());
            deadline.setDescription(dto.getDescription());
            deadline.setDeadlineDate(dto.getDeadlineDate());
        }
        return deadline;
    }

}
