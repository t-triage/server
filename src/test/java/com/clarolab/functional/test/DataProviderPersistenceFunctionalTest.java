/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test;

import com.clarolab.event.analytics.ExecutorStat;
import com.clarolab.event.slack.SlackSpec;
import com.clarolab.event.slack.SlackSpecService;
import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.*;
import com.clarolab.populate.DataProvider;
import com.clarolab.populate.RealDataProvider;
import com.clarolab.populate.TestTriagePopulate;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DataProviderPersistenceFunctionalTest extends BaseFunctionalTest {

    private int minDBId = 1;

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private UserService userService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private ProductService productService;

    @Autowired
    private DeadlineService deadlineService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private BuildTriageService buildTriageService;

    @Autowired
    private TestExecutionService testExecutionService;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private TriageSpecService triageSpecService;

    @Autowired
    private SlackSpecService slackSpecService;

    @Autowired
    private AutomatedTestIssueService automatedTestIssueService;

    @Autowired
    private RealDataProvider realDataProvider;

    @Autowired
    private ApplicationDomainService applicationDomainService;



    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void testUser() {
        Assert.assertNotNull(provider.getUser());
        Assert.assertTrue(provider.getUser().getId() > minDBId);

        Assert.assertNotNull(userService.find(provider.getUser().getId()));
    }

    @Test
    public void testProduct() {
        Assert.assertNotNull(provider.getProduct());
        Assert.assertTrue(provider.getProduct().getId() > minDBId);

        Assert.assertNotNull(productService.find(provider.getProduct().getId()));
    }

    @Test
    public void testDeadline() {
        Assert.assertNotNull(provider.getDeadline());
        Assert.assertTrue(provider.getDeadline().getProduct().getId() > minDBId);

        Assert.assertNotNull(deadlineService.find(provider.getDeadline().getId()));
    }

    @Test
    public void testConnector() {
        Assert.assertNotNull(provider.getConnector());
        Assert.assertTrue(provider.getConnector().getId() > minDBId);

        Assert.assertNotNull(connectorService.find(provider.getConnector().getId()));
    }

    @Test
    public void testContainer() {
        Assert.assertNotNull(provider.getContainer());
        Assert.assertTrue(provider.getContainer().getId() > minDBId);
        Assert.assertTrue(provider.getContainer().getConnector().getId() > minDBId);
        Assert.assertTrue(provider.getContainer().getProduct().getId() > minDBId);

        Assert.assertNotNull(containerService.find(provider.getContainer().getId()));
    }

    @Test
    public void testExecutor() {
        Assert.assertNotNull(provider.getExecutor());
        Assert.assertTrue(provider.getExecutor().getId() > minDBId);
        Assert.assertTrue(provider.getExecutor().getContainer().getId() > minDBId);
        Assert.assertTrue(provider.getExecutor().getContainer().getConnector().getId() > minDBId);

        Assert.assertNotNull(executorService.find(provider.getExecutor().getId()));
    }

    @Test
    public void testBuild() {
        Assert.assertNotNull(provider.getBuild());
        Assert.assertTrue(provider.getBuild().getId() > minDBId);
        Assert.assertTrue(provider.getBuild().getExecutor().getId() > minDBId);
        Assert.assertTrue(provider.getBuild().getExecutor().getContainer().getId() > minDBId);
        Assert.assertTrue(provider.getBuild().getReport().getId() > minDBId);
        Assert.assertTrue(provider.getReport().getId() > minDBId);

        Assert.assertNotNull(buildService.find(provider.getBuild().getId()));
        Assert.assertNotNull(reportService.find(provider.getBuild().getReport().getId()));
        Assert.assertNotNull(reportService.find(provider.getReport().getId()));
    }

    @Test
    public void testTestCase() {
        Assert.assertNotNull(provider.getTestExecution());
        Assert.assertTrue(provider.getTestExecution().getId() > minDBId);
        Assert.assertTrue(provider.getTestExecution().getReport().getId() > minDBId);
        Assert.assertTrue(provider.getBuild().getId() > minDBId);
        Assert.assertTrue(provider.getBuild().getReport().getId() > minDBId);

        Assert.assertNotNull(testExecutionService.find(provider.getTestExecution().getId()));
    }

    @Test
    public void testBuildTriage() {
        Assert.assertNotNull(provider.getBuildTriage());
        Assert.assertTrue(provider.getBuildTriage().getId() > minDBId);
        Assert.assertTrue(provider.getTriageSpec().getId() > minDBId);
        Assert.assertTrue(provider.getTriageSpec().getContainer().getId() > minDBId);
        Assert.assertTrue(provider.getBuildTriage().getBuild().getId() > minDBId);
        Assert.assertTrue(provider.getBuildTriage().getTriager().getId() > minDBId);
        Assert.assertTrue(provider.getBuildTriage().getReport().getId() > minDBId);

        Assert.assertNotNull(buildTriageService.find(provider.getBuildTriage().getId()));

    }

    @Test
    public void testProperty() {
        Assert.assertNotNull(provider.getProperty());
        Assert.assertTrue(provider.getProperty().getId() > minDBId);

        Assert.assertNotNull(propertyService.find(provider.getProperty().getId()));
    }

    @Test
    public void testExecutorProductCombination() {
        provider.getExecutor();
        provider.getProduct();

        Assert.assertTrue(provider.getExecutor().getId() > minDBId);
        Assert.assertTrue(provider.getProduct().getId() > minDBId);

        Assert.assertNotNull(productService.find(provider.getProduct().getId()));
        Assert.assertNotNull(executorService.find(provider.getExecutor().getId()));
    }

    @Test
    public void testTestCaseTriage() {
        TestTriage entry = provider.getTestCaseTriage();

        Assert.assertNotNull(entry);
        Assert.assertTrue(entry.getId() > minDBId);
        Assert.assertTrue(entry.getBuild().getId() > minDBId);
    }

    @Test
    public void testIssueTicket() {
        IssueTicket entry = provider.getIssueTicket();

        Assert.assertNotNull(entry);
        Assert.assertTrue(entry.getId() > minDBId);
        Assert.assertTrue(entry.getAssignee().getId() > minDBId);
        Assert.assertTrue(entry.getProduct().getId() > minDBId);
    }

    @Test
    public void testNote() {
        Note entry = provider.getNote();

        Assert.assertNotNull(entry);
        Assert.assertTrue(entry.getId() > minDBId);
    }

    @Test
    public void testApplicationEvent() {
        ApplicationEvent entry = provider.getApplicationEvent();

        Assert.assertNotNull(entry);
        Assert.assertTrue(entry.getId() > minDBId);
        Assert.assertTrue(entry.getSource().getId() > minDBId);
    }

    @Test
    public void testApplicationDomain() {
        ApplicationDomain entry = provider.getApplicationDomain();

        Assert.assertNotNull(entry);
        Assert.assertTrue(entry.getId() > minDBId);
    }

    @Test
    public void testSlackSpec() {
        SlackSpec entry = provider.getSlackSpec();

        Assert.assertNotNull(entry);
        Assert.assertTrue(entry.getId() > minDBId);
        Assert.assertTrue(entry.getProduct().getId() > minDBId);
    }

    @Test
    public void testExecutorStat() {
        ExecutorStat entry = provider.getExecutorStat();

        Assert.assertNotNull(entry);
        Assert.assertTrue(entry.getId() > minDBId);
    }

    @Test
    public void testAutomatedTestIssue() {
        AutomatedTestIssue entry = provider.getAutomatedTestIssue();

        Assert.assertNotNull(entry);
        Assert.assertTrue(entry.getId() > minDBId);

        AutomatedTestIssue dbEntity = automatedTestIssueService.find(entry.getId());

        Assert.assertNotNull(dbEntity);
        Assert.assertNotNull(dbEntity.getTestCase());
    }

    @Test
    public void testManyExecutorStat() {
        int amount = 10;
        ExecutorStat entry;

        // creates execution stats
        for (int i = 0; i < amount; i++) {
            provider.setExecutorStat(null);
            provider.setTimestamp(DataProvider.getTimeAdd(-1 * amount));
            entry = provider.getExecutorStat();

            Assert.assertNotNull(entry);
            Assert.assertTrue(entry.getId() > minDBId);
        }
    }

    @Test
    public void testPopulateTon() {
        int amount = 2;
        provider.setName("TestPop");
        provider.build(amount);
    }

    @Test
    public void testTestCaseUnique() {
        TestTriagePopulate testSample = realDataProvider.getTest();
        provider.setName("TestUnique");
        provider.getBuild(1);
        provider.getTestExecution(testSample);
        TestCase test1 = provider.getTestExecution().getTestCase();

        provider.setBuild(null);
        provider.setTestExecution(null);
        provider.getBuild(2);
        provider.getTestExecution(testSample);
        TestCase test2 = provider.getTestExecution().getTestCase();

        Assert.assertEquals(test1.getId(), test2.getId());
    }

    @Test
    public void testTestCaseNotUnique() {
        provider.setName("TestNotUnique");
        provider.getBuild(1);
        provider.getTestExecution();
        TestCase test1 = provider.getTestExecution().getTestCase();

        provider.setBuild(null);
        provider.setTestExecution(null);
        provider.getBuild(2);
        provider.getTestExecution();
        TestCase test2 = provider.getTestExecution().getTestCase();

        Assert.assertNotEquals(test1.getId(), test2.getId());
    }
}
