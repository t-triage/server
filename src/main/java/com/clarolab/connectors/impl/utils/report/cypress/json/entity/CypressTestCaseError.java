package com.clarolab.connectors.impl.utils.report.cypress.json.entity;

import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

@Data
@Log
@Builder
public class CypressTestCaseError {

    private String message;
    private String stack;

    private CypressTestCaseError(String message, String stack) {
        this.message = message;
        this.stack = stack;
    }

}
