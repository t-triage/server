/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 *
 * This is the actual test case that should be tested manually.
 * It also works as input to create a new automation test (that once is created it is associated to automatedTestCase).
 * Contains lot of fields that classify the test so it can be searched by that.
 *
 */

package com.clarolab.model.component;

import com.clarolab.model.Entry;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

import static com.clarolab.util.Constants.TABLE_AUTOMATED_COMPONENT;

@Entity
@Table(name = TABLE_AUTOMATED_COMPONENT, indexes = {
        @Index(name = "IDX_AUTOMATED_COMPONENT_ENABLED", columnList = "enabled")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AutomatedComponent extends Entry<AutomatedComponent> {

    @OneToMany(mappedBy = "component", fetch = FetchType.LAZY)
    private List<TestComponentRelation> testComponentRelations;

    @Type(type = "org.hibernate.type.TextType")
    private String name;

    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @Builder
    private AutomatedComponent(Long id, boolean enabled, long updated, long timestamp, String name, String description) {
        super(id, enabled, updated, timestamp);
        this.name = name;
        this.description = description;
    }
}
