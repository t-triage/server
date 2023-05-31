/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;

import static com.clarolab.util.Constants.DEFAULT_DATABASE_MAX_TIMELINE;
import static com.clarolab.util.Constants.DEFAULT_DATABASE_POOL;

@ConfigurationProperties(prefix = "spring.datasource")
public class DatasourceProperties {

    @NotBlank
    private String platform;
    @NotBlank
    private String url;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String driverClassName;
    @NotBlank
    private String initializationMode;
    @NotBlank
    private int maximumPoolSize;
    @NotBlank
    private int maximumLifetime;


    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getInitializationMode() {
        return initializationMode;
    }

    public void setInitializationMode(String initializationMode) {
        this.initializationMode = initializationMode;
    }

    public int getMaximumPoolSize() {
        if (maximumPoolSize <= 0) {
            return DEFAULT_DATABASE_POOL;
        }
        return maximumPoolSize;
    }

    public int getMaxLifetime() {
        if (maximumLifetime <= 0) {
            return DEFAULT_DATABASE_MAX_TIMELINE;
        }
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int value) {
        maximumPoolSize = value;
    }

    public void setMaximumLifetime(int maximumLifetime) {
        this.maximumLifetime = maximumLifetime;
    }
}
