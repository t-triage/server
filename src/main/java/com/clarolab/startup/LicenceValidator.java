/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.startup;

import com.clarolab.model.manual.service.ManualTestCaseService;
import com.clarolab.service.*;
import com.clarolab.util.DateUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.util.Constants.*;

@Log
@Component
public class LicenceValidator {


    @Autowired
    private LicenseService licenseService;

    @Autowired
    private UserService userService;

    @Autowired
    private ManualTestCaseService manualTestCaseService;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private ApplicationDomainService applicationDomainService;

    @Autowired
    private NotificationService notificationService;

    public boolean validateLicenceDomain(String domain) {
        String instance = applicationDomainService.getURL();
        if (instance.contains(domain)) {
            return true;
        }
        for (String host : getFreeInstances()) {
            if (instance.contains(host)) {
                return true;
            }
        }
        return false;
    }

    // If the license is free, it's going to return true
    public boolean validateLicenceType() {
        return licenseService.getLicense().isFree();
    }

    // If license is expired, returns true
    public boolean isExpired(License license) {
        return license.getExpirationTime() < DateUtils.now();
    }

    // A Free license allows you to create only 5 users.
    public boolean validateUserCreation() {

        boolean free = validateLicenceType();
        long currentUsers = userService.countEnabled();

        if (free) {
            if (currentUsers >= DEFAULT_FREE_LICENSE_MAX_USERS) {
                return false;
            } else if (currentUsers == DEFAULT_FREE_LICENSE_MAX_USERS - 1) {
                notificationService.createNotification("You have reached the limit of " + DEFAULT_FREE_LICENSE_MAX_USERS + " users",
                        "To be able to create more user accounts, please contact t-Triage Support for a full commercial license.",
                        0,
                        userService.getAllAdminUser());
            }
        }

        return true;
    }

    public boolean validateTestCreation() {

        boolean free = validateLicenceType();
        long currentsManualTests = manualTestCaseService.countEnabled();

        if (free) {
            if (currentsManualTests >= DEFAULT_FREE_LICENSE_MAX_MANUAL_TEST_CASES) {
                return false;
            } else if (currentsManualTests == DEFAULT_FREE_LICENSE_MAX_MANUAL_TEST_CASES - 1) {
                notificationService.createNotification("You have reached the limit of " + DEFAULT_FREE_LICENSE_MAX_MANUAL_TEST_CASES + " manual tests",
                        "To be able to create more tests, please contact t-Triage Support for a full commercial license.",
                        0,
                        userService.getAllAdminUser());
            }
        }

        return true;
    }

    public boolean validateTestTriaged() {
        return validateTestTriaged(1) > 0;
    }

    public Integer validateTestTriaged(int testsToAdd) {

        boolean free = validateLicenceType();
        Long currentsTestTriage = testTriageService.countByEnabledToday();

        if (free) {
            if ((currentsTestTriage + testsToAdd) > DEFAULT_FREE_LICENSE_MAX_TEST_TRIAGE)
                testsToAdd = DEFAULT_FREE_LICENSE_MAX_TEST_TRIAGE >= currentsTestTriage ? DEFAULT_FREE_LICENSE_MAX_TEST_TRIAGE - currentsTestTriage.intValue() : 0;
            if ((currentsTestTriage + testsToAdd) == DEFAULT_FREE_LICENSE_MAX_TEST_TRIAGE && testsToAdd > 0)
                notificationService.createNotification("You have reached the limit of " + DEFAULT_FREE_LICENSE_MAX_TEST_TRIAGE + " triaged tests",
                        "To be able to triage more tests, please contact t-Triage Support for a full commercial license.",
                        0,
                        userService.getAllAdminUser());
        }

        return testsToAdd;

    }

    private static String[] getFreeInstances() {
        return new String[] {"localhost", "clarolab", "act-on", "khoros"};
    }


}