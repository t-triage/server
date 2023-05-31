/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.testng.json.entity;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TestNGBase {

    private String name;
    private String started_at;
    private String finished_at;
    private long duration_ms;

}
