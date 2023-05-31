/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.circleCI.model.types;

public enum CircleCIStatusType {

    SUCCESS("success"),
    FAILED("failed"),
    RETRIED("retried"),
    IT_FAIL("infrastructure_fail"),
    CANCELED("canceled"),
    TIME_OUT("timedout"),
    RUNNING("running"),
    QUEUED("queued"),
    SCHEDULED("scheduled"),
    NOT_RUNNING("not_running"),
    NO_TESTS("no_tests"),
    FIXED("fixed"),
    UNKNOWN("unknown");

    private final String status;

    CircleCIStatusType(String status) {
        this.status = status;
    }

    public static CircleCIStatusType getStatus(String status){
        switch (status){
            case "success":
                return CircleCIStatusType.SUCCESS;
            case "failed":
                return CircleCIStatusType.FAILED;
            case "retried":
                return CircleCIStatusType.RETRIED;
            case "infrastructure_fail":
                return CircleCIStatusType.IT_FAIL;
            case "canceled":
                return CircleCIStatusType.CANCELED;
            case "timedout":
                return CircleCIStatusType.TIME_OUT;
            case "running":
                return CircleCIStatusType.RUNNING;
            case "queued":
                return CircleCIStatusType.QUEUED;
            case "scheduled":
                return CircleCIStatusType.SCHEDULED;
            case "not_running":
                return CircleCIStatusType.NOT_RUNNING;
            case "no_tests":
                return CircleCIStatusType.NO_TESTS;
            case "fixed":
                return CircleCIStatusType.FIXED;
             default:
                return CircleCIStatusType.UNKNOWN;
        }
    }

}
