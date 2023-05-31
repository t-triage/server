/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.model;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.populate.DataProvider;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.ApplicationDomainService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ApplicationDomainFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private ApplicationDomainService applicationDomainService;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void testBasic() {
        String prefix = "creationDomain.com";
        provider.setName(prefix);
        provider.getApplicationDomain();
    }

    @Test
    public void testValidEmail() {
        String prefix = "testValidEmail.com";
        String username = DataProvider.getRandomName("user", 4) + "@" + prefix;
        provider.setName(prefix);
        provider.getApplicationDomain();

        boolean valid = applicationDomainService.isValidEmail(username);

        Assert.assertTrue(valid);
    }

    @Test
    public void testInvalidEmail() {
        String prefix = "testInvalidEmail.com";
        String username = DataProvider.getRandomName("user", 4) + prefix;
        provider.getApplicationDomain();

        boolean valid = applicationDomainService.isValidEmail(username);

        Assert.assertFalse(valid);
    }

    @Test
    public void testBlacklist() {
        String prefix = "hotmail.com";
        String username = DataProvider.getRandomName("user", 4) + "@" + prefix;
        provider.setName(prefix);
        provider.getApplicationDomain();

        applicationDomainService.initializeDefaultBlacklist();
        boolean valid = applicationDomainService.isValidEmail(username);

        Assert.assertFalse(valid);
    }

    @Test
    public void testWhitelist() {
        String prefix = "clarolab.com";
        String username = DataProvider.getRandomName("user", 4) + "@" + prefix;
        provider.setName(prefix);
        provider.getApplicationDomain();

      //  applicationDomainService.initializeDefaultW();
        boolean valid = applicationDomainService.isValidEmail(username);

        Assert.assertTrue(valid);
    }

}
