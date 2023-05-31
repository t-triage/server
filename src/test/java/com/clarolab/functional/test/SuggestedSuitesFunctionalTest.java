/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test;

import com.clarolab.dto.UserDTO;
import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.*;
import com.clarolab.populate.DataProvider;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.*;
import com.clarolab.serviceDTO.BuildTriageServiceDTO;
import com.clarolab.serviceDTO.UserServiceDTO;
import com.clarolab.util.Constants;
import com.clarolab.util.DateUtils;
import com.clarolab.view.ExecutorView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.clarolab.util.Constants.MAX_TESTCASES_PER_DAY;

public class SuggestedSuitesFunctionalTest extends BaseFunctionalTest {


    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private UserServiceDTO userServiceDTO;

    @Autowired
    private BuildTriageServiceDTO buildTriageServiceDTO;

    @Autowired
    private BuildTriageService buildTriageService;

    @Autowired
    private TriageSpecService triageSpecService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private DeadlineService deadlineService;

    @Autowired
    private TestExecutionService testExecutionService;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void suggestedEmptyView() {
        String prefix = "suggestedView";
        long now = DateUtils.now();
        provider.setName(prefix);
        provider.setTimestamp(now);
        UserDTO user = userServiceDTO.convertToDTO(provider.getUser());
        provider.build(1);

        List<ExecutorView> list = buildTriageServiceDTO.getTopViews(user);

        Assert.assertNotNull(list);
        Assert.assertTrue(list.isEmpty());
    }


    @Test
    public void suggestedTodayList() {
        String prefix = "suggestedList";
        long now = DateUtils.now();
        provider.setName(prefix);
        provider.setTimestamp(now);
        UserDTO user = userServiceDTO.convertToDTO(provider.getUser());
        provider.getBuild(1);
        TriageSpec spec = provider.getTriageSpec();
        spec.setFrequencyCron(Constants.DEADLINE_FREQUENCY_LAST_DAY);
        spec.setEveryWeeks(1);
        triageSpecService.update(spec);
        provider.getTestExecutionFail();
        provider.getBuildTriage();

        List<ExecutorView> list = buildTriageServiceDTO.getTopViews(user);

        long tomorrow = DateUtils.beginDay(1);

        Assert.assertNotNull(String.format("There should be suggestions for today %d", tomorrow), list);
        Assert.assertEquals(String.format("The suggested amount does not match for time %d", tomorrow), 1, list.size());
    }

    @Test
    public void suggestedTomorrowList() {
        int amount = 3;
        // There should be several tickets for tomorrow
        Property property = propertyService.findByName(MAX_TESTCASES_PER_DAY);
        if (property == null) {
            property = DataProvider.getProperty();
            property.setName(MAX_TESTCASES_PER_DAY);
            property.setValue(String.valueOf(amount));
            property = propertyService.save(property);
        } else {
            property.setValue(String.valueOf(amount));
            propertyService.update(property);
        }

        // Creating one with deadline today just to get sure it does not interfere
        String prefix = "suggestedTomorrowList";
        long now = DateUtils.now();
        provider.setName(prefix + "NO1");
        provider.setTimestamp(now);
        UserDTO user = userServiceDTO.convertToDTO(provider.getUser());
        Product product = provider.getProduct();
        provider.getBuild(1);
        TriageSpec spec = provider.getTriageSpec();
        spec.setFrequencyCron(Constants.DEADLINE_FREQUENCY_LAST_DAY);
        spec.setEveryWeeks(1);
        triageSpecService.update(spec);
        provider.getTestExecutionFail();
        provider.getBuildTriage();

        provider.clear();
        provider.setName(prefix+ "NO2");
        provider.setUser(userServiceDTO.convertToEntity(user));
        provider.setProduct(product);
        // creates an executor with only 1 test
        provider.getTestExecutionFail();
        provider.getBuildTriage();
        // creates an executor with enough amount of tests
        provider.clear();
        provider.setName(prefix+ "Yes");
        provider.setUser(userServiceDTO.convertToEntity(user));
        provider.setProduct(product);
        for (int i = 0; i <= amount; i++) {
            provider.setTestExecution(null);
            provider.getTestExecutionFail();
        }
        // Set a product deadline for tomorrow
        Deadline deadline = provider.getDeadline();
        deadline.setDeadlineDate(DateUtils.beginDay(2));
        deadlineService.update(deadline);
        String goodSuite = provider.getExecutor().getName();

        provider.getBuildTriage();

        long tomorrow = DateUtils.beginDay(1);

        List<BuildTriage> testList = buildTriageService.pendingBigBuilds(provider.getUser(), tomorrow);

        Assert.assertNotNull(testList);
        Assert.assertEquals(1, testList.size());
        Assert.assertEquals(goodSuite, testList.get(0).getExecutorName());
    }

}
