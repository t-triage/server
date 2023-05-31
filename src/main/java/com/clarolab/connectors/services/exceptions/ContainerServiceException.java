/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.services.exceptions;

import com.google.common.base.Throwables;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.logging.Level;

@Log
public class ContainerServiceException extends IOException {

    public ContainerServiceException(Exception e) {
        super(e);
        log.log(Level.SEVERE, e.getLocalizedMessage());
        log.log(Level.SEVERE, Throwables.getStackTraceAsString(e));
    }

    public ContainerServiceException(String message) {
        super(message);
        log.log(Level.SEVERE, message);
    }

    public ContainerServiceException(String message, Exception e) {
        super(e);
        log.log(Level.SEVERE, message, e);
        //log.log(Level.SEVERE, Throwables.getStackTraceAsString(e));
    }
}