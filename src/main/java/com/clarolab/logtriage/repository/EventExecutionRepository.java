package com.clarolab.logtriage.repository;

import com.clarolab.logtriage.model.EventExecution;
import com.clarolab.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventExecutionRepository extends BaseRepository<EventExecution> {

    @Query("SELECT COUNT(e) > 0 FROM EventExecution e WHERE e.content = ?1")
    Boolean exists(String content);

    @Query("SELECT e FROM EventExecution e WHERE error_id = ?1 ORDER BY e.date DESC")
    List<EventExecution> findAllByErrorCase(Long errorCase);

}
