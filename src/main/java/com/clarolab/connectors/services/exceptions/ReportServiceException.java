/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.services.exceptions;

import com.clarolab.model.types.ReportType;
import com.google.common.base.Throwables;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.logging.Level;

@Getter
@Setter
@Log
public class ReportServiceException extends IOException {

    private String reason;
    private ReportType type;
    private Exception exception;

    @Builder
    public ReportServiceException(String message, String reason, ReportType type, Exception exception) {
        super(message);
        this.reason = reason;
        this.type = type;
        this.exception = exception;
        log.log(Level.SEVERE, message, exception);
    }

    public String getExceptionCause(){
        StringBuilder cause = new StringBuilder("[ Message : " + super.getMessage() + "]");
        if(exception != null)
                cause.append("\n[ Exception :\n" + Throwables.getStackTraceAsString(this.exception) + "\n]");
        return cause.toString();
    }
}