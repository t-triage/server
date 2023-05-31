/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.types;

public enum TestFailType {

    /*The idea is to use those manually*/

    NO_FAIL(0),

    EXTERNAL_CAUSE(1),

    TEST_HAS_BUG(2),

    TEST_NEED_IMPROVEMENT(3),

    TEST_ASSIGNED_TO_FIX(4),

    WONT_FILE(5),

    FLAKY(6),

    UNDEFINED(9);

    private final int failType;

    TestFailType(int failType) {
        this.failType = failType;
    }

    public int getFailType() {
        return failType;
    }
}
