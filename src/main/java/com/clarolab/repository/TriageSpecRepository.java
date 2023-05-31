/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.model.Container;
import com.clarolab.model.Executor;
import com.clarolab.model.Pipeline;
import com.clarolab.model.TriageSpec;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TriageSpecRepository extends BaseRepository<TriageSpec> {

    TriageSpec findTriageSpecByContainerAndExecutorAndPipeline(Container container, Executor executor, Pipeline pipeline);

    TriageSpec findTriageSpecByExecutor(Executor executor);

    List<TriageSpec> findAllByContainer(Container container);

    List<TriageSpec> findAllByPipelineOrderByTimestampDesc(Pipeline entity);

    @Query("SELECT DISTINCT t.container FROM TriageSpec t WHERE t.pipeline != null and t.enabled = true")
    List<Container> findContainersByPipelineNotNull();

    @Query("SELECT DISTINCT t.pipeline FROM TriageSpec t WHERE t.container = :container and t.enabled = true")
    List<Pipeline> findPipelinesByContainer(Container container);

}
