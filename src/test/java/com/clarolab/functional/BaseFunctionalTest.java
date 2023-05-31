/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional;

import com.clarolab.aaa.model.UserPrincipal;
import com.clarolab.integration.BaseIntegrationTest;
import com.clarolab.model.ImageModel;
import com.clarolab.model.User;
import com.clarolab.populate.DataProvider;
import com.clarolab.runner.category.FunctionalTestCategory;
import com.clarolab.service.LicenseService;
import com.clarolab.service.UserService;
import com.clarolab.startup.License;
import com.clarolab.util.DateUtils;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Calendar;

@Category(FunctionalTestCategory.class)
public abstract class BaseFunctionalTest extends BaseIntegrationTest {
    private static boolean initialized;

    @Autowired
    private UserService userService;

    @Autowired
    private LicenseService licenseService;

    @Before
    public void setUp() {
        createDemoLicense();
        if (!initialized)
            initialize();
    }

    private void initialize() {
        User user = getOrCreateUser();
        Authentication authentication = new TestingAuthenticationToken(new UserPrincipal(user.getId(), user.getUsername(), "", Lists.newArrayList()), this);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        initialized = true;
    }

    private User getOrCreateUser() {
        User user = userService.findByUsername(System.getProperty("qe.user"));
        if(user==null) {
            user = DataProvider.getUserAsAdmin();
            user.setUsername(System.getProperty("qe.user"));
            user.setRealname("Jon Snow");
            user.setPassword(userService.getEncryptedPassword(System.getProperty("qe.user")));

            user.setAvatar(ImageModel
                    .builder()
                    .enabled(true)
                    .name("DEMO IMAGE")
                    .timestamp(DateUtils.now())
                    .data("https://lh3.googleusercontent.com/6itEFCORQlcN17yqiZ25es8Khubvsg38EPrsCTVDxmGiOTy2E914BE0tpGB3LD_c-A")
                    .build());
            user = userService.save(user);
        }

        return user;
    }

    public License createDemoLicense() {
        License license;
        long date = DateUtils.now();

        long expStamp = DateUtils.daysFromToday(365);

        license = License.builder()
                .creationTime(date)
                .expirationTime(date + expStamp)
                .expired(false)
                .licenseCode("j2JxHD0xnPk0wOI3J37t1k4yiSD5epHTWDOi$XvvrvItfEEZWGyCyuiTcspJtSHI6PY7NJQFdli50nRTzIGnvVwXflA==")
                .free(false)
                .build();

        license = licenseService.save(license);

        return license;
    }
}
