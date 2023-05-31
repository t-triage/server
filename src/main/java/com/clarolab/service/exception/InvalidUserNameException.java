/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service.exception;

public class InvalidUserNameException extends ServiceException {

    public InvalidUserNameException(String message) {
        super(message);
    }

    public InvalidUserNameException(Exception e) {
        super(e);
    }

}
