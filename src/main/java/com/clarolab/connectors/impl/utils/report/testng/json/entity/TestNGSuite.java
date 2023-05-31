/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.testng.json.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class TestNGSuite extends TestNGBase {

    private List<TestNGSuiteTest> tests;

    @Builder
    private TestNGSuite(String name, String started_at, String finished_at, long duration_ms, List<TestNGSuiteTest> tests){
        super(name, started_at, finished_at, duration_ms);
        this.tests = tests;
    }

    String getId(){
        return this.getName().replace(" ", "_")+this.hashCode();
    }

}
