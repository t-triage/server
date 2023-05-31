/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.ReportDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.Build;
import com.clarolab.model.Report;
import com.clarolab.model.types.ReportType;
import com.clarolab.model.types.StatusType;
import com.clarolab.service.ReportService;
import com.clarolab.service.TestExecutionService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.*;

@Component
public class ReportMapper implements Mapper<Report, ReportDTO> {

    @Autowired
    private ReportService reportService;

    @Autowired
    private TestExecutionService testExecutionService;

    @Override
    public ReportDTO convertToDTO(Report report) {
        ReportDTO reportDTO = convertToDTOSimple(report);
        reportDTO.setTestExecutions(getIDList(report.getTestExecutions()));
        return reportDTO;
    }

    public ReportDTO convertToDTOSimple(Report report) {
        ReportDTO reportDTO = new ReportDTO();

        setEntryFields(report, reportDTO);

        reportDTO.setType(getEnumName(report.getType()));
        reportDTO.setStatus(getEnumName(report.getStatus()));
        reportDTO.setPassCount(report.getPassCount());
        reportDTO.setFailCount(report.getFailCount());
        reportDTO.setSkipCount(report.getSkipCount());
        reportDTO.setDuration(report.getDuration());
        reportDTO.setTotalTest(report.getTotalTest());
        reportDTO.setExecutiondate(report.getExecutiondate());
        reportDTO.setProductVersion(report.getProductVersion());
        reportDTO.setDescription(report.getDescription());
        reportDTO.setTestExecutions(Lists.newArrayList());
        return reportDTO;
    }

    @Override
    public Report convertToEntity(ReportDTO dto) {
        Report report;
        if (dto.getId() == null || dto.getId() < 1) {
            report = Report.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .type(ReportType.valueOf(dto.getType()))
                    .status(StatusType.valueOf(dto.getStatus()))
                    .passCount(dto.getPassCount())
                    .failCount(dto.getFailCount())
                    .skipCount(dto.getSkipCount())
                    .duration(dto.getDuration())

                    .executiondate(dto.getExecutiondate())
                    .testExecutions(getEntryListFromIDs(id -> testExecutionService.find(id), dto.getTestExecutions()))
                    .description(dto.getDescription())
                    .build();
        } else {
            report = reportService.find(dto.getId());
//            report.setId(dto.getId());
//            report.setUpdated(dto.getUpdated());
//            report.setTimestamp(dto.getTimestamp());
            report.setEnabled(dto.getEnabled());
            report.setType(ReportType.valueOf(dto.getType()));
            report.setStatus(StatusType.valueOf(dto.getStatus()));
            report.setPassCount(dto.getPassCount());
            report.setFailCount(dto.getFailCount());
            report.setSkipCount(dto.getSkipCount());
            report.setDuration(dto.getDuration());
            report.setExecutiondate(dto.getExecutiondate());
            report.setTestExecutions(getEntryListFromIDs(id -> testExecutionService.find(id), dto.getTestExecutions()));
            report.setDescription(dto.getDescription());
        }

        return report;
    }

    public ReportDTO convertToDTOSimple(Build entity) {
        ReportDTO reportDTO = convertToDTOSimple(entity.getReport());
        reportDTO.setBuildNumber(entity.getNumber());
        reportDTO.setExecutiondate(entity.getExecutedDate());
        return reportDTO;
    }
}
