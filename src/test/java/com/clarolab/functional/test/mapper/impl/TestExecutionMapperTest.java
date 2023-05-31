/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.mapper.impl;

import com.clarolab.dto.TestExecutionDTO;
import com.clarolab.functional.test.mapper.AbstractMapperTest;
import com.clarolab.mapper.impl.TestExecutionMapper;
import com.clarolab.model.TestExecution;
import com.clarolab.populate.UseCaseDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestExecutionMapperTest extends AbstractMapperTest<TestExecution, TestExecutionDTO> {

    @Autowired
    private TestExecutionMapper mapper;

    @Autowired
    private UseCaseDataProvider provider;

    @Test
    public void testEntityToDTOConversion() {
        TestExecution testExecution = getEntity();
        TestExecutionDTO testExecutionDTO = mapper.convertToDTO(testExecution);
        this.assertConversion(testExecution, testExecutionDTO);
    }

    @Test
    public void testDTOToEntityConversion() {
       TestExecutionDTO testExecutionDTO = getDTO();

       testExecutionDTO.setStatus("FAIL");

       TestExecution testExecution = mapper.convertToEntity(testExecutionDTO);
       this.assertConversion(testExecution, testExecutionDTO);
    }

    @Override
    public void assertConversion(TestExecution testExecution, TestExecutionDTO testExecutionDTO) {
        super.assertConversion(testExecution, testExecutionDTO);
    }

    public TestExecutionMapperTest() {
        super(TestExecution.class, TestExecutionDTO.class);
    }

    @Before
    public void clearProvider() {
        provider.clear();
    }

}
