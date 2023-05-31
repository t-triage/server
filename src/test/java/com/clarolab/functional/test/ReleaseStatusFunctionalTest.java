package com.clarolab.functional.test;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.AutomatedTestIssue;
import com.clarolab.model.TestCase;
import com.clarolab.model.types.UserFixPriorityType;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.ReleaseStatusService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ReleaseStatusFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private ReleaseStatusService releaseStatusService;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void executorStatusWithBlockersTest() {
        String prefix = "executorStatusWithBlocker";
        provider.setName(prefix);
        provider.getExecutor();
        TestCase testCase = provider.getTestCase();
        AutomatedTestIssue automatedTestIssue = provider.getAutomatedTestIssue();
        automatedTestIssue.setUserFixPriorityType(UserFixPriorityType.BLOCKER);
        testCase.setAutomatedTestIssue(automatedTestIssue);

        Assert.assertSame(false, releaseStatusService.getExecutorStatus(provider.getExecutor().getId()));
    }

}
