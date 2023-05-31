/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.mapper.impl;

import com.clarolab.dto.TestCaseDTO;
import com.clarolab.functional.test.mapper.AbstractMapperTest;
import com.clarolab.mapper.impl.TestCaseMapper;
import com.clarolab.model.TestCase;
import com.clarolab.populate.UseCaseDataProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestCaseMapperTest extends AbstractMapperTest<TestCase, TestCaseDTO> {

    @Autowired
    private TestCaseMapper mapper;

    @Autowired
    private UseCaseDataProvider provider;

    @Test
    public void testEntityToDTOConversion() {
        TestCase testCase = getEntity();
        TestCaseDTO testCaseDTO = mapper.convertToDTO(testCase);
        this.assertConversion(testCase, testCaseDTO);
    }

    @Test
    public void testDTOToEntityConversion() {
        TestCaseDTO testCaseDTO = getDTO();
        TestCase testCase = mapper.convertToEntity(testCaseDTO);
        Assert.assertNull(testCase);
    }

    @Override
    public void assertConversion(TestCase testCase, TestCaseDTO testCaseDTO) {
      //  super.assertConversion(testCase, testCaseDTO);

        Assert.assertEquals(testCase.getName(), testCaseDTO.getName());
        Assert.assertEquals(testCase.getLocationPath(), testCaseDTO.getLocationPath());

    }

    public TestCaseMapperTest() {
        super(TestCase.class, TestCaseDTO.class);
    }

    @Before
    public void clearProvider() {
        provider.clear();
    }
}
