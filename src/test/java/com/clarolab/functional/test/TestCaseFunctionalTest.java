package com.clarolab.functional.test;

import com.clarolab.dto.db.TestTriageHistoryDTO;
import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.TestExecution;
import com.clarolab.model.TestTriage;
import com.clarolab.model.types.ApplicationFailType;
import com.clarolab.model.types.StateType;
import com.clarolab.model.types.StatusType;
import com.clarolab.model.types.TestFailType;
import com.clarolab.populate.DataProvider;
import com.clarolab.populate.RealDataProvider;
import com.clarolab.populate.TestTriagePopulate;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.TestTriageService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class TestCaseFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private RealDataProvider realDataProvider;

    @Autowired
    private TestTriageService testTriageService;


    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void createSameTest() {
        TestTriagePopulate testSample = realDataProvider.getTest();

        provider.setName("SameTest1");
        provider.getBuild(1);
        TestExecution test1 = provider.getTestExecution(testSample);
        TestTriage triage1 = provider.getTestCaseTriage();

        provider.clear();
        provider.setName("SameTest2");
        TestExecution test2 = provider.getTestExecution(testSample);
        TestTriage triage2 = provider.getTestCaseTriage();

        List<TestTriage> sameAs1 = testTriageService.findLastSameTests(triage1);
        List<TestTriage> sameAs2 = testTriageService.findLastSameTests(triage2);

        Assert.assertNotNull(sameAs1);
        Assert.assertNotNull(sameAs2);
        Assert.assertEquals(2, sameAs1.size());
        Assert.assertEquals(2, sameAs2.size());

        Assert.assertEquals(sameAs2.get(0).getTestCase().getId(), sameAs2.get(1).getTestCase().getId());

    }

    @Test
    public void createSimilarTestsDiffPath() {
        String sufix = "123123";
        TestTriagePopulate testSample = realDataProvider.getTest();
        testSample.setTestCaseName("Test" + sufix);
        testSample.setSuiteName("Suite" + sufix);
        testSample.setPath("Path" + sufix);

        provider.setName("SameTest1");
        provider.getBuild(1);
        TestExecution test1 = provider.getTestExecution(testSample);
        TestTriage triage1 = provider.getTestCaseTriage();

        provider.clear();
        provider.setName("SameTest2");
        testSample.setPath("DifferentPath" + sufix);
        TestExecution test2 = provider.getTestExecution(testSample);
        TestTriage triage2 = provider.getTestCaseTriage();

        List<TestTriage> sameAs1 = testTriageService.findLastSameTests(triage1);
        List<TestTriage> sameAs2 = testTriageService.findLastSameTests(triage2);

        Assert.assertNotNull(sameAs1);
        Assert.assertNotNull(sameAs2);
        Assert.assertEquals(1, sameAs1.size());
        Assert.assertEquals(1, sameAs2.size());
    }

    @Test
    public void createSimilarTestsDiffSuite() {
        String sufix = "123123";
        TestTriagePopulate testSample = realDataProvider.getTest();
        testSample.setTestCaseName("Test" + sufix);
        testSample.setSuiteName("Suite" + sufix);
        testSample.setPath("Path" + sufix);

        provider.setName("SameTest1");
        provider.getBuild(1);
        TestExecution test1 = provider.getTestExecution(testSample);
        TestTriage triage1 = provider.getTestCaseTriage();

        provider.clear();
        provider.setName("SameTest2");
        testSample.setSuiteName("DifferentPath" + sufix);
        TestExecution test2 = provider.getTestExecution(testSample);
        TestTriage triage2 = provider.getTestCaseTriage();

        List<TestTriage> sameAs1 = testTriageService.findLastSameTests(triage1);
        List<TestTriage> sameAs2 = testTriageService.findLastSameTests(triage2);

        Assert.assertNotNull(sameAs1);
        Assert.assertNotNull(sameAs2);
        Assert.assertEquals("Same test cases even different suitename", 2, sameAs1.size());
        Assert.assertEquals("Same test cases even different suitename", 2, sameAs2.size());
    }

    @Test
    public void createSimilarTestsDiffName() {
        String sufix = "123123";
        TestTriagePopulate testSample = realDataProvider.getTest();
        testSample.setTestCaseName("Test" + sufix);
        testSample.setSuiteName("Suite" + sufix);
        testSample.setPath("Path" + sufix);

        provider.setName("SameTest1");
        provider.getBuild(1);
        TestExecution test1 = provider.getTestExecution(testSample);
        TestTriage triage1 = provider.getTestCaseTriage();

        provider.clear();
        provider.setName("SameTest2");
        testSample.setTestCaseName("DifferentPath" + sufix);
        TestExecution test2 = provider.getTestExecution(testSample);
        TestTriage triage2 = provider.getTestCaseTriage();

        List<TestTriage> sameAs1 = testTriageService.findLastSameTests(triage1);
        List<TestTriage> sameAs2 = testTriageService.findLastSameTests(triage2);

        Assert.assertNotNull(sameAs1);
        Assert.assertNotNull(sameAs2);
        Assert.assertEquals(1, sameAs1.size());
        Assert.assertEquals(1, sameAs2.size());
    }

    @Test
    public void history() {
        int amount = 3;

        // Create another test just not to get it
        provider.getTestExecution();
        provider.getBuildTriage();

        // Creates the tests history
        provider.clear();
        String sufix = DataProvider.getRandomName("history");
        TestTriagePopulate testSample = realDataProvider.getTest();
        testSample.setTestCaseName("Test" + sufix);
        testSample.setSuiteName("Suite" + sufix);
        testSample.setPath("Path" + sufix);
        testSample.setAs(StatusType.FAIL, 0, amount + 1);

        for (int i = 1; i <= amount; i++) {
            provider.clearForNewBuild();
            provider.getBuild(i);
            provider.getTestExecution(testSample);
            provider.getBuildTriage();
        }

        List<TestTriageHistoryDTO> tests = testTriageService.getTestHistory(provider.getTestCaseTriage());

        Assert.assertEquals("History amount of tests is not accurate", amount, tests.size());

        TestTriageHistoryDTO firstTest = tests.get(0);

        Assert.assertEquals("Triage ApplicationFailType don't match", ApplicationFailType.UNDEFINED, firstTest.getApplicationFailType());
        Assert.assertEquals("Triage TestFailType don't match", TestFailType.UNDEFINED, firstTest.getTestFailType());
        Assert.assertEquals("Triage BuildNumber don't match", 1, firstTest.getBuildNumber());
        Assert.assertEquals("Triage CurrentState don't match", StateType.FAIL, firstTest.getCurrentState());
        Assert.assertTrue("Triage ApplicationFailType don't match", firstTest.getId() > 1);

    }
}
