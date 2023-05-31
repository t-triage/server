/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.detail;

import com.clarolab.model.Entry;
import com.clarolab.model.TestExecution;
import com.clarolab.model.TestTriage;
import com.clarolab.model.types.ApplicationFailType;
import com.clarolab.model.types.TestFailType;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_ERROR_DETAIL;

@Entity
@Table(name = TABLE_ERROR_DETAIL, indexes = {
        @Index(name = "IDX_ERROR_DETAIL_TYPE", columnList = "exceptionType")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetail extends Entry {

    private String exceptionType;

    @Type(type = "org.hibernate.type.TextType")
    private String message;

    @Type(type = "org.hibernate.type.TextType")
    private String causedBy;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "testexecution_id")
    private TestExecution testExecution;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "previoustesttriage_id")
    private TestTriage previousTestTriage;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private ApplicationFailType applicationFailType;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private TestFailType testFailType;

    @Builder
    private ErrorDetail(Long id, boolean enabled, long updated, long timestamp, String exceptionType, String message, String causedBy, TestExecution testExecution, TestTriage previousTestTriage) {
        super(id, enabled, updated, timestamp);
        this.exceptionType = exceptionType;
        this.message = message;
        this.causedBy = causedBy;
        this.testExecution = testExecution;
        this.previousTestTriage = previousTestTriage;

        if(previousTestTriage!=null) {
            this.applicationFailType = previousTestTriage.getApplicationFailType();
            this.testFailType = previousTestTriage.getTestFailType();
        }
    }
}
