package com.clarolab.connectors.impl.utils.report.junit.json.entity;

import lombok.Data;

import java.util.List;

@Data
public class JunitTestSuites {

    private double duration;
    private String id;
    private String name;
    private String timestamp;

    private List<JunitTestCaseV2> cases;
}
