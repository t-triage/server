/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service.exception;

public class BadRequestServiceException extends ServiceException {

    public BadRequestServiceException(String message) {
        super(message);
    }

    public BadRequestServiceException(Exception e) {
        super(e);
    }
}
