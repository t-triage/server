/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import static com.clarolab.util.Constants.TABLE_PROPERTY;

@Entity
@Table(name = TABLE_PROPERTY, indexes = {
        @Index(name = "IDX_PROPERTY_NAME", columnList = "name", unique = true)
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Property extends Entry {

    @Column(nullable = false)
    String name;

    //Short and Generic values
    @Column(nullable = false)
    String value;

    //Used in cases where the value is a long text... in T&C for example
    @Type(type = "org.hibernate.type.TextType")
    String longValue;

    @Column(nullable = false)
    boolean useLongValue;

    @Column(nullable = false)
    boolean hidden;

    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @Builder
    private Property(Long id, boolean enabled, long updated, long timestamp, String name, String value, String longValue, boolean useLongValue, boolean hidden, String description) {
        super(id, enabled, updated, timestamp);
        this.name = name;
        this.value = value;
        this.longValue = longValue;
        this.useLongValue = useLongValue;
        this.hidden = hidden;
        this.description = description;
    }
}
