/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.model.Pipeline;
import com.clarolab.model.PipelineTest;
import com.clarolab.model.TestCase;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PipelineTestRepository extends BaseRepository<PipelineTest> {

    PipelineTest findFirstByPipelineAndTest(Pipeline pipeline, TestCase test);

    List<PipelineTest> findAllByPipeline(Pipeline pipeline);
}
