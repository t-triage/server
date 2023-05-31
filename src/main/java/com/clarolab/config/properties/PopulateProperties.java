/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

@ConfigurationProperties(prefix = "app.populate")
public class PopulateProperties {

    private Boolean enable = false;
    private Integer amount = 0;
    private Integer monthAgo = 6;
    private Set<String> configuration = new HashSet<>();
    private String file;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Set<String> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Set<String> configuration) {
        this.configuration = configuration;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Integer getMonthAgo() {
        return monthAgo;
    }

    public void setMonthAgo(Integer monthAgo) {
        this.monthAgo = monthAgo;
    }
}
