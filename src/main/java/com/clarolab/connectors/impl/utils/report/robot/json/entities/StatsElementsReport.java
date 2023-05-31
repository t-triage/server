/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.robot.json.entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatsElementsReport {

    private String id;
    private int fail;
    private int pass;
    private String name;
    private String content;
    private String doc;
    private String links;
    private String combined;
    private String info;

}
