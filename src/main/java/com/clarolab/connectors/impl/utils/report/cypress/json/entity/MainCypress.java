package com.clarolab.connectors.impl.utils.report.cypress.json.entity;

import com.clarolab.model.TestExecution;
import com.clarolab.model.types.StatusType;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.List;

@Data
@Log
@Builder
public class MainCypress {

    @Builder.Default
    private List<CypressSuite> suites = new ArrayList<>();
    private CypressReportSummary statistic;

    @Builder
    public MainCypress(List<CypressSuite> suites, CypressReportSummary statistic) {
        this.suites = suites;
        this.statistic = statistic;
    }

    public void addSuite(CypressSuite suite) {
        this.suites.add(suite);
    }

    public void addSuites(List<CypressSuite> suites) {
        this.suites.addAll(suites);
    }

    public StatusType getStatus() {
        if (statistic.getFailures() > 0)
            return StatusType.FAIL;
        if (statistic.getFailures() == 0 && statistic.getPending() > 0)
            return StatusType.SKIP;
        if (statistic.getPasses() == statistic.getTests() && statistic.getPassPercent() == 100)
            return StatusType.PASS;

        return StatusType.UNKNOWN;
    }

    public int getPassed() {
        return statistic.getPasses();
    }

    public int getFailed() {
        return statistic.getFailures();
    }

    public int getSkipped() {
        return statistic.getPending() + statistic.getSkipped();
    }

    public double getDuration() {
        return statistic.getDuration();
    }

    public List<TestExecution> getTests(boolean isForDebug) {
        List<TestExecution> tests = Lists.newArrayList();
        suites.forEach(suite -> tests.addAll(suite.getTestCases(isForDebug)));
        return tests;
    }
}
