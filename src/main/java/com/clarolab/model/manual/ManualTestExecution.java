/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 *
 * This is like the testExecution but for Manual test.
 * It is created when a test plan is requested
 * its workflow is followed by the status
 * the assignee is the actual user that is first assigned and then overwritten by the one that actualy tested
 */

package com.clarolab.model.manual;

import com.clarolab.model.Entry;
import com.clarolab.model.User;
import com.clarolab.model.manual.types.ExecutionStatusType;
import com.clarolab.util.DateUtils;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.clarolab.util.Constants.TABLE_MANUAL_TEST_EXECUTION;

@Entity
@Table(name = TABLE_MANUAL_TEST_EXECUTION, indexes = {
        @Index(name = "IDX_MANUAL_TEST_EXECUTION_TEST", columnList = "testCase_id"),
        @Index(name = "IDX_MANUAL_TEST_EXECUTION_PLAN", columnList = "testPlan_id"),
        @Index(name = "IDX_MANUAL_TEST_EXECUTION_STATUS", columnList = "status"),
        @Index(name = "IDX_MANUAL_TEST_EXECUTION_ASSIGN", columnList = "user_id"),
        @Index(name = "IDX_MANUAL_TEST_EXECUTION_ORDER", columnList = "executionOrder")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ManualTestExecution extends Entry implements Comparable<ManualTestExecution> {
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "testCase_id")
    private ManualTestCase testCase;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "testPlan_id")
    private ManualTestPlan testPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User assignee;

    // Data set after execution
    private ExecutionStatusType status;
    private String environment;
    @Type(type = "org.hibernate.type.TextType")
    private String comment;

    private int executionOrder;

    private Long lastExecutionTime;

    @Builder
    private ManualTestExecution(Long id, boolean enabled, long updated, long timestamp, ManualTestCase testCase, ManualTestPlan testPlan, User assignee, ExecutionStatusType status, String environment, String comment, int executionOrder, Long lastExecutionTime) {
        super(id, enabled, updated, timestamp);
        this.testCase = testCase;
        this.testPlan = testPlan;
        this.assignee = assignee;
        this.status = status;
        this.environment = environment;
        this.comment = comment;
        this.executionOrder = executionOrder;
        this.lastExecutionTime = lastExecutionTime;
    }

    public void setStatus(ExecutionStatusType status) {
        this.status = status;
        this.setLastExecutionTime(DateUtils.now());
        testPlan.setUpdated(DateUtils.now());  // used test plan - change its updated time
    }
    
    private String getComponentNames() {
        List<ProductComponent> componentList = testCase.getComponents();

        String names = componentList.stream()
                .map( component -> String.valueOf(component.getName()) )
                .collect( Collectors.joining( "," ) );
        
        return names;
    }

    private String getTestName() {
        return getTestCase().getName();
    }

    private int getStatusNumber() {
        if (status == null) {
            return 1;
        } else {
            return status.getType();
        }
    }

    public boolean isPass() {
        return getStatus() == ExecutionStatusType.PASS;
    }

    public boolean isFail() {
        return getStatus() == ExecutionStatusType.FAIL;
    }

    @Override
    // Compare by status and then name
    public int compareTo(ManualTestExecution other) {

        
        // return getTestCase().getName().compareTo(other.getTestCase().getName());
        return Comparator.comparing(ManualTestExecution::getStatusNumber)
                .thenComparing(ManualTestExecution::getComponentNames)
                .thenComparing(ManualTestExecution::getTestName)
                .compare(this, other);
    }
}
