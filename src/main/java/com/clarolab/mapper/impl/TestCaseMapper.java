/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.TestCaseDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.TestCase;
import com.clarolab.service.TestCaseService;
import com.clarolab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class TestCaseMapper implements Mapper<TestCase, TestCaseDTO> {

    @Autowired
    private UserService userService;
    @Autowired
    private TestCaseService testCaseService;

    @Override
    public TestCaseDTO convertToDTO(TestCase entity) {
        TestCaseDTO dto = new TestCaseDTO();

        setEntryFields(entity, dto);

        dto.setName(entity.getName());
        dto.setLocationPath(entity.getLocationPath());
        return dto;
    }

    @Override
    public TestCase convertToEntity(TestCaseDTO dto) {
        TestCase entity = null;
        if (dto.getId() == null || dto.getId() < 1) {
            // Data is never created by gui

        } else {
            entity = testCaseService.find(dto.getId());
            // Data is never updated
        }
        return entity;
    }

}
