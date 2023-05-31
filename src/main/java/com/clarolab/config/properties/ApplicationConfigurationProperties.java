/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.configuration")
public class ApplicationConfigurationProperties {

    private String url;
    private Boolean internalUsersEnabled = true;

    public String getUrl() {
        return url;
    }

    public void setUrl(String value) {
        this.url = value;
    }

    public boolean isInternalUsersEnabled() {
        return internalUsersEnabled;
    }

    public void setInternalUsersEnabled(Boolean internalUsersEnabled) {
        this.internalUsersEnabled = internalUsersEnabled;
    }
}
