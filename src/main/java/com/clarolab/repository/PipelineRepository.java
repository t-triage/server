/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.model.Pipeline;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PipelineRepository extends BaseRepository<Pipeline> {

    List<Pipeline> findAllByNameIgnoreCaseLike(String name);

    List<Pipeline> findAllByEnabledIsTrue();
}
