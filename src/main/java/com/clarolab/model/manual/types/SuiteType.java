/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.manual.types;

public enum SuiteType {

    BVT(0),

    SMOKE(1),

    REGRESSION(2),

    INTEGRATION(3),

    SANITY(4),

    UNDEFINED(5),

    FEATURE(6),
    
    RELEASE(7);

    private final int suite;

    SuiteType(int suite) {
        this.suite = suite;
    }

    public int getSuite() {
        return suite;
    }



}
