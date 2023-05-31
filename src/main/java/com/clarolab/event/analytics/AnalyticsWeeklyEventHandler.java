/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.event.analytics;

import com.clarolab.event.process.AbstractEventHandler;
import com.clarolab.event.process.ApplicationEventType;
import com.clarolab.event.process.EventHandler;
import com.clarolab.model.ApplicationEvent;
import com.clarolab.serviceDTO.ExecutorServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsWeeklyEventHandler extends AbstractEventHandler implements EventHandler {

    @Autowired
    private ExecutorServiceDTO executorServiceDTO;

    @Autowired
    private ProductStatService productStatService;

    @Override
    public ApplicationEventType[] handleTypes() {
        ApplicationEventType[] handleTypes = {ApplicationEventType.TIME_NEW_WEEK};

        return handleTypes;
    }

    @Override
    public boolean process(ApplicationEvent event) {
        return processPreviousWeek(event);
    }

    // Analyze the executorStats created during the previous week and
    private boolean processPreviousWeek(ApplicationEvent event) {
        // TODO create a ProductStat summarizing ExecutorStats. Should be uncommented for evolution stats
   /*     ProductStat productStat = new ProductStat();

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(event.getEventTime());
        cal.add(Calendar.DAY_OF_MONTH, -7);

        long pass = 0;
        long skip = 0;
        long newFails = 0;
        long fails = 0;
        long nowPassing = 0;
        long toTriage = 0;
        long totalTests = 0;

        List<ExecutorView> views = executorServiceDTO.getExecutorViewsBetween(cal.getTimeInMillis(), event.getTimestamp());
        for (ExecutorView view : views) {
            pass += view.getPassCount();
            skip += view.getSkipCount();
            newFails += view.getTotalNewFails();
            fails += view.getTotalFails();
            nowPassing += view.getTotalNowPassing();
            toTriage += view.getTotalTestsToTriage();
            totalTests += view.getTotalTests();
        }

        productStat.setPass(pass);
        productStat.setSkip(skip);
        productStat.setNewFails(newFails);
        productStat.setFails(fails);
        productStat.setNowPassing(nowPassing);
        productStat.setToTriage(toTriage);
        productStat.setTotalTests(totalTests);
        productStat.setTimestamp(event.getTimestamp());

        String date = BaseDateFormat.format(event.getEventTime());
        productStat.setActualDate(date);
        productStat.setExecutionDate(date);
        productStat.setDeadline(date);

        productStatService.save(productStat);*/

        return true;
    }


}
