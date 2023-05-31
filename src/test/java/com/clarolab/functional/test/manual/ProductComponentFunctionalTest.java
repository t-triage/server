/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.manual;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.manual.ManualTestCase;
import com.clarolab.model.manual.ProductComponent;
import com.clarolab.model.manual.service.ManualTestCaseService;
import com.clarolab.model.manual.service.ProductComponentService;
import com.clarolab.populate.UseCaseDataProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ProductComponentFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;


    @Autowired
    private ProductComponentService productComponentService;

    @Autowired
    private ManualTestCaseService manualTestCaseService;


    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void create() {
        ProductComponent test = provider.getProductComponent();
        ProductComponent dbTest = productComponentService.find(test.getId());

        Assert.assertNotNull("ProductComponent should have been created in the DB", dbTest);
    }

    @Test
    public void searchProductComponentByName() {

        String name = "target";
        provider.setName(name);
        ProductComponent productComponent = provider.getProductComponent();
        provider.setProductComponent(null);
        provider.setName("Manchi");
        ProductComponent manchiProductComponent = provider.getProductComponent();
        List<ProductComponent> productComponentQuery = productComponentService.search(name, false);
        Assert.assertEquals(productComponent.getName(), productComponentQuery.iterator().next().getName());
    }

    @Test
    public void suggested() {
        provider.setName("SuggestedComponent");
        ManualTestCase test1 = provider.getManualTestCase(1);
        provider.setManualTestCase(null);
        ManualTestCase test2 = provider.getManualTestCase(1);
        provider.setManualTestCase(null);
        ManualTestCase test3 = provider.getManualTestCase(1);
        provider.setManualTestCase(null);
        ManualTestCase test4 = provider.getManualTestCase(1);

        test2.setComponent1(test1.getComponent2());
        test2.setComponent2(null);
        test2.setComponent3(null);
        manualTestCaseService.update(test2);

        test3.setComponent1(null);
        test3.setComponent2(null);
        test3.setComponent3(null);
        manualTestCaseService.update(test3);

        test4.setComponent1(null);
        test4.setComponent2(test1.getComponent1());
        test4.setComponent3(test1.getComponent2());
        manualTestCaseService.update(test4);


        List<ProductComponent> relateds = productComponentService.suggested(test1.getComponent2());
        Assert.assertEquals(2, relateds.size());
        Assert.assertTrue(relateds.get(0).equals(test1.getComponent1()) || relateds.get(0).equals(test1.getComponent3()));
        Assert.assertTrue(relateds.get(1).equals(test1.getComponent1()) || relateds.get(1).equals(test1.getComponent3()));


    }
}
