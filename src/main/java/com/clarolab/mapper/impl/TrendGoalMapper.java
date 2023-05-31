/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.TrendGoalDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.TrendGoal;
import com.clarolab.service.ExecutorService;
import com.clarolab.service.TrendGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class TrendGoalMapper implements Mapper<TrendGoal, TrendGoalDTO> {

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private TrendGoalService trendGoalService;


    @Override
    public TrendGoalDTO convertToDTO(TrendGoal entry) {
        TrendGoalDTO dto = new TrendGoalDTO();

        setEntryFields(entry, dto);

        dto.setExpectedGrowth(entry.getExpectedGrowth());
        dto.setRequiredGrowth(entry.getRequiredGrowth());
        dto.setExpectedPassing(entry.getExpectedPassing());
        dto.setRequiredPassing(entry.getRequiredPassing());
        dto.setExpectedStability(entry.getExpectedStability());
        dto.setRequiredStability(entry.getRequiredStability());
        dto.setExpectedTriageDone(entry.getExpectedTriageDone());
        dto.setRequiredTriageDone(entry.getRequiredTriageDone());
        dto.setExpectedCommits(entry.getExpectedCommits());
        dto.setRequiredCommits(entry.getRequiredCommits());


        return dto;
    }

    @Override
    public TrendGoal convertToEntity(TrendGoalDTO dto) {
        TrendGoal entity;
        if (dto.getId() == null || dto.getId() < 1) {
            entity = TrendGoal.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .expectedGrowth(dto.getExpectedGrowth())
                    .requiredGrowth(dto.getRequiredGrowth())
                    .expectedPassing(dto.getExpectedPassing())
                    .requiredPassing(dto.getRequiredPassing())
                    .expectedStability(dto.getExpectedStability())
                    .requiredStability(dto.getRequiredStability())
                    .expectedTriageDone(dto.getExpectedTriageDone())
                    .requiredTriageDone(dto.getRequiredTriageDone())
                    .expectedCommits(dto.getExpectedCommits())
                    .requiredCommits(dto.getRequiredCommits())
                    .build();
        } else {
            entity = trendGoalService.find(dto.getId());
            entity.setEnabled(dto.getEnabled());
            entity.setExpectedGrowth(dto.getExpectedGrowth());
            entity.setRequiredGrowth(dto.getRequiredGrowth());
            entity.setExpectedPassing(dto.getExpectedPassing());
            entity.setRequiredPassing(dto.getRequiredPassing());
            entity.setExpectedStability(dto.getExpectedStability());
            entity.setRequiredStability(dto.getRequiredStability());
            entity.setExpectedTriageDone(dto.getExpectedTriageDone());
            entity.setRequiredTriageDone(dto.getRequiredTriageDone());
            entity.setExpectedCommits(dto.getExpectedCommits());
            entity.setRequiredCommits(dto.getRequiredCommits());
        }
        
        return entity;
    }
}
