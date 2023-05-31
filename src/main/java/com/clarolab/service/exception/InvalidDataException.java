/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service.exception;

public class InvalidDataException extends ServiceException {

    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(Exception e) {
        super(e);
    }
}
