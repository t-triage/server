/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.model;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.manual.Functionality;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.FunctionalityService;
import com.clarolab.service.filter.FilterCriteria;
import com.clarolab.service.filter.FilterSpecification;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class FunctionalityFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private FunctionalityService functionalityService;

    @Autowired
    private UseCaseDataProvider provider;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void testSearch() {
        String name = "functionality";
        provider.setName(name);

        provider.getFunctionalityEntity();

        List<Functionality> list = functionalityService.search(name);

        Assert.assertEquals("Functionality should be found", 1, list.size());
    }

    @Test
    public void testEmptyFunctionalityNameResult() {
        FilterSpecification spec = new FilterSpecification(new FilterCriteria("name", ":", "noname"));

        List<Functionality> results = functionalityService.findAll(spec, Pageable.unpaged()).getContent();
        Assert.assertTrue(results.isEmpty());
    }

    @Test
    public void testCreate() {
        Functionality functionality = provider.getFunctionalityEntity();
        Functionality dbTest = functionalityService.find(functionality.getId());

        Assert.assertNotNull("Test should have been created in the DB", dbTest);
        
    }


}
