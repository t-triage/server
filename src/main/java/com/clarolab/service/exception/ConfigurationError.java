/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service.exception;

public class ConfigurationError extends ServiceException {

    public ConfigurationError(String message) {
        super(message);
    }

    public ConfigurationError(Exception e) {
        super(e);
    }
}
