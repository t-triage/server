/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.robot.json.entities;

import com.clarolab.connectors.impl.utils.report.robot.json.entities.types.SuiteMessageType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuiteReportMessageElement {

    private SuiteMessageType level;
    private String content;
    private String timestamp;

}
