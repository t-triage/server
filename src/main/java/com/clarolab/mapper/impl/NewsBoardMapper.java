/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.NewsBoardDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.NewsBoard;
import com.clarolab.service.NewsBoardService;
import com.clarolab.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class NewsBoardMapper implements Mapper<NewsBoard, NewsBoardDTO> {

    @Autowired
    private NewsBoardService newsBoardService;

    @Autowired
    private NewsBoardMapper newsBoardMapper;

    @Override
    public NewsBoardDTO convertToDTO(NewsBoard entity) {
        /* El manualTestCase en esta capa NO deberia ser null. Si llega null es porque hay algo mal*/
        /*if (manualTestCase == null) {
            return null;
        }*/
        NewsBoardDTO dto = new NewsBoardDTO();

        setEntryFields(entity, dto);

        dto.setEventTime(entity.getEventTime());
        dto.setText(entity.getMessage());
        dto.setImportant(DateUtils.beginDay(0) < entity.getEventTime());
        dto.setType(entity.getType().name());

        return dto;
    }

    @Override
    public NewsBoard convertToEntity(NewsBoardDTO dto) {
        return null;
    }

}
