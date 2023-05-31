/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.populate;

import com.clarolab.aaa.util.AuthContextHelper;
import com.clarolab.agents.TriageAgent;
import com.clarolab.dto.AutomatedTestIssueDTO;
import com.clarolab.dto.IssueTicketDTO;
import com.clarolab.dto.TestTriageDTO;
import com.clarolab.dto.UserDTO;
import com.clarolab.dto.push.ArtifactDTO;
import com.clarolab.dto.push.DataDTO;
import com.clarolab.event.analytics.*;
import com.clarolab.event.process.ApplicationEventType;
import com.clarolab.event.slack.SlackSpec;
import com.clarolab.event.slack.SlackSpecService;
import com.clarolab.jira.model.JiraConfig;
import com.clarolab.jira.service.JiraConfigService;
import com.clarolab.model.*;
import com.clarolab.model.component.AutomatedComponent;
import com.clarolab.model.component.TestComponentRelation;
import com.clarolab.model.manual.*;
import com.clarolab.model.manual.instruction.ManualTestRequirement;
import com.clarolab.model.manual.instruction.ManualTestStep;
import com.clarolab.model.manual.service.*;
import com.clarolab.model.manual.types.*;
import com.clarolab.model.onboard.Guide;
import com.clarolab.model.onboard.GuideAnswer;
import com.clarolab.model.onboard.GuideType;
import com.clarolab.model.onboard.UserReaction;
import com.clarolab.model.types.IssueType;
import com.clarolab.model.types.StatusType;
import com.clarolab.model.types.TestFailType;
import com.clarolab.model.types.UserFixPriorityType;
import com.clarolab.populate.util.ReportsTestHelper;
import com.clarolab.service.*;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.clarolab.populate.DataProvider.getRandomNumber;
import static com.clarolab.populate.DataProvider.getTimeAdd;

@Component
@Setter
@Transactional(propagation = Propagation.REQUIRED)
public class UseCaseDataProvider {

    private User user;
    private Property property;
    private Product product;
    private JiraConfig jiraConfig;
    private Deadline deadline;
    private Connector connector;
    private Container container;
    private TriageSpec triageSpec;
    private Executor executor;
    private Build build;
    private Report report;
    private TestExecution testExecution;
    private BuildTriage buildTriage;
    private IssueTicket issueTicket;
    private Note note;
    private Notification notification;
    private ApplicationEvent applicationEvent;
    private SlackSpec slackSpec;
    private ExecutorStat executorStat;
    private EvolutionStat evolutionStat;
    private ProductStat productStat;
    private AutomatedTestIssue automatedTestIssue;
    private ApplicationDomain applicationDomain;
    private TestPin pin;
    private NewsBoard newsBoard;
    private Guide guide;
    private UserReaction userReaction;
    private CVSRepository cvsRepository;
    private Pipeline pipeline;
    private PipelineTest pipelineTest;
    private TrendGoal trendGoal;
    private ProductGoal productGoal;
    private CVSLog cvsLog;
    private AutomatedComponent automatedComponent;
    private TestComponentRelation testComponentRelation;

    private String name;
    private long timestamp = 0;
    private boolean useRandom = true;

    private DataDTO dataDTO;

    private ManualTestCase manualTestCase;
    private ProductComponent productComponent;
    private ManualTestRequirement manualTestRequirement;
    private ManualTestStep manualTestStep;
    private ManualTestPlan manualTestPlan;
    private ManualTestExecution manualTestExecution;

    private ManualTestStat manualTestStat;
    private Functionality functionalityEntity;

    @Autowired
    private ManualTestExecutionService manualTestExecutionService;

    @Autowired
    private ManualTestPlanService manualTestPlanService;

    @Autowired
    private ProductComponentService productComponentService;

    @Autowired
    private ManualTestRequirementService manualTestRequirementService;

    @Autowired
    private ManualTestStepService manualTestStepService;

    @Autowired
    private ManualTestCaseService manualTestCaseService;

    @Autowired
    private  ManualTestStatService manualTestStatService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserPreferenceService userPreferenceService;

    @Autowired
    private AuthContextHelper authContextHelper;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private ProductService productService;

    @Autowired
    private JiraConfigService jiraConfigService;

    @Autowired
    private CVSRepositoryService cvsRepositoryService;

    @Autowired
    private DeadlineService deadlineService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private BuildTriageService buildTriageService;

    @Autowired
    private TestExecutionService testExecutionService;

    @Autowired
    private TriageAgent triageAgent;

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
    private IssueTicketService issueTicketService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private SlackSpecService slackSpecService;

    @Autowired
    private ApplicationEventService applicationEventService;

    @Autowired
    private ExecutorStatService executorStatService;

    @Autowired
    private ProductStatService productStatService;

    @Autowired
    private EvolutionStatService evolutionStatService;

    @Autowired
    private RealDataProvider realDataProvider;

    @Autowired
    private AutomatedTestIssueService automatedTestIssueService;

    @Autowired
    private ApplicationDomainService applicationDomainService;

    @Autowired
    private TestPinService testPinService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private NewsBoardService newsBoardService;

    @Autowired
    private GuideService guideService;

    @Autowired
    private UserReactionService userReactionService;

    @Autowired
    private PipelineService pipelineService;

    @Autowired
    private PipelineTestService pipelineTestService;

    @Autowired
    private TrendGoalService trendGoalService;

    @Autowired
    private ProductGoalService productGoalService;

    @Autowired
    private LogService logService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PopulateDemoData populateDemoData;
    private LicenseService licenseService;

    @Autowired
    private FunctionalityService functionalityService;

    @Autowired
    private AutomatedComponentService automatedComponentService;

    @Autowired
    private TestComponentRelationService testComponentRelationService;

    public User getUser() {
        if (user == null) {
            user = DataProvider.getUserAsAdmin();
            user.setUsername(DataProvider.getEmail(name));
            if (name == null || name.isEmpty() || name.contains("opulat")) {
                // If no specific name set, we put a real one
                user.setRealname(realDataProvider.getUserRealName() + getRandomName(" ", 1));
            } else {
                user.setRealname(getRandomName(name));
            }

            user.setTimestamp(getCreationDate());

            user = userService.save(user);
        }
        return user;
    }

    public Product getProduct() {
        if (product == null) {
            product = DataProvider.getProduct();
            product.setTimestamp(getCreationDate());
            product.setName(getRandomName(name));
            product = productService.save(product);
            product.setGoal(getProductGoal());
        }

        return product;
    }


    public JiraConfig getJiraConfig() {
        if (jiraConfig == null) {
            jiraConfig = DataProvider.getJiraConfig();
            jiraConfig.setJiraVersion("cloud");
            jiraConfig.setJiraUrl("https://testing-site-guido.atlassian.net");
            jiraConfig.setFinalToken("");
            jiraConfig.setProjectKey("GT2");
            jiraConfig.setRefreshToken("NZLIhg85L6rvTHHxSaQqp6eeVlP3DxW4VEjt8PbE22qYU");
            jiraConfig.setClosedStateId("41");
            jiraConfig.setInitialStateId("11");
            jiraConfig.setReopenStateId("21");
            jiraConfig.setResolvedStateId("31");
            jiraConfig.setProjectKey("GT2");
            jiraConfig.setProduct(getProduct());
            jiraConfigService.save(jiraConfig);
        }

        return jiraConfig;
    }

    public CVSRepository getCvsRepository() {
        if (cvsRepository == null) {
            cvsRepository = DataProvider.getCvsRepository();
            cvsRepository.setUrl("https://bitbucket.org/TTriage/qa-reports");
            cvsRepository.setLocalPath(System.getProperty("user.dir"));
            cvsRepository.setUsername("ttriage");
            cvsRepository.setBranch("master");
            cvsRepository.setPassword("Claromeco1");

            cvsRepository = cvsRepositoryService.save(cvsRepository);

        }
        return cvsRepository;
    }

    public Deadline getDeadline() {
        if (deadline == null) {
            // requirements
            getProduct();

            // creates the object
            List<Deadline> deadlines = new ArrayList<>(1);
            deadline = DataProvider.getDeadline();
            deadline.setTimestamp(getCreationDate());
            deadline.setDeadlineDate(getCreationDate());
            deadline.setProduct(getProduct());
            deadline.setName(getRandomName(name));
            deadline = deadlineService.save(deadline);

            deadlines.add(deadline);
            getProduct().getDeadlines().add(deadline);
        }

        return deadline;
    }


    public Connector getConnector() {
        if (connector == null) {
            connector = DataProvider.getConnector();
            connector.setName(getRandomName(name));
            connector.setTimestamp(getCreationDate());

            connector = connectorService.save(connector);
        }
        return connector;
    }


    public Container getContainer() {
        if (container == null) {
            // requirements
            getProduct();
            getConnector();

            // creates the object
            container = DataProvider.getContainer();
            container.setProduct(getProduct());
            container.setConnector(getConnector());
            if (name == null || name.isEmpty()) {
                container.setName(getRandomName(realDataProvider.getContainer() + " ", 1));
            } else {
                container.setName(getRandomName(name, 1));
            }

            container = containerService.save(container);
            container.setTimestamp(getCreationDate());

            getConnector().add(container);

            getTriageSpec();
        }
        return container;
    }


    public TriageSpec getTriageSpec() {
        // requirements
        getContainer();
        if (triageSpec == null) {
            // requirements
            getUser();

            // creates the object
            triageSpec = DataProvider.getTriageFlowSpec();
            triageSpec.setContainer(getContainer());
            triageSpec.setTriager(getUser());
            triageSpec.setTimestamp(getCreationDate());

            triageSpec = triageSpecService.save(triageSpec);
        }
        return triageSpec;
    }


    public void setNewTriageSpec() {
        if (triageSpec == null) {
            getTriageSpec();
            return;
        }
        if (triageSpec.getExecutor() == null) {
            // creates the object
            triageSpec = DataProvider.getTriageFlowSpec();
            triageSpec.setContainer(getContainer());
            triageSpec.setExecutor(getExecutor());
            triageSpec.setTriager(getUser());
            triageSpec.setTimestamp(getCreationDate());

            triageSpec = triageSpecService.save(triageSpec);
        } else {
            triageSpec.setExecutor(getExecutor());
            triageSpec.setTriager(getUser());

            triageSpecService.update(triageSpec);
        }
    }


    public SlackSpec getSlackSpec() {
        if (slackSpec == null) {
            // requirements
            getProduct();

            // creates the object
            slackSpec = DataProvider.getSlackSpec();
            slackSpec.setProduct(getProduct());
            slackSpec.setTimestamp(getCreationDate());

            slackSpec = slackSpecService.save(slackSpec);
        }
        return slackSpec;
    }


    public ExecutorStat getExecutorStat() {
        if (executorStat == null) {
            // creates the object
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            long date = getCreationDate();
            String printDate = format.format(date);

            executorStat = DataProvider.getExecutorStat();
            executorStat.setActualDate(printDate);
            executorStat.setExecutionDate(printDate);
            executorStat.setDeadline(printDate);
            executorStat.setTimestamp(date);
            executorStat.setExecutor(getExecutor());
            executorStat.setLastBuildTriage(getBuildTriage());
            executorStat.setBuildNumber(build.getNumber());
            executorStat.setProduct(getProduct());
            executorStat = executorStatService.save(executorStat);
        }

        return executorStat;
    }

    public EvolutionStat getEvolutionStat() {
        if (evolutionStat == null) {
            long date = getCreationDate();
            TrendGoal goal = getTrendGoal();

            evolutionStat = DataProvider.getEvolutionStat();
            evolutionStat.setProduct(getProduct());
            evolutionStat.setExecutor(getExecutor());
            evolutionStatService.save(evolutionStat);

            evolutionStat.setTimestamp(date);
            evolutionStatService.update(evolutionStat);
        }

        return evolutionStat;
    }

    public ProductStat getProductStat() {
        if (productStat == null) {
            // creates the object
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            long date = getCreationDate();
            String printDate = format.format(date);

            productStat = DataProvider.getProductStat();
            productStat.setActualDate(printDate);
            productStat.setDeadline(printDate);
            productStat.setTimestamp(date);
            productStat.setProduct(getProduct());
            productStat = productStatService.save(productStat);
        }

        return productStat;
    }


    public Executor getExecutor() {
        if (executor == null) {
            // requirements
            getContainer();

            // creates the object
            executor = DataProvider.getExecutor();
            if (name == null || name.isEmpty()) {
                executor.setName(getRandomName(realDataProvider.getExecutor() + " ", 1));
            } else {
                executor.setName(getRandomName(name, 1));
            }
            executor.setContainer(getContainer());
            executor.setTimestamp(getCreationDate());

            executor = executorService.save(executor);

            getContainer().add(executor);
        }

        return executor;
    }


    public Property getProperty() {
        if (property == null) {
            property = DataProvider.getProperty();
            property.setName(getRandomName(name));
            property.setTimestamp(getCreationDate());

            property = propertyService.save(property);
        }

        return property;
    }


    public Build getBuild() {
        return getBuild(0);
    }


    public Build getBuild(int buildNumber) {

        if (build == null) {
            // requirements
            TrendGoal trendGoal = getTrendGoal();
            getExecutor().setGoal(trendGoal);

            ProductGoal productGoal = getProductGoal();
            getProduct().setGoal(productGoal);
            // creates the object
            build = DataProvider.getBuild();
            report = DataProvider.getReport();

            build.setTimestamp(getCreationDate());
            if (buildNumber != 0) {
                build.setDisplayName(String.valueOf(buildNumber));
                build.setNumber(buildNumber);
            } else {
                build.setDisplayName(getRandomName(name));
                build.setNumber(0);
            }
            build.setExecutedDate(getCreationDate());
            build.add(DataProvider.getArtifact());

            report.setDescription(getRandomName(name, 20));
            report.setTimestamp(getCreationDate());

            report = reportService.save(report);
            build.setReport(report);

            build.setExecutor(getExecutor());

            build = buildService.save(build);

            getExecutor().add(build);
        }
        return build;
    }


    public Report getReport() {
        if (report == null) {
            getBuild();
        }
        return report;
    }


    private TestExecution getBasicTestExecution(StatusType type) {
        if (testExecution == null) {
            // requirements
            getBuild();
            getReport();

            // creates the object
            TestCase newCase = testCaseService.newOrFind(DataProvider.getRandomName(name), null);
            if (newCase.getId() == null) {
                newCase = testCaseService.save(newCase);
            }
            testExecution = DataProvider.getTestCase();
            testExecution.setReport(getReport());
            testExecution.setName(DataProvider.getRandomName(name));
            testExecution.setSuiteName(DataProvider.getRandomName(name));
            testExecution.setTimestamp(getCreationDate());
            testExecution.setTestCase(newCase);
            testExecution.initTestExecutionSteps();
            if (type != null) {
                testExecution.setStatus(type);
            }

            testExecution = testExecutionService.save(testExecution);

            getBuild().getReport().add(testExecution);

        }
        return testExecution;
    }


    public TestExecution getTestExecution(StatusType type) {
        return getBasicTestExecution(type);
    }


    public TestExecution getTestExecutionFail() {
        return getBasicTestExecution(StatusType.FAIL);
    }


    public TestExecution getTestExecution() {
        // return getBasicTestExecution(null);
        return getTestExecution(StatusType.FAIL);
    }


    public TestExecution getTestExecution(TestTriagePopulate populate) {
        if (testExecution == null) {
            // requirements
            getBuild();
            getReport();

            // creates the object
            TestCase newCase = testCaseService.newOrFind(populate.getTestCaseName(), populate.getPath());
            if (newCase.getId() == null) {
                newCase = testCaseService.save(newCase);
            }
            testExecution = DataProvider.getTestCase();
            testExecution.setReport(getReport());
            testExecution.setSuiteName(populate.getSuiteName());
            if (populate.getStatusAtBuild(build) == null) {
                testExecution.setStatus(StatusType.FAIL);
            } else {
                testExecution.setStatus(populate.getStatusAtBuild(build));
            }
            testExecution.setTimestamp(getCreationDate());
            testExecution.setTestCase(newCase);
            testExecution.setErrorDetails(populate.getErrorDetails());
            testExecution.setErrorStackTrace(populate.getErrorStackTrace());
            testExecution.initTestExecutionSteps();

            testExecution = testExecutionService.save(testExecution);

            getBuild().getReport().add(testExecution);

        }
        return testExecution;
    }


    // Creates a new execution based on the previous one
    public TestExecution getTestExecution(TestExecution testLike) {
        if (testExecution == null) {
            // requirements
            getBuild();
            getReport();

            // creates the object
            TestCase testCase = testLike.getTestCase();
            if (testCase.getId() == null) {
                testCase = testCaseService.save(testCase);
            }
            testExecution = DataProvider.getTestCase();
            testExecution.setReport(getReport());
            testExecution.setStatus(testLike.getStatus());
            testExecution.setTestCase(testCase);
            testExecution.setErrorDetails(testLike.getErrorDetails());
            testExecution.setErrorStackTrace(testLike.getErrorStackTrace());
            testExecution.initTestExecutionSteps();

            testExecution = testExecutionService.save(testExecution);

            getBuild().getReport().add(testExecution);

        }
        return testExecution;
    }

    // PROCESS ACTIONS

    public BuildTriage getBuildTriage() {
        if (buildTriage == null) {
            // requirements
            getBuild();
            getTriageSpec();

            // creates the object
            List<Build> builds = buildService.getNonProcessedBuilds(getExecutor());
            TriageSpec spec = triageSpecService.getTriageSpec(getExecutor());
            if (spec == null) {
                spec = triageSpec;
            }
            for (Build pendingBuild : builds) {
                triageAgent.generateBuildTriageAndTest(pendingBuild, spec);
            }
            triageAgent.activateOnlyLastBuildTriage(getExecutor(), builds, getBuild());

            buildTriage = buildTriageService.getByBuildIs(getBuild());
        }

        return buildTriage;
    }


    public TestTriage getTestCaseTriage() {
        getTestExecution();
        getBuildTriage();
        return testTriageService.findLastTriage(getTestExecution(), getBuild());
    }


    public Note getNote() {
        if (note == null) {
            // Requirements
            getUser();

            note = DataProvider.getNote();
            note.setName(DataProvider.getRandomName(name));
            note.setTimestamp(getCreationDate());
            note.setAuthor(getUser());

            note = noteService.save(note);
        }

        return note;
    }

    public Notification getNotification() {
        if (notification == null) {
            notification = Notification.builder()
                    .subject("DataProvided subject")
                    .description("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin imperdiet tristique lorem ac pharetra. Praesent varius risus eros, quis vestibulum lorem molestie vitae.")
                    .seen(false)
                    .priority(0)
                    .user(getUser())
                    .build();

            notification = notificationService.save(notification);
        }

        return notification;
    }

    public AutomatedTestIssue getAutomatedTestIssue() {
        if (automatedTestIssue == null) {
            // Requirements
            TestTriage test = getTestCaseTriage();

            automatedTestIssue = DataProvider.getAutomatedTestIssue();
            automatedTestIssue.setTestCase(getTestExecution().getTestCase());
            automatedTestIssue.setIssueType(IssueType.OPEN);
            automatedTestIssue.setTestTriage(test);
            automatedTestIssue.setTriager(test.getTriager());
            automatedTestIssue.setUserFixPriorityType(UserFixPriorityType.AUTOMATIC);
            automatedTestIssue.setProduct(getProduct());
            automatedTestIssue = automatedTestIssueService.save(automatedTestIssue);

            test.setTestFailType(TestFailType.TEST_ASSIGNED_TO_FIX);
            testTriageService.update(test);
            test.getTestCase().setAutomatedTestIssue(automatedTestIssue);
            testCaseService.update(test.getTestCase());
        }

        return automatedTestIssue;
    }


    public ApplicationEvent getApplicationEvent() {
        if (applicationEvent == null) {
            getProduct();
            getUser();

            applicationEvent = DataProvider.getApplicationEvent();
            applicationEvent.setTimestamp(getCreationDate());
            applicationEvent.setEventTime(getCreationDate());
            applicationEvent.setSource(getProduct());
            applicationEvent.setType(ApplicationEventType.UNKNOWN);
            applicationEvent.setParameter(getUser());
            applicationEvent.setDisplayName(getRandomName(name));
            applicationEvent.setOriginatingMethod("getApplicationEvent");

            applicationEvent = applicationEventService.save(applicationEvent);
        }

        return applicationEvent;
    }


    public ApplicationDomain getApplicationDomain() {
        if (applicationDomain == null) {

            applicationDomain = DataProvider.getApplicationDomain();
            applicationDomain.setTimestamp(getCreationDate());
            if (name != null && !name.isEmpty()) {
                applicationDomain.setDomainName(name);
            }

            applicationDomain = applicationDomainService.save(applicationDomain);
        }

        return applicationDomain;
    }


    public IssueTicket getIssueTicket() {
        if (issueTicket == null) {
            // requirements
            getProduct();
            getUser();
            getTestExecution();

            issueTicket = DataProvider.getIssueTicket();
            issueTicket.setSummary(DataProvider.getRandomName(name));
            issueTicket.setTimestamp(getCreationDate());
            issueTicket.setProduct(getProduct());
            issueTicket.setAssignee(getUser());
            issueTicket.setTestCase(getTestExecution().getTestCase());

            issueTicket = issueTicketService.save(issueTicket);
        }

        return issueTicket;
    }


    public TestPin getPin() {
        if (pin == null) {
            // requirements
            getUser();
            getTestExecution();

            pin = DataProvider.getPin();
            pin.setCreateDate(getCreationDate());
            pin.setAuthor(getUser());

            pin = testPinService.save(pin);

            getTestCase().setPin(pin);
            testCaseService.update(getTestCase());
        }

        return pin;
    }


    public TestCase getTestCase() {
        return getTestExecution().getTestCase();
    }


    public IssueTicketDTO getIssueTicketDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(getUser().getId());
        userDTO.setUsername(getUser().getUsername());
        userDTO.setRealname(getUser().getRealname());
        userDTO.setAvatar(null);
        IssueTicketDTO issueTicket = new IssueTicketDTO();
        issueTicket.setUrl(DataProvider.getRandomName("http://url.jira.com/Issues/"));
        issueTicket.setTestCaseId(getTestExecution().getTestCase().getId());
        issueTicket.setSummary(DataProvider.getRandomName(name));
        issueTicket.setComponent(DataProvider.getRandomName(name));
        issueTicket.setFile(DataProvider.getRandomName(name));
        issueTicket.setDescription(DataProvider.getRandomName(name));
        issueTicket.setNote(null);
        issueTicket.setPriority(3);
        issueTicket.setDueDate(getTimeAdd(8));
        issueTicket.setIssueType(IssueType.OPEN.name());
        issueTicket.setTimestamp(getCreationDate());
        issueTicket.setProduct(getProduct().getId());
        issueTicket.setAssignee(userDTO);

        return issueTicket;
    }


    public IssueTicketDTO getIssueTicketDTOOnlyWithUrl() {
        IssueTicketDTO issueTicket = new IssueTicketDTO();
        issueTicket.setUrl(DataProvider.getRandomName("http://url.jira.com/Issues/"));
        issueTicket.setTestCaseId(getTestExecution().getTestCase().getId());
        issueTicket.setProduct(getProduct().getId());
        issueTicket.setPriority(5);
        issueTicket.setDueDate(0);
        return issueTicket;
    }


    public AutomatedTestIssueDTO getAutomatedTestIssueDTO(TestTriageDTO testTriageDTO) {
        AutomatedTestIssueDTO dto = new AutomatedTestIssueDTO();
        dto.setIssueType(IssueType.OPEN.name());
        dto.setUserFixPriority(UserFixPriorityType.AUTOMATIC.name());
        dto.setTestCaseId(testTriageDTO.getTestCaseId());
        dto.setTestTriage(testTriageDTO);
        dto.setTriager(testTriageDTO.getTriager());
        return dto;
    }

    public Functionality getFunctionalityEntity() {
        if (functionalityEntity == null) {
            functionalityEntity = Functionality.builder()
                    .enabled(true)
                    .name(getRandomName("functionality", 5))
                    .risk("Risk")
                    .story("Story")
                    .externalId("Funct_001")
                    .build();
            functionalityEntity = functionalityService.save(functionalityEntity);

        }
        return functionalityEntity;
    }

    public ManualTestCase getManualTestCase(int amount) {
        if (manualTestCase == null) {

            ArrayList<ManualTestStep> steps = Lists.newArrayList();
            for (int i = 0; i < amount; i++) {
                steps.add(getNewManualTestStep());
                steps.get(i).setStepOrder(i);
            }

            ArrayList<TechniqueType> list = Lists.newArrayList();
            list.add(TechniqueType.SECURITY);

            ManualTestCase.ManualTestCaseBuilder testBuilder = ManualTestCase.builder()
                    .id(null)
                    .name(getRandomName(name))
                    .product(getProduct())
                    .requirement(getManualTestRequirement())
                    .lastExecution(getManualTestExecution())
                    .steps(steps)
                    .mainStep(steps.get(amount - 1))
                    .priority(TestPriorityType.HIGH)
                    .techniques(list)
                    .suite(SuiteType.SMOKE)
                    .functionality(getRandomName("functionality", 5))
                    .functionalityEntity(getFunctionalityEntity())
                    .owner(getUser())
                    .automationStatus(AutomationStatusType.PENDING_MEDIUM)
                    .automatedTestCase(null)
                    .component1(getProductComponent());
            setProductComponent(null);
            testBuilder.component2(getProductComponent());
            setProductComponent(null);
            testBuilder.component3(getProductComponent());
            setProductComponent(null);

            manualTestCase = manualTestCaseService.save(testBuilder.build());

            for (ManualTestStep step : steps) {
                step.setTestCase(manualTestCase);
                manualTestStepService.update(step);
            }

            manualTestCase.getLastExecution().setTestCase(manualTestCase);
            manualTestCase = manualTestCaseService.update(manualTestCase);
        }
        return manualTestCase;
    }


    public ProductComponent getProductComponent() {
        if (productComponent == null) {
            String componentName = name;
            if (StringUtils.isEmpty(name) || name.length() > 10) {
                componentName = realDataProvider.getContainer();
            } else {
                componentName = getRandomName(name);
            }
            productComponent = ProductComponent.builder()
                    .name(componentName)
                    .description(getRandomName("ProductComponentDescription ", 20))
                    .product(getProduct())
                    .build();

            productComponent = productComponentService.save(productComponent);
        }
        return productComponent;

    }

    public Guide getGuide() {
        if (guide == null) {
            guide = Guide.builder()
                    .pageUrl(getName())
                    .elementType(GuideType.TOOLTIP_TEXT)
                    .html("<text>")
                    .icon("icon")
                    .image("image")
                    .pageCondition("condition")
                    .pageIdentifier("id")
                    .text("text")
                    .title("title")
                    .pageUrl("/triage/SuiteList")
                    .video("video")
                    .build();
            guide = guideService.save(guide);
        }
        return guide;
    }

    public UserReaction getUserReaction() {
        if (userReaction == null) {
            // Requirements
            getGuide();
            getUser();

            userReaction = UserReaction.builder()
                    .guide(getGuide())
                    .user(getUser())
                    .answer(getName())
                    .answerType(GuideAnswer.DISMISS)
                    .build();
            userReaction = userReactionService.save(userReaction);
        }
        return userReaction;
    }


    public ManualTestRequirement getManualTestRequirement() {
        if (manualTestRequirement == null) {
            manualTestRequirement = ManualTestRequirement.builder()
                    .name(getRandomName(name))
                    .build();

            manualTestRequirement = manualTestRequirementService.save(manualTestRequirement);
        }
        return manualTestRequirement;
    }


    public ManualTestStep getNewManualTestStep() {
        manualTestStep = ManualTestStep.builder()
                .step(getRandomName(name))
                .expectedResult(getRandomName("expectedResult ", 5))
                .data(getRandomName("data", 5))
                .build();

        manualTestStep = manualTestStepService.save(manualTestStep);

        return manualTestStep;
    }


    public ManualTestPlan getManualTestPlan() {
        if (manualTestPlan == null) {

            manualTestPlan = ManualTestPlan.builder()
                    .name(getRandomName(name))
                    .description(getRandomName("description ", 10))
                    .environment(getRandomName("environment ", 7))
                    .assignee(getUser())
                    .fromDate(getCreationDate())
                    .toDate(getTimeAdd(getCreationDate(), 5))
                    .status(PlanStatusType.PENDING)
                    .build();

            manualTestPlan = manualTestPlanService.save(manualTestPlan);
        }
        return manualTestPlan;
    }


    public ManualTestExecution getManualTestExecution() {
        if (manualTestExecution == null) {
            manualTestExecution = ManualTestExecution.builder()
                    .testPlan(getManualTestPlan())
                    .assignee(getUser())
                    .status(ExecutionStatusType.PENDING)
                    .environment(getRandomName("environment ", 7))
                    .comment(getRandomName("comment ", 20))
                    .build();

            manualTestExecution = manualTestExecutionService.save(manualTestExecution);
        }
        return manualTestExecution;
    }

    public ManualTestStat getManualTestStat() {
        if (manualTestStat == null){
            manualTestStat=manualTestStat.builder()
                    .updated(getCreationDate())
                    .enabled(true)
                    .totalTests(getCreationDate())
                    .executed(getCreationDate())
                    .pass(0L)
                    .fails(1L)
                    .build();
            manualTestStat = manualTestStatService.save(manualTestStat);
        }
     return  manualTestStat;
    }

    public NewsBoard getNewsBoard(String text) {
        if (newsBoard == null) {
            if (StringUtils.isEmpty(text)) {
                text = getRandomName(getName());
            }
            newsBoard = newsBoardService.create(text, 0, ApplicationEventType.BUILD_TRIAGE_GENERATED_FOR_EXECUTOR);
        }

        return newsBoard;
    }

    public Pipeline getPipeline() {
        if (pipeline == null) {
            pipeline = new Pipeline();
            pipeline.setName(getRandomName(name));
            pipeline.setDescription(getRandomName(name));
            pipeline.setProduct(getProduct());
            pipeline = pipelineService.save(pipeline);

            TriageSpec spec = pipelineService.createOrGetSpec(pipeline);
            spec.setTriager(getUser());
            triageSpecService.update(spec);
        }
        return pipeline;
    }

    public List<PipelineTest> pipelineAssingTests(int amount) {
        List<TestCase> tests = testCaseService.findAll();

        if (amount == 0) {
            amount = 1;
        }
        amount = Integer.min(amount, tests.size());

        List<Long> testIds = new ArrayList<>(amount);

        for (int i = 0; i < amount - 1; i++) {
            int testPosition = (int) (Math.random() * (tests.size() - 1));
            testIds.add(tests.get(testPosition).getId());
        }

        // Adds a not executed test
        setTestExecution(null);
        testIds.add(getTestExecution(StatusType.FAIL).getTestCase().getId());


        pipelineService.assignToPipeline(getPipeline(), testIds);

        return pipelineService.findAll(pipeline);
    }

    public PipelineTest getPipelineTest() {
        if (pipelineTest == null) {
            pipelineTest = new PipelineTest();
            pipelineTest.setPipeline(getPipeline());
            pipelineTest.setTest(getTestCase());
            pipelineTest = pipelineTestService.save(pipelineTest);
        }
        return pipelineTest;
    }

    public TrendGoal getTrendGoal() {
        if (trendGoal == null) {
            trendGoal = TrendGoal.builder()
                    .enabled(true)
                    .expectedGrowth(400L)
                    .requiredGrowth(250L)
                    .expectedTriageDone(20L)
                    .requiredTriageDone(15L)
                    .expectedPassing(30L)
                    .requiredPassing(23L)
                    .expectedStability(18L)
                    .requiredStability(12L)
                    .expectedCommits(125L)
                    .requiredCommits(98L)
                    .build();
            trendGoal = trendGoalService.save(trendGoal);

            getExecutor().setGoal(trendGoal);

            executorService.update(getExecutor());
        }

        return trendGoal;
    }

    public ProductGoal getProductGoal() {
        if (productGoal == null) {
            productGoal = ProductGoal.builder()
                    .enabled(true)
                    .expectedTestCase(400L)
                    .requiredTestCase(250L)
                    .expectedPassRate(20L)
                    .requiredPassRate(15L)
                    .build();
            productGoal = productGoalService.save(productGoal);

            getProduct().setGoal(productGoal);

            productService.update(getProduct());
        }

        return productGoal;
    }

    public AutomatedComponent getAutomatedComponent() {
        if (automatedComponent == null) {
            automatedComponent = new AutomatedComponent();
            automatedComponent.setName(getRandomName(name));
            automatedComponent.setDescription(getRandomName(name));
            automatedComponent = automatedComponentService.save(automatedComponent);
        }
        return automatedComponent;
    }

    public List<TestComponentRelation> testAssignAutomatedComponent(int amount) {
        List<TestCase> testsCases = testCaseService.findAll();

        if (amount == 0) {
            amount = 1;
        }

        amount = Integer.min(amount, testsCases.size());

        List<Long> testCaseIds = new ArrayList<>(amount);

        for (int i = 0; i < amount -1; i++) {
            int testPosition = (int) (Math.random() * (testsCases.size() - 1));
            testCaseIds.add(testsCases.get(testPosition).getId());
        }

        automatedComponentService.setComponentToTests(getAutomatedComponent(), testCaseIds);

        return automatedComponentService.findAllByComponent(automatedComponent);
    }

    public TestComponentRelation getTestComponentRelation() {
        if (testComponentRelation == null) {
            testComponentRelation = new TestComponentRelation();
            testComponentRelation.setComponent(getAutomatedComponent());
            testComponentRelation.setTestCase(getTestCase());
            testComponentRelation = testComponentRelationService.save(testComponentRelation);
        }
        return testComponentRelation;
    }

    public CVSLog getCVSLog() {
        if (cvsLog == null) {
            cvsLog = CVSLog.builder()
                    .enabled(true)
                    .commitHash("c5265382")
                    .commitDate(1604860121)
                    .authorText("francisco.vives@act-on.com")
                    .authorRealname("Francisco Vives")
                    .locationPath("src/test/java/com/clarolab/functional/test/integration/FileRepositoryFunctionalTest.java")
                    .updatedLines(105)
                    .approverText("[skip ci] CVSLog y generar de archivos")
                    .product(getProduct())
                    .build();
            cvsLog = logService.save(cvsLog);
        }
        return cvsLog;
    }


    public long getCreationDate() {
        if (timestamp == 0) {
            return DateUtils.now();
        } else {
            return timestamp;
        }
    }


    public void updateReport() {
        if (report != null) {
            report.updateStats();
            reportService.update(report);
        }
    }


    public DataDTO getDataDTO(StatusType type) {
        Build build = getBuild();

        return DataDTO.builder()
                .buildNumber(build.getNumber() + 1)
                .buildStatus(type.name())
                .buildUrl("SomeURL")
                .jobId(0l)
                .jobName(build.getExecutorName())
                .triggerName("TEST")
                .viewName(build.getExecutor().getContainerName())
                .timestamp(DateUtils.now())
                .artifacts(getArtifactsDTO(1))
                .build();
    }


    public ArtifactDTO getArtifactDTO() {
        return ArtifactDTO.builder().fileName("TEST-Filename.xml").fileType("XML").content(ReportsTestHelper.getRandomJUnitReport()).build();
    }


    public List<ArtifactDTO> getArtifactsDTO(int amount) {
        List<ArtifactDTO> list = Lists.newArrayList();
        for (int x = 0; x < amount; x++) {
            list.add(ArtifactDTO
                    .builder()
                    .fileName("TEST-Filename" + x + ".xml")
                    .fileType("XML")
                    .content(ReportsTestHelper.getRandomJUnitReport())
                    .build());
        }
        return list;
    }


    public void clear() {
        name = null;
        timestamp = 0;
        user = null;
        property = null;
        product = null;
        productGoal = null;
        deadline = null;
        connector = null;
        container = null;
        triageSpec = null;
        executor = null;
        build = null;
        report = null;
        testExecution = null;
        buildTriage = null;
        issueTicket = null;
        note = null;
        applicationEvent = null;
        slackSpec = null;
        executorStat = null;
        evolutionStat = null;
        automatedTestIssue = null;
        applicationDomain = null;
        pin = null;
        testCaseService.cleanNewTests();
        dataDTO = null;
        useRandom = true;
        manualTestCase = null;
        manualTestPlan = null;
        manualTestExecution = null;
        manualTestRequirement = null;
        productComponent = null;
        newsBoard = null;
        guide = null;
        userReaction = null;
        cvsRepository = null;
        pipeline = null;
        pipelineTest = null;
        trendGoal = null;
        automatedComponent = null;
        testComponentRelation = null;
        manualTestStat = null;
        functionalityEntity = null;
    }


    public void clearForNewBuild() {
        setBuild(null);
        setReport(null);
        setTestExecution(null);
        setBuildTriage(null);
        setAutomatedTestIssue(null);
        setIssueTicket(null);
    }


    public void clearContainer() {
        clearForNewBuild();
        setContainer(null);
        setTriageSpec(null);
    }


    // sample method to test the creation of everything
    public void build() {
        getDeadline();
        getProperty();
        getTestExecution();
        getTriageSpec();
        getBuildTriage();
        getTestCaseTriage();
        getIssueTicket();
        getIssueTicketDTO();
        getIssueTicketDTOOnlyWithUrl();
        getNote();
        getNotification();
        getApplicationEvent();
        getSlackSpec();
        getExecutorStat();
        getAutomatedTestIssue();
        getApplicationDomain();
        getPin();
        getNewsBoard(null);
        getGuide();
        getUserReaction();
    }


    public void buildTests(int amount) {
        int connectorAmount = Math.min(amount, 1);
        int amountContainer = 1;
        int buildAmount = 2;
        int deadlineAmount = 1;
        int executorAmount = 1;
        build(amount, connectorAmount, amountContainer, buildAmount, deadlineAmount, executorAmount);
    }


    public void build(int amount) {
        int connectorAmount = Math.min(amount, 3);
        int amountContainer = 2;
        int buildAmount = amount;
        int deadlineAmount = amount;
        int executorAmount = amount;
        build(amount, connectorAmount, amountContainer, buildAmount, deadlineAmount, executorAmount);
    }


    /**
     * Given amount=10 parameter, it creates:
     * Property: 1 (hardcoded)
     * Product: 1 (hardcoded)
     * Connectors: 3 (hardcoded)
     * Deadlines: 10
     * Containers: 10
     * Users: 10
     * Triage Spec: 10
     * Executor: 100
     * Build: 1,000
     * Test Cases: 10,000
     * Issue Ticket: 100 (in each unique test case)
     *
     * @param amount
     */
    public void build(int amount, int connectorAmount, int amountContainer, int buildAmount, int deadlineAmount, int executorAmount) {
        int propertyAmount = 1;

        // creates 1 property (it could be n)
        for (int i = 0; i < propertyAmount; i++) {
            setProperty(null);
            getProperty();
        }

        // creates 1 domain (it could be n)
        for (int i = 0; i < propertyAmount; i++) {
            setApplicationDomain(null);
            getApplicationDomain();
        }

        // creates only one product with 1 connector
        getProduct();
        getSlackSpec();

        // creates deadlines
        for (int i = 0; i < deadlineAmount; i++) {
            setDeadline(null);
            setTimestamp(getTimeAdd(30 * amount));
            getDeadline();
        }
        setTimestamp(0);

        // creates application events
        for (int i = 0; i < amount; i++) {
            setApplicationEvent(null);
            ApplicationEvent event = getApplicationEvent();
            event.setDisplayName(DataProvider.getRandomName("Event: " + i));
            event.setType(ApplicationEventType.TIME_NEW_DAY);
            applicationEventService.update(event);
        }

        // creates 3 connectors
        for (int i = 0; i < connectorAmount; i++) {
            setConnector(null);
            getConnector();
        }

        // creates n containers, executors, builds, tests and mae the triage
        for (int i = 0; i < amountContainer; i++) {
            setContainer(null);
            setExecutor(null);
            setTriageSpec(null);
            setName(null);
            getUser();
            setName(realDataProvider.getContainer());
            getContainer();

            getTriageSpec();
            if (amountContainer == 1) {
                // Set it for everyday
                TriageSpec spec = getTriageSpec();
                spec.setFrequencyCron(DateUtils.tomorrowDeadlineFrequency());
                spec.setEveryWeeks(1);
                triageSpecService.update(spec);
            }

            for (int j = 0; j < executorAmount; j++) {
                setExecutor(null);
                setName(realDataProvider.getExecutor());
                executorService.update(getExecutor());
                getTrendGoal();
                for (int k = 0; k < buildAmount; k++) {
                    timestamp = getTimeAdd(-1 * k);
                    setBuild(null);
                    getBuild(k + 1);
                    for (int l = 0; l < amount; l++) {
                        setTestExecution(null);
                        getTestExecution(realDataProvider.getTest());
                    }
                    updateReport();

                    setBuildTriage(null);
                    getBuildTriage();
                }
                setIssueTicket(null);
                setAutomatedTestIssue(null);
                if (automatedTestIssueService.get(getTestCaseTriage()) == null) {
                    getAutomatedTestIssue();
                }
                if (issueTicketService.find(getTestCaseTriage()) == null) {
                    getIssueTicket();
                }
                setAutomatedTestIssue(null);
                setIssueTicket(null);
            }
            setUser(null);
        }

        // creates execution stats
        for (int i = 0; i < amount; i++) {
            setExecutorStat(null);
            setTimestamp(getTimeAdd(-1 * amount));
            getExecutorStat();
        }

        // Create pipelines
        for (int j = 0; j < executorAmount; j++) {
            getPipeline();
            pipelineAssingTests(amount);
            setPipeline(null);
        }

        // Create a repository
        // getCvsRepository();
    }


    private String getRandomName(String text) {
        if (!useRandom) {
            return text;
        }
        return DataProvider.getRandomName(text);
    }


    private String getRandomName(String text, int size) {
        if (!useRandom) {
            return text;
        }
        return DataProvider.getRandomName(text, size);
    }

    public UserPreference newPreferenceGivenUser(String username) {
        User currentUser = userService.findByUsername(username);
        List<Container> containers = containerService.findAll();

        if (containers.isEmpty() || currentUser == null)
            throw new AssertionError("Current user or container were not found!");

        UserPreference userPreference = UserPreference.builder()
                .enabled(true)
                .updated(DataProvider.getTime())
                .timestamp(DataProvider.getTime())
                .user(currentUser)
                .rowPerPage(1)
                .currentPageNUmber(1)
                .currentContainer(containers.get(0).getId())
                .build();
        return userPreferenceService.save(userPreference);
    }

 /*   public License newLicense() {

        License license;
        long date = DateUtils.now();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, +1);
        long expStamp = cal.getTimeInMillis();


        license = License.builder()
                .created(date)
                .expiration(date + expStamp)
                .expired(false)
                .licenseCode("xxxxxxxx-xxxx-xxxxxxxx-xxxx")
                .free(false)
                .build();

        license = licenseService.save(license);

        return license;

    }*/


    public String getName() {
        return name;
    }
}
