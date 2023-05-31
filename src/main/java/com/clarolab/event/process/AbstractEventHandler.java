/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.event.process;

import com.clarolab.model.ApplicationEvent;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Setter
public abstract class AbstractEventHandler {

    // returns if it handles or not the event
    public boolean handles(ApplicationEvent event) {
        for (ApplicationEventType type : handleTypes()) {
            if (type.equals(event.getType())) {
                return true;
            }
        }
        return false;
    }

    public abstract ApplicationEventType[] handleTypes();

    // Process the event
    public abstract boolean process(ApplicationEvent event);
    
    public Integer getPriority() {
        return 1;
    }

}
