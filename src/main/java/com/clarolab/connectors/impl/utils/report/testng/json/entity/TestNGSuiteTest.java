/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.testng.json.entity;

import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Data
public class TestNGSuiteTest extends TestNGBase {

    private List<TestNGSuiteTestClass> clazz;

    @Builder
    private TestNGSuiteTest(String name, String started_at, String finished_at, long duration_ms, String groups, List<TestNGSuiteTestClass> clazz){
        super(name, started_at, finished_at, duration_ms);
        this.clazz = CollectionUtils.isNotEmpty(clazz) ? clazz : Lists.newArrayList();
    }
}
