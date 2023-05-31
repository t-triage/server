package com.clarolab.connectors.impl.utils.report.allure.json.entity;

import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

@Log
@Builder
@Data
public class AllureStatusDetails {

    private String message;
    private String trace;
    private boolean flaky;
}
