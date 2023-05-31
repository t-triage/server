/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.mapper.impl;

import com.clarolab.dto.TestTriageDTO;
import com.clarolab.dto.UserDTO;
import com.clarolab.functional.test.mapper.AbstractMapperTest;
import com.clarolab.functional.test.mapper.TestDTOFactory;
import com.clarolab.mapper.impl.TestTriageMapper;
import com.clarolab.model.TestTriage;
import com.clarolab.populate.UseCaseDataProvider;
import lombok.extern.java.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.logging.Level;

@Log
public class TestTriageMapperTest extends AbstractMapperTest<TestTriage, TestTriageDTO> {

    @Autowired
    private TestTriageMapper mapper;

    @Autowired
    private UseCaseDataProvider provider;

    @Test
    public void testEntityToDTOConversion() {
        TestTriage testTriage = getEntity();

        testTriage.setNote(provider.getNote());

        TestTriageDTO testTriageDTO = mapper.convertToDTO(testTriage);
        this.assertConversion(testTriage, testTriageDTO);
    }

    @Test
    public void testDTOToEntityConversion() {
        TestTriageDTO testTriageDTO = getDTO();

        testTriageDTO.setTestFailType("FLAKY");
        testTriageDTO.setApplicationFailType("FLAKY");
        testTriageDTO.setCurrentState("FAIL");
        testTriageDTO.getTestExecution().setStatus("FAIL");
        try {
            UserDTO userDTO = TestDTOFactory.create(UserDTO.class);
            userDTO.setRoleType("ROLE_ADMIN");
            testTriageDTO.setTriager(userDTO);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Test error", e);
        }

        TestTriage testTriage = mapper.convertToEntity(testTriageDTO);
        this.assertConversion(testTriage, testTriageDTO);
    }

    @Override
    public void assertConversion(TestTriage tt, TestTriageDTO ttDTO) {
        super.assertConversion(tt, ttDTO);

        Assert.assertEquals(tt.getApplicationFailType().name(), ttDTO.getApplicationFailType());
        Assert.assertEquals(tt.getTestFailType().name(), ttDTO.getTestFailType());
        Assert.assertEquals(tt.getCurrentState().name(), ttDTO.getCurrentState());

        Assert.assertEquals(tt.getFile(), ttDTO.getFile());
        Assert.assertEquals(tt.getTags(), ttDTO.getTags());
        Assert.assertEquals(tt.getRank(), ttDTO.getRank());
        Assert.assertEquals(tt.getSnooze(), ttDTO.getSnooze());
        Assert.assertEquals(tt.isTriaged(), ttDTO.isTriaged());
        Assert.assertEquals(tt.getExecutorName(), ttDTO.getExecutorName());
        Assert.assertEquals(tt.getTriager().getId(), ttDTO.getTriager().getId());
        Assert.assertEquals(tt.getNote().getId(), ttDTO.getNote().getId());
//        Assert.assertEquals(tt.getBuildId(), ttDTO.createNewBuild());
//        Assert.assertEquals(tt.getTestExecution().getId(), ttDTO.getTestExecution().getId());
//        Assert.assertEquals(tt.getPreviousTriageIds().size(), ttDTO.getPreviousTriage().size());


    }

    public TestTriageMapperTest() {
        super(TestTriage.class, TestTriageDTO.class);
    }

    @Before
    public void clearProvider() {
        provider.clear();
    }

}
