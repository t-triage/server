/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.event.process;

import com.clarolab.model.ApplicationEvent;

public interface EventHandler {

    ApplicationEventType[] handleTypes();

    boolean process(ApplicationEvent event);

    boolean handles(ApplicationEvent event);
    
    // priority to be executed
    Integer getPriority();
}
