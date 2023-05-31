package com.clarolab.unit.test;

import com.clarolab.model.AutomatedTestIssue;
import com.clarolab.model.TestTriage;
import com.clarolab.model.types.StateType;
import com.clarolab.populate.DataProvider;
import com.clarolab.unit.BaseUnitTest;
import org.junit.Assert;
import org.junit.Test;

import static com.clarolab.util.Constants.AUTOMATION_TREND_SIZE;

public class AutomationIssueTrendUnitTest extends BaseUnitTest {

    @Test
    public void failTrend() {
        AutomatedTestIssue issue = DataProvider.getAutomatedTestIssue();
        TestTriage test = new TestTriage();

        test.setCurrentState(StateType.FAIL);
        issue.addTrend(test);
        test.setCurrentState(StateType.PERMANENT);
        issue.addTrend(test);
        test.setCurrentState(StateType.NEWFAIL);
        issue.addTrend(test);

        boolean allFail = issue.getSuccessTrend().stream().allMatch(t-> (t.booleanValue() == false));

        Assert.assertEquals("All tests should be failing, it should be failing", "000", issue.getTrend());
        Assert.assertTrue("All tests should be failing, it should be failing", allFail);
    }

    @Test
    public void passTrend() {
        AutomatedTestIssue issue = DataProvider.getAutomatedTestIssue();
        TestTriage test = new TestTriage();

        test.setCurrentState(StateType.PASS);
        issue.addTrend(test);
        test.setCurrentState(StateType.NEWPASS);
        issue.addTrend(test);

        boolean allFail = issue.getSuccessTrend().stream().allMatch(t-> (t.booleanValue() == true));

        Assert.assertEquals("All tests should be failing, it should be failing", "11", issue.getTrend());
        Assert.assertTrue("All tests should be failing, it should be failing", allFail);
    }

    @Test
    public void historyTrend() {
        int amount = AUTOMATION_TREND_SIZE;

        AutomatedTestIssue issue = DataProvider.getAutomatedTestIssue();
        TestTriage test = new TestTriage();

        test.setCurrentState(StateType.PASS);
        issue.addTrend(test);

        for (int i = 0; i < amount - 2; i++) {
            test.setCurrentState(StateType.FAIL);
            issue.addTrend(test);
        }

        Assert.assertEquals("All tests should be failing, it should be failing except the last one", "100000000", issue.getTrend());

        test.setCurrentState(StateType.PASS);
        issue.addTrend(test);

        Assert.assertEquals("Only first and last test pass", "1000000001", issue.getTrend());

        test.setCurrentState(StateType.FAIL);
        issue.addTrend(test);

        Assert.assertEquals("All tests should be failing, except the second one", "0000000010", issue.getTrend());
        Assert.assertEquals("Last test was a failure", false, issue.getSuccessTrend().get(amount - 1));
        Assert.assertEquals("Second to last is pass", true, issue.getSuccessTrend().get(amount - 2));

    }
}
