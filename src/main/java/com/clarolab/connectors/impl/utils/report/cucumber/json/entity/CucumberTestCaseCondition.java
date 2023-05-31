/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.cucumber.json.entity;

import com.clarolab.model.types.StatusType;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

@Data
@Log
@Builder
public class CucumberTestCaseCondition {

    private String name;
    private long duration;
    private CucumberTestCaseStatus status;

    public boolean hasFailure(){
        return this.status.equals(StatusType.FAIL) || this.status.equals(StatusType.SKIP);
    }

    public String getError(){
        return this.name + " has a failure.";
    }

    public String getErrorCause(){
        return this.status.getError();
    }

}
