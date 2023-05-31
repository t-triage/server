package com.clarolab.connectors.impl.utils.report.python.json.entity;

import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;

@Data
@Log
@Builder
public class PythonTestCaseError {

    private String message;

    private PythonTestCaseError(String message) {
        this.message = message;
    }

}