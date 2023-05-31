/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import com.clarolab.config.properties.ApplicationConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

import static com.clarolab.util.Constants.DEFAULT_DATABASE_POOL;

@Component
public class EnvironmentVar {

    @Autowired
    private Environment environment;

    @Autowired
    private ApplicationConfigurationProperties applicationConfigurationProperties;

    public String getURL() {
        return applicationConfigurationProperties.getUrl();
    }

    public String getBackendURL() {
        return System.getenv("app.back.url");
    }

    public boolean isProduction() {
        return !(environment.acceptsProfiles(Profiles.of("dev")) || isTest());
    }

    public boolean isTest() {
        return environment.acceptsProfiles(Profiles.of("test"));
    }

    public String getDBDriverClassName() {
        return getValue("db.driver", true, "org.postgresql.Driver");
    }

    public String getDBUrl() {
        return getValue("db.url", false, null);
    }

    public String getDBUsername() {
        return getValue("db.username", true, "ttriage");
    }

    public String getDBPassword() {
        return getValue("db.password", false, null);
    }

    public int getMaximumPoolSize() {
        String value = getValue("db.maximumPoolSize", true, String.valueOf(DEFAULT_DATABASE_POOL));
        return Integer.parseInt(value);
    }

    public int getMaxLifetime() {
        String value = getValue("db.maximumLifetime", true, String.valueOf(DEFAULT_DATABASE_POOL));
        return Integer.parseInt(value);
    }


    private String getValue(String name, boolean always, String defaultVallue) {
        String value = System.getenv(name);
        if (value == null || value.isEmpty()) {
            if (always) {
                return defaultVallue;
            } else {
                return value;
            }
        } else {
            return value;
        }
    }
}
