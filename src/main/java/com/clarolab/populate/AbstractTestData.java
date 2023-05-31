package com.clarolab.populate;

import com.clarolab.model.AutomatedTestIssue;
import com.clarolab.model.IssueTicket;
import com.clarolab.model.TestTriage;
import com.clarolab.model.types.ApplicationFailType;
import com.clarolab.model.types.IssueType;
import com.clarolab.model.types.TestFailType;
import com.clarolab.model.types.UserFixPriorityType;
import com.clarolab.service.*;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class AbstractTestData {
    @Autowired
    protected UseCaseDataProvider provider;

    @Autowired
    protected TestCaseService testCaseService;

    @Autowired
    protected TestTriageService testTriageService;

    @Autowired
    protected AutomatedTestIssueService automatedTestIssueService;

    @Autowired
    protected PropertyService propertyService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected IssueTicketService issueTicketService;

    @Autowired
    protected ConnectorService connectorService;

    @Autowired
    protected ContainerService containerService;

    @Autowired
    protected ProductService productService;

    @Autowired
    protected DeadlineService deadlineService;

    @Autowired
    protected ExecutorService executorService;

    @Autowired
    protected NoteService noteService;

    @Autowired
    protected RealDataProvider realDataProvider;


    public abstract void populate();




    protected AutomatedTestIssue createTestIssue(TestTriage triage) {
        AutomatedTestIssue automationIssue = AutomatedTestIssue.builder()
                .enabled(true)
                .testCase(triage.getTestCase())
                .issueType(IssueType.OPEN)
                .userFixPriorityType(UserFixPriorityType.AUTOMATIC)
                .triager(provider.getUser())
                .testTriage(triage)
                .build();

        automationIssue = automatedTestIssueService.save(automationIssue);
        triage.getTestCase().setAutomatedTestIssue(automationIssue);
        testCaseService.update(triage.getTestCase());

        triage.setTestFailType(TestFailType.TEST_ASSIGNED_TO_FIX);

        return automationIssue;
    }

    protected void createProductIssue(TestTriage triage, String description) {
        IssueTicket issueTicket = DataProvider.getIssueTicket();
        issueTicket.setSummary(description);
        issueTicket.setProduct(triage.getProduct());
        issueTicket.setTestCase(triage.getTestCase());

        issueTicket = issueTicketService.save(issueTicket);

        triage.getTestCase().setIssueTicket(issueTicket);
        testCaseService.update(triage.getTestCase());

        triage.setApplicationFailType(ApplicationFailType.FILED_TICKET);
    }

    protected void createProductIssue(TestTriage triage) {
        createProductIssue(triage, DataProvider.getRandomName("Standard text"));
    }

    protected void triage(TestTriage triage) {
        triage.setTriaged(true);
        triage.setTriager(provider.getUser());
        testTriageService.update(triage);
    }

    protected void clearNewMethod(String prefix) {
        provider.clearForNewBuild();
        provider.setExecutor(null);
        provider.setName(prefix);
    }


}
