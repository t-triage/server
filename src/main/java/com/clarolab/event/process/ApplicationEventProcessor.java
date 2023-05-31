/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.event.process;

import com.clarolab.model.ApplicationEvent;
import com.clarolab.service.ApplicationEventService;
import com.clarolab.service.PropertyService;
import com.clarolab.service.exception.NotFoundServiceException;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.logging.Level;

import static com.clarolab.util.Constants.*;

@Component
@Log
public class ApplicationEventProcessor {

    @Autowired
    private ApplicationEventService applicationEventService;

    @Autowired
    ApplicationHandlerProvider applicationHandlerProvider;

    @Autowired
    protected PropertyService propertyService;


    public boolean process() {
        propertyService.warmUp();
        boolean executionSuccess = true;

        ApplicationEvent event = applicationEventService.getNextPendingEvent();
        int i = 0;
         while (i < getMaxEventsToProcess() && event != null) {
            executionSuccess = process(event);
            i++;
            event = applicationEventService.getNextPendingEvent();
        }

        return executionSuccess;
    }

    @Transactional
    public ApplicationEvent processNextEvent() {
        ApplicationEvent event = applicationEventService.getNextPendingEvent();
        if (event != null) {
            event.setProcessed(true);
            event.setSuccess(false);
            applicationEventService.update(event);
            process(event);
        }
        return event;
    }


    @Transactional
    public boolean process(ApplicationEvent event) {
        if (event == null) {
            return false;
        }
        boolean executionSuccess = true;

        event.setProcessed(true);
        event.setSuccess(false);
        event = applicationEventService.update(event);

        for (EventHandler handler : applicationHandlerProvider.getHandlersFor(event)) {
            boolean success = false;
            if (handler.handles(event)) {
                try {
                    Thread.sleep(DEFAULT_EVENT_PROCESS_DELAY); // waits in order to not hit the server that much
                    success = handler.process(event);
                } catch (Exception ex) {
                    String message = "Error processing eventId: " + event.getId() + " handler: " + handler.getClass();
                    log.log(Level.SEVERE, message, ex);
                    success = false;
                }
                executionSuccess = success && executionSuccess;
            }
        }
        if (executionSuccess) {
            event.setSuccess(executionSuccess);
            try {
                applicationEventService.update(event);
            } catch (NotFoundServiceException ex) {
                log.log(Level.WARNING, String.format("This may be related that the CleanupEventsEventHandler has deleted the event type: %s", event.getType()), ex);
            }
        }

        return executionSuccess;
    }

    private int getMaxEventsToProcess() {
        return propertyService.valueOf(MAX_EVENTS_TO_PROCESS, DEFAULT_MAX_EVENTS_TO_PROCESS);
    }


}
