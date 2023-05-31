/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.junit.json.entity;

import com.clarolab.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

@Log
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class JunitTestCaseBaseError implements JunitTestCaseError{

    private String type = StringUtils.getEmpty();
    private String text= StringUtils.getEmpty();

}
