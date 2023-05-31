/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.robot.json.entities.types;

public enum SuiteMessageType {

    FAIL("FAIL"),
    DEBUG("DEBUG"),
    INFO("INFO"),
    TRACE("TRACE"),
    WARN("WARN");

    private final String level;

    SuiteMessageType(String level) {
        this.level = level;
    }

    public static SuiteMessageType getStatus(String status) {
        switch (status) {
            case "FAIL":
                return SuiteMessageType.FAIL;
            case "DEBUG":
                return SuiteMessageType.DEBUG;
            case "INFO":
                return SuiteMessageType.INFO;
            case "TRACE":
                return SuiteMessageType.TRACE;
            case "WARN":
                return SuiteMessageType.WARN;
            default:
                return null;
        }
    }
}
