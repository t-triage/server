package com.clarolab.functional.test.rules;

import com.clarolab.agents.rules.StaticRuleDispatcher;
import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.functional.test.util.TestConfig;
import com.clarolab.model.Build;
import com.clarolab.model.TestTriage;
import com.clarolab.model.types.StateType;
import com.clarolab.model.types.StatusType;
import com.clarolab.populate.RealDataProvider;
import com.clarolab.populate.TestTriagePopulate;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.ContainerService;
import com.clarolab.service.ErrorDetailService;
import com.clarolab.service.ReportService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.clarolab.model.helper.tag.TagHelper.FLAKY_TRIAGE;
import static com.clarolab.model.types.DeducedReasonType.Rule30;
import static com.clarolab.model.types.DeducedReasonType.Rule34;
import static com.clarolab.model.types.DeducedReasonType.Rule41;
import static com.clarolab.model.types.DeducedReasonType.Rule45;

public class SameVersionRuleFunctionalTest extends BaseFunctionalTest {

    @Autowired
    protected ErrorDetailService errorDetailService;

    @Autowired
    protected RealDataProvider realDataProvider;

    @Autowired
    protected UseCaseDataProvider provider;

    @Autowired
    protected StaticRuleDispatcher staticRuleDispatcher;

    @Autowired
    private ContainerService containerService;
    
    @Autowired
    private ReportService reportService;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void passFail() {
        TestConfig test = newTestConfig()
                .rank(3)
                .newState(StateType.FAIL)
                .currentStatus(StatusType.FAIL)
                .triaged(true)
                .rule(Rule45)
                .tags(FLAKY_TRIAGE)
                .includeTags(true)
                .build();

        TestTriagePopulate testSpec = test.getNewTestPopulate();
        testSpec.setAs(StatusType.PASS, 1, 1);
        testSpec.setAs(StatusType.FAIL, 2, 2);

        createTest(test, testSpec);
    }

    @Test
    public void passSkip() {
        TestConfig test = newTestConfig()
                .rank(3)
                .newState(StateType.FAIL)
                .currentStatus(StatusType.FAIL)
                .triaged(true)
                .rule(Rule45)
                .build();

        TestTriagePopulate testSpec = test.getNewTestPopulate();
        testSpec.setAs(StatusType.PASS, 1, 1);
        testSpec.setAs(StatusType.SKIP, 2, 2);

        createTest(test, testSpec);
    }

    @Test
    public void passCancel() {
        TestConfig test = newTestConfig()
                .rank(3)
                .newState(StateType.FAIL)
                .currentStatus(StatusType.FAIL)
                .triaged(true)
                .rule(Rule45)
                .build();

        TestTriagePopulate testSpec = test.getNewTestPopulate();
        testSpec.setAs(StatusType.PASS, 1, 1);
        testSpec.setAs(StatusType.CANCELLED, 2, 2);

        createTest(test, testSpec);
    }

    @Test
    public void passFailFail() {
        TestConfig test = newTestConfig()
                .rank(3)
                .newState(StateType.FAIL)
                .currentStatus(StatusType.FAIL)
                .triaged(true)
                .rule(Rule45)
                .tags(FLAKY_TRIAGE)
                .includeTags(true)
                .build();

        TestTriagePopulate testSpec = test.getNewTestPopulate();
        testSpec.setAs(StatusType.PASS, 1, 1);
        testSpec.setAs(StatusType.FAIL, 2, 3);

        createTest(test, testSpec);
    }

    @Test
    public void passFailPassFail() {
        TestConfig test = newTestConfig()
                .rank(3)
                .newState(StateType.FAIL)
                .currentStatus(StatusType.FAIL)
                .triaged(true)
                .rule(Rule45)
                .tags(FLAKY_TRIAGE)
                .includeTags(true)
                .build();

        TestTriagePopulate testSpec = test.getNewTestPopulate();
        testSpec.setAs(StatusType.PASS, 1, 1);
        testSpec.setAs(StatusType.FAIL, 2, 2);
        testSpec.setAs(StatusType.PASS, 3, 3);
        testSpec.setAs(StatusType.FAIL, 4, 4);

        createTest(test, testSpec);
    }

    @Test
    public void failFail() {
        TestConfig test = newTestConfig()
                .rank(7)
                .newState(StateType.FAIL)
                .currentStatus(StatusType.FAIL)
                .triaged(false)
                .rule(Rule34)
                .build();

        TestTriagePopulate testSpec = test.getNewTestPopulate();
        testSpec.setAs(StatusType.FAIL, 1, 2);

        createTest(test, testSpec);
    }

    @Test
    public void failPass() {
        TestConfig test = newTestConfig()
                .rank(0)
                .newState(StateType.PASS)
                .currentStatus(StatusType.PASS)
                .triaged(true)
                .rule(Rule30)
                .build();

        TestTriagePopulate testSpec = test.getNewTestPopulate();
        testSpec.setAs(StatusType.FAIL, 1, 1);
        testSpec.setAs(StatusType.PASS, 2, 2);

        createTest(test, testSpec);
    }

    @Test
    public void passFailNewVersionFail() {
        TestConfig test = newTestConfig()
                .rank(6)
                .newState(StateType.FAIL)
                .currentStatus(StatusType.FAIL)
                .triaged(false)
                .rule(Rule41)
                .build();

        TestTriagePopulate testSpec = test.getNewTestPopulate();
        testSpec.setAs(StatusType.PASS, 1, 1);
        testSpec.setAs(StatusType.FAIL, 2, 2);
        testSpec.setAs(StatusType.FAIL, 3, 3);


        TestTriage triage = null;

        provider.clearForNewBuild();
        Build build = provider.getBuild(1);
        setProductVersion("1");
        provider.getTestExecution(testSpec);
        triage = provider.getTestCaseTriage();

        provider.clearForNewBuild();
        build = provider.getBuild(2);
        setProductVersion("2");
        provider.getTestExecution(testSpec);
        triage = provider.getTestCaseTriage();


        provider.clearForNewBuild();
        build = provider.getBuild(3);
        provider.getTestExecution(testSpec);
        triage = provider.getTestCaseTriage();


        assertRules(test, triage);
    }

    @Test
    public void passFailNullVersionFail() {
        TestConfig test = newTestConfig()
                .rank(6)
                .newState(StateType.FAIL)
                .currentStatus(StatusType.FAIL)
                .triaged(false)
                .rule(Rule41)
                .build();

        TestTriagePopulate testSpec = test.getNewTestPopulate();
        testSpec.setAs(StatusType.PASS, 1, 1);
        testSpec.setAs(StatusType.FAIL, 2, 2);
        testSpec.setAs(StatusType.FAIL, 3, 3);


        TestTriage triage = null;
        setProductVersion("1");
        provider.getReport().setProductVersion("1");
        executorService.update(provider.getExecutor());
        reportService.update(provider.getReport());

        provider.clearForNewBuild();
        Build build = provider.getBuild(1);
        provider.getTestExecution(testSpec);
        triage = provider.getTestCaseTriage();

        provider.clearForNewBuild();
        build = provider.getBuild(2);
        provider.getTestExecution(testSpec);
        triage = provider.getTestCaseTriage();

        provider.getReport().setProductVersion(null);
        reportService.update(provider.getReport());

        provider.clearForNewBuild();
        build = provider.getBuild(3);
        provider.getTestExecution(testSpec);
        triage = provider.getTestCaseTriage();


        assertRules(test, triage);
    }

    private void setProductVersion(String version) {
        provider.getReport().setProductVersion(version);
        executorService.update(provider.getExecutor());
        reportService.update(provider.getReport());
    }

    protected void assertRules(TestConfig test, TestTriage lastTriage) {
        // Validate deduction
        Assert.assertEquals("Wrong rule applied", test.getRule(), lastTriage.getStateReasonType());
        Assert.assertEquals(String.format("Deduced currentState does not match. Applied rule %s", lastTriage.getStateReasonType().name()), test.getNewState(), lastTriage.getCurrentState());
        Assert.assertEquals("Rank don't match", test.getRank(), lastTriage.getRank());
        Assert.assertEquals("Triage status don't match", test.isTriaged(), lastTriage.isTriaged());

        if (test.getTags() != null && !test.getTags().isEmpty()) {
            if (test.isIncludeTags()) {
                Assert.assertTrue(String.format("Tag not included: expected: %s actual: %s", test.getTags(), lastTriage.getTags()), lastTriage.containTag(test.getTags()));
            } else {
                Assert.assertFalse(String.format("Tag should not be included: not expected: %s actual: %s", test.getTags(), lastTriage.getTags()), lastTriage.containTag(test.getTags()));
            }
        }
    }

    protected TestConfig.TestConfigBuilder newTestConfig() {
        return TestConfig.builder()
                .realDataProvider(realDataProvider)
                .errorDetailService(errorDetailService);
    }

    protected void createTest(TestConfig test, TestTriagePopulate newTest) {
        TestTriage triage = null;

        int index = 1;
        boolean hasItems = true;
        while (hasItems) {
            provider.clearForNewBuild();
            Build build = provider.getBuild(index);
            setProductVersion("1");
            if (newTest.getStatusAtBuild(build) != null) {
                provider.getTestExecution(newTest);
                triage = provider.getTestCaseTriage();
                hasItems = true;
            } else {
                hasItems = false;
            }
            index = index + 1;
        }

        assertRules(test, triage);
    }
}
