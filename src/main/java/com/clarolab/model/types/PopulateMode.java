/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.types;

public enum PopulateMode {

    PULL(0),// Used when the data is pulled from the app via Scheduler
    PUSH(1), // Used when the data is pushed form CI
    ALL(2),
    UPLOAD(3),

     // Used when the data is pushed or/and pulled form CI

    UNDEFINED(-1);


    private final int popuplateMode;

    PopulateMode(int popuplateMode) {
        this.popuplateMode = popuplateMode;
    }

    public int getPopuplateMode() {
        return popuplateMode;
    }


}
