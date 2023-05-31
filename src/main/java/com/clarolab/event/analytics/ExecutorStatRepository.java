/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.event.analytics;

import com.clarolab.model.Container;
import com.clarolab.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExecutorStatRepository extends BaseRepository<ExecutorStat> {

    @Query("SELECT e FROM ExecutorStat e WHERE e.timestamp > ?1 AND e.timestamp < ?2 GROUP BY e.productName, e.id")
    List<ExecutorStat> findAllBetweenGroupByProduct(long prev, long now);


    List<ExecutorStat> findAllByExecutorContainerAndActualDateGreaterThanEqualAndActualDateLessThanEqualOrderByExecutorAscIdDesc(Container container,String from, String to);

}
