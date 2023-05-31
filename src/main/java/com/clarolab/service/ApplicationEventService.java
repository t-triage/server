/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.event.process.ApplicationEventType;
import com.clarolab.model.ApplicationEvent;
import com.clarolab.model.Entry;
import com.clarolab.repository.ApplicationEventRepository;
import com.clarolab.repository.BaseRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;

import static com.clarolab.util.Constants.*;

@Service
@Log
public class ApplicationEventService extends BaseService<ApplicationEvent> {

    @Autowired
    private ApplicationEventRepository applicationEventRepository;

    @Autowired
    private PropertyService propertyService;

    @Override
    public BaseRepository<ApplicationEvent> getRepository() {
        return applicationEventRepository;
    }

    public ApplicationEvent getNextPendingEvent() {
        return applicationEventRepository.findFirstByProcessedOrderByEventTimeAsc(false);
    }

    public int getCountPendingEvents() {
        return applicationEventRepository.countByProcessed(false);
    }

    private int getMaxEventsToProcess() {
        return propertyService.valueOf(MAX_EVENTS_TO_PROCESS, DEFAULT_MAX_EVENTS_TO_PROCESS);
    }

    public ApplicationEvent getLastEvent(ApplicationEventType type) {
        return applicationEventRepository.findFirstByTypeOrderByEventTimeDesc(type);
    }

    public ApplicationEvent getLastPendingEvent(ApplicationEventType type) {
        return applicationEventRepository.findFirstByTypeAndProcessedOrderByEventTimeDesc(type, false);
    }

    public ApplicationEvent getLastEvent(Entry source) {
        return applicationEventRepository.findFirstBySourceOrderByEventTimeDesc(source);
    }

    public int deleteOldEvents(long timestamp) {
        int deletedCount = applicationEventRepository.deleteApplicationEventByTime(timestamp);
        log.log(Level.INFO, String.format("ApplicationEvent: Finish deleting %d tests", deletedCount));
        return deletedCount;
    }

    public int countWithTypeAndExtraParam(ApplicationEventType type, String extraParam) {
        return applicationEventRepository.countByProcessedAndTypeAndExtraParameter(false, type, extraParam);
    }

    public List<ApplicationEvent> getEventsSince(ApplicationEventType type, long since) {
        return applicationEventRepository.getAllByTypeAndEventTimeGreaterThan(type, since);
    }

    public List<ApplicationEvent> findLatestsEvents() {
        return applicationEventRepository.findTop20ByEnabledOrderByIdDesc(true);
    }
}
