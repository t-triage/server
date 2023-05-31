/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.agents;

import com.clarolab.event.process.ApplicationEventBuilder;
import com.clarolab.event.process.ApplicationEventType;
import com.clarolab.model.ApplicationEvent;
import com.clarolab.service.ApplicationEventService;
import com.clarolab.service.PropertyService;
import com.clarolab.util.DateUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Calendar;

import static com.clarolab.util.Constants.DEFAULT_AGENT_DELAY;
import static com.clarolab.util.Constants.DEFAULT_AGENT_TIME_TIMEOUT;

@Log
@Component
@Transactional(propagation = Propagation.REQUIRED)
public class EventTimeAgent implements SmartAgent {

    @Autowired
    private ApplicationEventBuilder applicationEventBuilder;

    @Autowired
    private ApplicationEventService applicationEventService;

    @Autowired
    private PropertyService propertyService;

    @Override
    @Scheduled(fixedRate = DEFAULT_AGENT_TIME_TIMEOUT, initialDelay = DEFAULT_AGENT_DELAY)
    public void execute() {
        if (!propertyService.valueOf("EVENT_SERVICE_ENABLED", true))
            return;

        log.info("Executing Event Agent");

        createDailyEvent();
        createWeeklyEvent();
        createTuesdayEvent();
        createMonthlyEvent();
    }

    private boolean createTimeEvent(ApplicationEventType type, int calendarType) {
        ApplicationEvent event = applicationEventService.getLastEvent(type);

        if (event == null) {
            // This is the first time the server starts, no need to process stats
            event = applicationEventBuilder.newEvent(type, true);
            applicationEventBuilder.save(event);
            return true;
        }

        Timestamp timestamp = new Timestamp(event.getTimestamp());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());

        Calendar calNow = Calendar.getInstance();
        calNow.setTimeInMillis(DateUtils.now());

        if (cal.get(calendarType) != calNow.get(calendarType)) {
            event = applicationEventBuilder.newEvent(type, false);
            applicationEventBuilder.save(event);
            return true;
        }

        return false;
    }

    private boolean createDayEvent(ApplicationEventType type, int weekDay) {
        ApplicationEvent event = applicationEventService.getLastEvent(type);

        Calendar calNow = Calendar.getInstance();
        calNow.setTimeInMillis(DateUtils.now());

        if (calNow.get(Calendar.DAY_OF_WEEK) != weekDay) {
            // it's not the day we want to track
            return false;
        }

        if (event == null) {
            // This is the first time the server starts, no need to process stats
            event = applicationEventBuilder.newEvent(type, true);
            applicationEventBuilder.save(event);
            return true;
        }

        Timestamp timestamp = new Timestamp(event.getTimestamp());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());

        if (cal.get(Calendar.WEEK_OF_YEAR) != calNow.get(Calendar.WEEK_OF_YEAR)) {
            event = applicationEventBuilder.newEvent(type, false);
            applicationEventBuilder.save(event);
            return true;
        }

        return false;
    }

    public boolean createDailyEvent() {
        return createTimeEvent(ApplicationEventType.TIME_NEW_DAY, Calendar.DAY_OF_MONTH);
    }

    public boolean createWeeklyEvent() {
        return createTimeEvent(ApplicationEventType.TIME_NEW_WEEK, Calendar.WEEK_OF_YEAR);
    }

    public boolean createTuesdayEvent() {
        return createDayEvent(ApplicationEventType.TIME_NEW_TUESDAY, Calendar.SATURDAY);
    }

    public boolean createMonthlyEvent() {
        return createTimeEvent(ApplicationEventType.TIME_NEW_MONTH, Calendar.MONTH);
    }
}
