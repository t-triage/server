package com.clarolab.connectors.impl.utils.report.python.json.entity;

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
public class MainPython {

    @Builder.Default
    private List<PythonSuite> suites = new ArrayList<>();
    private PythonReportSummary statistic;

    @Builder
    public MainPython(List<PythonSuite> suites, PythonReportSummary statistic) {
        this.suites = suites;
        this.statistic = statistic;
    }

    public void addSuite(PythonSuite suite) {
        this.suites.add(suite);
    }

    public void addSuites(List<PythonSuite> suites) {
        this.suites.addAll(suites);
    }

    public StatusType getStatus() {
        if (statistic.getFailures() > 0)
            return StatusType.FAIL;
        if ((statistic.getPasses() + statistic.getWarning()) == statistic.getTests())
            return StatusType.PASS;

        return StatusType.UNKNOWN;
    }

    public int getPassed() {
        return statistic.getPasses();
    }

    public int getFailed() {
        return statistic.getFailures();
    }

    public int getWarning() {
        return statistic.getWarning();
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
