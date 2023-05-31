/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.manual.types;

public enum ExecutionStatusType {

    UNDEFINED(0),
    IN_PROGRESS(2),
    PENDING(3),
    BLOCKED(4),
    FAIL(5),
    PASS(6),
    NO(7);

    private final int type;

    ExecutionStatusType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }



}
