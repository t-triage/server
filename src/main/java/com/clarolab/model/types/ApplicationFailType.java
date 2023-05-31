/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.types;

public enum ApplicationFailType {

    /*The idea is to use those manually*/

    NO_FAIL(0),

    EXTERNAL_CAUSE(1),

    FEATURE_NOT_COMPLETED(2),

    FEATURE_IN_MAINTENANCE(3),

    WONT_FILE(5),

    FLAKY(6),

    FILED_TICKET(7),

    UNDEFINED(9);

    private final int failType;

    ApplicationFailType(int failType) {
        this.failType = failType;
    }

    public int getFailType() {
        return failType;
    }
}
