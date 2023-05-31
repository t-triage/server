package com.clarolab.connectors.impl.utils.report.allure.json.entity;

import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

@Log
@Builder
@Data
public class AllureTime {

    private long start;
    private long stop;
    private long duration;

    private long minDuration;
    private long maxDuration;
    private long sumDuration;
}
