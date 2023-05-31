/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.manual.types;

import lombok.extern.java.Log;

import java.util.Arrays;
import java.util.List;

@Log
public enum AutomationStatusType {

    PENDING_LOW(0),
    PENDING_MEDIUM(1),
    PENDING_HIGH(2),
    PENDING_MUST(3),

    DIFFICULT(4),

    NO(5),

    DONE(6),

    FAILING(7),

    UNDEFINED(8);

    private final int type;

    AutomationStatusType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }


    public static List<AutomationStatusType> getPendingToAutomate() {
        return Arrays.asList(AutomationStatusType.PENDING_LOW, AutomationStatusType.PENDING_MEDIUM, AutomationStatusType.PENDING_HIGH, AutomationStatusType.PENDING_MUST);
    }

}
