/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.junit.json.entity;

import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

@Log
@Data
public class JunitTestCaseSimpleError extends JunitTestCaseBaseError{

    @Builder
    public JunitTestCaseSimpleError(String type, String text){
        super(type, text);
    }


    @Override
    public String getError() {
        return super.getType();
    }

    @Override
    public String getErrorDetail() {
        return super.getText();
    }

    @Override
    public boolean isSkipError() {
        return false;
    }
}
