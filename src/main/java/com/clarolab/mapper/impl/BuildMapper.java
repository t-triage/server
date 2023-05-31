/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.BuildDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.Build;
import com.clarolab.model.types.PopulateMode;
import com.clarolab.model.types.StatusType;
import com.clarolab.service.BuildService;
import com.clarolab.service.ExecutorService;
import com.clarolab.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.*;

@Component
public class BuildMapper implements Mapper<Build, BuildDTO> {

    @Autowired
    private BuildService buildService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private ExecutorService executorService;

    @Override
    public BuildDTO convertToDTO(Build build) {
        if(build == null) return null;

        BuildDTO buildDTO = new BuildDTO();
        setEntryFields(build, buildDTO);

        buildDTO.setNumber(build.getNumber());
        buildDTO.setExecutedDate(build.getExecutedDate());
        buildDTO.setBuildId(build.getBuildId());
        buildDTO.setDisplayName(build.getDisplayName());
        buildDTO.setPopulateMode(build.getPopulateMode().name());
        buildDTO.setStatus(getEnumName(build.getStatus()));

        buildDTO.setReportId(build.getReport() == null ? null : build.getReport().getId());
        buildDTO.setExecutorId(build.getExecutor() == null ? null : build.getExecutor().getId());
        return buildDTO;
    }

    @Override
    public Build convertToEntity(BuildDTO dto) {
        Build build;
        if (dto.getId() == null || dto.getId() < 1) {
            //So if ID is not there or is invalid we assume that is a new entity.
            build = Build.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .number(dto.getNumber())
                    .executedDate(dto.getExecutedDate())
                    .buildId(dto.getBuildId())
                    .displayName(dto.getDisplayName())
                    .status(StatusType.valueOf(dto.getStatus()))
                    .populateMode(PopulateMode.UNDEFINED)
                    .report(getNullableByID(dto.getId(), id -> reportService.find(id)))
                    .executor(getNullableByID(dto.getId(), id -> executorService.find(id)))
                    .build();
        } else {
            build = buildService.find(dto.getId());
//            build.setId(dto.getId()); Don't allow to update this.
            build.setEnabled(dto.getEnabled());
//            build.setTimestamp(dto.getTimestamp()); Don't allow to update this.
//            build.setUpdated(dto.getUpdated()); Don't allow to update this.
            build.setNumber(dto.getNumber());
            build.setExecutedDate(dto.getExecutedDate());
            build.setBuildId(dto.getBuildId());
            build.setDisplayName(dto.getDisplayName());
            build.setStatus(StatusType.valueOf(dto.getStatus()));
            build.setReport(getNullableByID(dto.getId(), id -> reportService.find(id)));
            build.setExecutor(getNullableByID(dto.getId(), id -> executorService.find(id)));
        }
        return build;
    }

}
