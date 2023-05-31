/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.testng.json.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TestNGSuiteTestClassTestMethodException {

    private String full_stacktrace;
    private String message;
    private String clazz;

}
