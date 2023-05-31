package com.clarolab.functional.test.jira;

import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.jira.model.DashboardNumeration;
import com.clarolab.jira.model.JiraConfig;
import com.clarolab.jira.service.JiraConfigService;
import com.clarolab.jira.service.JiraOAuthService;
import com.clarolab.jira.service.JiraObjectService;
import static org.junit.Assert.*;

import com.clarolab.model.Product;
import com.clarolab.populate.UseCaseDataProvider;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class JiraFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private JiraObjectService jiraObjectService;
    @Autowired
    private JiraConfigService jiraConfigService;
    @Autowired
    private JiraOAuthService jiraOAuthService;

    @Autowired
    private UseCaseDataProvider provider;

    private final String actualFinalToken = "123";
    private final String actualRefreshToken = "123";
    private JiraConfig jiraConfig;

    private void createJiraConfig() {
        provider.clear();
        Product product = provider.getProduct();
        jiraConfig = JiraConfig.builder()
                .jiraUrl("https://clarotestriage.atlassian.net/")
                .jiraVersion("cloud")
                .projectKey("TES")
                .refreshToken(actualRefreshToken)
                .initialStateId("10004")
                .reopenStateId("10005")
                .resolvedStateId("10006")
                .closedStateId("10007")
                .product(product)
                .reporterEmail("santy_vita@hotmail.com")
                .clientID("BF10P0wVqHon8oz4jT1r4YB5QUBLrD0P")
                .clientSecret("QS1t2DQjPIsS8Xkg2_HAmpXd7lPIisb3FvgNqjQ51QQfVNyB36FdvlngU4PPY9jb")
                .cloudId("53036a6f-4752-45f4-bb8e-22e9d9657802")
                .finalToken(actualFinalToken)
                .build();
        jiraConfigService.save(jiraConfig);
    }

    @Before
    public void initiateJiraConfig(){
        createJiraConfig();
    }

    @Test
    @Ignore
    public void ticketStatus(){
        String status = jiraObjectService.getJiraTicketStatus("TES-2", jiraConfig);
        assertEquals("Respuesta esperada correcta","Selected for Development",status);
    }

    @Test
    @Ignore
    public void jiraStatus(){
        String list = jiraObjectService.jiraStatus(jiraConfig);
        assertTrue("bien",list.contains("reOpen") || list.contains("resolved") || list.contains("open") || list.contains("closed"));
    }

    @Test
    @Ignore
    public void projectStatus() {
        String response = jiraObjectService.getProjectStatus(jiraConfig.getProduct().getId(),"TES");
        assertTrue("bien", response.contains("https://api.atlassian.com/ex/jira/53036a6f-4752-45f4-bb8e-22e9d9657802/rest/api/3/issuetype/10004"));
    }


    @Test
    @Ignore
    public void projectList(){
        String a = jiraObjectService.getProjectList(jiraConfig.getProduct().getId());
        assertTrue("error",a.contains("total") && a.contains("2") && a.contains("https://api.atlassian.com/ex/jira/53036a6f-4752-45f4-bb8e-22e9d9657802/rest/api/3/project/10001"));
    }

    @Test
    @Ignore
    public void refreshToken(){
        String tokenOriginal = jiraConfig.getFinalToken();
        String refreshOriginal = jiraConfig.getRefreshToken();
        jiraConfig = jiraOAuthService.refreshToken(jiraConfig);
        assertNotEquals("son iguales",tokenOriginal,jiraConfig.getFinalToken());
        assertNotEquals("son iguales",refreshOriginal,jiraConfig.getRefreshToken());
    }

    @Test
    public void createJiraIssue(){
        jiraConfig.setIssueType("10004");
        String response = jiraObjectService.createJiraIssue(jiraConfig,"ttriag","h1.Biggest heading");
        assertNotNull(response);
        //see the issue on the jira dashboard
    }

    @Test
    @Ignore
    public void getJiraTaskId(){
        String response = jiraObjectService.jiraTaskId("TES");
        assertNotNull(response);
    }

    @Test
    @Ignore
    public void addJiraComment(){
        jiraObjectService.addJiraComment(jiraConfig,"TES-2","HOsdaLA");
        //see comment on jira dashboard
    }

    @Test
    @Ignore
    public void transitionIssue(){
        boolean response = jiraObjectService.transitionIssue(jiraConfig,"TES-7", DashboardNumeration.RESOLVED.getColumn());
        assertTrue(response);
    }

    @Test
    @Ignore
    public void updatePriority(){
        jiraObjectService.updatePriority(jiraConfig,"TES-2","5");
        //see the change of priority in the jira dashboard
    }

    @Test
    @Ignore
    public void searchIssuetype(){
        String response = jiraObjectService.searchIssueType(jiraConfig.getProduct().getId(),"10000");
        assertNotNull(response);
    }
}
