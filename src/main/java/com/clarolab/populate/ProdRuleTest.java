package com.clarolab.populate;

import com.clarolab.agents.rules.StaticRuleDispatcher;
import com.clarolab.event.process.CleanupEventsEventHandler;
import com.clarolab.event.slack.SlackService;
import com.clarolab.model.*;
import com.clarolab.model.types.StatusType;
import com.clarolab.service.ExecutorService;
import com.clarolab.service.TestCaseService;
import com.clarolab.service.TestTriageService;
import com.clarolab.service.TriageSpecService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.logging.Level;

@Component
@Log
public class ProdRuleTest {

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private StaticRuleDispatcher staticRuleDispatcher;

    @Autowired
    private TriageSpecService triageSpecService;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private TestCaseService testCaseService;
    
    @Autowired
    private CleanupEventsEventHandler cleanupEventsEventHandler;
    
    @Autowired
    private SlackService slackService;
    
    
    public void testProductionScenario() {
        log.log(Level.INFO, "Started testProductionScenario");
        cleanup();
    }
    
    public void slack() {
        slackService.setSlackUserIds();
    }
    
    public void cleanup() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -75);

        
        cleanupEventsEventHandler.cleanupOneByOne(cal.getTime().getTime());
    }

    public void testDispatcherActon() {
        provider.clear();

        int buildNumber = 14;
        Executor executor = executorService.find(7655l);
        String testCaseName = "Delete";
        String testCasePath = "chrome.Email Templates page";

        TestCase persistedCase = testCaseService.newOrFind(testCaseName, testCasePath);
        TestTriagePopulate testSample = new TestTriagePopulate();
        testSample.setTestCaseName(testCaseName);
        testSample.setPath(testCasePath);
        testSample.setAs(StatusType.FAIL, 0, buildNumber + 1);

        setProvider(executor);

        Build build = provider.getBuild(buildNumber);
        TestExecution testExecution = provider.getTestExecution(testSample);

        TestTriage testTriage = staticRuleDispatcher.process(testExecution, build, provider.getTriageSpec());
        testTriage.isTriaged();
    }

    public void setProvider(Executor executor) {
        provider.setExecutor(executor);
        provider.setContainer(executor.getContainer());
        provider.setProduct(executor.getContainer().getProduct());
        provider.setTriageSpec(triageSpecService.getTriageSpec(executor));
    }

}
