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
import com.clarolab.model.TestCase;
import lombok.*;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_TEST_COMPONENT_RELATION;

@Entity
@Table(name = TABLE_TEST_COMPONENT_RELATION, indexes = {
        @Index(name = "IDX_TEST_COMPONENT_COMPONENT", columnList = "component_id"),
        @Index(name = "IDX_TEST_COMPONENT_TEST", columnList = "testCase_id")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TestComponentRelation extends Entry<TestComponentRelation> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "testCase_id")
    private TestCase testCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_id")
    private AutomatedComponent component;
    
    private long probability;

    @Builder
    private TestComponentRelation(Long id, boolean enabled, long updated, long timestamp, TestCase testCase, AutomatedComponent component, long probability) {
        super(id, enabled, updated, timestamp);
        this.testCase = testCase;
        this.component = component;
        this.probability = probability;
    }
}
