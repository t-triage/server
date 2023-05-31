/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import static com.clarolab.util.Constants.TABLE_APPLICATION_DOMAIN;

@Entity
@Table(name = TABLE_APPLICATION_DOMAIN, indexes = {
        @Index(name = "IDX_APP_DOMAIN_DOMAIN_ENABLED", columnList = "domainName,enabled"),
        @Index(name = "IDX_APP_DOMAIN_ALLOWED_ENABLED", columnList = "allowed,enabled")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDomain extends Entry {
    private String domainName;
    private boolean allowed;

    @Builder
    private ApplicationDomain(Long id, boolean enabled, long updated, long timestamp, String domainName, boolean allowed) {
        super(id, enabled, updated, timestamp);
        this.domainName = domainName;
        this.allowed = allowed;
    }
}
