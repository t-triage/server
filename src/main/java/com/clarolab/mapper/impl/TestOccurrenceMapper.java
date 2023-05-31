/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.TestTriageDTO;
import com.clarolab.dto.details.TestOccurrenceDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.detail.TestOccurrence;
import com.clarolab.util.StringUtils;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class TestOccurrenceMapper implements Mapper<TestOccurrence, TestOccurrenceDTO> {

    @Override
    public TestOccurrenceDTO convertToDTO(TestOccurrence testOccurrence) {
        TestOccurrenceDTO dto = new TestOccurrenceDTO();

        setEntryFields(testOccurrence, dto);

        dto.setSuiteID(testOccurrence.getSuiteID());
        dto.setSuiteName(testOccurrence.getSuiteName());
        dto.setGroupName(StringUtils.classTail(testOccurrence.getSuiteName()));

        //We need to generate this here because we have a recursive structured that must end here and not call mapper.
        TestTriageDTO testTriageDTO = TestTriageMapper.basicTestTriageDTO(testOccurrence.getTestTriage());
        dto.setTestTriage(testTriageDTO);

        return dto;
    }

    @Override
    public TestOccurrence convertToEntity(TestOccurrenceDTO dto) {
        return null;
    }


}
