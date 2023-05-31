/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.types;

import lombok.extern.java.Log;

import static com.clarolab.util.StringUtils.getSystemError;

@Log
public enum ReportType {

    UNKNOWN(0),
    ROBOT(1),
    TESTNG(2),
    JUNIT(3),
    CUCUMBER(4),
    ALLURE(5),
    PROTRACTOR(6){
        @Override
        public boolean allowsMultipleArtifacts() {
            return super.allowsMultipleArtifacts();
        }
    },
    PROTRACTOR_STEPS(7){
        @Override
        public boolean allowsMultipleArtifacts() {
            return false;
        }
    },
    CYPRESS(8),
    JEST(9),
    PYTHON(10);

    private final int reportType;

    ReportType(int reportType) {
        this.reportType = reportType;
    }

    public boolean allowsMultipleArtifacts() {
        return true;
    }

    public int getReportType() {
        return this.reportType;
    }

    public static String getType(ReportType type){
        if (type == null) {
            log.severe(getSystemError("Unable to process report type " + type));
            return "UNKNOWN";
        }
        return type.name();
    }

}
