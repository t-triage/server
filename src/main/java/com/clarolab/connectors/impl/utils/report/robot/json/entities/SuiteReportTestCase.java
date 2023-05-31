/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.robot.json.entities;

import com.clarolab.connectors.impl.utils.report.robot.json.entities.types.SuiteStatusResultType;
import com.clarolab.model.TestExecutionStep;
import com.clarolab.model.types.StatusType;
import com.clarolab.util.DateUtils;
import com.clarolab.util.LogicalCondition;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Data
@Builder
public class SuiteReportTestCase {

    private String name;
    private String doc;
    private String id;
    private String timeout;
    private List<SuiteReportKeywordData> kw;
    private SuiteStatusReport status;
//    private SuiteReportTag tags;

    public long getDuration() {
        return DateUtils.convertDate(status.getEndtime()) - DateUtils.convertDate(status.getStarttime());
    }

    public StatusType getStatus() {
        return this.convertStatus(this.status.getStatus());
    }

    public String getFailReason() {
        if (isFailNoNeeded())
            return null;

        return this.status.getContent();
    }

    public String getFailReasonDetail() {
        if (isFailNoNeeded())
            return null;

        //Something failed at SuiteSetup, so test was not executed and keywords were not generated
        if (LogicalCondition.NOT(CollectionUtils.isNotEmpty(kw)))
            return this.status.getContent();

        SuiteReportKeywordData fail = kw.stream().filter(k -> k.hasFailure())
                .findFirst()
                .orElse(null);

        String reason = fail != null ? fail.getFail() : null;
        return reason;
    }

    public List<TestExecutionStep> getSteps(){
        List<TestExecutionStep> steps = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(kw))
            kw.forEach(kyw -> steps.add(kyw.getStep()));
        return steps;
    }

    private boolean isFailNoNeeded() {
        return LogicalCondition.OR(this.getStatus().equals(StatusType.PASS), this.getStatus().equals(StatusType.SUCCESS));
    }

    private StatusType convertStatus(SuiteStatusResultType status) {
        switch (status) {
            case FAIL:
                return StatusType.FAIL;
            case PASS:
                return StatusType.PASS;
            default:
                return StatusType.UNKNOWN;
        }
    }
}
