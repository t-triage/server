/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.event.process;

public enum ApplicationEventType {

    UNKNOWN(0),
    BUILD_TRIAGE_GENERATED_FOR_EXECUTOR(1),
    TRIAGE_AGENT_EXECUTED(2),
    TEST_TRIAGED(3),
    BUILD_TRIAGE_GENERATED_FOR_CONTAINER(4),
    TIME_NEW_DAY(5),
    TIME_NEW_MONTH(6),
    BUILD_TRIAGED(7),
    ISSUE_REOPENED_AUTOMATICALLY(8),
    ISSUE_RESOLVED_AUTOMATICALLY(9),
    TIME_NEW_WEEK(10),
    AUTOMATION_TEST_CHANGED(11),
    TIME_NEW_TUESDAY(11);


    private final int eventType;

    ApplicationEventType(int eventType) {
        this.eventType = eventType;
    }

    public int getEventType() {
        return eventType;
    }
}
