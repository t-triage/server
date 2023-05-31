package com.clarolab.connectors.impl.utils.report.python.json.entity;

import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

@Log
@Data
@Builder
public class PythonReportSummary {
    private int suites;
    private int tests;
    private int passes;
    private int warning;
    private int failures;
    private double duration;
    private int testsRegistered;
    private double passPercent;
    private double pendingPercent;
}
