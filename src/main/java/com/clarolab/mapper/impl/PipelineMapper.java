/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.PipelineDTO;
import com.clarolab.dto.TestTriageDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.*;
import com.clarolab.model.types.StateType;
import com.clarolab.service.*;
import com.clarolab.serviceDTO.ExecutorServiceDTO;
import com.clarolab.view.PipelineView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.clarolab.mapper.MapperHelper.setEntryFields;
import static com.clarolab.model.helper.tag.TagHelper.AUTO_TRIAGED;

@Component
public class PipelineMapper implements Mapper<Pipeline, PipelineDTO> {

    @Autowired
    private PipelineService pipelineService;

    @Autowired
    private TriageSpecService triageSpecService;

    @Autowired
    private TriageDeadlineService triageDeadlineService;

    @Autowired
    private ExecutorServiceDTO executorServiceDTO;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TriageSpecMapper triageSpecMapper;

    @Autowired
    private UserService userService;

    @Override
    public PipelineDTO convertToDTO(Pipeline entity) {
        PipelineDTO dto = new PipelineDTO();

        setEntryFields(entity, dto);

        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());

        TriageSpec spec = triageSpecService.getTriageSpec(entity);

        if (spec != null) {
            dto.setAssignee(spec.getTriager() == null ? null : userMapper.convertToDTO(spec.getTriager()));
            dto.setTriageSpec(triageSpecMapper.convertToDTO(spec));
        }

        dto.setToTriage(4l);

        dto.setStatus("N/A");

        List<TestTriage> triages = pipelineService.ongoingTestTriages(entity);
        PipelineView pipelineView = convertToView(entity, triages);

        dto.setAutoTriaged(pipelineView.getBarAutoTriaged());
        dto.setPassCount(pipelineView.getBarNowPassing());
        dto.setManualTriaged(pipelineView.getManualTriaged());
        dto.setExecutedDate(pipelineView.getExecutiondate());
        dto.setDaysToDeadline(pipelineView.getDaysToDeadline());
        dto.setDeadline(pipelineView.getDeadline());
        dto.setDeadlineTooltip(pipelineView.getDeadlineTooltip());
        dto.setTotalFails(pipelineView.getTotalFails());
        dto.setTotalNewFails(pipelineView.getTotalNewFails());
        dto.setTotalNewPass(pipelineView.getTotalNewPass());
        dto.setTotalNowPassing(pipelineView.getTotalNowPassing());
        dto.setTotalTests(pipelineView.getTotalTests());
        dto.setTotalTriageDone(pipelineView.getTotalTriageDone());
        dto.setTotalTestsToTriage(pipelineView.getTotalTestsToTriage());
        dto.setTotalNotExecuted(pipelineView.getTotalNotExecuted());
        dto.setToTriage(pipelineView.getToTriage());

        dto.setBarAutoTriaged(pipelineView.getBarAutoTriaged());
        dto.setBarFails(pipelineView.getBarFails());
        dto.setBarManualTriaged(pipelineView.getBarManualTriaged());
        dto.setBarNewFails(pipelineView.getBarNewFails());
        dto.setBarNotExecuted(pipelineView.getBarNotExecuted());

        return dto;
    }

    public PipelineView convertToView(Pipeline pipeline, List<TestTriage> triages) {
        final long total = triages.size();
        final long pass = testTriageService.countByStateAndTriaged(triages, StateType.PASS, true);
        final long totalSkip = testTriageService.countByStateAndTriaged(triages, StateType.SKIP, false);
        final long newPass = testTriageService.countByStateAndTriaged(triages, StateType.NEWPASS, true);

        final long autoTriaged = testTriageService.countByStateNotAndTag(triages, StateType.PASS, AUTO_TRIAGED);

        Map<String, List<TestTriageDTO>> triageMap = executorServiceDTO.getCategorizedFilteredAndSortedTestTriagesForPipelines(triages);

        final long newFails = triageMap.containsKey("NEWFAIL") ? triageMap.get("NEWFAIL").size() : 0;
        final long fails = triageMap.containsKey("FAIL") ? triageMap.get("FAIL").size() : 0;
        final long triageDone = triageMap.containsKey("TRIAGEDONE") ? triageMap.get("TRIAGEDONE").size() : 0;
        final long notExecuted = triageMap.containsKey("NOTEXECUTED") ? triageMap.get("NOTEXECUTED").size() : 0;

        final long toTriage = newFails + fails;

        User triager = null;
        TriageSpec spec = triageSpecService.getTriageSpec(pipeline);
        if (spec != null) {
            triager = spec.getTriager();
        }


        TriageDeadline triageDeadline = triageDeadlineService.computeTriageDeadline(pipeline);

        return PipelineView.builder()
                .allTestTriages(triageMap)
                .pipeline(pipeline)
                .triagger(triager == null ? null : userMapper.convertToDTO(triager))
                .triageSpec(spec)
                .deadline(triageDeadline.getDeadline())
                /*All totals now are pre calculated */
                .toTriage(toTriage)
                .autoTriaged(autoTriaged)
                .totalNewFails(newFails)
                .totalFails(fails)
                .totalNewPass(newPass)
                .totalTriageDone(triageDone)
                .totalNotExecuted(notExecuted)
                .totalPass(newPass + pass)
                .totalSkip(totalSkip)
                .totalTests(total)

                .build();
    }

    @Override
    public Pipeline convertToEntity(PipelineDTO dto) {
        if (dto == null) {
            return null;
        }

        Pipeline entity;
        if (dto.getId() == null || dto.getId() < 1) {
            entity = Pipeline.builder()
                    .id(null)
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .build();
        } else {
            entity = pipelineService.find(dto.getId());
            entity.setEnabled(dto.getEnabled());
            entity.setName(dto.getName());
            entity.setDescription(dto.getDescription());
        }

        if (dto.getAssignee() != null) {
            TriageSpec spec = pipelineService.createOrGetSpec(entity);
            spec.setTriager(userService.find(dto.getAssignee().getId()));
        }

        return entity;
    }
}
