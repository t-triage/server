/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.robot.json.entities.types;

public enum SuiteStatusResultType {

    FAIL("FAIL"),
    PASS("PASS");

    private final String status;

    SuiteStatusResultType(String status) {
        this.status = status;
    }

}
