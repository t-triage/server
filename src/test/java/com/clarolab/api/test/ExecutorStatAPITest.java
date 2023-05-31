/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.api.test;

import com.clarolab.agents.StatsAgent;
import com.clarolab.api.BaseAPITest;
import com.clarolab.model.ApplicationEvent;
import com.clarolab.populate.DataProvider;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.ApplicationEventService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.util.Constants.API_EXECUTOR_STAT_URI;
import static com.clarolab.util.Constants.LIST_PATH;

public class ExecutorStatAPITest extends BaseAPITest {

    @Autowired
    private UseCaseDataProvider provider = new UseCaseDataProvider();

    @Autowired
    private ApplicationEventService applicationEventService;

    @Autowired
    private StatsAgent statsAgent;

    @Test
    public void testBasic() {
        int amount = 10;

        // creates execution stats to query
        for (int i = 0; i < amount; i++) {
            provider.setExecutorStat(null);
            provider.setTimestamp(DataProvider.getTimeAdd(-1 * amount));
            provider.getExecutorStat();
        }

        testUri(API_EXECUTOR_STAT_URI + LIST_PATH);
    }

    @Test
    public void testExecutorsFromEvents() {
        int amount = 1;
        ApplicationEvent event;


        // Generates executors and tests for the previous days
        provider.setName("TestPop");
        provider.build(amount);

        statsAgent.execute();

        testUri(API_EXECUTOR_STAT_URI + LIST_PATH);
    }

    @Before
    public void clearProvider() {
        provider.clear();
    }

}
