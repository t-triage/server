/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.services.exceptions;

import lombok.extern.java.Log;

import java.io.IOException;
import java.util.logging.Level;

@Log
public class ConnectorServiceException extends IOException {

    public ConnectorServiceException(Exception e, String message) {
        super(e.getMessage());
        log.log(Level.SEVERE, message, e);
    }

    public ConnectorServiceException(Exception e) {
        super(e.getMessage());
    }

    public ConnectorServiceException(String message) {
        //super(message);
        log.log(Level.SEVERE, message);
    }
}