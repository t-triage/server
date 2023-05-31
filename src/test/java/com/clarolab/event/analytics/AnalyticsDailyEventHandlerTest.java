package com.clarolab.event.analytics;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.manual.ManualTestCase;
import com.clarolab.model.manual.ManualTestExecution;
import com.clarolab.model.manual.repository.ManualTestCaseRepository;
import com.clarolab.model.manual.repository.ManualTestExecutionRepository;
import com.clarolab.model.manual.service.ManualTestCaseService;
import com.clarolab.model.manual.service.ManualTestExecutionService;
import com.clarolab.model.manual.types.ExecutionStatusType;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.util.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class AnalyticsDailyEventHandlerTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private ManualTestCaseService manualTestCaseService;

    @Autowired
    private ManualTestCaseRepository manualTestCaseRepository;

    @Autowired
    private ManualTestStatService manualTestStatService;

    @Autowired
    private ManualTestExecutionService manualTestExecutionService;

    @Autowired
    private ManualTestExecutionRepository manualTestExecutionRepository;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void testManualTestCaseCreation() {
        int totalTestsBefore = manualTestCaseService.findAll().size();
        int expectedTotalTestCreated = 20;  // amount of test cases to be created

        for (int i = 0; i < expectedTotalTestCreated ; i++) {
            assertTrue(createNewManualTestCase(-1));
        }

        long totalTestsAfter = manualTestCaseService.findAll().size();
        long totalTestsCreated = totalTestsAfter - totalTestsBefore; // 3

        assertEquals(totalTestsCreated, expectedTotalTestCreated);
    }

    @Test
    public void testManualTestExecutionCreation() {
        long yesterday = DateUtils.beginDay(-1);

        int totalExecutionsBefore = manualTestExecutionService.findByLastExecutionTime(yesterday).size();

        int expectedPassedExecutions = 15;
        int expectedFailedExecutions = 5;
        int expectedTotalExecutions = expectedPassedExecutions + expectedFailedExecutions;

        for (int i = 0; i < expectedTotalExecutions; i++) {
            ExecutionStatusType status = i < expectedPassedExecutions ? ExecutionStatusType.PASS : ExecutionStatusType.FAIL;
            assertTrue(createNewManualTestExecution(status));
        }

        long passedExecutions = 0;
        long failedExecutions = 0;
        long totalExecutions;

        List<ManualTestExecution> manualTestExecutions = manualTestExecutionService.findByLastExecutionTime(yesterday);
        totalExecutions = manualTestExecutions.size() - totalExecutionsBefore;

        for (ManualTestExecution mte : manualTestExecutions) {
            if (mte.getStatus() == ExecutionStatusType.PASS) {
                passedExecutions += 1;
            } else if (mte.getStatus() == ExecutionStatusType.FAIL) {
                failedExecutions += 1;
            }
        }

        assertEquals(passedExecutions, expectedPassedExecutions);
        assertEquals(failedExecutions, expectedFailedExecutions);
        assertEquals(totalExecutions, expectedTotalExecutions);
    }

    @Test
    public void testManualTestStatCreation() {
        Long expectedNewTotalTests = 250L;
        Long expectedTotalExecuted = 230L;
        Long expectedPassed = 195L;
        Long expectedFailed = 35L;

        ManualTestStat manualTestStat = ManualTestStat.builder()
                .totalTests(expectedNewTotalTests)
                .executed(expectedTotalExecuted)
                .pass(expectedPassed)
                .fails(expectedFailed)
                .build();

        manualTestStat = manualTestStatService.save(manualTestStat);

        assertEquals(manualTestStat.getTotalTests(), expectedNewTotalTests);
        assertEquals(manualTestStat.getExecuted(), expectedTotalExecuted);
        assertEquals(manualTestStat.getPass(), expectedPassed);
        assertEquals(manualTestStat.getFails(), expectedFailed);
    }

    private boolean createNewManualTestCase(int daysAgo) {
        try {
            provider.setManualTestCase(null);
            ManualTestCase manualTestCase = provider.getManualTestCase(2);
            manualTestCase.setTimestamp(DateUtils.beginDay(daysAgo));
            manualTestCaseRepository.save(manualTestCase); // Use repository because the service modifies the timestamp

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean createNewManualTestExecution(ExecutionStatusType status) {
        long yesterday = DateUtils.beginDay(-1);

        try {
            provider.setManualTestExecution(null);
            ManualTestExecution manualTestExecution = provider.getManualTestExecution();
            manualTestExecution.setTimestamp(yesterday);
            manualTestExecution.setStatus(status);
            manualTestExecutionRepository.save(manualTestExecution);

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}