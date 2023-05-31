package com.clarolab.connectors.impl.utils.report.allure.json.entity;

import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

@Log
@Data
@Builder
public class AllureReportSummaryStatistic {

    private int failed;
    private int broken;
    private int skipped;
    private int passed;
    private int unknown;
    private int total;
}
