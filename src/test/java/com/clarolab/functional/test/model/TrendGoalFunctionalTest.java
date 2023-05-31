/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.model;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.TrendGoal;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.TrendGoalService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TrendGoalFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private TrendGoalService trendGoalService;

    @Autowired
    private UseCaseDataProvider provider;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void create() {
        TrendGoal trendGoal = provider.getTrendGoal();

        Assert.assertNotNull("Trend Goal could not be persisted", trendGoal);
        Assert.assertTrue("Trend Goal ID was not updated", trendGoal.isPersistent());
    }


}
