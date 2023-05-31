/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.startup;

import com.clarolab.model.Entry;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;

import static com.clarolab.util.Constants.TABLE_LICENSE;

@Entity
@Table(name = TABLE_LICENSE, indexes = {
        @Index(name = "IDX_LICENSE_CREATION_TIME", columnList = "creationTime"),
        @Index(name = "IDX_LICENSE_EXPIRATION_TIME", columnList = "expirationTime"),

})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class License extends Entry<License> implements Serializable {

    //default serialVersion id
    private static final long serialVersionUID = 6401510061996550764L;

    private long creationTime;
    private long expirationTime;
    private boolean free;
    private boolean expired;
    @Type(type = "org.hibernate.type.TextType")
    private String licenseCode;

    @Builder
    public License(Long id, boolean enabled, long updated, long timestamp, long creationTime, long expirationTime,boolean free, String licenseCode, boolean expired) {
        super(id, enabled, updated, timestamp);
        this.creationTime = creationTime;
        this.expirationTime = expirationTime;
        this.free = free;
        this.licenseCode = licenseCode;
        this.expired = expired;
    }
}
