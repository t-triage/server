/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service.exception;

import lombok.extern.java.Log;

import java.util.logging.Level;

@Log
public abstract class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
        log.log(Level.SEVERE, message);
    }

    public ServiceException(Exception e) {
        super(e.getMessage());
        log.log(Level.SEVERE, e.getLocalizedMessage(), e);
    }

    public ServiceException(String message, Exception e) {
        super(message);
        log.log(Level.SEVERE, e.getLocalizedMessage(), e);
    }
}
