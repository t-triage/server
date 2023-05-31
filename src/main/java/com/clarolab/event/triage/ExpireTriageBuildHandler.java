/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.event.triage;

import com.clarolab.event.process.AbstractEventHandler;
import com.clarolab.event.process.ApplicationEventType;
import com.clarolab.event.process.EventHandler;
import com.clarolab.model.ApplicationEvent;
import com.clarolab.populate.DataProvider;
import com.clarolab.service.BuildTriageService;
import com.clarolab.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.util.Constants.DAYS_TO_EXPIRE_TRIAGE;
import static com.clarolab.util.Constants.DEFAULT_DAYS_TO_EXPIRE_TRIAGE;

public class ExpireTriageBuildHandler extends AbstractEventHandler implements EventHandler {

    @Autowired
    BuildTriageService buildTriageService;

    @Autowired
    private PropertyService propertyService;

    @Override
    public ApplicationEventType[] handleTypes() {
        ApplicationEventType[] handleTypes = {ApplicationEventType.TIME_NEW_DAY};
        return handleTypes;
    }

    @Override
    public boolean process(ApplicationEvent event) {
        Long fromTime = DataProvider.getTimeAdd(-1 * getDaysToExpire());
        buildTriageService.expireOldEvents(fromTime);

        return true;
    }

    private int getDaysToExpire() {
        return propertyService.valueOf(DAYS_TO_EXPIRE_TRIAGE, DEFAULT_DAYS_TO_EXPIRE_TRIAGE);
    }
}
