/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.jpa")
public class HibernateProperties {

    private Boolean showSql = false;

    public Boolean getShowSql() {
        return showSql;
    }

    public void setShowSql(Boolean showSql) {
        this.showSql = showSql;
    }
}
