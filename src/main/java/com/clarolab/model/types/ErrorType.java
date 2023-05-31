/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.types;

import lombok.Getter;

@Getter
public enum ErrorType {

    EQUAL(0), // Errors are exactly the same than the previous one

    VERY_SIMILAR(1), // Errors very similar than the previous one

    SIMILAR(2), // Errors are similar than the previous one

    NOT_EQUAL(3), // Errors are nt equal than the previous one

    NO_ERROR(4), // There is not error here

    UNDEFINED(99); // Errors undefined

    private final int errorType;

    ErrorType(int errorType) {
        this.errorType = errorType;
    }

    public boolean shouldBeTriaged(ErrorType type){
        return type.equals(EQUAL);
    }


}
