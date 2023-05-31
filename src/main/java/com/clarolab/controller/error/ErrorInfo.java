/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.error;

public class ErrorInfo {

    private int code;

    private String error;
    private String description;

    public ErrorInfo() {
    }

    public ErrorInfo(int code, String error, String description) {
        this.code = code;
        this.error = error;
        this.description = error; //Hiding description due to dynamic security assessment.
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getError() {
        return error;
    }
}
