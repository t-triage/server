/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.event.process;

import com.clarolab.model.ApplicationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// Provides every Handler available in the system

@Component
public class ApplicationHandlerProvider {

    @Autowired
    List<EventHandler> availableHandlers;

    /**
     * This method should be completed with every Available Handler.
     * If you create a new one, you should implement EventHandler
     * @return
     */
    public List<EventHandler> getAvailableHandlers() {
        return availableHandlers;
    }


    public List<EventHandler> getHandlersFor(ApplicationEvent event) {
        List<EventHandler> handlers = new ArrayList<>(5);

        for (EventHandler instance : getAvailableHandlers()) {
            if (instance.handles(event)) {
                handlers.add(instance);
            }
        }

        List<EventHandler> sortedList = handlers.stream().
        sorted(Comparator.comparing(EventHandler::getPriority))
                .collect(Collectors.toList());
        
        return sortedList;
    }
}
