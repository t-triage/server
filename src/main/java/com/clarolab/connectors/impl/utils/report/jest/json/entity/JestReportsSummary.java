package com.clarolab.connectors.impl.utils.report.jest.json.entity;

import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

@Log
@Data
@Builder
public class JestReportsSummary {
        private int suites;
        private int numTotalTests;
        private int numPassedTests;
        private int numPendingTests;
        private int numFailedTests;
        private double duration;
        private int skipped;
    }

