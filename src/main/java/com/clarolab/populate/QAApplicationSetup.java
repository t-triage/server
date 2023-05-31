/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.populate;

import com.clarolab.model.ImageModel;
import com.clarolab.model.Property;
import com.clarolab.model.User;
import com.clarolab.model.types.RoleType;
import com.clarolab.service.LicenseService;
import com.clarolab.service.PropertyService;
import com.clarolab.service.UserService;
import com.clarolab.startup.License;
import com.clarolab.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static com.clarolab.util.Constants.MAX_TESTCASES_TO_PROCESS;

@Component
@Order(1)
public class QAApplicationSetup implements ApplicationRunner {

    @Autowired
    private PopulateSystemProperties populateSystemProperties;

    @Autowired
    private UserService userService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private LicenseService licenseService;

    public void run(ApplicationArguments args) {
        if (!wasExecuted()) {
            populateSystemProperties();
            createAdmin();
        }
    }

    public License createDemoLicense() {
        License license;
        long date = DateUtils.now();

        license = License.builder()
                .creationTime(date)
                .free(true)
                .expired(false)
                .licenseCode(null)
                .build();


        license = licenseService.save(license);

        return license;
    }

    private User createAdmin() {
        if (!userService.isInternalUserEnabled()) {
            return null;
        }
        String username = "admin@ttriage.com";
        User byUsername = userService.findByUsername(username);
        if (byUsername != null) return byUsername;

        User user = DataProvider.getUserAsAdmin();
        user.setUsername(username);
        user.setRealname("tTriage Admin");
        user.setPassword(userService.getEncryptedPassword("t-triageAdm"));
        user.setAvatar(ImageModel
                .builder()
                .enabled(true)
                .name("IMAGE")
                .timestamp(DateUtils.now())
                .updated(DateUtils.now())
                .data("https://lh3.googleusercontent.com/6itEFCORQlcN17yqiZ25es8Khubvsg38EPrsCTVDxmGiOTy2E914BE0tpGB3LD_c-A")
                .build());

        user.setRoleType(RoleType.ROLE_ADMIN);

        return userService.save(user);
    }

    private void populateSystemProperties() {
        populateSystemProperties.populateSystemProperties();
    }

    private boolean wasExecuted() {
        Property property = propertyService.findByName(MAX_TESTCASES_TO_PROCESS);
        return property != null;
    }

}
