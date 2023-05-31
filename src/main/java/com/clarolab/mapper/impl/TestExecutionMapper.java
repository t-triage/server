/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.TestExecutionDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.TestCase;
import com.clarolab.model.TestExecution;
import com.clarolab.model.TestPin;
import com.clarolab.model.types.StatusType;
import com.clarolab.service.ReportService;
import com.clarolab.service.TestExecutionService;
import com.clarolab.serviceDTO.UserServiceDTO;
import com.clarolab.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.getNullableByID;
import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class TestExecutionMapper implements Mapper<TestExecution, TestExecutionDTO> {

    @Autowired
    private TestExecutionService testExecutionService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserServiceDTO userServiceDTO;


    @Override
    public TestExecutionDTO convertToDTO(TestExecution testExecution) {
        TestExecutionDTO dto = new TestExecutionDTO();

        setEntryFields(testExecution, dto);

        dto.setDuration(testExecution.getDuration());
        dto.setAge(testExecution.getAge());
        dto.setErrorDetails(StringUtils.notNull(testExecution.getErrorDetails()));
        dto.setErrorStackTrace(StringUtils.notNull(testExecution.getErrorStackTrace()));
        dto.setStatus(testExecution.getStatus().name());
        dto.setFailedSince(testExecution.getFailedSince() == 0 ? 1 : testExecution.getFailedSince());
        dto.setStandardOutput(testExecution.getStandardOutput());
        dto.setSkippedMessage(testExecution.getSkippedMessage());
        dto.setReport(testExecution.getReport() == null ? null : testExecution.getReport().getId());

        dto.setName(testExecution.getName());
        dto.setSuiteName(testExecution.getSuiteName());
        dto.setPath(testExecution.getLocationPath());
        dto.setGroupName(getGroupName(testExecution));
        dto.setDisplayName(StringUtils.methodToWords(dto.getName()));
        dto.setShortName(StringUtils.classTail(dto.getName()));
        dto.setParameters(StringUtils.getParameters(dto.getName()));

        String[] screenshots = {};
        if (testExecution.getScreenshotURL() != null && !testExecution.getScreenshotURL().isEmpty()) {
            screenshots = new String[]{testExecution.getScreenshotURL()};
        }
        String[] videos = {};
        if (testExecution.getVideoURL() != null && !testExecution.getVideoURL().isEmpty()) {
            videos = new String[]{testExecution.getVideoURL()};
        }
        dto.setScreenshotURLs(screenshots);
        dto.setVideoURLs(videos);

        TestPin pin = testExecution.getPin();
        if (pin != null) {
            dto.setPin(true);
            dto.setPinAuthor(pin.getAuthor() == null ? null : userServiceDTO.convertToDTO(pin.getAuthor()));
            dto.setPinDate(pin.getCreateDate());
        } else {
            dto.setPin(false);
        }


        return dto;
    }

    public TestExecutionDTO convertToDTO(TestCase testCase) {
        TestExecutionDTO dto = new TestExecutionDTO();

        setEntryFields(testCase, dto);

        dto.setName(testCase.getName());
        dto.setPath(testCase.getLocationPath());
        dto.setGroupName(getGroupName(testCase));
        dto.setDisplayName(StringUtils.methodToWords(dto.getName()));
        dto.setShortName(StringUtils.classTail(dto.getName()));
        dto.setParameters(StringUtils.getParameters(dto.getName()));

        TestPin pin = testCase.getPin();
        if (pin != null) {
            dto.setPin(true);
            dto.setPinAuthor(pin.getAuthor() == null ? null : userServiceDTO.convertToDTO(pin.getAuthor()));
            dto.setPinDate(pin.getCreateDate());
        } else {
            dto.setPin(false);
        }


        return dto;

    }

    @Override
    public TestExecution convertToEntity(TestExecutionDTO dto) {
        TestExecution testExecution;
        if (dto.getId() == null || dto.getId() < 1) {
            testExecution = TestExecution.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .age(dto.getAge())
                    .errorDetails(dto.getErrorDetails())
                    .errorStackTrace(dto.getErrorStackTrace())
                    .status(StatusType.valueOf(dto.getStatus()))
                    .failedSince(dto.getFailedSince())
                    .build();
        } else {
            testExecution = testExecutionService.find(dto.getId());

//            testExecution.setId(dto.getId());
//            testExecution.setTimestamp(dto.getTimestamp());
//            testExecution.setUpdated(dto.getUpdated());
            testExecution.setEnabled(dto.getEnabled());

            testExecution.setDuration(dto.getDuration());
            testExecution.setAge(dto.getAge());
            testExecution.setErrorDetails(dto.getErrorDetails());
            testExecution.setErrorStackTrace(dto.getErrorStackTrace());
            testExecution.setStatus(StatusType.valueOf(dto.getStatus()));
            testExecution.setFailedSince(dto.getFailedSince());
            testExecution.setReport(getNullableByID(dto.getReport(), id -> reportService.find(id)));
        }
        return testExecution;
    }

    public String getGroupName(TestExecution testExecution) {
        String suiteName = StringUtils.classTail(testExecution.getSuiteName());
        String groupName = getGroupName(testExecution.getTestCase());

        if (StringUtils.isEmpty(groupName) || groupName.equalsIgnoreCase(suiteName)) {
            return suiteName;
        } else {
            if (StringUtils.isEmpty(suiteName)) {
                return groupName;
            } else {
                return String.format("%s: %s", suiteName, groupName);
            }
        }
    }

    public String getGroupName(TestCase testCase) {
        return StringUtils.classTail(testCase.getLocationPath());
    }
}
