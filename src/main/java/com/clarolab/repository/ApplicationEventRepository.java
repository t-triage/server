/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.event.process.ApplicationEventType;
import com.clarolab.model.ApplicationEvent;
import com.clarolab.model.Entry;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ApplicationEventRepository extends BaseRepository<ApplicationEvent> {

    List<ApplicationEvent> findTop30ByProcessedOrderByEventTimeAsc(boolean processed);

    ApplicationEvent findFirstByProcessedOrderByEventTimeAsc(boolean processed);

    int countByProcessed(boolean processed);

    List<ApplicationEvent> findAllByProcessed(boolean processed);

    ApplicationEvent findFirstByTypeOrderByEventTimeDesc(ApplicationEventType type);

    ApplicationEvent findFirstByTypeAndProcessedOrderByEventTimeDesc(ApplicationEventType type, boolean processed);

    List<ApplicationEvent> getAllByTypeAndEventTimeGreaterThan(ApplicationEventType type, long timestamp);

    ApplicationEvent findFirstBySourceOrderByEventTimeDesc(Entry source);

    Long deleteByEventTimeLessThan(long timestamp);

    int countByProcessedAndTypeAndExtraParameter(boolean processed, ApplicationEventType type, String extraParameter);

    List<ApplicationEvent> findTop20ByEnabledOrderByIdDesc(boolean enabled);
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "CALL DELETE_APPLICATION_EVENT_BY_TIMESTAMP(:timestamp);", nativeQuery = true)
    int deleteApplicationEventByTime(@Param("timestamp") long timestamp);

}
