/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.onboard;

public enum GuideType {

    TOOLTIP_TEXT(0),
    TOOLTIP_VIDEO(1),
    TOOLTIP_IMAGE(2),
    SURVEY(3);

    private final int type;

    GuideType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }



}
