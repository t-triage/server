/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.model.Build;
import com.clarolab.model.Executor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildRepository extends BaseRepository<Build> {

    List<Build> findAllByExecutorAndProcessedAndEnabledOrderByNumberAscExecutor(Executor executor, boolean processed, boolean enabled);

    List<Build> findAllByProcessedAndEnabledOrderByNumberAscExecutor(boolean processed, boolean enabled);

    List<Build> findAllByIdInOrderByNumber(long[] buildIds);

    List<Build> findAllByExecutorOrderByNumberDesc(Executor executor);

    Build getTopByExecutorOrderByNumberDesc(Executor executor);

}
