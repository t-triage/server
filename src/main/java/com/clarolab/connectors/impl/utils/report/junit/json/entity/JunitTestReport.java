package com.clarolab.connectors.impl.utils.report.junit.json.entity;

import com.clarolab.entities.JenkinsBase;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class JunitTestReport extends JenkinsBase {

    public static final String EMPTY_STRING = "";

    /**
     * This will be returned by the API in cases where the build has not been
     * run.
     */
    public static final JunitTestReport NO_TEST_REPORT = new JunitTestReport(0, 0, 0, EMPTY_STRING,
            Collections.<JunitTestChildReport>emptyList());

    private int failCount;
    private int skipCount;
    private int totalCount;
    private String urlName;
    private List<JunitTestChildReport> childReports;

    private JunitTestReport(int failCount, int skipCount, int totalCount, String urlName,
                            List<JunitTestChildReport> childReports) {
        super();
        this.failCount = failCount;
        this.skipCount = skipCount;
        this.totalCount = totalCount;
        this.urlName = urlName;
        this.childReports = childReports;
    }

    public JunitTestReport() {
        this.failCount = 0;
        this.skipCount = 0;
        this.totalCount = 0;
        // FIXME: What is the best choice to initialize?
        this.urlName = EMPTY_STRING;
    }
}
