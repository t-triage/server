/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.serviceDTO;

import com.clarolab.dto.ContainerDTO;
import com.clarolab.dto.PipelineDTO;
import com.clarolab.dto.TestTriageDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.mapper.impl.PipelineMapper;
import com.clarolab.model.Container;
import com.clarolab.model.Pipeline;
import com.clarolab.model.TestCase;
import com.clarolab.model.TestTriage;
import com.clarolab.service.PipelineService;
import com.clarolab.service.PipelineTestService;
import com.clarolab.service.TTriageService;
import com.clarolab.service.TestCaseService;
import com.clarolab.view.PipelineView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PipelineServiceDTO implements BaseServiceDTO<Pipeline, PipelineDTO, PipelineMapper> {

    @Autowired
    private PipelineService service;

    @Autowired
    private PipelineMapper mapper;

    @Autowired
    private PipelineTestService pipelineTestService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestTriageServiceDTO testTriageServiceDTO;

    @Autowired
    private ContainerServiceDTO containerServiceDTO;

    @Autowired
    private PipelineServiceDTO pipelineServiceDTO;

    @Override
    public TTriageService<Pipeline> getService() {
        return service;
    }

    @Override
    public Mapper<Pipeline, PipelineDTO> getMapper() {
        return mapper;
    }

    @Override
    public BaseServiceDTO<Pipeline, PipelineDTO, PipelineMapper> getServiceDTO() {
        return this;
    }

    public PipelineDTO assignToPipeline(Long pipelineId, List<Long> testCaseIds) {
        Pipeline pipeline = findEntity(pipelineId);
        if (pipeline == null) {
            return null;
        }
        service.assignToPipeline(pipeline, testCaseIds);
        return convertToDTO(pipeline);
    }

    public List<PipelineDTO> search(String name) {
        List<Pipeline> answer = service.search(name);
        return convertToDTO(answer);
    }

    public List<TestTriageDTO> ongoingTestTriages(Long pipelineId) {
        Pipeline pipeline = findEntity(pipelineId);
        if (pipeline == null) {
            return new ArrayList<>();
        }
        List<TestTriage> answer = service.ongoingTestTriages(pipeline);
        return testTriageServiceDTO.convertToDTO(answer);
    }

    public PipelineView detail(Long pipelineId, boolean includeTests) {
        Pipeline pipeline = findEntity(pipelineId);
        if (pipeline == null) {
            return null;
        }
        List<TestTriage> triages = service.ongoingTestTriages(pipeline);
        PipelineView view = mapper.convertToView(pipeline, triages);

        if (!includeTests) {
            view.setAllTestTriages(null);
        }
        return view;
    }

    public Long deleteByPipelineAndCase(Long pipelineId, Long testCaseId) {
        Pipeline pipeline = findEntity(pipelineId);
        TestCase testCase = testCaseService.find(testCaseId);
        if (pipeline == null || testCase == null) {
            return 0L;
        }
        return pipelineTestService.delete(pipeline, testCase);
    }

    public List<ContainerDTO> containers() {
        List<Container> containers = service.containers();
        return containerServiceDTO.convertToDTO(containers);
    }

    public List<PipelineDTO> getPipelinesEnabled() {
        List<Pipeline> pipelines = service.getPipelinesEnabled();
        return pipelineServiceDTO.convertToDTO(pipelines);
    }

    public List<PipelineDTO> findPipelinesByContainer(Long containerId){
        List<Pipeline> pipelines = containerId != null ? service.findPipelinesByContainer(containerId)
                : new ArrayList<>();
        return pipelineServiceDTO.convertToDTO(pipelines);
    }

}
