/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.types;

public enum UserFixPriorityType {

    AUTOMATIC(0),

    HIGH(1),

    MEDIUM(2),

    LOW(3),

    UNDEFINED(4),

    BLOCKER(5);

    private final int userPriority;

    UserFixPriorityType(int userPriority) {
        this.userPriority = userPriority;
    }

    public int getUserPriority() {
        return userPriority;
    }



}
