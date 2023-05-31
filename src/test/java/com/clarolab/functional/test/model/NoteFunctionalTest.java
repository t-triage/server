/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.model;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.TestTriage;
import com.clarolab.populate.RealDataProvider;
import com.clarolab.populate.TestTriagePopulate;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.TestTriageService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class NoteFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private RealDataProvider realDataProvider;

    @Autowired
    private TestTriageService testTriageService;


    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void noTriageNote() {
        TestTriage test = provider.getTestCaseTriage();
        TestTriage previousTest = testTriageService.findValidPreviousTriageWithNote(test);

        Assert.assertNull("A note is never generated", previousTest);
    }

    @Test
    public void lastTriageNote() {
        String prefix = "lastTriageNote";
        TestTriagePopulate sample = new TestTriagePopulate();
        sample.initializeFail(prefix);

        provider.getBuild(1);
        provider.getTestExecution(sample);
        TestTriage test = provider.getTestCaseTriage();
        test.setNote(provider.getNote());
        test.setTriaged(true);
        testTriageService.update(test);

        provider.clearForNewBuild();
        provider.getBuild(2);
        provider.getTestExecution(sample);
        test = provider.getTestCaseTriage();


        TestTriage previousTest = testTriageService.findValidPreviousTriageWithNote(test);

        Assert.assertNotNull("A note is created in first build", previousTest);
    }
}
