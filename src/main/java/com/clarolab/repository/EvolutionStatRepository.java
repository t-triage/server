package com.clarolab.repository;

import com.clarolab.event.analytics.EvolutionStat;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvolutionStatRepository extends BaseRepository<EvolutionStat> {

    @Query("SELECT e FROM EvolutionStat e WHERE e.timestamp > ?2 AND e.timestamp <?3 AND e.executor.id = ?1 ORDER BY e.timestamp DESC")
    List<EvolutionStat> findAllByExecutorSince(long executorid, long from, long to);

}