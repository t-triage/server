/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.robot.json.entities;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SuiteReportMessage {

    private List<SuiteReportMessageElement> msg;
}
