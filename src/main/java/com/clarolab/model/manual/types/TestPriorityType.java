/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.manual.types;

public enum TestPriorityType {

    AUTOMATIC(0),

    HIGH(1),

    MEDIUM(2),

    LOW(3),

    UNDEFINED(4);

    private final int priority;

    TestPriorityType(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
