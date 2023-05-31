/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.mapper.impl;

import com.clarolab.dto.AutomatedTestIssueDTO;
import com.clarolab.functional.test.mapper.AbstractMapperTest;
import com.clarolab.mapper.impl.AutomatedTestIssueMapper;
import com.clarolab.mapper.impl.TestTriageMapper;
import com.clarolab.mapper.impl.UserMapper;
import com.clarolab.model.AutomatedTestIssue;
import com.clarolab.model.helper.AutomationIssueHelper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AutomatedTestIssueMapperTest extends AbstractMapperTest<AutomatedTestIssue, AutomatedTestIssueDTO> {

    @Autowired
    private AutomatedTestIssueMapper mapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TestTriageMapper testTriageMapper;

    @Test
    public void testEntityToDTOConversion() {
        AutomatedTestIssue entity = getEntity();
        AutomatedTestIssueDTO dto = mapper.convertToDTO(entity);
        this.assertConversion(entity, dto);
    }

    @Test
    public void testDTOToEntityConversion() {
        AutomatedTestIssueDTO dto = getDTO();

        dto.setIssueType("OPEN");
        dto.setUserFixPriority("AUTOMATIC");

        AutomatedTestIssue entity = mapper.convertToEntity(dto);
        this.assertConversion(entity, dto);
    }

    @Override
    public void assertConversion(AutomatedTestIssue entity, AutomatedTestIssueDTO dto) {
        super.assertConversion(entity, dto);
    }

    public AutomatedTestIssueMapperTest() { super(AutomatedTestIssue.class, AutomatedTestIssueDTO.class); }

    public AutomatedTestIssueDTO getDTO() {
        AutomatedTestIssueDTO dto = super.getDTO();

        dto.setTriager(userMapper.convertToDTO(provider.getUser()));
        dto.setTestTriage(testTriageMapper.convertToDTO(provider.getTestCaseTriage()));

        return dto;
    }

    @Test
    public void trendEmpty() {
        AutomatedTestIssue entity = new AutomatedTestIssue();
        entity.setTrend("");
        Assert.assertNotNull(AutomationIssueHelper.toString(entity));
    }

    @Test
    public void trendFirstPass() {
        AutomatedTestIssue entity = new AutomatedTestIssue();
        entity.setTrend("1");
        Assert.assertNotNull(AutomationIssueHelper.toString(entity));
    }

    @Test
    public void trendFirstFail() {
        AutomatedTestIssue entity = new AutomatedTestIssue();
        entity.setTrend("0");
        Assert.assertNotNull(AutomationIssueHelper.toString(entity));
    }

    @Test
    public void trendTwoFail() {
        AutomatedTestIssue entity = new AutomatedTestIssue();
        entity.setTrend("00");
        Assert.assertNotNull(AutomationIssueHelper.toString(entity));
    }

    @Test
    public void trendTwoMix() {
        AutomatedTestIssue entity = new AutomatedTestIssue();
        entity.setTrend("10");
        Assert.assertNotNull(AutomationIssueHelper.toString(entity));
    }

    @Test
    public void trendLongFail() {
        AutomatedTestIssue entity = new AutomatedTestIssue();
        entity.setTrend("011");
        Assert.assertNotNull(AutomationIssueHelper.toString(entity));
    }

    @Test
    public void trendLongPass() {
        AutomatedTestIssue entity = new AutomatedTestIssue();
        entity.setTrend("000011");
        Assert.assertNotNull(AutomationIssueHelper.toString(entity));
    }


}
