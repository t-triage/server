/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.populate;

import com.clarolab.model.EnvironmentVar;
import com.clarolab.model.Property;
import com.clarolab.model.helper.ResourcesHelper;
import com.clarolab.service.ApplicationDomainService;
import com.clarolab.service.PropertyService;
import com.clarolab.service.exception.InvalidDataException;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static com.clarolab.util.Constants.*;

@Component
@Log
public class PopulateSystemProperties {

    @Autowired
    private EnvironmentVar environmentVar;

    @Autowired
    PopulateDemoData populate;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    ApplicationDomainService applicationDomainService;

    private static List<Property> properties = new ArrayList<>(5);

    public void run(ApplicationArguments args) throws Exception {
        populateSystemProperties();
    }

    public void populateSystemProperties() {
        if (properties.size() > 0) {
            return;
        }
        if(populate.shouldPopulateSQL()){
            // Does nothing since the properties were added in the sql
            return;
        }
        if (!environmentVar.isTest()) {
            initProperties();
        }
        printConfig();
    }
    
    // Public method only for test
    public void initProperties() {
        initializeProperties();
        initializeOtherConfig();
    }

    private void initializeProperties() {
        Property property;

        property = DataProvider.getProperty();
        property.setName(MAX_TESTCASES_TO_PROCESS);
        property.setDescription("Number of previous test cases to compare");
        property.setValue(String.valueOf(DEFAULT_MAX_TESTCASES_TO_PROCESS));
        properties.add(saveProperty(property));

        property = DataProvider.getProperty();
        property.setName(SAVED_TIME_MIN);
        property.setDescription("Minutes in saved time for each automated triage");
        property.setValue(String.valueOf(DEFAULT_SAVED_TIME_MIN));
        properties.add(saveProperty(property));

        property = DataProvider.getProperty();
        property.setName(CONSECUTIVE_PASS_COUNT);
        property.setDescription("Number of consecutive pass counts in order to consider it stable");
        property.setValue(String.valueOf(DEFAULT_CONSECUTIVE_PASS_COUNT));
        properties.add(saveProperty(property));

        property = DataProvider.getProperty();
        property.setName(DAYS_TO_EXPIRE_TRIAGE);
        property.setDescription("If the suite was not triaged in X days it will be expired and assumed it wont be triaged this time");
        property.setValue(String.valueOf(DEFAULT_DAYS_TO_EXPIRE_TRIAGE));
        properties.add(saveProperty(property));

        property = DataProvider.getProperty();
        property.setName(PREVIOUS_FAIL_COUNT);
        property.setDescription("Number of fail counts before considering it a permanent failure");
        property.setValue(String.valueOf(DEFAULT_PREVIOUS_FAIL_COUNT));
        properties.add(saveProperty(property));

        property = DataProvider.getProperty();
        property.setName(MAX_EVENTS_TO_PROCESS);
        property.setDescription("Events: How many events are processed per iteration");
        property.setValue(String.valueOf(DEFAULT_MAX_EVENTS_TO_PROCESS));
        properties.add(saveProperty(property));

        property = DataProvider.getProperty();
        property.setName(OLD_EVENTS_TO_DELETE_DAYS);
        property.setDescription("Events: Cleaning up events that are N days old in the system");
        property.setValue(String.valueOf(DEFAULT_OLD_EVENTS_TO_DELETE_DAYS));
        properties.add(saveProperty(property));

        property = DataProvider.getProperty();
        property.setName(MAX_TESTCASES_PER_DAY);
        property.setDescription("Average number of test cases that are triaged a day per suite. It is useful to estimate daily tasks.");
        property.setValue(String.valueOf(DEFAULT_MAX_TESTCASES_PER_DAY));
        properties.add(saveProperty(property));

        property = DataProvider.getProperty();
        property.setName(PREVIOUS_DAYS_VALID_INFO);
        property.setDescription("Number of past days where the info worth being displaying as tags at the Kanban. Default 7 days.");
        property.setValue(String.valueOf(DEFAULT_PREVIOUS_DAYS_VALID_INFO));
        properties.add(saveProperty(property));

        property = DataProvider.getProperty();
        property.setName(GDPR_ENABLED);
        property.setDescription("Enable options to keep GDPR compatibility like cookie banner.");
        property.setValue(String.valueOf(DEFAULT_GDPR_ENABLED));
        properties.add(saveProperty(property));

        property = DataProvider.getProperty();
        property.setName(TERM_AND_CONDITIONS);
        property.setHidden(true);
        property.setDescription("Set the default Terms and Conditions for new users-");
        property.setValue(DEFAULT_TandC_FILE);
        property.setLongValue(ResourcesHelper.getDefaulTermAndCondition(DEFAULT_TandC_FILE));
        property.setUseLongValue(true);
        properties.add(saveProperty(property));

        property = DataProvider.getProperty();
        property.setName(TRIAGE_HELP);
        property.setHidden(true);
        property.setDescription("Set the default help for the test triage actions (Triage > Kanban > Test > Help)");
        property.setValue(DEFAULT_TriageHelp_FILE);
        property.setLongValue(ResourcesHelper.getDefaulTermAndCondition(DEFAULT_TriageHelp_FILE));
        property.setUseLongValue(true);
        properties.add(saveProperty(property));

        property = DataProvider.getProperty();
        property.setName(GOOGLE_ANALYTICS_UA);
        property.setDescription("Set the default Google Analytics Tracking code. I.e: UA-XXXXX-XX");
        property.setValue(DEFAULT_GOOGLE_ANALYTICS_UA);
        properties.add(saveProperty(property));

        property = DataProvider.getProperty();
        property.setName(INTERNAL_LOGIN_ENABLED);
        property.setDescription("Enable / Disable internal user registration and login.");
        property.setValue(String.valueOf(DEFAULT_INTERNAL_LOGIN_ENABLED));
        properties.add(saveProperty(property));

        property = DataProvider.getProperty();
        property.setName(FEATURE_MANUAL_TEST_ENABLED);
        property.setDescription("Enable / Disable the feature with manual tests.");
        property.setValue(String.valueOf(DEFAULT_FEATURE_MANUAL_TEST_ENABLED));
        if (!environmentVar.isProduction()) {
            property.setValue(String.valueOf(true));
        }
        properties.add(saveProperty(property));

        property = DataProvider.getProperty();
        property.setName(SLACK_ENABLED);
        property.setDescription("If the slack integration is enabled");
        property.setValue(String.valueOf(DEFAULT_SLACK_ENABLED));
        if (!environmentVar.isProduction()) {
            property.setValue(String.valueOf(false));
        }
        properties.add(saveProperty(property));

        if (!environmentVar.isProduction()) {
            property = DataProvider.getProperty();
            property.setName("DEBUG_STEP_PROCESS");
            property.setDescription("For debugging purposes, Pull imports and old build and stops");
            property.setValue(String.valueOf(DEBUG_STEP_PROCESS));
            properties.add(saveProperty(property));
        }

        /**
        property = DataProvider.getProperty();
        property.setName(RULE_ENGINE_ON);
        property.setDescription("Turning false t-Triage will stop generating automated triages.");
        property.setValue(String.valueOf(DEFAULT_RULE_ENGINE_ON));
        properties.add(saveProperty(property));
         **/

    }

    private Property saveProperty(Property property) {
        try {
            return propertyService.save(property);
        } catch (InvalidDataException ex) {
            log.log(Level.WARNING, "Error saving a property");
        }
        return property;

    }

    private void initializeOtherConfig() {
        if (environmentVar.isProduction()) {
            applicationDomainService.initializeDefaultBlacklist();
        }
    }


    private void printConfig() {

        log.info(String.format("Environment: app.url", environmentVar.getURL()));
        log.info(String.format("Environment: app.back.url", environmentVar.getBackendURL()));
        log.info(String.format("Environment: db.url", environmentVar.getDBUrl()));
        log.info(String.format("Environment: db.username", environmentVar.getDBUsername()));
        log.info(String.format("Environment: db.driver", environmentVar.getDBDriverClassName()));

    }

}
