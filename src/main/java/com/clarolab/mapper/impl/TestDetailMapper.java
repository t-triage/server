/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.details.TestDetailDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.detail.TestDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.getDTOList;
import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class TestDetailMapper implements Mapper<TestDetail, TestDetailDTO> {

    @Autowired
    private ErrorOccurenceMapper errorOccurenceMapper;

    @Autowired
    private TestOccurrenceMapper testOccurrenceMapper;

    @Override
    public TestDetailDTO convertToDTO(TestDetail testDetail) {
        if (testDetail == null) {
            return null;
        }
        TestDetailDTO dto = new TestDetailDTO();
        //Entry data
        setEntryFields(testDetail, dto);
        //TestDetail data
        dto.setHistoricPasses(testDetail.getHistoricPasses());
        dto.setFailsSince(testDetail.getFailsSince());

        dto.setConsecutiveFails(testDetail.getConsecutiveFails());
        dto.setConsecutivePasses(testDetail.getConsecutivePasses());
        dto.setHistoricFails(testDetail.getHistoricFails());
        dto.setPassSince(testDetail.getPassSince());

        dto.setErrorOccurrenceDTOS(getDTOList(errorOccurrence -> errorOccurenceMapper.convertToDTO(errorOccurrence), testDetail.getErrorOccurrences()));
        dto.setTestOccurrenceDTOS(getDTOList(testOccurrence -> testOccurrenceMapper.convertToDTO(testOccurrence), testDetail.getTestOccurrences()));
        return dto;
    }

    @Override
    public TestDetail convertToEntity(TestDetailDTO dto) {
        return null;
    }

}
