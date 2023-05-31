/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.services.exceptions;

import lombok.extern.java.Log;

import java.io.IOException;
import java.util.logging.Level;

@Log
public class ExecutorServiceException extends IOException {

    public ExecutorServiceException(String message) {
        super(message);
        log.log(Level.SEVERE, message);
    }

    public ExecutorServiceException(String message, Exception e) {
        super(message);
        log.log(Level.SEVERE, message, e);
    }
}