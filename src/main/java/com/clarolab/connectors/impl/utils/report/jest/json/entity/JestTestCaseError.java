package com.clarolab.connectors.impl.utils.report.jest.json.entity;

import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

@Data
@Log
@Builder
public class JestTestCaseError {

    private String message;

    private JestTestCaseError(String message) {
        this.message = message;
    }

}

