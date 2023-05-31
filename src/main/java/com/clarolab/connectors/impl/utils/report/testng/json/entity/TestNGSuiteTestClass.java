/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.testng.json.entity;

import com.clarolab.model.types.StatusType;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class TestNGSuiteTestClass {

    private String name;

    @Builder.Default
    private List<TestNGSuiteTestClassTestMethod> test_method = Lists.newArrayList();

    public long skippedByAssumtion(){
        return this.test_method.stream().filter(element -> element.isSkippedByAssumption()).count();
    }

    public TestNGSuiteTestClassTestMethod getFailedConfig(){
        return this.test_method.stream().filter(test -> test.is_config() && test.getStatus().equals(StatusType.FAIL)).findFirst().orElse(null);
    }

}
