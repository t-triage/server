/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.agents;

import com.clarolab.event.productivity.ProductivityDailyEventProcessor;
import com.clarolab.service.PropertyService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.clarolab.util.Constants.*;

@Log
@Component
@Transactional(propagation = Propagation.REQUIRED)
public class ProductivityAgent implements SmartAgent{

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private ProductivityDailyEventProcessor productivityDailyEventProcessor;

    @Override
    @Scheduled(cron = DEFAULT_AGENT_PRODUCTIVITY_JOB_TIMEOUT)
    public void execute() {
        if (!propertyService.valueOf(AGENT_PRODUCTIVITY_SERVICE_ENABLED, DEFAULT_AGENT_PRODUCTIVITY_SERVICE_ENABLED)) return;

        log.info("Executing Productivity Agent");
        productivityDailyEventProcessor.process();
        log.info("End Executing Stats Productivity");
    }

}
