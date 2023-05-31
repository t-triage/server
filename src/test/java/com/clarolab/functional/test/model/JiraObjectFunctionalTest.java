package com.clarolab.functional.test.model;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.jira.model.JiraConfig;
import com.clarolab.jira.service.JiraConfigService;
import com.clarolab.jira.service.JiraObjectService;
import com.clarolab.mapper.impl.ManualTestCaseMapper;
import com.clarolab.model.AutomatedTestIssue;
import com.clarolab.model.Product;
import com.clarolab.model.manual.service.ManualTestCaseService;
import com.clarolab.model.types.StateType;
import com.clarolab.model.types.TestFailType;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.AutomatedTestIssueService;
import com.clarolab.service.ProductService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;


public class JiraObjectFunctionalTest extends BaseFunctionalTest {

    @Autowired
    ProductService productService;

    @Autowired
    JiraConfigService jiraConfigService;

    @Autowired
    JiraObjectService jiraObjectService;

    @Autowired
    AutomatedTestIssueService automatedTestIssueService;

    @Autowired
    ManualTestCaseService manualTestCaseService;

    @Autowired
    ManualTestCaseMapper manualTestCaseMapper;

    @Autowired
    private UseCaseDataProvider provider;

    JiraConfig jiraConfig = new JiraConfig();
    Product product = new Product();


    @Before
    public void clearProvider() {
        provider.clear();
        product = provider.getProduct();
        jiraConfig = provider.getJiraConfig();
    }


    @Test
    public void TestTicketCreationWithMock() {
        String summary = "JUnit test Jira Issue creation";
        String description = "This is a JUnit test for testing Jira Issue creations";
        JiraObjectService objectService = Mockito.mock(JiraObjectService.class);
        Mockito.when(objectService.createJiraIssue(jiraConfig, summary, description)).thenReturn("GT2-10");
        String response = objectService.createJiraIssue(jiraConfig, summary, description);

        Assert.assertNotNull(response);
        Assert.assertEquals("GT2-", response.substring(0, 4));
    }


    @Test
    public void testTransitionTicketWithMock() {
        boolean response;
        String ticketId = "GT2-10";
        String summary = "Summary: Test Transition Ticket.";
        String description = "Description: testing transition issue in Jira.";

        JiraObjectService objectService = Mockito.mock(JiraObjectService.class);
        Mockito.when(objectService.createJiraIssue(jiraConfig, summary, description)).thenReturn("GT2-10");
        Mockito.when(objectService.transitionIssue(jiraConfig, ticketId, "41")).thenReturn(true);
        ticketId = objectService.createJiraIssue(jiraConfig, summary, description);
        response = objectService.transitionIssue(jiraConfig, ticketId, "41");

        //ticketId = jiraObjectService.createJiraIssue(jiraConfig, summary, description);
        //response = jiraObjectService.transitionIssue(jiraConfig, ticketId, "41");

        Assert.assertTrue(response);
    }

    @Test
    public void TestTicketCreation() {
        //Resultado, deberia crear un ticket en el proyecto de testeo de Jira. https://testing-site-guido.atlassian.net
        String summary = "JUnit test Jira Issue creation";
        String description = "This is a JUnit test for testing Jira Issue creations";
        String response = jiraObjectService.createJiraIssue(jiraConfig, summary, description);

        Assert.assertNotNull(response);
        Assert.assertEquals("GT2-", response.substring(0, 4));
    }


    @Test
    public void testTicketCreationAfterTestFail() {
        //Resultado, deberia crear un ticket en el proyecto de testeo de Jira. https://testing-site-guido.atlassian.net

        AutomatedTestIssue automatedTestIssue = provider.getAutomatedTestIssue();
        automatedTestIssue.getTestTriage().setTestFailType(TestFailType.TEST_ASSIGNED_TO_FIX);
        automatedTestIssue.getTestTriage().setCurrentState(StateType.NEWFAIL);
        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssue = automatedTestIssueService.getAutomatedTestIssue(automatedTestIssue.getTestTriage().getTestCase());
        String actualStatus = jiraObjectService.getJiraTicketStatus(automatedTestIssue.getRelatedIssueID(), jiraConfig);
        Assert.assertNotNull(automatedTestIssue.getRelatedIssueID());
        Assert.assertEquals("GT2-", automatedTestIssue.getRelatedIssueID().substring(0, 4));
        //Status = 10003 means is in Initial State.
        Assert.assertEquals("10003", actualStatus);
    }

    @Test
    public void testMultipleFails() {
        //Resultado, deberia crear un ticket en el proyecto de testeo de Jira. https://testing-site-guido.atlassian.net

        AutomatedTestIssue automatedTestIssue = provider.getAutomatedTestIssue();
        automatedTestIssue.getTestTriage().setTestFailType(TestFailType.TEST_ASSIGNED_TO_FIX);
        automatedTestIssue.getTestTriage().setCurrentState(StateType.NEWFAIL);
        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssue = automatedTestIssueService.getAutomatedTestIssue(automatedTestIssue.getTestTriage().getTestCase());
        String actualStatus = jiraObjectService.getJiraTicketStatus(automatedTestIssue.getRelatedIssueID(), jiraConfig);
        //Status = 3 means is passing.
        Assert.assertEquals("10003", actualStatus);
        Assert.assertNotNull(automatedTestIssue.getRelatedIssueID());
        Assert.assertEquals("GT2-", automatedTestIssue.getRelatedIssueID().substring(0, 4));

    }

    @Test
    public void testTransitionTicket() {
        boolean response;
        String ticketId;
        String summary = "Summary: Test Transition Ticket.";
        String description = "Description: testing transition issue in Jira.";

        //Creates ticket on Initial State column (By Default).
        ticketId = jiraObjectService.createJiraIssue(jiraConfig, summary, description);
        //moves created ticket to Done column.
        response = jiraObjectService.transitionIssue(jiraConfig, ticketId, "41");

        //Check the transition was successful. If response = false --> Something went wrong.
        Assert.assertTrue(response);
    }

    @Test
    public void testTransitionAfterFailAndPass() {
        AutomatedTestIssue automatedTestIssue = provider.getAutomatedTestIssue();
        automatedTestIssue.getTestTriage().setTestFailType(TestFailType.TEST_ASSIGNED_TO_FIX);
        automatedTestIssue.getTestTriage().setCurrentState(StateType.NEWFAIL);
        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssue.getTestTriage().setCurrentState(StateType.PASS);
        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssue = automatedTestIssueService.getAutomatedTestIssue(automatedTestIssue.getTestTriage().getTestCase());
        String actualStatus = jiraObjectService.getJiraTicketStatus(automatedTestIssue.getRelatedIssueID(), jiraConfig);
        //Status = 3 means is passing.
        Assert.assertEquals("3", actualStatus);
    }

    @Test
    public void testTransitionAfterFailAndPassAndFailAgain() {
        AutomatedTestIssue automatedTestIssue = provider.getAutomatedTestIssue();
        automatedTestIssue.getTestTriage().setTestFailType(TestFailType.TEST_ASSIGNED_TO_FIX);
        automatedTestIssue.getTestTriage().setCurrentState(StateType.NEWFAIL);
        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssue.getTestTriage().setCurrentState(StateType.PASS);
        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssue.getTestTriage().setCurrentState(StateType.FAIL);
        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssue = automatedTestIssueService.getAutomatedTestIssue(automatedTestIssue.getTestTriage().getTestCase());
        String actualStatus = jiraObjectService.getJiraTicketStatus(automatedTestIssue.getRelatedIssueID(), jiraConfig);
        //Status = 10003 means is Open.
        Assert.assertEquals("10003", actualStatus);
    }

    @Test
    public void testTransitionAfterFailPassClosed() {
        AutomatedTestIssue automatedTestIssue = provider.getAutomatedTestIssue();
        automatedTestIssue.getTestTriage().setTestFailType(TestFailType.TEST_ASSIGNED_TO_FIX);
        automatedTestIssue.getTestTriage().setCurrentState(StateType.NEWFAIL);
        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssue.getTestTriage().setCurrentState(StateType.PASS);
        //Hacerlo pasar varias veces hasta que se setee como FIXED(minimo 4 pass).
        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);

        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssue = automatedTestIssueService.getAutomatedTestIssue(automatedTestIssue.getTestTriage().getTestCase());
        String actualStatus = jiraObjectService.getJiraTicketStatus(automatedTestIssue.getRelatedIssueID(), jiraConfig);
        //Status = 10005 means is Closed.
        Assert.assertEquals("10005", actualStatus);
    }

    @Test
    public void testLinkedTicketCreationAfterClosedAndReOpen() {
        AutomatedTestIssue automatedTestIssue = provider.getAutomatedTestIssue();
        automatedTestIssue.getTestTriage().setTestFailType(TestFailType.TEST_ASSIGNED_TO_FIX);
        automatedTestIssue.getTestTriage().setCurrentState(StateType.NEWFAIL);
        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssue.getTestTriage().setCurrentState(StateType.PASS);
        //Hacerlo pasar varias veces hasta que se setee como FIXED(minimo 4 pass).
        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssue.getTestTriage().setCurrentState(StateType.FAIL);
        automatedTestIssueService.updateAutomationIssue(automatedTestIssue.getTestTriage(), automatedTestIssue);
        automatedTestIssue = automatedTestIssueService.getAutomatedTestIssue(automatedTestIssue.getTestTriage().getTestCase());
        String actualStatus = jiraObjectService.getJiraTicketStatus(automatedTestIssue.getRelatedIssueID(), jiraConfig);
        //Status = 10004 means is ReOpen.
        Assert.assertEquals("10004", actualStatus);
    }



}