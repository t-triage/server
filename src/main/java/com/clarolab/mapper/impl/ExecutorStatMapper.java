/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.ExecutorStatDTO;
import com.clarolab.event.analytics.ExecutorStat;
import com.clarolab.event.analytics.ExecutorStatService;
import com.clarolab.mapper.Mapper;
import com.clarolab.service.BuildTriageService;
import com.clarolab.service.ExecutorService;
import com.clarolab.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.getNullableByID;
import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class ExecutorStatMapper implements Mapper<ExecutorStat, ExecutorStatDTO> {

    @Autowired
    private ExecutorStatService executorStatService;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private BuildTriageService buildTriageService;

    @Autowired
    private ProductService productService;

    @Override
    public ExecutorStatDTO convertToDTO(ExecutorStat executorStat) {
        /* El executorStat en esta capa NO deberia ser null. Si llega null es porque hay algo mal
      /*  if (executorStat == null) {
            return null;
        }*/
        ExecutorStatDTO executorStatDTO = new ExecutorStatDTO();

        setEntryFields(executorStat, executorStatDTO);

        executorStatDTO.setExecutorId(executorStat.getExecutor() == null ? null : executorStat.getExecutor().getId());
        executorStatDTO.setLastBuildTriageId(executorStat.getLastBuildTriage() == null ? null : executorStat.getLastBuildTriage().getId());
        executorStatDTO.setActualDate(executorStat.getActualDate());
        executorStatDTO.setState(executorStat.getState());
        executorStatDTO.setTags(executorStat.getTags());
        executorStatDTO.setPass(executorStat.getPass());
        executorStatDTO.setSkip(executorStat.getSkip());
        executorStatDTO.setNewFails(executorStat.getNewFails());
        executorStatDTO.setFails(executorStat.getFails());
        executorStatDTO.setNowPassing(executorStat.getNowPassing());
        executorStatDTO.setToTriage(executorStat.getToTriage());
        executorStatDTO.setDuration(executorStat.getDuration());
        executorStatDTO.setStabilityIndex(executorStat.getStabilityIndex());
        executorStatDTO.setExecutionDate(executorStat.getExecutionDate());
        executorStatDTO.setAssignee(executorStat.getAssignee());
        executorStatDTO.setPriority(executorStat.getPriority());
        executorStatDTO.setProductName(executorStat.getProductName());
        executorStatDTO.setSuiteName(executorStat.getSuiteName());
        executorStatDTO.setContainerName(executorStat.getContainerName());
        executorStatDTO.setDefaultPriority(executorStat.getDefaultPriority());
        executorStatDTO.setDeadline(executorStat.getDeadline());
        executorStatDTO.setDaysToDeadline(executorStat.getDaysToDeadline());
        executorStatDTO.setDeadlinePriority(executorStat.getDeadlinePriority());


        executorStatDTO.setEvolutionPass(executorStat.getEvolutionPass());
        executorStatDTO.setEvolutionSkip(executorStat.getEvolutionSkip());
        executorStatDTO.setEvolutionNewFails(executorStat.getEvolutionNewFails());
        executorStatDTO.setEvolutionFails(executorStat.getEvolutionFails());
        executorStatDTO.setEvolutionNowPassing(executorStat.getEvolutionNowPassing());
        executorStatDTO.setEvolutionToTriage(executorStat.getEvolutionToTriage());

        executorStatDTO.setProductId(executorStat.getProduct() == null ? null : executorStat.getProduct().getId());

        return executorStatDTO;
    }

    @Override
    public ExecutorStat convertToEntity(ExecutorStatDTO dto) {
        ExecutorStat executorStat;
        if (dto.getId() == null || dto.getId() < 1) {
            executorStat = ExecutorStat.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .executor(getNullableByID(dto.getExecutorId(), id -> executorService.find(id)))
                    .lastBuildTriage(getNullableByID(dto.getLastBuildTriageId(), id -> buildTriageService.find(id)))
                    .actualDate(dto.getActualDate())
                    .state(dto.getState())
                    .tags(dto.getTags())
                    .pass(dto.getPass())
                    .skip(dto.getSkip())
                    .newFails(dto.getNewFails())
                    .fails(dto.getFails())
                    .nowPassing(dto.getNowPassing())
                    .toTriage(dto.getToTriage())
                    .duration(dto.getDuration())
                    .stabilityIndex(dto.getStabilityIndex())
                    .executionDate(dto.getExecutionDate())
                    .assignee(dto.getAssignee())
                    .priority(dto.getPriority())
                    .productName(dto.getProductName())
                    .suiteName(dto.getSuiteName())
                    .containerName(dto.getContainerName())
                    .defaultPriority(dto.getDefaultPriority())
                    .deadline(dto.getDeadline())
                    .daysToDeadline(dto.getDaysToDeadline())
                    .deadlinePriority(dto.getDeadlinePriority())
                    .evolutionPass(dto.getEvolutionPass())
                    .evolutionSkip(dto.getEvolutionSkip())
                    .evolutionNewFails(dto.getEvolutionNewFails())
                    .evolutionFails(dto.getEvolutionFails())
                    .evolutionNowPassing(dto.getEvolutionNowPassing())
                    .evolutionToTriage(dto.getEvolutionToTriage())
                    .product(getNullableByID(dto.getProductId(), id -> productService.find(id)))
                    .build();

        } else {
            executorStat = executorStatService.find(dto.getId());
           //this is inmutable
        }
        return executorStat;
    }

}
