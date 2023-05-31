/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.model.*;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.TriageSpecRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log
public class TriageSpecService extends BaseService<TriageSpec> {

    @Autowired
    private TriageSpecRepository triageSpecRepository;

    @Override
    public BaseRepository<TriageSpec> getRepository() {
        return triageSpecRepository;
    }


    public TriageSpec geTriageFlowSpecByExecutor(Executor executor){
        return triageSpecRepository.findTriageSpecByExecutor(executor);
    }

    public TriageSpec geTriageFlowSpecByContainer(Container container){
        return triageSpecRepository.findTriageSpecByContainerAndExecutorAndPipeline(container, null, null);
    }

    public TriageSpec getTriageSpec(Executor executor) {
        List<TriageSpec> specs = triageSpecRepository.findAllByContainer(executor.getContainer());
        TriageSpec defaultSpec = null;

        if (specs.size() == 1) {
            return specs.get(0);
        }

        for (TriageSpec spec : specs) {
            if (spec.getExecutor() != null && spec.getExecutorId().equals(executor.getId())) {
                return spec;
            }
            if (spec.getExecutor() == null) {
                defaultSpec = spec;
            }
        }

        return defaultSpec;
    }

    public TriageSpec getTriageSpec(Container container) {
        List<TriageSpec> specs = triageSpecRepository.findAllByContainer(container);

        if (specs.isEmpty()) {
            return null;
        } else {
            return specs.get(0);
        }
    }

    public TriageSpec buildNewSpec(TriageSpec specContainer, Executor executor) {
        TriageSpec spec = TriageSpec.builder()
                .container(specContainer.getContainer())
                .executor(executor)
                .triager(specContainer.getTriager())
                .everyWeeks(specContainer.getEveryWeeks())
                .frequencyCron(specContainer.getFrequencyCron())
                .priority(specContainer.getPriority())
                .expectedMinAmountOfTests(specContainer.getExpectedMinAmountOfTests())
                .expectedPassRate(specContainer.getExpectedPassRate())
                .build();
        return spec;
    }

    private TriageSpec checkUnique(TriageSpec entry) {
        List<TriageSpec> dbs = findAll();
        TriageSpec answer = triageSpecRepository.findTriageSpecByContainerAndExecutorAndPipeline(entry.getContainer(), entry.getExecutor(), null);
        if (answer != null) {
            long containerId = entry.getContainer().getId();
            long executorId = 0;
            if (entry.getExecutor() != null) {
                executorId = entry.getExecutorId();
            }

            System.err.println(String.format("Triage already exist. Container: %d Executor: %d", containerId, executorId));
            log.severe(String.format("Triage already exist. Container: %d Executor: %d", containerId, executorId));
        }
        return answer;
    }

    public User getTriager(BuildTriage triage) {
        TriageSpec spec = getTriageSpec(triage.getExecutor());
        if (spec == null) {
            return null;
        } else {
            return spec.getTriager();
        }
    }

    public TriageSpec getTriageSpec(Pipeline entity) {
        List<TriageSpec> specs = triageSpecRepository.findAllByPipelineOrderByTimestampDesc(entity);

        if (specs.size() > 0) {
            return specs.get(0);
        } else {
            return null;
        }
    }

    public List<Container> containersWithPipeline() {
        List<Container> containers = triageSpecRepository.findContainersByPipelineNotNull();
        return containers;
    }

    public List<Pipeline> findPipelinesByContainer(Container container){
        List<Pipeline> pipelines = container != null ?
                triageSpecRepository.findPipelinesByContainer(container):new ArrayList<>();
        List<Pipeline> result = new ArrayList<>();
        for (Pipeline p: pipelines) {
            if (p.isEnabled()) {
                result.add(p);
            }
        }
        return result;
    }
}
