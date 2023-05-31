/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper.impl;

import com.clarolab.dto.BuildTriageDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.MapperHelper;
import com.clarolab.model.BuildTriage;
import com.clarolab.model.types.StateType;
import com.clarolab.service.BuildService;
import com.clarolab.service.BuildTriageService;
import com.clarolab.service.TriageSpecService;
import com.clarolab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class BuildTriageMapper implements Mapper<BuildTriage, BuildTriageDTO> {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private TriageSpecMapper triageSpecMapper;

    @Autowired
    private TriageSpecService triageSpecService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private BuildTriageService buildTriageService;

    @Override
    public BuildTriageDTO convertToDTO(BuildTriage buildTriage) {
        BuildTriageDTO buildTriageDTO = new BuildTriageDTO();

        setEntryFields(buildTriage, buildTriageDTO);

        buildTriageDTO.setCurrentState(MapperHelper.getEnumName(buildTriage.getCurrentState()));
        buildTriageDTO.setTags(buildTriage.getTags());
        buildTriageDTO.setFile(buildTriage.getFile());
        buildTriageDTO.setRank(buildTriage.getRank());
        buildTriageDTO.setStandardOutputUrl(buildTriage.getBuild().getStandardOutputUrl());
        buildTriageDTO.setTriaged(buildTriage.isTriaged());
        buildTriageDTO.setTriager(buildTriage.getTriager() == null ? null : userMapper.convertToDTO(buildTriage.getTriager()));
        buildTriageDTO.setNote(buildTriage.getNote() == null ? null : noteMapper.convertToDTO(buildTriage.getNote()));

        buildTriageDTO.setBuild(buildTriage.getBuild() == null ? null : buildTriage.getBuild().getId());
        buildTriageDTO.setContainer(buildTriage.getContainer() == null ? null : buildTriage.getContainer().getId());
        buildTriageDTO.setExecutor(buildTriage.getExecutor() == null ? null : buildTriage.getExecutor().getId());
        buildTriageDTO.setTriageSpec(buildTriage.getSpec() == null ? null : triageSpecMapper.convertToDTO(buildTriage.getSpec()));

        buildTriageDTO.setDeadline(buildTriage.getDeadline());

        return buildTriageDTO;
    }


    @Override
    public BuildTriage convertToEntity(BuildTriageDTO dto) {
        BuildTriage buildTriage;
        if (dto.getId() == null || dto.getId() < 1) {
            buildTriage = BuildTriage.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .currentState(StateType.valueOf(dto.getCurrentState()))
                    .tags(dto.getTags())
                    .file(dto.getFile())
                    .rank(dto.getRank())
                    .triaged(dto.isTriaged())
                    .triager(userMapper.convertToEntity(dto.getTriager()))
                    .note(noteMapper.convertToEntity(dto.getNote()))
                    .lastBuild(MapperHelper.getNullableByID(dto.getBuild(), id -> buildService.find(id)))
                    .triageSpec(triageSpecMapper.convertToEntity(dto.getTriageSpec()))
                    .build();
        } else {
            buildTriage = buildTriageService.find(dto.getId());

//            buildTriage.setId(dto.getId());
//            buildTriage.setUpdated(dto.getUpdated());
//            buildTriage.setTimestamp(dto.getTimestamp());
            buildTriage.setEnabled(dto.getEnabled());
            buildTriage.setCurrentState(StateType.valueOf(dto.getCurrentState()));
            buildTriage.setTags(dto.getTags());
            buildTriage.setFile(dto.getFile());
            buildTriage.setRank(dto.getRank());
            buildTriage.setTriaged(dto.isTriaged());
            buildTriage.setTriager(userService.find(dto.getTriager().getId()));
            buildTriage.setNote(noteMapper.convertToEntity(dto.getNote()));
            buildTriage.setBuild(MapperHelper.getNullableByID(dto.getBuild(), id -> buildService.find(id)));
            buildTriage.setSpec(triageSpecService.find(dto.getTriageSpec().getId()));
            buildTriage.initialize();

        }

        return buildTriage;
    }

}
