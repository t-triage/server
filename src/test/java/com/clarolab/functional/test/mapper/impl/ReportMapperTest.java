/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.mapper.impl;

import com.clarolab.dto.ReportDTO;
import com.clarolab.functional.test.mapper.AbstractMapperTest;
import com.clarolab.mapper.impl.ReportMapper;
import com.clarolab.model.Report;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ReportMapperTest extends AbstractMapperTest<Report, ReportDTO> {

    @Autowired
    private ReportMapper mapper;

    @Test
    public void testEntityToDTOConversion() {
        Report report = getEntity();
        ReportDTO reportDTO = mapper.convertToDTO(report);
        this.assertConversion(report, reportDTO);
    }

    @Test
    public void testDTOToEntityConversion() {
        ReportDTO reportDTO = getDTO();

        reportDTO.setStatus("SUCCESS");
        reportDTO.setType("ROBOT");

        Report report = mapper.convertToEntity(reportDTO);
        this.assertConversion(report, reportDTO);
    }

    @Override
    public void assertConversion(Report report, ReportDTO dto) {
        super.assertConversion(report, dto);

        Assert.assertEquals(report.getType().name(), dto.getType());
        Assert.assertEquals(report.getStatus().name(), dto.getStatus());
        Assert.assertEquals(report.getPassCount(), dto.getPassCount());
        Assert.assertEquals(report.getFailCount(), dto.getFailCount());
        Assert.assertEquals(report.getSkipCount(), dto.getSkipCount());
        Assert.assertEquals(report.getDuration(), dto.getDuration(), 0.001);
        Assert.assertEquals(report.getExecutiondate(), dto.getExecutiondate());
        Assert.assertEquals(report.getDescription(), dto.getDescription());

    }

    public ReportMapperTest() {
        super(Report.class, ReportDTO.class);
    }
}
