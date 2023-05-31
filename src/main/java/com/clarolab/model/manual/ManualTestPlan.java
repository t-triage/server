/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 *
 * This is a request to test. It is like a Test Run.
 * A plan basically is: tasks (ManualTestExecution), who (assignee), when (fromDate-toDate).
 */

package com.clarolab.model.manual;

import com.clarolab.model.Entry;
import com.clarolab.model.User;
import com.clarolab.model.manual.types.PlanStatusType;
import lombok.*;

import javax.persistence.*;

import static com.clarolab.util.Constants.TABLE_MANUAL_TEST_PLAN;

@Entity
@Table(name = TABLE_MANUAL_TEST_PLAN, indexes = {
        @Index(name = "IDX_MANUAL_TEST_PLAN_STATUS", columnList = "status"),
        @Index(name = "IDX_MANUAL_TEST_PLAN_WHEN", columnList = "status,fromDate,toDate")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ManualTestPlan extends Entry {
    private String name;
    private String description;
    private String environment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User assignee;

    private long fromDate;
    private long toDate;

    private PlanStatusType status;

    @Builder
    private ManualTestPlan(Long id, boolean enabled, long updated, long timestamp, String name, String description, String environment, User assignee, long fromDate, long toDate, PlanStatusType status) {
        super(id, enabled, updated, timestamp);
        this.name = name;
        this.description = description;
        this.environment = environment;
        this.assignee = assignee;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.status = status;
    }
}
