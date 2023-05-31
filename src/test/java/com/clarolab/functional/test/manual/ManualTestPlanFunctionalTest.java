/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.manual;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.manual.ManualTestPlan;
import com.clarolab.model.manual.service.ManualTestPlanService;
import com.clarolab.populate.UseCaseDataProvider;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ManualTestPlanFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private ManualTestPlanService manualTestPlanService;


    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void create() {
        ManualTestPlan testPlan = provider.getManualTestPlan();
        ManualTestPlan dbTest = manualTestPlanService.find(testPlan.getId());

        Assert.assertNotNull("Plan should have been created in the DB", dbTest);
    }

    @Test
    public void searchPlanName() {
        String name = "TestName";
        String searchString = " Tes nAm ";
        provider.setName(name);
        ManualTestPlan testPlan = provider.getManualTestPlan();
        provider.setManualTestPlan(null);
        ManualTestPlan test1 = provider.getManualTestPlan();
        List<ManualTestPlan> manualPlans = new ArrayList<>();
        manualPlans.add(test1);
        manualPlans.add(testPlan);
        List<ManualTestPlan> manualPlansQuery = manualTestPlanService.searchByName(searchString);
        Assert.assertTrue(CollectionUtils.isEqualCollection(manualPlans, manualPlansQuery));
    }
}
