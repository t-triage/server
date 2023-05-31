/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.robot.json.entities;

import com.clarolab.connectors.impl.utils.report.robot.json.entities.types.SuiteStatusResultType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuiteStatusReport {

    private String content;
    private String critical;
    private String endtime;
    private String starttime;
    private String elapsedtime;
    private SuiteStatusResultType status;

    public boolean isFailure() {
        return this.getStatus().equals(SuiteStatusResultType.FAIL);
    }

}
