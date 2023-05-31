/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import com.clarolab.util.DateUtils;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_TEST_EXECUTION_STEP;

@Entity
@Table(name = TABLE_TEST_EXECUTION_STEP, indexes = {
        @Index(name = "IDX_TESTEXECUTION_NAME", columnList = "name"),
        @Index(name = "IDX_TESTEXECUTION_ORDER", columnList = "stepOrder"),
        @Index(name = "IDX_TESTEXECUTION_STEP_CLEAN", columnList = "timestamp"),
        @Index(name = "IDX_TESTEXECUTION_EXECUTION", columnList = "testExecution_id")
})

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TestExecutionStep extends Entry {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "testExecution_id")
    private TestExecution testExecution;

    @Type(type = "org.hibernate.type.TextType")
    private String name;
    @Type(type = "org.hibernate.type.TextType")
    private String parameters;
    @Type(type = "org.hibernate.type.TextType")
    private String output;

    protected int stepOrder;

    @Type(type = "org.hibernate.type.TextType")
    private String screenshotURL;

    @Type(type = "org.hibernate.type.TextType")
    private String videoURL;

    @Builder
    private TestExecutionStep(String name, String parameters, String output, TestExecution testExecution, int stepOrder, String screenshotURL, String videoURL) {
        this.name = name;
        this.parameters = parameters;
        this.output = output;
        this.testExecution = testExecution;
        this.stepOrder = stepOrder;
        this.screenshotURL = screenshotURL;
        this.videoURL = videoURL;
        this.setTimestamp(DateUtils.now());
        this.setUpdated(this.getTimestamp());
    }



}
