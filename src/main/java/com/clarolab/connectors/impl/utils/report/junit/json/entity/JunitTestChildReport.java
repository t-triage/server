package com.clarolab.connectors.impl.utils.report.junit.json.entity;

import lombok.Data;

@Data
public class JunitTestChildReport {

    private JunitTestChild child;
    private JunitTestResult result;
}
