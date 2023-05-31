/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.startup;

import com.clarolab.QAReportApplication;
import com.clarolab.populate.PopulateDemoData;
import lombok.extern.java.Log;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

import javax.validation.constraints.NotNull;

@Log
public class ApplicationRunListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        log.info("Application Successfully started");
        ConfigurableApplicationContext applicationContext = applicationReadyEvent.getApplicationContext();
        QAReportApplication.setApplicationContext(applicationContext);
        executePopulate(applicationContext);
    }

    private void executePopulate(@NotNull ConfigurableApplicationContext applicationContext) {
        PopulateDemoData demoData = applicationContext.getBean(PopulateDemoData.class);
        demoData.createDemoData();
    }
}
