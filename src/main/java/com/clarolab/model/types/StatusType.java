/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.types;

import com.clarolab.entities.JenkinsBuildResult;
import com.clarolab.entities.JenkinsTestCase;
import lombok.extern.java.Log;

import static com.clarolab.util.StringUtils.getSystemError;

@Log
public enum StatusType {

    SUCCESS(0),

    PASS(1),

    FAIL(2),

    SKIP(3),

    ABORTED(4),

    CANCELLED(5),

    //Regression means a test that passed before, but failed in last build execution
    REGRESSION(6),

    UNKNOWN(7),

    BUILDING(8),

    UNSTABLE(9),

    NOT_BUILT(10),

    REBUILDING(11),

    INFRASTRUCTURE_FAIL(12),

    TIME_OUT(13),

    QUEUED(14),

    SCHEDULED(15),

    NO_TESTS(16),

    FIXED(17),

    CANCELED(18),

    //Exclusively for Allure report:: BROKEN gets set for unexpected exceptions if for example locator is bad, but test should be FAILED if absence of element is a bug.
    BROKEN(19);

    private final int statusType;

    StatusType(int statusType) {
        this.statusType = statusType;
    }

    public int getStatusType() {
        return this.statusType;
    }

    public static StatusType getTestCaseStatus(JenkinsTestCase testCase) {
        if (testCase.isSkipped()) {
            return StatusType.SKIP;
        }
        switch (testCase.getStatus().toLowerCase()) {
            case "failed":
                return StatusType.FAIL;
            case "passed":
                return StatusType.PASS;
            case "skipped":
                return StatusType.SKIP;
            case "regression":
                return StatusType.REGRESSION;
            case "fixed":
                return StatusType.FIXED;
            default:
                log.severe(getSystemError("Unable to process test status " + testCase.getStatus()));
                return StatusType.UNKNOWN;
        }
    }

    public static String getStatus(StatusType status) {
        switch (status) {
            case FAIL:
                return "Failed";
            case PASS:
                return "Pass";
            case FIXED:
                return "Fixed";
            case SKIP:
                return "Skip";
            case REGRESSION:
                return "Regression";
            case SUCCESS:
                return "Success";
            case ABORTED:
                return "Aborted";
            case CANCELLED:
            case CANCELED:
                return "Canceled";
            case BROKEN:
                return "Broken";
            case UNSTABLE:
                return "Unstable";
            case UNKNOWN:
                return "Unknown";
            default:
                log.severe(getSystemError("Unable to process status " + status));
                return "Unknown";
        }
    }

    public static StatusType getStatus(String status) {
        switch (status.toUpperCase()) {
            case "PASSED":
            case "PASS":
            case "SUCCESS":
            case "SUCCESSFUL":
                return StatusType.PASS;
            case "FIXED":
                return StatusType.FIXED;
            case "FAILED":
            case "FAIL":
            case "FAILURE":
            case "UNSTABLE":
            case "UNKNOWN":
            case "CANCELED":
                return StatusType.FAIL;
            case "SKIPPED":
            case "SKIP":
                return StatusType.SKIP;
            case "RUNNING":
                return StatusType.BUILDING;
            case "BROKEN":
                return StatusType.BROKEN;
            case "ABORTED":
                return StatusType.ABORTED;
            default:
                log.severe(getSystemError("Unable to process status " + status));
                return StatusType.UNKNOWN;
        }
    }

    public static StatusType getStatus(JenkinsBuildResult result) {
        if (result == null) {
            return StatusType.UNKNOWN;
        }

        switch (result) {
            case ABORTED:
                return StatusType.ABORTED;
            case FAILURE:
                return StatusType.FAIL;
            case SUCCESS:
                return StatusType.SUCCESS;
            case CANCELLED:
                return StatusType.CANCELLED;
            case UNKNOWN:
                return StatusType.UNKNOWN;
            case BUILDING:
                return StatusType.BUILDING;
            case UNSTABLE:
                return StatusType.UNSTABLE;
            case NOT_BUILT:
                return StatusType.NOT_BUILT;
            case REBUILDING:
                return StatusType.REBUILDING;
            default:
                log.severe(getSystemError("Unable to process build result status " + result));
                return StatusType.UNKNOWN;
        }
    }

}
