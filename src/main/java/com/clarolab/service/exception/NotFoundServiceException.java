/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service.exception;

public class NotFoundServiceException extends ServiceException {

    public NotFoundServiceException(String message) {
        super(message);
    }

    public NotFoundServiceException(Exception e) {
        super(e);
    }

    public NotFoundServiceException(String message, Exception e) {
        super(message, e);
    }
}
