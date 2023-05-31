/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.event.process;

import com.clarolab.model.ApplicationEvent;
import com.clarolab.service.*;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Calendar;
import java.util.logging.Level;

import static com.clarolab.util.Constants.*;


@Component
@Log
public class CleanupEventsEventHandler extends AbstractEventHandler implements EventHandler {

    @Autowired
    private LogService logService;

    @Autowired
    private ApplicationEventService applicationEventService;

    @Autowired
    private TestExecutionService testExecutionService;

    @Autowired
    private PropertyService propertyService;
    
    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Override
    public ApplicationEventType[] handleTypes() {
        // ApplicationEventType[] handleTypes = {};
        ApplicationEventType[] handleTypes = {ApplicationEventType.TIME_NEW_DAY};
        return handleTypes;
    }

    @Override
    public boolean process(ApplicationEvent event) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, getCleanupDays());

        try {
            cleanup(cal.getTime().getTime());
        } catch (Exception ex) {
            log.log(Level.SEVERE, String.format("Couldn't cleanup eventId: %d cleanup days: %d", event.getId(), getCleanupDays()), ex);
            return true;
        }

        return true;
    }
    public boolean cleanup(long timestamp) {
        long timestampReferenceStart = Calendar.getInstance().getTimeInMillis();
        log.log(Level.INFO, String.format("Start cleaning tests records at %d", timestampReferenceStart));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, getCleanupDays());

        testTriageService.deleteOld(cal.getTimeInMillis());
        applicationEventService.deleteOldEvents(cal.getTimeInMillis());
        logService.deleteOld(timestamp);

        TransactionStatus transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
        transactionManager.commit(transactionStatus);

        long timestampReferenceEnd= Calendar.getInstance().getTimeInMillis();
        log.log(Level.INFO, String.format("End cleaning tests records at %d. It takes %d", timestampReferenceEnd,  timestampReferenceEnd - timestampReferenceStart));

        return true;
    }
    public boolean cleanupOneByOne(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, getCleanupDays());

        log.log(Level.INFO, String.format("Start cleaning %d", timestamp));
        testTriageService.deleteOldOneByOne(timestamp);
        applicationEventService.deleteOldEvents(cal.getTimeInMillis());
        logService.deleteOld(timestamp);

         TransactionStatus transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
         transactionManager.commit(transactionStatus);

        log.log(Level.INFO, String.format("End cleaning %d", timestamp));

        return true;
    }

    private int getCleanupDays() {
        return propertyService.valueOf(OLD_EVENTS_TO_DELETE_DAYS, DEFAULT_OLD_EVENTS_TO_DELETE_DAYS);
    }
    
    @Override
    public Integer getPriority() {
        return 5;
    }

}
