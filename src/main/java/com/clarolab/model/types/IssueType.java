/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.types;

import java.util.Arrays;
import java.util.List;

public enum IssueType {

    OPEN(2000, 20), // detected automatically that the test is failing.

    CONFIRMED(2000, 20), // market manually as an automation bug

    CRITICAL(3000, 50), // market manually as critical

    FIXED(4000, 0), //Fixed by dev

    ARCHIVED(5000, 0), //This wont be fixed ever

    REOPEN(7000, 100), //after it was fixed, it was reopen and track the amounts of reopen 1, 2, etc.

    PASSING(8000, 0), //It used to fail, now passing 1 2 3 times, may be previously fixed, may be confirmed

    UNDEFINED(9000, 90),

    WONT_FIX(10000, 20),

    BLOCKER(11000, 0);

    private final int stateType;
    private final int quantity;

    IssueType(int stateType, int amount) {
        this.stateType = stateType;
        this.quantity = amount;
    }

    public int getStateType() {
        return stateType;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isResolved() {
        return this == FIXED;
    }

    public boolean isOpen() {
        return this == OPEN;
    }

    public boolean isReOpen() {
        return this == REOPEN;
    }

    public boolean isPassing() {
        return this == PASSING;
    }

    public boolean isFixed() {
        return this == FIXED;
    }

    public boolean isWontFix() {
        return this == IssueType.WONT_FIX;
    }

    public boolean isBlocker() {
        return this == BLOCKER;
    }

    public static List<IssueType> getFixed() {
        return Arrays.asList(OPEN, REOPEN);
    }
}
