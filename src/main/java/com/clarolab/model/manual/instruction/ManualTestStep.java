/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.manual.instruction;

import com.clarolab.model.Entry;
import com.clarolab.model.manual.ManualTestCase;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_MANUAL_TEST_STEP;

@Entity
@Table(name = TABLE_MANUAL_TEST_STEP, indexes = {
        @Index(name = "IDX_MANUAL_TEST_STEP_ENABLED", columnList = "enabled"),
        @Index(name = "IDX_MANUAL_TEST_STEP_TEST_CASE", columnList = "testCase_id"),
        @Index(name = "IDX_MANUAL_TEST_STEP_ORDER", columnList = "testCase_id,stepOrder")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ManualTestStep extends Entry {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "testCase_id")
    protected ManualTestCase testCase;

    @Type(type = "org.hibernate.type.TextType")
    protected String step;

    @Type(type = "org.hibernate.type.TextType")
    protected String expectedResult;

    @Type(type = "org.hibernate.type.TextType")
    protected String data;

    protected int stepOrder;

    protected long externalId;


    @Builder
    private ManualTestStep(Long id, boolean enabled, long updated, long timestamp, ManualTestCase testCase, String step, String expectedResult, String data, int stepOrder, long externalId) {
        super(id, enabled, updated, timestamp);
        this.testCase = testCase;
        this.step = step;
        this.expectedResult = expectedResult;
        this.stepOrder = stepOrder;
        this.data = data;
        this.externalId = externalId;
    }

    public boolean equals(ManualTestStep manualTestStep) {
        if (this.getId() == manualTestStep.getId()) {
            return true;
        }
        return false;
    }
}
