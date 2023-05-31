/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.onboard;

public enum GuideAnswer {

    PENDING(0),
    DISMISS(1),
    ACCEPTED(2);

    private final int type;

    GuideAnswer(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static GuideAnswer withType(int value) {
        switch (value) {
            case 0:
                return PENDING;
            case 1:
                return DISMISS;
            case 2:
                return ACCEPTED;
            default:
                return null;
        }
    }

    public boolean wasAnswered() {
        return PENDING != this;
    }

}
