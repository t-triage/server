package com.clarolab.connectors.impl.utils.report.junit.json.entity;

import com.clarolab.entities.JenkinsBase;
import lombok.Data;

import java.util.List;

@Data
public class JunitTestResult extends JenkinsBase {

    private double duration;
    private boolean empty;
    private int failCount;
    private int passCount;
    private int skipCount;

    private List<JunitTestSuites> suites;
}
