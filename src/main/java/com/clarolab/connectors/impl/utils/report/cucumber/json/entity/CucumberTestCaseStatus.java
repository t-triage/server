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
public class CucumberTestCaseStatus {

    private StatusType status;
    private String error;

}
