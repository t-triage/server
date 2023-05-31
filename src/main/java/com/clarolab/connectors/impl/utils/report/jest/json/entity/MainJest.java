package com.clarolab.connectors.impl.utils.report.jest.json.entity;

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
public class MainJest {

    @Builder.Default
    private List<JestSuite> suites = new ArrayList<>();
    private JestReportsSummary statistic;

    @Builder
    public MainJest(List<JestSuite> suites, JestReportsSummary statistic) {
        this.suites = suites;
        this.statistic = statistic;
    }

    public void addSuite(JestSuite suite) {
        this.suites.add(suite);
    }

    public void addSuites(List<JestSuite> suites) {
        this.suites.addAll(suites);
    }

    public StatusType getStatus() {
        if (statistic.getNumFailedTests() > 0)
            return StatusType.FAIL;
        if (statistic.getNumFailedTests() == 0 && statistic.getNumPendingTests()+statistic.getSkipped() > 0)
            return StatusType.SKIP;
        if (statistic.getNumPassedTests() == statistic.getNumTotalTests())
            return StatusType.PASS;

        return StatusType.UNKNOWN;
    }

    public int getPassed() {
        return statistic.getNumPassedTests();
    }

    public int getFailed() {
        return statistic.getNumFailedTests();
    }

    public int getSkipped() {
        return statistic.getNumPendingTests()+statistic.getSkipped();
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
