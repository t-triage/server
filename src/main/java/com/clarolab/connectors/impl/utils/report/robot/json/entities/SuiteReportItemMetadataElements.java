/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.robot.json.entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuiteReportItemMetadataElements {

    private String name;
    private String content;

}
