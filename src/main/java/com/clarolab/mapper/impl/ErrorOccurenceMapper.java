/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.TestTriageDTO;
import com.clarolab.dto.details.ErrorOccurrenceDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.detail.ErrorOccurrence;
import com.clarolab.util.StringUtils;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class ErrorOccurenceMapper implements Mapper<ErrorOccurrence, ErrorOccurrenceDTO> {

    @Override
    public ErrorOccurrenceDTO convertToDTO(ErrorOccurrence errorOccurrence) {
        ErrorOccurrenceDTO dto = new ErrorOccurrenceDTO();

        setEntryFields(errorOccurrence, dto);

        dto.setSuiteID(errorOccurrence.getSuiteID());
        dto.setSuiteName(errorOccurrence.getSuiteName());
        dto.setGroupName(StringUtils.classTail(errorOccurrence.getSuiteName()));
        dto.setTestID(errorOccurrence.getTestID());
        dto.setTestName(errorOccurrence.getTestName());
        dto.setDisplayName(StringUtils.methodToWords(errorOccurrence.getTestName()));

        //We need to generate this here because we have a recursive structured that must end here and not call mapper.
        TestTriageDTO testTriageDTO = TestTriageMapper.basicTestTriageDTO(errorOccurrence.getTestTriage());
        dto.setTestTriage(testTriageDTO);

        return dto;
    }

    @Override
    public ErrorOccurrence convertToEntity(ErrorOccurrenceDTO dto) {
        return null;
    }
}
