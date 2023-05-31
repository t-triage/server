/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.model.Pipeline;
import com.clarolab.model.PipelineTest;
import com.clarolab.model.TestCase;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.PipelineTestRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log
public class PipelineTestService extends BaseService<PipelineTest> {

    @Autowired
    private PipelineTestRepository pipelineTestRepository;

    @Override
    public BaseRepository<PipelineTest> getRepository() {
        return pipelineTestRepository;
    }


    public PipelineTest find(Pipeline pipeline, TestCase testCase) {
        return pipelineTestRepository.findFirstByPipelineAndTest(pipeline, testCase);
    }

    public Long delete(Pipeline pipeline, TestCase testCase) {
        Long deletedId = 0L;
        PipelineTest pipelineTest = find(pipeline, testCase);
        if (pipelineTest != null) {
            deletedId = pipelineTest.getId();
            delete(deletedId);
        }
        return deletedId;
    }

    public List<PipelineTest> findAll(Pipeline pipeline) {
        return pipelineTestRepository.findAllByPipeline(pipeline);
    }

}