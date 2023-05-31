/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.services.exceptions;

import lombok.Data;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.logging.Level;

@Data
@Log
public class BuildServiceException extends IOException {

    private String executor;
    private int build;

    public BuildServiceException(String executor, int build) {
        super(String.format("[Error trying to get build #%s for executor %d]", executor, build));
        log.log(Level.SEVERE, String.format("[Error trying to get build for executor %s]", executor));
        this.executor = executor;
        this.build = build;
    }

    public BuildServiceException(String executor) {
        super(String.format("[Error trying to get build for executor %s]", executor));
        log.log(Level.SEVERE, String.format("[Error trying to get build for executor %s]", executor));
        this.executor = executor;
    }

    public BuildServiceException(Exception e) {
        super(e);
    }

    public BuildServiceException(String message, Exception e) {
        super(message);
        log.log(Level.SEVERE, message, e);
    }
}