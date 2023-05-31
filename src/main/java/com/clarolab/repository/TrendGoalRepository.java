/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.model.Executor;
import com.clarolab.model.TrendGoal;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrendGoalRepository extends BaseRepository<TrendGoal> {

    List<Executor> findAllByEnabled(boolean enabled);


}
