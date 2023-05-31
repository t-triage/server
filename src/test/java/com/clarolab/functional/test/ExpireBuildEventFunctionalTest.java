/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.BuildTriage;
import com.clarolab.populate.DataProvider;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.BuildTriageService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ExpireBuildEventFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private BuildTriageService buildTriageService;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void testExpireEvent() {
        String prefix = "testExpireEvent";
        provider.setName(prefix);
        provider.build(3);
        long time = DataProvider.getTimeAdd(19);
        buildTriageService.expireOldEvents(time);

        List<BuildTriage> triages = buildTriageService.findAllOlderThan(time);
        Assert.assertTrue(!triages.isEmpty());
        for (BuildTriage triage : triages ) {
            if (triage.getExecutor().getName().startsWith(prefix)) {
                Assert.assertTrue(!triage.isEnabled());
                Assert.assertTrue(triage.isTriaged());
            }
        }
    }

    @Test
    public void testDontExpire() {
        String prefix = "testDontExpire";
        provider.setName(prefix);
        provider.build(3);
        long time = DataProvider.getTimeAdd(-4);
        buildTriageService.expireOldEvents(time);

        List<BuildTriage> triages = buildTriageService.findAll();
        Assert.assertTrue(!triages.isEmpty());
        for (BuildTriage triage : triages ) {
            if (triage.getExecutor().getName().startsWith(prefix)) {
                // these events should not be expired
                Assert.assertTrue(triage.isEnabled());
                Assert.assertTrue(!triage.isTriaged());
            }
        }
    }
}
