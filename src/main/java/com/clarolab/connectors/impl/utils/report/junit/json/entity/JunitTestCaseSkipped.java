/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.junit.json.entity;

import com.clarolab.util.StringUtils;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

@Log
@Data
@Builder
public class JunitTestCaseSkipped implements JunitTestCaseError{

    private String message;

    @Override
    public String getError() {
        return this.message;
    }

    @Override
    public String getErrorDetail() {
        return StringUtils.getEmpty();
    }

    @Override
    public boolean isSkipError() {
        return true;
    }
}
