/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.ExecutorDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.Executor;
import com.clarolab.model.TriageSpec;
import com.clarolab.model.types.ReportType;
import com.clarolab.service.*;
import com.clarolab.serviceDTO.TriageSpecServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.*;

@Component
public class ExecutorMapper implements Mapper<Executor, ExecutorDTO> {

    @Autowired
    private ExecutorService executorService;
    @Autowired
    private ContainerService containerService;
    @Autowired
    private BuildService buildService;
    
    @Autowired
    private TriageSpecService triageSpecService;
    
    @Autowired
    private TriageSpecServiceDTO triageSpecServiceDTO;

    @Autowired
    private TrendGoalService trendGoalService;

    @Override
    public ExecutorDTO convertToDTO(Executor executor) {
        ExecutorDTO executorDTO = new ExecutorDTO();

        setEntryFields(executor, executorDTO);

        executorDTO.setContainer(executor.getContainer() == null ? null : executor.getContainer().getId());
        executorDTO.setName(executor.getName());
        executorDTO.setUrl(executor.getUrl());
        executorDTO.setTrendGoal(executor.getGoal() == null ? null : executor.getGoal().getId());
        executorDTO.setDescription(executor.getDescription());
        executorDTO.setLastBuilds(getIDList(executor.getLastBuilds()));
        executorDTO.setReportType(executor.getReportType() != null ? executor.getReportType().name() : null);

        TriageSpec spec = triageSpecService.geTriageFlowSpecByExecutor(executor);
        executorDTO.setTriageSpec(spec == null ? null : triageSpecServiceDTO.convertToDTO(spec));

        return executorDTO;
    }

    @Override
    public Executor convertToEntity(ExecutorDTO dto) {
        Executor executor;
        if (dto.getId() == null || dto.getId() < 1) {
            executor = Executor.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .container(getNullableByID(dto.getContainer(), id -> containerService.find(id)))
                    .name(dto.getName())
                    .url(dto.getUrl())
                    .reportType(ReportType.UNKNOWN)
                    .description(dto.getDescription())
                    .builds(getEntryListFromIDs(id -> buildService.find(id), dto.getLastBuilds()))
                    .build();
        } else {
            executor = executorService.find(dto.getId());
            executor.setEnabled(dto.getEnabled());
            executor.setContainer(getNullableByID(dto.getContainer(), id -> containerService.find(id)));
            executor.setName(dto.getName());
            executor.setUrl(dto.getUrl());
            executor.setDescription(dto.getDescription());
            executor.setReportType(dto.getReportType() == null ? null : ReportType.valueOf(dto.getReportType()));
        }
        triageSpecServiceDTO.convertToEntity(dto.getTriageSpec());
        
        return executor;
    }
}
