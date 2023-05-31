/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.TriageSpecDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.TriageSpec;
import com.clarolab.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.getNullableByID;
import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class TriageSpecMapper implements Mapper<TriageSpec, TriageSpecDTO> {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private TriageSpecService triageSpecService;
    @Autowired
    private ExecutorService executorService;
    @Autowired
    private ContainerService containerService;
    @Autowired
    private PipelineService pipelineService;

    @Override
    public TriageSpecDTO convertToDTO(TriageSpec triageSpec) {
        TriageSpecDTO triageSpecDTO = new TriageSpecDTO();

        setEntryFields(triageSpec, triageSpecDTO);

        triageSpecDTO.setTriager(triageSpec.getTriager() == null ? null : userMapper.convertToDTO(triageSpec.getTriager()));
        triageSpecDTO.setPriority(triageSpec.getPriority());
        triageSpecDTO.setExpectedPassRate(triageSpec.getExpectedPassRate());
        triageSpecDTO.setExpectedMinAmountOfTests(triageSpec.getExpectedMinAmountOfTests());

        triageSpecDTO.setExecutor(triageSpec.getExecutor() == null ? null : triageSpec.getExecutor().getId());
        triageSpecDTO.setContainer(triageSpec.getContainer() == null ? null : triageSpec.getContainer().getId());
        triageSpecDTO.setPipeline(triageSpec.getPipeline() == null ? null : triageSpec.getPipeline().getId());

        triageSpecDTO.setFrequencyCron(triageSpec.getFrequencyCron());
        triageSpecDTO.setEveryWeeks(triageSpec.getEveryWeeks());

        return triageSpecDTO;
    }

    @Override
    public TriageSpec convertToEntity(TriageSpecDTO dto) {
        TriageSpec triageSpec;
        if (dto.getId() == null || dto.getId() < 1) {
            triageSpec = TriageSpec.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .triager(userMapper.convertToEntity(dto.getTriager()))
                    .priority(dto.getPriority())
                    .expectedPassRate(dto.getExpectedPassRate())
                    .expectedMinAmountOfTests(dto.getExpectedMinAmountOfTests())
                    .executor(getNullableByID(dto.getExecutor(), id -> executorService.find(id)))
                    .container(getNullableByID(dto.getContainer(), id -> containerService.find(id)))
                    .pipeline(getNullableByID(dto.getPipeline(), id -> pipelineService.find(id)))
                    .frequencyCron(dto.getFrequencyCron())
                    .everyWeeks(dto.getEveryWeeks())
                    .build();

        } else {
            triageSpec = triageSpecService.find(dto.getId());
//            triageSpec.setId(dto.getId());
//            triageSpec.setUpdated(dto.getUpdated());
//            triageSpec.setTimestamp(dto.getTimestamp());
            triageSpec.setEnabled(dto.getEnabled());
            triageSpec.setTriager(userService.find(dto.getTriager().getId()));
            triageSpec.setPriority(dto.getPriority());
            triageSpec.setExpectedPassRate(dto.getExpectedPassRate());
            triageSpec.setExpectedMinAmountOfTests(dto.getExpectedMinAmountOfTests());
            triageSpec.setExecutor(getNullableByID(dto.getExecutor(), id -> executorService.find(id)));
            triageSpec.setContainer(getNullableByID(dto.getContainer(), id -> containerService.find(id)));
            triageSpec.setPipeline(getNullableByID(dto.getPipeline(), id -> pipelineService.find(id)));
            triageSpec.setFrequencyCron(dto.getFrequencyCron());
            triageSpec.setEveryWeeks(dto.getEveryWeeks());
        }

        return triageSpec;
    }

}
