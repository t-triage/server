/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 *
 * Requirement of a test case
 */

package com.clarolab.model.manual.instruction;

import com.clarolab.model.Entry;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import static com.clarolab.util.Constants.TABLE_MANUAL_TEST_REQUIREMENT;

@Entity
@Table(name = TABLE_MANUAL_TEST_REQUIREMENT, indexes = {
        @Index(name = "IDX_MANUAL_TEST_REQUIREMENT_NAME", columnList = "name")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ManualTestRequirement extends Entry {

    @Type(type = "org.hibernate.type.TextType")
    protected String name;

    @Builder
    private ManualTestRequirement(Long id, boolean enabled, long updated, long timestamp, String name) {
        super(id, enabled, updated, timestamp);
        this.name = name;
    }
}
