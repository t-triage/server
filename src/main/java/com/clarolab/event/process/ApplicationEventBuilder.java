/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.event.process;

import com.clarolab.model.ApplicationEvent;
import com.clarolab.service.ApplicationEventService;
import com.clarolab.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationEventBuilder {

    @Autowired
    private ApplicationEventService applicationEventService;

    @Autowired
    private ApplicationHandlerProvider applicationHandlerProvider;


    public ApplicationEvent newEvent(ApplicationEventType type, boolean processed) {
        ApplicationEvent event = newEvent();
        event.setType(type);
        event.setProcessed(processed);

        return event;
    }

    public void unknownEvent() {
        newEvent(ApplicationEventType.UNKNOWN, false);
    }

    public ApplicationEvent newEvent() {
        long timestamp = DateUtils.now();
        return ApplicationEvent.builder()
                .enabled(true)
                .timestamp(timestamp)
                .updated(timestamp)
                .processed(false)
                .eventTime(timestamp)
                .build();
    }

    // Keep only one instance with extra parameter appending existing values
    public void appendExtraParameter(ApplicationEvent event, String separator) {
        ApplicationEvent dbEvent = applicationEventService.getLastPendingEvent(event.getType());

        if (dbEvent == null) {
            save(event);
        } else {
            dbEvent.setExtraParameter(dbEvent.getExtraParameter() + separator + event.getExtraParameter());
            applicationEventService.update(dbEvent);
        }

    }

    // We can add extra unique parameters to validate for example unique source
    public void saveUnique(ApplicationEvent event, boolean extraParamUnique) {
        boolean shouldSave = true;

        if (extraParamUnique) {
            shouldSave = !areEventsWithExtraParam(event.getType(), event.getExtraParameter());
        }

        if (shouldSave) {
            save(event);
        }

    }

    public void save(ApplicationEvent event) {
        // This method is in case we want to save in a new thread or not save

        if (isImportantEvent(event)) {
            applicationEventService.save(event);
        }
    }

    private boolean isImportantEvent(ApplicationEvent event) {
        // It will be true only if there is any handler interested in the event.
        List<EventHandler> handlers = applicationHandlerProvider.getHandlersFor(event);
        return handlers.size() > 0;
    }

    private boolean areEventsWithExtraParam(ApplicationEventType type, String extraParam) {
        return applicationEventService.countWithTypeAndExtraParam(type, extraParam) > 0;
    }
}
