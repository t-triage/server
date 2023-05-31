/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.agents;

import com.clarolab.event.process.ApplicationEventProcessor;
import com.clarolab.service.PropertyService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.clarolab.util.Constants.DEFAULT_AGENT_DELAY;
import static com.clarolab.util.Constants.DEFAULT_AGENT_STATS_JOB_TIMEOUT;

@Log
@Component
@Transactional(propagation = Propagation.REQUIRED)
public class StatsAgent implements SmartAgent {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private ApplicationEventProcessor applicationEventProcesor;

    @Override
    @Scheduled(fixedRate = DEFAULT_AGENT_STATS_JOB_TIMEOUT, initialDelay = 0)
    public void execute() {
        if (!propertyService.valueOf("STATS_SERVICE_ENABLED", true)) return;

        log.info("Executing Stats Agent");
        applicationEventProcesor.process();
        log.info("End Executing Stats Agent");
    }
}
