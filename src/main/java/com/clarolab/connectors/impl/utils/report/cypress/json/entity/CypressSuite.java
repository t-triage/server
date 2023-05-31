
package com.clarolab.connectors.impl.utils.report.cypress.json.entity;

import com.clarolab.connectors.impl.AbstractTestCreator;
import com.clarolab.model.TestExecution;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@Log
public class CypressSuite extends AbstractTestCreator {

    private String uri;
    private String title;
    private List<CypressTestCase> testCases;

    @Builder
    private CypressSuite(String uri, String title, List<CypressTestCase> testCases) {
        this.uri = uri;
        this.title = title;
        this.testCases = testCases;
    }

    public List<TestExecution> getTestCases(boolean isForDebug) {
        List<TestExecution> tests = Lists.newArrayList();
        testCases.forEach(test -> test.setContext(getContext()));
        testCases.forEach(test -> {
            TestExecution testExecution = test.getTest(this, isForDebug);
            testExecution.setName(test.getTitle());
            testExecution.setSuiteName(this.title);
            testExecution.setLocationPath(this.uri);
            testExecution.add(test.getSteps());
            tests.add(testExecution);
        });

        return tests;
    }

}
