package com.clarolab.functional.test.model;

import com.clarolab.dto.TestTriageDTO;
import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.Pipeline;
import com.clarolab.model.PipelineTest;
import com.clarolab.model.TestCase;
import com.clarolab.model.TestTriage;
import com.clarolab.model.types.StatusType;
import com.clarolab.populate.DataProvider;
import com.clarolab.populate.TestTriagePopulate;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.PipelineService;
import com.clarolab.service.PipelineTestService;
import com.clarolab.service.ReleaseStatusService;
import com.clarolab.service.TestTriageService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PipelineFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private PipelineService pipelineService;

    @Autowired
    private PipelineTestService pipelineTestService;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private ReleaseStatusService releaseStatusService;

    @Before
    public void clearProvider() {
        provider.clear();
        provider.getContainer();
    }

    @Test
    public void addUniques() {
        int amount = 3;
        List<Long> testCaseIds = new ArrayList<>(amount);
        Pipeline pipeline = provider.getPipeline();

        for (int i = 0; i < amount; i++) {
            provider.setTestExecution(null);
            testCaseIds.add(provider.getTestCase().getId());
        }

        pipelineService.assignToPipeline(pipeline, testCaseIds);
        List<PipelineTest> testsRelations = pipelineService.findAll(pipeline);

        Assert.assertEquals("Not all relations were created", amount, testsRelations.size());
    }

    @Test
    public void addWithDuplicates() {
        int amount = 3;
        List<Long> testCaseIds = new ArrayList<>(amount);
        provider.getContainer();
        Pipeline pipeline = provider.getPipeline();

        for (int i = 0; i < amount; i++) {
            provider.setTestExecution(null);
            testCaseIds.add(provider.getTestCase().getId());
        }
        pipelineService.assignToPipeline(pipeline, testCaseIds);

        for (int i = 0; i < amount; i++) {
            provider.setTestExecution(null);
            testCaseIds.add(provider.getTestCase().getId());
        }

        pipelineService.assignToPipeline(pipeline, testCaseIds);
        List<PipelineTest> testsRelations = pipelineService.findAll(pipeline);

        Assert.assertEquals("Not all relations were created", amount * 2, testsRelations.size());

    }

    @Test
    public void search() {
        String name = "SearchPipe";
        provider.setName(name);

        provider.getPipeline();

        List<Pipeline> list = pipelineService.search(name);

        Assert.assertEquals("Pipeline should be found", 1, list.size());
    }

    @Test
    public void delete() {
        int amount = 3;
        List<Long> testCaseIds = new ArrayList<>(amount);
        provider.getContainer();
        Pipeline pipeline = provider.getPipeline();

        for (int i = 0; i < amount; i++) {
            provider.setTestExecution(null);
            testCaseIds.add(provider.getTestCase().getId());
        }

        pipelineService.assignToPipeline(pipeline, testCaseIds);
        List<PipelineTest> testsRelations = pipelineService.findAll(pipeline);

        PipelineTest toDelete = testsRelations.get(0);
        Long deletedId = pipelineTestService.delete(toDelete.getPipeline(), toDelete.getTest());

        Assert.assertEquals("Deleted relation dont match", toDelete.getId(), deletedId);

        testsRelations = pipelineService.findAll(pipeline);

        Assert.assertEquals("Deleted relations have not changed with the deletion", amount - 1, testsRelations.size());
    }

    @Test
    public void ongoing() {
        String testName = "ongoing";
        int amount = 3;
        int amountTests = 4;
        List<Long> testCaseIds = new ArrayList<>(amountTests);
        List<TestTriagePopulate> testPopulates = new ArrayList<>(amountTests);
        Pipeline pipeline = provider.getPipeline();

        TestTriagePopulate testPass = new TestTriagePopulate();
        testPass.setAs(StatusType.PASS, 0, 2);
        testPopulates.add(testPass);

        TestTriagePopulate testFail = new TestTriagePopulate();
        testFail.setAs(StatusType.FAIL, 0, 2);
        testPopulates.add(testFail);

        TestTriagePopulate testTriaged = new TestTriagePopulate();
        testTriaged.setAs(StatusType.FAIL, 0, 2);
        testPopulates.add(testTriaged);

        TestTriagePopulate testMissing = new TestTriagePopulate();
        testMissing.setAs(StatusType.FAIL, 0, 1);
        testPopulates.add(testMissing);

        // Build 1
        provider.getBuild(1);
        for (TestTriagePopulate test : testPopulates) {
            test.setTestCaseName(DataProvider.getRandomName(testName));
            provider.getTestExecution(test);
            provider.setTestExecution(null);
        }
        provider.getBuildTriage();

        // Build 2
        provider.clearForNewBuild();
        provider.getBuild(2);
        provider.getTestExecution(testPass);
        provider.setTestExecution(null);
        provider.getTestExecution(testFail);
        provider.setTestExecution(null);
        provider.getTestExecution(testTriaged);
        provider.setTestExecution(null);
        provider.getBuildTriage();


        // Triage 1 test
        List<TestTriage> triages = testTriageService.findAllOngoingTests(testCaseService.newOrFind(testTriaged.getTestCaseName(), testTriaged.getPath()));
        Assert.assertEquals("Only 1 triage is active", 1, triages.size());
        TestTriage testToTriage = triages.get(0);
        testToTriage.setTriaged(true);
        testToTriage.setTriager(provider.getUser());
        testTriageService.update(testToTriage);


        // assignToPipeline
        List<TestCase> testCases = new ArrayList<>(amountTests);
        testCases.add(testCaseService.newOrFind(testPass.getTestCaseName(), testPass.getPath()));
        testCases.add(testCaseService.newOrFind(testFail.getTestCaseName(), testFail.getPath()));
        testCases.add(testCaseService.newOrFind(testTriaged.getTestCaseName(), testTriaged.getPath()));
        testCases.add(testCaseService.newOrFind(testMissing.getTestCaseName(), testMissing.getPath()));
        testCaseIds = testCases.stream().map(testCase -> testCase.getId()).collect(Collectors.toList());
        pipelineService.assignToPipeline(provider.getPipeline(), testCaseIds);

        List<TestTriage> testsRelations = pipelineService.ongoingTestTriages(pipeline);

        Assert.assertEquals("Not all relations were created/retrieved", amountTests, testsRelations.size());

        boolean apiPipelineStatus = releaseStatusService.getPipelineStatus(pipeline.getId());

        Assert.assertFalse("One of the tests was not executed, i.e. the pipeline shuldn't be fine", apiPipelineStatus);
    }

    @Test
    public void passPipeline() {
        int amount = 3;
        List<Long> testCaseIds = new ArrayList<>(amount);
        Pipeline pipeline = provider.getPipeline();

        for (int i = 0; i < amount; i++) {
            provider.setTestExecution(null);
            provider.getTestExecution(StatusType.PASS);
            testCaseIds.add(provider.getTestCase().getId());
        }

        pipelineService.assignToPipeline(pipeline, testCaseIds);
        List<PipelineTest> testsRelations = pipelineService.findAll(pipeline);

        Assert.assertEquals("Not all relations were created", amount, testsRelations.size());

        boolean apiPipelineStatus = releaseStatusService.getPipelineStatus(pipeline.getId());

        Assert.assertFalse("All tests were PASS, i.e. the pipeline should be fine", apiPipelineStatus);
    }

}
