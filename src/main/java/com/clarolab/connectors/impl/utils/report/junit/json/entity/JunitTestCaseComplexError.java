/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.junit.json.entity;

import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

@Log
@Data
public class JunitTestCaseComplexError extends JunitTestCaseBaseError{

    private String stackTrace;
    private String systemOut;
    private String systemErr;
    private static final String SEPARATOR = "\n****************************************************\n";

    @Builder
    public JunitTestCaseComplexError(String type, String text, String stackTrace, String systemOut, String systemErr){
        super(type, text);
        this.stackTrace = stackTrace;
        this.systemOut = systemOut;
        this.systemErr = systemErr;
    }

    @Override
    public String getError() {
        return super.getType();
    }

    @Override
    public String getErrorDetail() {
        return super.getText() +
                this.SEPARATOR +
                this.stackTrace +
                this.SEPARATOR +
                this.systemOut +
                this.SEPARATOR +
                this.systemErr;
    }

    @Override
    public boolean isSkipError() {
        return false;
    }
}
