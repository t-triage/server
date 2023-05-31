/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.junit.json.entity;

public interface JunitTestCaseError {

    String getError();

    String getErrorDetail();

    boolean isSkipError();
}
