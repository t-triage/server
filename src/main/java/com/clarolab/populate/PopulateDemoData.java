/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.populate;

import com.clarolab.agents.EventTimeAgent;
import com.clarolab.agents.TriageAgent;
import com.clarolab.config.properties.PopulateProperties;
import com.clarolab.connectors.impl.utils.report.ReportUtils;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.dto.push.ArtifactDTO;
import com.clarolab.dto.push.DataDTO;
import com.clarolab.event.analytics.EvolutionStat;
import com.clarolab.event.process.ApplicationEventType;
import com.clarolab.event.slack.SlackSpec;
import com.clarolab.event.slack.SlackSpecService;
import com.clarolab.jira.model.JiraConfig;
import com.clarolab.jira.service.JiraConfigService;
import com.clarolab.logtriage.model.ErrorCase;
import com.clarolab.logtriage.model.EventExecution;
import com.clarolab.logtriage.model.LogAlert;
import com.clarolab.logtriage.model.SearchExecutor;
import com.clarolab.logtriage.service.ErrorCaseService;
import com.clarolab.logtriage.service.EventExecutionService;
import com.clarolab.logtriage.service.LogAlertService;
import com.clarolab.logtriage.service.SearchExecutorService;
import com.clarolab.model.*;
import com.clarolab.model.component.AutomatedComponent;
import com.clarolab.model.helper.tag.TagHelper;
import com.clarolab.model.manual.ManualTestCase;
import com.clarolab.model.manual.ManualTestExecution;
import com.clarolab.model.manual.ManualTestPlan;
import com.clarolab.model.manual.instruction.ManualTestRequirement;
import com.clarolab.model.manual.instruction.ManualTestStep;
import com.clarolab.model.manual.service.*;
import com.clarolab.model.manual.types.AutomationStatusType;
import com.clarolab.model.manual.types.ExecutionStatusType;
import com.clarolab.model.manual.types.SuiteType;
import com.clarolab.model.manual.types.TestPriorityType;
import com.clarolab.model.types.*;
import com.clarolab.service.*;
import com.clarolab.startup.License;
import com.clarolab.util.Constants;
import com.clarolab.util.DateUtils;
import com.clarolab.util.JsonUtils;
import com.clarolab.util.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.extern.java.Log;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;

import static com.clarolab.util.Constants.*;
import static com.clarolab.util.DateUtils.BaseDateFormat;

@Component
@Log
@Transactional(propagation = Propagation.REQUIRED)
public class PopulateDemoData{

    private static boolean WAS_EXECUTED = false;

    @Autowired
    private PopulateProperties properties;

    @Autowired
    private RealDataProvider realDataProvider;

    @Autowired
    private SpecialCasesTestData specialCasesTestData;

    @Autowired
    private SecurityTestData securityTestData;

    @Autowired
    private EventTimeAgent eventTimeAgent;

    @Autowired
    private ApplicationEventService applicationEventService;

    @Autowired
    private ProdRuleTest prodRuleTest;

    @Autowired
    private AutomatedComponentService automatedComponentService;

    private Product product;

    private String JENKINS_URL = "http://dev.ttriage.com/view/";

    private static final String[] CONTAINERS_NAME_ORIGINAL = {"Content Regression Tests", "Smoke Test", "Upgrade Tests", "Localization Tests", "JestReport"};
    private static final String[] EXECUTORS_NAME_ORIGINAL = {"Content", "AntiVirus", "JestReport"};
    private static final String[] PIPELINES_NAME_ORIGINAL = {"Creation", "Security"};
    private static final String[] AUTOMATED_COMPONENTS_NAME_ORIGINAL = {"Internationalization", "Core Product", "Clients", "Performance", "Search", "Users"};
    private static final String[] TEST_NAMES_ORIGINAL = {"createContent", "updateContent", "deleteContent", "searchContent", "createUser", "updateUser", "deleteUser", "searchUser", "createBigContent", "createImageContent", "createXSSContent"};
    //public static final String[] TEST_NAMES = {TEST_NAMES_ORIGINAL[3]};
    public static final String[] TEST_NAMES = TEST_NAMES_ORIGINAL;
    // private static final String[] EXECUTORS_NAME = EXECUTORS_NAME_ORIGINAL;
    private static final String[] EXECUTORS_NAME = {EXECUTORS_NAME_ORIGINAL[0]};
    public static final int BUILD_AMOUNT = 20;
    public static final TestTriagePopulate[] tests = new TestTriagePopulate[TEST_NAMES.length - 3 + 1];
    // tests = new TestTriagePopulate[TEST_NAMES.length];
    // tests = new TestTriagePopulate[TEST_NAMES.length + 5];

    private String suite1 = getRandomName("CRUD for Content and User");
    private User user;
    private License license;
//    private String basePath = "resources/";
    private String jestBasePath ="test_data/";

    protected List<String> jestFiles = Lists.newArrayList(jestBasePath + "admin-storybook-jest-results.json",jestBasePath + "enduser-storybook-jest-results.json");
    Map<String, InputStream> reports;
    @Autowired
    private UserService userService;

    @Autowired
    private JiraConfigService jiraConfigService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private ProductService productService;

    @Autowired
    private DeadlineService deadlineService;

    @Autowired
    private BuildService buildService;

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
    private PipelineService pipelineService;

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private EvolutionStatService evolutionStatService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TriageSpecService triageSpecService;

    @Autowired
    private SlackSpecService slackSpecService;

    @Autowired
    private EventExecutionService eventExecutionService;

    @Autowired
    private SearchExecutorService searchExecutorService;

    @Autowired
    private LogAlertService logAlertService;

    @Autowired
    private ErrorCaseService errorCaseService;

    @Autowired
    private ApplicationDomainService applicationDomainService;

    @Autowired
    private AutomatedTestIssueService automatedTestIssueService;

    @Autowired
    private IssueTicketService issueTicketService;

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private DataSource datasource;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestPinService testPinService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ServiceAuthService serviceAuthService;

    @Autowired
    private EnvironmentVar environmentVar;

    @Autowired
    private ManualTestStepService manualTestStepService;

    @Autowired
    private ManualTestCaseService manualTestCaseService;

    @Autowired
    private ManualTestRequirementService manualTestRequirementService;

    @Autowired
    private ManualTestExecutionService manualTestExecutionService;

    @Autowired
    private ActonPopulate actonPopulate;

    @Autowired
    private ProductComponentService productComponentService;

    @Autowired
    private CVSRepositoryService cvsRepositoryService;

    @Autowired
    private LogService logService;

    @Autowired
    private LicenseService licenseService;
    @Autowired
    private PushService pushService;

    public void createDemoData() {
        if (properties.getEnable() && !WAS_EXECUTED) {
            try {
                if (hasUsers()) {
                    if (properties.getConfiguration().contains("prodtest")) {
                        productionTest();
                    } else {
                        WAS_EXECUTED = true;
                        log.info("Skip Demo Data Population: Already populated");
                    }
                } else {
                    createData();
                    if (environmentVar.isTest()) {
                        WAS_EXECUTED = true;
                    }
                    log.info("All Demo Data Populated!");
                }

                // } catch (ServiceException ex) {
            } catch (Exception ex) {
                log.log(Level.SEVERE, "PopulateDemoDate could not populate correctly", ex);
            }
            for (int i = 0; i < tests.length; i++) {
                tests[i] = null;
            }
            provider.clear();
            provider.setUseRandom(false);

        } else
            log.info("Skip Demo Data Population");
    }

    private void createData() {
        User logged = null;
        provider.setUseRandom(false);
        provider.setName("Demo");
        getTestDemoSpec();
        if (!properties.getConfiguration().contains("sql")) {
            user = createUser();
            createDomain();
        }
        if (shouldPopulateSQL()) {
            this.populateFromSql(properties.getFile());
        }
        if (properties.getConfiguration().contains("all") || containProperty("demo") || containProperty("tdemo")) {
            createDemoLicense();
            createProductAndDeadlines();
            createSuite();
            logged = user;
            createWelcomeMessage();
            createManualTests();
            createManualPlansAndExecutions();
            createApplicationEvents();
            createLogs();
            createEvolutionStats();
            createEventExecutions();
//            createPushReports();
//            createPushReports2();
//            pushCypressReport();
            pushPythonReport();
//            jestReport();
//            createJiraConfig();
        }
        if (properties.getConfiguration().contains("all") || containProperty("populate") || containProperty("tdemo")) {
            populateTon(properties.getAmount());
            if (logged == null) {
                logged = provider.getUser();
            }
        }
        if (properties.getConfiguration().contains("all") || containProperty("jive")) {
            configureProductJive();
        }
        if (properties.getConfiguration().contains("all") || containProperty("ttriage") || containProperty("tdemo")) {
            configureProductTriage();
        }
        if (properties.getConfiguration().contains("all") || containProperty("jenkins")) {
            configureProductTriageJenkins();
        }
        if (properties.getConfiguration().contains("all") || containProperty("lithium")) {
            configureProductLithium();
        }
        if (properties.getConfiguration().contains("all") || containProperty("lithium_screenshots_example")) {
            configureProductLithiumScreenshotsExample();
        }
        if (properties.getConfiguration().contains("all") || containProperty("performance")) {
            populateForPerformanceTest();
        }
        if (properties.getConfiguration().contains("all") || containProperty("jenkins_clarolab")) {
            configureProductJenkinsClarolab();
        }
        if (properties.getConfiguration().contains("all") || containProperty("flux")) {
            configureProductFlux();
        }

        if (properties.getConfiguration().contains("all") || containProperty("minimal")) {
            configureProductMinimal();
        }


        if (properties.getConfiguration().contains("all") || containProperty("populateOld")) {
            populateOld(properties.getAmount(), properties.getMonthAgo(), false);
            if (logged == null) {
                logged = provider.getUser();
            }
        }
        if (properties.getConfiguration().contains("all") || containProperty("populateOldDays")) {
            populateOld(properties.getAmount(), properties.getMonthAgo(), true);
            if (logged == null) {
                logged = provider.getUser();
            }
        }

        if (properties.getConfiguration().contains("all") || containProperty("special")) {
            createDisabledData();
            populateSpecial();
            populateSpecialCases();
        }

        if (properties.getConfiguration().contains("all") || containProperty("security")) {
            populateSecurity();
        }

        if (properties.getConfiguration().contains("all") || containProperty("bambooTest")) {
            populateBambooTest();
        }

        if (properties.getConfiguration().contains("all") || containProperty("acton")) {
            actonPopulate.populate();
        }
    }

    private void createSuite() {

        Connector connector;
        Container container;
        Executor executor;
        Build build;
        TestExecution test;
        List<TestTriage> triages;
        User triagger = DataProvider.getUserAsAdmin();
        connector = createConnector();
        connector = connectorService.save(connector);

        SlackSpec slackSpec = createSlackSpec(product);
        slackSpec = slackSpecService.save(slackSpec);

        container = createContainer(connector, 0);
        container = containerService.save(container);
        connector.add(container);
        TriageSpec spec = createTriageFlowSpec(container, null);
        spec = triageSpecService.save(spec);

        // CREATES A DISABLED EXECUTOR TO VALIDATE IT IS NOT DISPLAYED
        executor = createExecutor(container, 0);
        container.add(executor);
        build = newBuild(1);
        build.setEnabled(false);
        build = buildService.save(build);
        executor.add(build);
        test = createTest(tests[5], 0);
        test = testExecutionService.save(test);
        build.getReport().add(test);
        triageAgent.processExecutor(executor);
        executor.setName("DONTSHOW DISABLED");
        executor.setEnabled(false);
        executorService.update(executor);


        for (int e = 0; e < EXECUTORS_NAME.length; e++) {
            // Creates build

            executor = createExecutor(container, e);
            container.add(executor);

            TriageSpec newSpec = null;
            if (e == 0) {
                // The first executor will overwrite the spec
                // No special reason, just testing
                newSpec = createTriageFlowSpec(container, executor);
                newSpec.setExpectedPassRate(100);
                newSpec.setExpectedMinAmountOfTests(1000);
                newSpec.setEveryWeeks(1);
                newSpec.setFrequencyCron(Constants.DEADLINE_FREQUENCY_EVERY_DAY);
                newSpec = triageSpecService.save(newSpec);
            }

            for (int i = 0; i < BUILD_AMOUNT; i++) {
                build = newBuild(i + 1);
                build = buildService.save(build);
                executor.add(build);

                for (int j = 0; j < tests.length; j++) {
                    test = createTest(tests[j], i);
                    test = testExecutionService.save(test);
                    build.getReport().add(test);
                    tests[j].setNewTestCase(test);
                }
                build.getReport().updateStats();
                reportService.update(build.getReport());


                // Perform planned Triage actions to the previous testTriage
                // in order to see the consequences over the first triage
                if (i == 1) {
                    triages = testTriageService.findAll();

                    for (TestTriagePopulate theTest : tests) {

                        // Creates an automation issue if proper
                        if (theTest.isAutomationIssue()) {
                            TestTriage triageToAutomate = TestTriagePopulate.getTriageFor(triages, theTest.getPreviousTestExecution());
                            AutomatedTestIssue automationIssue = automatedTestIssueService.get(triageToAutomate);

                            if (automationIssue == null) {
                                automationIssue = theTest.createAutomatedTicket(triageToAutomate, user, automationIssue);

                                if (automationIssue != null) {
                                    automationIssue.setTestTriage(triageToAutomate);
                                    automationIssue = automatedTestIssueService.save(automationIssue);
                                    triageToAutomate.getTestCase().setAutomatedTestIssue(automationIssue);
                                    testCaseService.update(triageToAutomate.getTestCase());

                                    triageToAutomate.setTestFailType(TestFailType.TEST_ASSIGNED_TO_FIX);
                                    triageToAutomate.setTriaged(true);
                                    testTriageService.update(triageToAutomate);
                                }
                            }
                        }

                        // Creates an product issue if proper
                        if (theTest.isProductIssue()) {
                            TestTriage triageWithBug = TestTriagePopulate.getTriageFor(triages, theTest.getPreviousTestExecution());
                            IssueTicket issue = issueTicketService.find(triageWithBug);

                            if (issue == null) {
                                issue = theTest.createProductTicket(triageWithBug, user, issue);

                                if (issue != null) {
                                    issue = issueTicketService.save(issue);
                                    triageWithBug.getTestCase().setIssueTicket(issue);
                                    testCaseService.update(triageWithBug.getTestCase());

                                    triageWithBug.setApplicationFailType(ApplicationFailType.FILED_TICKET);
                                    triageWithBug.setTriaged(true);
                                    testTriageService.update(triageWithBug);
                                }
                            }
                        }
                    }
                }

                // Perform planned Triage actions to the previous testTriage
                // in order to see the consequences over the latest triage
                if (i == BUILD_AMOUNT - 1) {
                    triages = testTriageService.findAll();

                    for (TestTriagePopulate theTest : tests) {
                        theTest.findAndSetPreviousTriage(triages);
                        boolean updateTriage = theTest.setPreviousTriageState(user, provider);

                        if (updateTriage) {
                            if (theTest.getPreviousTestTriage().getPin() != null) {
                                theTest.getPreviousTestTriage().getTestCase().setPin(testPinService.save(theTest.getPreviousTestTriage().getPin()));
                                testCaseService.update(theTest.getPreviousTestTriage().getTestCase());
                            }
                            theTest.getPreviousTestTriage().setTriaged(true);
                            theTest.setPreviousTestTriage(testTriageService.update(theTest.getPreviousTestTriage()));
                        }
                    }
                }

                // Make the triage build (this is generally performed in background task)
                if (e == 0) {
                    triageAgent.processExecutor(build.getExecutor());
                } else {
                    triageAgent.processExecutor(build.getExecutor());
                }

            }
        }

        // Creates another Executor with 1 existing test from the previous job
        executor = createExecutor(container, 1);
        container.add(executor);
        build = newBuild(1);
        build = buildService.save(build);
        executor.add(build);
        test = createTest(tests[4], 0);
        test = testExecutionService.save(test);
        build.getReport().add(test);
        triageAgent.processExecutor(executor);


        int amountOfBuildTriage = EXECUTORS_NAME.length * (BUILD_AMOUNT - 1);
        amountOfBuildTriage = (EXECUTORS_NAME.length * BUILD_AMOUNT) + 2;

        List<BuildTriage> builds = buildTriageService.findAll();
        if (amountOfBuildTriage != builds.size()) {
            log.severe("Amount of Builds/export generated vs. stored. Expected: " + amountOfBuildTriage + ". Actual: " + builds.size());
        }

        int amountOfTriages = EXECUTORS_NAME.length * (BUILD_AMOUNT - 1) * tests.length;
        amountOfTriages = (EXECUTORS_NAME.length * BUILD_AMOUNT * tests.length) + 2;
        triages = testTriageService.findAll();
        if (amountOfTriages != triages.size()) {
            log.severe("Amount of Triages generated vs. stored. Expected: " + amountOfTriages + ". Actual: " + triages.size());
        }

        List<Build> allBuilds = buildService.findAll();
        for (Build aBuild : allBuilds) {
            buildService.setReport(aBuild);
        }

        for (TestTriagePopulate theTest : tests) {
            theTest.setTriage(triages);
            theTest.getTestTriage().setTriager(user);
            testTriageService.update(theTest.getTestTriage());

            if (theTest.getTestTriage() == null) {
                log.severe("This test has no triage id: " + theTest.getTestExecution().getId());
            }
        }

        for (TestTriagePopulate theTest : tests) {
            theTest.setTriage(triages);
            if (Math.random() < 0.5) {
                provider.setUser(user);
                provider.setNote(null);
                theTest.getTestTriage().setNote(provider.getNote());

                testTriageService.update(theTest.getTestTriage());
            }
        }

        for (int i = 0; i < PIPELINES_NAME_ORIGINAL.length; i++) {
            provider.setContainer(container);
            provider.setProduct(product);
            createPipeline(i, Integer.min(7, tests.length - 1), product);
        }

        for (int i = 0; i < AUTOMATED_COMPONENTS_NAME_ORIGINAL.length; i++) {
            createAutomatedComponent(i, Integer.min(6, tests.length - 1));
        }

    }

    private User createUser() {
        User user = DataProvider.getUserAsAdmin();
        user.setUsername("info@clarolab.com");
        user.setRealname("Jon Snow");
        user.setPassword(userService.getEncryptedPassword("123123"));
        user.setAvatar(ImageModel
                .builder()
                .enabled(true)
                .name("DEMO IMAGE")
                .timestamp(DateUtils.now())
                .updated(DateUtils.now())
                .data("https://lh3.googleusercontent.com/6itEFCORQlcN17yqiZ25es8Khubvsg38EPrsCTVDxmGiOTy2E914BE0tpGB3LD_c-A")
                .build());

        user.setRoleType(RoleType.ROLE_ADMIN);
        user = userService.save(user);

        createNotifications(user);

        return user;
    }

    private ApplicationDomain createDomain() {
        ApplicationDomain entity = DataProvider.getApplicationDomain();
        entity.setDomainName("clarolab.com");
        entity.setAllowed(true);

        return applicationDomainService.save(entity);
    }

    private Executor createExecutor(Container container, int executorNumber) {
        Executor executor;
        executor = Executor.builder()
                .name(EXECUTORS_NAME_ORIGINAL[executorNumber])
                .description("Job" + executorNumber + " Description " + EXECUTORS_NAME_ORIGINAL[executorNumber])
                .url(container.getUrl() + EXECUTORS_NAME_ORIGINAL[executorNumber])
                .goal(provider.getTrendGoal())
                .build();
        executor.setContainer(container);
        executor = executorService.save(executor);
        return executor;
    }

    private Container createContainer(Connector connector, int e) {
        Container container = Container.builder()
                .name(CONTAINERS_NAME_ORIGINAL[e])
                .description("Job" + e + " Description " + CONTAINERS_NAME_ORIGINAL[e])
                .connector(connector)
                .url(JENKINS_URL + EXECUTORS_NAME[e])
                .product(product)
                .populateMode(PopulateMode.PUSH)
                .reportType(ReportType.JUNIT)
                .build();
        return container;
    }

    private Pipeline createPipeline(int number, int amountTests, Product product) {
        Pipeline entity;
        entity = Pipeline.builder()
                .name("Pipeline for " + PIPELINES_NAME_ORIGINAL[number])
                .description("Pipeline generated " + number + " Description " + PIPELINES_NAME_ORIGINAL[number])
                .product(product)
                .build();
        entity = pipelineService.save(entity);

        pipelineService.createOrGetSpec(entity);

        provider.setPipeline(entity);
        provider.pipelineAssingTests(amountTests);

        return entity;
    }

    private AutomatedComponent createAutomatedComponent(int number, int amountTests) {
        AutomatedComponent entity;
        entity = AutomatedComponent.builder()
                .name(AUTOMATED_COMPONENTS_NAME_ORIGINAL[number])
                .description("Automated component generated ")
                .build();

        entity = automatedComponentService.save(entity);

        provider.setAutomatedComponent(entity);
        provider.testAssignAutomatedComponent(amountTests);

        return entity;
    }

    // https://hooks.slack.com/services/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
    private SlackSpec createSlackSpec(Product product) {
        SlackSpec spec = DataProvider.getSlackSpec();
        spec.setProduct(product);
        spec.setToken("xoxp-546678226724-546101465248-548197344391-132f5c06df0be7ba24c435f35c412a3e");
        spec.setChannel("notification");


        return spec;
    }

    private void createJiraConfig() {
        JiraConfig jiraConfig;
        jiraConfig = JiraConfig.builder()
                .jiraUrl("https://t-triage.atlassian.net")
                .jiraVersion("cloud")
                .projectKey("TT")
                .refreshToken("v1.MehQ8nZH-gJ746v3wjfa9BT7EcwcLcMM8mdxNj130SrRB8FysXjvn-flArPmElsrkRZhZenOM5XFIyMlXPUK2VU")
                .initialStateId("10000")
                .reopenStateId("10001")
                .resolvedStateId("10002")
                .closedStateId("10003")
                .product(productService.findProductByName("MyApp"))
                .reporterEmail("Santiagovitale8@gmail.com")
                .clientID("pYyOful0cLBFRxvulu1hLRsCd14oAEDx")
                .clientSecret("Ts2ZV2Sw_xl8KDngm4QeXcR6csv99dFpPAinKetiRUFux06uvXW2VJDKObm_j2DJ")
                .cloudId("9d29bd33-ec26-468f-a1bc-e30a7b270c08")
                .finalToken("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Ik16bERNemsxTVRoRlFVRTJRa0ZGT0VGRk9URkJOREJDTVRRek5EZzJSRVpDT1VKRFJrVXdNZyJ9.eyJodHRwczovL2F0bGFzc2lhbi5jb20vb2F1dGhDbGllbnRJZCI6InBZeU9mdWwwY0xCRlJ4dnVsdTFoTFJzQ2QxNG9BRUR4IiwiaHR0cHM6Ly9hdGxhc3NpYW4uY29tL2VtYWlsRG9tYWluIjoiZ21haWwuY29tIiwiaHR0cHM6Ly9hdGxhc3NpYW4uY29tL3N5c3RlbUFjY291bnRJZCI6IjYyYTA5ZmMwODc4M2U3MDA2ZjYxYzBmYSIsImh0dHBzOi8vYXRsYXNzaWFuLmNvbS9zeXN0ZW1BY2NvdW50RW1haWxEb21haW4iOiJjb25uZWN0LmF0bGFzc2lhbi5jb20iLCJodHRwczovL2F0bGFzc2lhbi5jb20vdmVyaWZpZWQiOnRydWUsImh0dHBzOi8vYXRsYXNzaWFuLmNvbS9maXJzdFBhcnR5IjpmYWxzZSwiaHR0cHM6Ly9hdGxhc3NpYW4uY29tLzNsbyI6dHJ1ZSwiaXNzIjoiaHR0cHM6Ly9hdGxhc3NpYW4tYWNjb3VudC1wcm9kLnB1czIuYXV0aDAuY29tLyIsInN1YiI6ImF1dGgwfDYyNzQyMGM1ZTAxYzE0MDA2YTUzMjM1ZCIsImF1ZCI6ImFwaS5hdGxhc3NpYW4uY29tIiwiaWF0IjoxNjU3MTk5Nzg1LCJleHAiOjE2NTcyMDMzODUsImF6cCI6InBZeU9mdWwwY0xCRlJ4dnVsdTFoTFJzQ2QxNG9BRUR4Iiwic2NvcGUiOiJtYW5hZ2U6amlyYS1jb25maWd1cmF0aW9uIG1hbmFnZTpqaXJhLXByb2plY3QgbWFuYWdlOmppcmEtd2ViaG9vayB3cml0ZTpqaXJhLXdvcmsgcmVhZDpqaXJhLXdvcmsgcmVhZDpqaXJhLXVzZXIgbWFuYWdlOmppcmEtZGF0YS1wcm92aWRlciBvZmZsaW5lX2FjY2VzcyJ9.JZzDIOBVelvIz1x5N8u7Kbx1hK8NJfTgoW_EyKCm5Gsi1bklXFsS8StiB-jt4janClYbePisuC5wksnXmT6qrUBi_IbU4Dlo4II4TsXh80PSW1qV1mC8D8cNIhonug7uDl8tZG8nxBCg_2GkgBwJY8CfQrZEZS47n8T0xLORP-5kvrM3Eiu9uIXcnPqNZJd8E1aUtmxqEvrHLGXNZW7X-UYjFgi4yXmbuy7A1pGPyt52R3eBm3p2ikbTl2w1w1s8bEJoqkO_ZxTNqkBIgSZnONMkTzRDrpP3Bn3n4mmiZ9Fux0-eCGczwy1qC-st6jM29t4vntHyQvrbOVmg3Dwacw")
                .build();
//        jiraConfigService.save(jiraConfig);
//        jiraConfig = JiraConfig.builder()
//                .jiraUrl("https://t-triage.atlassian.net")
//                .jiraVersion("cloud")
//                .projectKey("ON")
//                .refreshToken("v1.MehQ8nZH-gJ746v3wjfa9BT7EcwcLcMM8mdxNj130SrRB8FysXjvn-flArPmElsrkRZhZenOM5XFIyMlXPUK2VU")
//                .initialStateId("10004")
//                .reopenStateId("10005")
//                .resolvedStateId("10006")
//                .closedStateId("10007")
//                .product(productService.findProductByName("MyApp"))
//                .reporterEmail("Santiagovitale8@gmail.com")
//                .clientID("pYyOful0cLBFRxvulu1hLRsCd14oAEDx")
//                .clientSecret("Ts2ZV2Sw_xl8KDngm4QeXcR6csv99dFpPAinKetiRUFux06uvXW2VJDKObm_j2DJ")
//                .cloudId("9d29bd33-ec26-468f-a1bc-e30a7b270c08")
//                .finalToken("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Ik16bERNemsxTVRoRlFVRTJRa0ZGT0VGRk9URkJOREJDTVRRek5EZzJSRVpDT1VKRFJrVXdNZyJ9.eyJodHRwczovL2F0bGFzc2lhbi5jb20vb2F1dGhDbGllbnRJZCI6InBZeU9mdWwwY0xCRlJ4dnVsdTFoTFJzQ2QxNG9BRUR4IiwiaHR0cHM6Ly9hdGxhc3NpYW4uY29tL2VtYWlsRG9tYWluIjoiZ21haWwuY29tIiwiaHR0cHM6Ly9hdGxhc3NpYW4uY29tL3N5c3RlbUFjY291bnRJZCI6IjYyYTA5ZmMwODc4M2U3MDA2ZjYxYzBmYSIsImh0dHBzOi8vYXRsYXNzaWFuLmNvbS9zeXN0ZW1BY2NvdW50RW1haWxEb21haW4iOiJjb25uZWN0LmF0bGFzc2lhbi5jb20iLCJodHRwczovL2F0bGFzc2lhbi5jb20vdmVyaWZpZWQiOnRydWUsImh0dHBzOi8vYXRsYXNzaWFuLmNvbS9maXJzdFBhcnR5IjpmYWxzZSwiaHR0cHM6Ly9hdGxhc3NpYW4uY29tLzNsbyI6dHJ1ZSwiaXNzIjoiaHR0cHM6Ly9hdGxhc3NpYW4tYWNjb3VudC1wcm9kLnB1czIuYXV0aDAuY29tLyIsInN1YiI6ImF1dGgwfDYyNzQyMGM1ZTAxYzE0MDA2YTUzMjM1ZCIsImF1ZCI6ImFwaS5hdGxhc3NpYW4uY29tIiwiaWF0IjoxNjU3MTk5Nzg1LCJleHAiOjE2NTcyMDMzODUsImF6cCI6InBZeU9mdWwwY0xCRlJ4dnVsdTFoTFJzQ2QxNG9BRUR4Iiwic2NvcGUiOiJtYW5hZ2U6amlyYS1jb25maWd1cmF0aW9uIG1hbmFnZTpqaXJhLXByb2plY3QgbWFuYWdlOmppcmEtd2ViaG9vayB3cml0ZTpqaXJhLXdvcmsgcmVhZDpqaXJhLXdvcmsgcmVhZDpqaXJhLXVzZXIgbWFuYWdlOmppcmEtZGF0YS1wcm92aWRlciBvZmZsaW5lX2FjY2VzcyJ9.JZzDIOBVelvIz1x5N8u7Kbx1hK8NJfTgoW_EyKCm5Gsi1bklXFsS8StiB-jt4janClYbePisuC5wksnXmT6qrUBi_IbU4Dlo4II4TsXh80PSW1qV1mC8D8cNIhonug7uDl8tZG8nxBCg_2GkgBwJY8CfQrZEZS47n8T0xLORP-5kvrM3Eiu9uIXcnPqNZJd8E1aUtmxqEvrHLGXNZW7X-UYjFgi4yXmbuy7A1pGPyt52R3eBm3p2ikbTl2w1w1s8bEJoqkO_ZxTNqkBIgSZnONMkTzRDrpP3Bn3n4mmiZ9Fux0-eCGczwy1qC-st6jM29t4vntHyQvrbOVmg3Dwacw")
//                .build();
//        jiraConfigService.save(jiraConfig);
//        jiraConfig = JiraConfig.builder()
//                .jiraUrl("https://clarotestriage.atlassian.net")
//                .jiraVersion("cloud")
//                .projectKey("PRO")
//                .refreshToken("v1.McYnLM_ryvTmmpI3lKa3BIQ3qobLR0flA0jZTQxU-SVpZF4-O1TikehaQBliz6hKmQZp1iU5x0RcJp9TyoarFek")
//                .initialStateId("10004")
//                .reopenStateId("10005")
//                .resolvedStateId("10006")
//                .closedStateId("10007")
//                .product(productService.findProductByName("Demo"))
//                .reporterEmail("Santiagovitale8@gmail.com")
//                .clientID("BF10P0wVqHon8oz4jT1r4YB5QUBLrD0P")
//                .clientSecret("QS1t2DQjPIsS8Xkg2_HAmpXd7lPIisb3FvgNqjQ51QQfVNyB36FdvlngU4PPY9jb")
//                .cloudId("53036a6f-4752-45f4-bb8e-22e9d9657802")
//                .finalToken("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Ik16bERNemsxTVRoRlFVRTJRa0ZGT0VGRk9URkJOREJDTVRRek5EZzJSRVpDT1VKRFJrVXdNZyJ9.eyJodHRwczovL2F0bGFzc2lhbi5jb20vb2F1dGhDbGllbnRJZCI6IkJGMTBQMHdWcUhvbjhvejRqVDFyNFlCNVFVQkxyRDBQIiwiaHR0cHM6Ly9hdGxhc3NpYW4uY29tL2VtYWlsRG9tYWluIjoiaG90bWFpbC5jb20iLCJodHRwczovL2F0bGFzc2lhbi5jb20vc3lzdGVtQWNjb3VudElkIjoiNjJjYzJhNDdlNTQ2ZThlYWI4ZWUyNjIwIiwiaHR0cHM6Ly9hdGxhc3NpYW4uY29tL3N5c3RlbUFjY291bnRFbWFpbERvbWFpbiI6ImNvbm5lY3QuYXRsYXNzaWFuLmNvbSIsImh0dHBzOi8vYXRsYXNzaWFuLmNvbS92ZXJpZmllZCI6dHJ1ZSwiaHR0cHM6Ly9hdGxhc3NpYW4uY29tL2ZpcnN0UGFydHkiOmZhbHNlLCJodHRwczovL2F0bGFzc2lhbi5jb20vM2xvIjp0cnVlLCJpc3MiOiJodHRwczovL2F0bGFzc2lhbi1hY2NvdW50LXByb2QucHVzMi5hdXRoMC5jb20vIiwic3ViIjoiYXV0aDB8NjJjYzI3NTkyYzgwMWVkYzMyODRiMDM3IiwiYXVkIjoiYXBpLmF0bGFzc2lhbi5jb20iLCJpYXQiOjE2NTc1NjMzOTksImV4cCI6MTY1NzU2Njk5OSwiYXpwIjoiQkYxMFAwd1ZxSG9uOG96NGpUMXI0WUI1UVVCTHJEMFAiLCJzY29wZSI6Im1hbmFnZTpqaXJhLWNvbmZpZ3VyYXRpb24gbWFuYWdlOmppcmEtcHJvamVjdCBtYW5hZ2U6amlyYS13ZWJob29rIHdyaXRlOmppcmEtd29yayByZWFkOmppcmEtd29yayByZWFkOmppcmEtdXNlciBtYW5hZ2U6amlyYS1kYXRhLXByb3ZpZGVyIG9mZmxpbmVfYWNjZXNzIn0.mDNa0k6W4Iu3OkYSs8rpPVAsMSnQVxITcPwwK3Sps0QEN3RDtTyJbOk7wYu16dqGM1nU4WWM90_UD5_V6d-tdt43LDBPTrst26gpLKIUqyZnGEqaSjgeo4fSy2wNOfv6VU3WxdSnHJHTFTW6w4UzX3xvm5Nxjy6n-siVmj6LUn6LB3ru7jSJFSB6oyIzinOqDzpjfqNtYWQ0MconR65fCWnRj5jH9yZluL6W6fWRAcyEgHcunq3wJ_LrK1ISFfOJH24dboNGdqvgFQiX8X-_yHQFpsJHZ0KxdK1AYjTyoga1WhAwb0C_J-Mq31aLVetZ8pBh6XCjacM6BfxDIgBBEQ")
//                .build();
//        jiraConfigService.save(jiraConfig);
//        jiraConfig = JiraConfig.builder()
//                .jiraUrl("https://clarotestriage.atlassian.net")
//                .jiraVersion("cloud")
//                .projectKey("TES")
//                .refreshToken("v1.McYnLM_ryvTmmpI3lKa3BIQ3qobLR0flA0jZTQxU-SVpZF4-O1TikehaQBliz6hKmQZp1iU5x0RcJp9TyoarFek")
//                .initialStateId("10004")
//                .reopenStateId("10005")
//                .resolvedStateId("10006")
//                .closedStateId("10007")
//                .product(productService.findProductByName("Demo"))
//                .reporterEmail("Santiagovitale8@gmail.com")
//                .clientID("BF10P0wVqHon8oz4jT1r4YB5QUBLrD0P")
//                .clientSecret("QS1t2DQjPIsS8Xkg2_HAmpXd7lPIisb3FvgNqjQ51QQfVNyB36FdvlngU4PPY9jb")
//                .cloudId("53036a6f-4752-45f4-bb8e-22e9d9657802")
//                .finalToken("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Ik16bERNemsxTVRoRlFVRTJRa0ZGT0VGRk9URkJOREJDTVRRek5EZzJSRVpDT1VKRFJrVXdNZyJ9.eyJodHRwczovL2F0bGFzc2lhbi5jb20vb2F1dGhDbGllbnRJZCI6IkJGMTBQMHdWcUhvbjhvejRqVDFyNFlCNVFVQkxyRDBQIiwiaHR0cHM6Ly9hdGxhc3NpYW4uY29tL2VtYWlsRG9tYWluIjoiaG90bWFpbC5jb20iLCJodHRwczovL2F0bGFzc2lhbi5jb20vc3lzdGVtQWNjb3VudElkIjoiNjJjYzJhNDdlNTQ2ZThlYWI4ZWUyNjIwIiwiaHR0cHM6Ly9hdGxhc3NpYW4uY29tL3N5c3RlbUFjY291bnRFbWFpbERvbWFpbiI6ImNvbm5lY3QuYXRsYXNzaWFuLmNvbSIsImh0dHBzOi8vYXRsYXNzaWFuLmNvbS92ZXJpZmllZCI6dHJ1ZSwiaHR0cHM6Ly9hdGxhc3NpYW4uY29tL2ZpcnN0UGFydHkiOmZhbHNlLCJodHRwczovL2F0bGFzc2lhbi5jb20vM2xvIjp0cnVlLCJpc3MiOiJodHRwczovL2F0bGFzc2lhbi1hY2NvdW50LXByb2QucHVzMi5hdXRoMC5jb20vIiwic3ViIjoiYXV0aDB8NjJjYzI3NTkyYzgwMWVkYzMyODRiMDM3IiwiYXVkIjoiYXBpLmF0bGFzc2lhbi5jb20iLCJpYXQiOjE2NTc1NjMzOTksImV4cCI6MTY1NzU2Njk5OSwiYXpwIjoiQkYxMFAwd1ZxSG9uOG96NGpUMXI0WUI1UVVCTHJEMFAiLCJzY29wZSI6Im1hbmFnZTpqaXJhLWNvbmZpZ3VyYXRpb24gbWFuYWdlOmppcmEtcHJvamVjdCBtYW5hZ2U6amlyYS13ZWJob29rIHdyaXRlOmppcmEtd29yayByZWFkOmppcmEtd29yayByZWFkOmppcmEtdXNlciBtYW5hZ2U6amlyYS1kYXRhLXByb3ZpZGVyIG9mZmxpbmVfYWNjZXNzIn0.mDNa0k6W4Iu3OkYSs8rpPVAsMSnQVxITcPwwK3Sps0QEN3RDtTyJbOk7wYu16dqGM1nU4WWM90_UD5_V6d-tdt43LDBPTrst26gpLKIUqyZnGEqaSjgeo4fSy2wNOfv6VU3WxdSnHJHTFTW6w4UzX3xvm5Nxjy6n-siVmj6LUn6LB3ru7jSJFSB6oyIzinOqDzpjfqNtYWQ0MconR65fCWnRj5jH9yZluL6W6fWRAcyEgHcunq3wJ_LrK1ISFfOJH24dboNGdqvgFQiX8X-_yHQFpsJHZ0KxdK1AYjTyoga1WhAwb0C_J-Mq31aLVetZ8pBh6XCjacM6BfxDIgBBEQ")
//                .build();
//        jiraConfigService.save(jiraConfig);
//
    }

    private Connector createConnector() {
        Connector connector;
        connector = Connector.builder().name("Jenkins").url(JENKINS_URL).type(ConnectorType.JENKINS).userName("testUser").build();
        return connector;
    }

    private TestExecution createTest(TestTriagePopulate test, int build) {
        String suiteName = suite1;
        if (test.getSuiteName() != null && !test.getSuiteName().isEmpty()) {
            suiteName = test.getSuiteName();
        }

        TestCase testCase = testCaseService.newOrFind(test.getTestCaseName(), "com.clarolab");
        TestExecution newTest = TestExecution.builder()
                .testCase(testCase)
                .errorStackTrace(test.getErrorStackTrace())
                .errorDetails(test.getErrorDetails())
                .suiteName(suiteName)
                .screenshotURL("http://www.ttriage.com/tsections/img/workspace.png")
                .status(test.getBuildSpec().get(build))
                .build();

        if (test.getSteps() != null) {
            for (String step : test.getSteps()) {
                TestExecutionStep testStep = new TestExecutionStep().builder()
                        .name(step)
                        .parameters("param1")
                        .build();
                newTest.add(testStep);
            }
        }

        return newTest;
    }

    private TriageSpec createTriageFlowSpec(Container container, Executor executor) {
        TriageSpec spec = DataProvider.getTriageFlowSpec();
        spec.setExpectedMinAmountOfTests(5);
        spec.setExpectedPassRate(95);
        spec.setPriority(1);
        spec.setFrequencyCron(Constants.DEADLINE_FREQUENCY_EVERY_DAY);
        spec.setEveryWeeks(1);
        spec.setExecutor(executor);
        spec.setContainer(container);
        spec.setTriager(user);

        return spec;
    }

    private TriageSpec createTriageFlowSpec(Container container, Executor executor, User aUser) {
        TriageSpec spec = DataProvider.getTriageFlowSpec();
        spec.setExpectedMinAmountOfTests(10);
        spec.setExpectedPassRate(85);
        spec.setFrequencyCron(Constants.DEADLINE_FREQUENCY_3DAYS);
        spec.setEveryWeeks(1);
        spec.setExecutor(executor);
        spec.setContainer(container);
        spec.setTriager(aUser);

        return spec;
    }


    private void createProductAndDeadlines() {
        // Test product that will be used by the populated tests
        product = Product.builder()
                .name("MyApp")
                .description("Product that inspire your road.")
                .packageNames("com.clarolab")
                .logPattern("%d %p %C{1.} [%t] %m%n")
                .enabled(true)
                .build();

        // Create repository wont be created by now
        if (false) {
            List<CVSRepository> repositories = new ArrayList();

            CVSRepository cvsRepository = provider.getCvsRepository();
            cvsRepository.setProduct(product);
            repositories.add(cvsRepository);

            product.setRepositories(repositories);
        }

        // Creates a couple of future deadlines (in 8 days and 30 days)
        List<Deadline> deadlines = new ArrayList();

        Deadline deadline = DataProvider.getDeadline();
        deadline.setProduct(product);
        deadline.setDescription("Version 1 focused on new content");
        deadline.setName("v1.0");
        deadlines.add(deadline);

        deadline = DataProvider.getDeadline();
        deadline.setProduct(product);
        deadline.setDescription("Version 2 will contain the most amazing features");
        deadline.setName("v2.0");
        deadlines.add(deadline);
        // deadlines.add(deadlineService.save(deadline));
        deadlines.add(deadline);
        product.setDeadlines(deadlines);

        product = productService.save(product);
    }

    public static Build newBuild(int buildNumber) {
        Build build = DataProvider.getBuild();
        Report report = DataProvider.getReport();
        report.setFailCount(13);
        report.setPassCount(3);
        report.setSkipCount(0);
        report.setTotalTests();
        report.setProductVersion(DataProvider.getRandomName("version-") + "-" + buildNumber);
        build.setStatus(StatusType.FAIL);
        build.setReport(report);
        build.setNumber(buildNumber);
        report.setDescription(getRandomName("Build" + buildNumber));

        // Setting build date, for example build 1 may be 20 days old, build 2 19 days old.
        build.setTimestamp(DataProvider.getTimeAdd(buildNumber - BUILD_AMOUNT));

        return build;
    }

    public static String getRandomName(String base) {
        // return base + "(" + RandomStringUtils.randomAlphabetic(4) + ")";
        return base;
    }

    private void createNotifications(User user) {

        String[] subjects = {"Welcome to t-Triage", "Notification test", "Random subject", "And another random subject", "Old subject"};
        String[] descriptions = {"Welcome.", "This is a description test.", "With a random description.", "And another random description too.", "Old description"};

        for (int x = 0; x < subjects.length; x++) {
            Notification notification = provider.getNotification();

            // Change this line in case of adding many more notifications
            notification.setTimestamp(DateUtils.offSetDays(-x * x * x));
            notification.setSeen(x != 0);
            notification.setSubject(subjects[x]);
            notification.setDescription(descriptions[x]);
            notification.setUser(user);

            notificationService.update(notification);
            provider.setNotification(null);
        }

    }


    // Only used for debugging purposes
    public static TestTriagePopulate[] getTestDemoSpec2() {
        // build, status
        Map<Integer, StatusType> buildSpec;
        TestTriagePopulate test;
        int i = 0;

        /**
         * Test 3: FAIL
         * Test 3: anterior: permanent, consecutivePasses = 5
         * Validate: PERMANENT
         */
        test = new TestTriagePopulate();
        buildSpec = new HashMap<>();
        test.setBuildSpec(buildSpec);
        tests[i] = test;
        test.setTestCaseName(getRandomName(TEST_NAMES[i]));
        test.setDescription("Test " + (i + 1) + ". Passed few, Fail All | Permanent > Permanent");
        setAll(buildSpec, StatusType.FAIL);
        buildSpec.put(0, StatusType.PASS); // first 5 builds passed
        buildSpec.put(1, StatusType.PASS); // first 5 builds passed
        buildSpec.put(2, StatusType.PASS); // first 5 builds passed
        test.setExpectedStatus(StateType.PERMANENT);
        test.setNewTriageStatus(StateType.PERMANENT);
        i = i + 1;

        return tests;

    }

    public static TestTriagePopulate[] getTestDemoSpec() {

        RealDataProvider realDataProvider = new RealDataProvider();
        // build, status
        Map<Integer, StatusType> buildSpec;
        TestTriagePopulate test;
        int i = 0;
        TestTriagePopulate testWithError = realDataProvider.getBasicTest();

        /**
         * Test 1: pass
         * Test 1: previa: fail, label = flaky
         * Validate: PASS
         */
        test = new TestTriagePopulate();
        buildSpec = new HashMap<>();
        test.setBuildSpec(buildSpec);
        tests[i] = test;
        test.setTestCaseName(getRandomName(TEST_NAMES[i]));
        test.setDescription("Test " + (i + 1) + ".  Fail Flaky | Pass > PASS");
        setAll(buildSpec, StatusType.FAIL);
        buildSpec.put(BUILD_AMOUNT - 1, StatusType.PASS);
        buildSpec.put(BUILD_AMOUNT / 2, StatusType.PASS); // to make flaky
        buildSpec.put(BUILD_AMOUNT / 3, StatusType.PASS); // to make flaky
        test.setTag(TagHelper.FLAKY_TRIAGE);
        test.setExpectedStatus(StateType.PASS);
        i = i + 1;

        /**
         * Test 2: pass
         * Test 2: previa: permanent
         * Validate: PASS
         */
        test = new TestTriagePopulate();
        buildSpec = new HashMap<>();
        test.setBuildSpec(buildSpec);
        tests[i] = test;
        test.setTestCaseName(getRandomName(TEST_NAMES[i]));
        test.setDescription("Test " + (i + 1) + ". Fail | Pass > NewPass");
        test.setPin(true);
        setAll(buildSpec, StatusType.FAIL);
        buildSpec.put(BUILD_AMOUNT - 1, StatusType.PASS);
        test.setAutomationIssue(true);
        test.setProductIssue(true);
        test.setExpectedStatus(StateType.PASS);
        i = i + 1;


        /**
         * Test 3: FAIL
         * Test 3: anterior: permanent, consecutivePasses = 5
         * Validate: FAIL
         */
        test = new TestTriagePopulate();
        buildSpec = new HashMap<>();
        test.setBuildSpec(buildSpec);
        tests[i] = test;
        test.setTestCaseName(getRandomName(TEST_NAMES[i]));
        test.setDescription("Test " + (i + 1) + ". Passed few, Fail All | Pass > Fail");
        setAll(buildSpec, StatusType.FAIL);
        buildSpec.put(0, StatusType.PASS); // first 5 builds passed
        buildSpec.put(1, StatusType.PASS); // first 5 builds passed
        buildSpec.put(2, StatusType.PASS); // first 5 builds passed
        test.setExpectedStatus(StateType.FAIL);
        test.setNewTriageStatus(StateType.PERMANENT);
        test.setSteps(new String[]{"Login", "Create Content", "Delete Content"});
        i = i + 1;

        /**
         * Test 4: FAIL
         * Test 4: anterior: New Fail, bugFiled isEmpty and anterior.failCount < 5
         * Validate: StatusType
         */
        test = new TestTriagePopulate();
        buildSpec = new HashMap<>();
        test.setBuildSpec(buildSpec);
        tests[i] = test;
        test.setTestCaseName(getRandomName(TEST_NAMES[i]));
        test.setDescription("Test " + (i + 1) + ". Passed solid, last Fail > NewFail");
        setAll(buildSpec, StatusType.PASS);
        buildSpec.put(0, StatusType.FAIL);
        buildSpec.put(1, StatusType.FAIL);
        buildSpec.put(BUILD_AMOUNT - 1, StatusType.FAIL); // last failed
        test.setExpectedStatus(StateType.NEWFAIL);
        test.setPin(true);
        test.setSteps(new String[]{"Login", "Create Content with body: Test", "Go to Search", "Type: Test", "Verify content appears"});
        i = i + 1;

        /**
         * Test 5: FAIL
         * 5 last failed
         * Validate: FAIL
         */
        test = new TestTriagePopulate();
        buildSpec = new HashMap<>();
        test.setBuildSpec(buildSpec);
        tests[i] = test;
        test.setTestCaseName(getRandomName(TEST_NAMES[i]));
        test.setErrorDetails(testWithError.getErrorDetails());
        test.setErrorStackTrace(testWithError.getErrorStackTrace());
        test.setDescription("Test " + (i + 1) + ". Last 5 has filed | Fail > Fail");
        setAll(buildSpec, StatusType.PASS);
        buildSpec.put(BUILD_AMOUNT - 5, StatusType.FAIL); // last 5 failed
        buildSpec.put(BUILD_AMOUNT - 4, StatusType.FAIL); // last 5 failed
        buildSpec.put(BUILD_AMOUNT - 3, StatusType.FAIL); // last 5 failed
        buildSpec.put(BUILD_AMOUNT - 2, StatusType.FAIL); // last 5 failed
        buildSpec.put(BUILD_AMOUNT - 1, StatusType.FAIL); // last 5 failed
        test.setExpectedStatus(StateType.FAIL);
        test.setTag(TagHelper.FLAKY_TRIAGE);
        test.setComment("User comments based on previous triage");
        i = i + 1;

        /**
         * Test 6: UNDEFINED
         * Validate: FAIL
         */
        /*test = new TestTriagePopulate();
        buildSpec = new HashMap<>();
        test.setBuildSpec(buildSpec);
        tests[i] = test;
        test.setTestCaseName(getRandomName(TEST_NAMES[i]));
        test.setDescription("Test " + (i + 1) + ". Undefined | Fail > Fail");
        setAll(buildSpec, StatusType.UNKNOWN);
        buildSpec.put(BUILD_AMOUNT - 1, StatusType.FAIL);
        test.setExpectedStatus(StateType.FAIL);
        i = i + 1;*/

        /**
         * Test 7: FAIL
         * Test 7: anterior: snooze, permanent
         * Validate: Permanent
         */
        test = new TestTriagePopulate();
        buildSpec = new HashMap<>();
        test.setBuildSpec(buildSpec);
        tests[i] = test;
        test.setTestCaseName(getRandomName(TEST_NAMES[i]));
        test.setDescription("Test " + (i + 1) + ". Fail| Fail snooze > Permanent");
        setAll(buildSpec, StatusType.FAIL);
        test.setErrorDetails(testWithError.getErrorDetails());
        test.setErrorStackTrace(testWithError.getErrorStackTrace());
        test.setExpectedStatus(StateType.PERMANENT);
        test.setAutomationIssue(true);
        test.setProductIssue(true);
        test.setSnooze(getTimeAdd(1));
        i = i + 1;

        /**
         * Test 8: Skip
         * Validate: Fail
         */
        /*test = new TestTriagePopulate();
        buildSpec = new HashMap<>();
        test.setBuildSpec(buildSpec);
        tests[i] = test;
        test.setTestCaseName(getRandomName(TEST_NAMES[i]));
        test.setDescription("Test " + (i + 1) + ". Skip > Fail");
        setAll(buildSpec, StatusType.SKIP);
        test.setExpectedStatus(StateType.FAIL);
        i = i + 1;*/

        /**
         * Test 9: Invalid / Cancelled
         * Validate: FAIL
         */
        /*test = new TestTriagePopulate();
        buildSpec = new HashMap<>();
        test.setBuildSpec(buildSpec);
        tests[i] = test;
        test.setTestCaseName(getRandomName(TEST_NAMES[i]));
        test.setDescription("Test " + (i + 1) + ". Invalid | Canceled > FAIL");
        setAll(buildSpec, StatusType.CANCELLED);
        test.setExpectedStatus(StateType.FAIL);
        i = i + 1;*/

        /**
         * Test 10: Pass
         * Validate: Pass
         */
        test = new TestTriagePopulate();
        buildSpec = new HashMap<>();
        test.setBuildSpec(buildSpec);
        tests[i] = test;
        test.setTestCaseName(getRandomName(TEST_NAMES[i]));
        test.setDescription("Test " + (i + 1) + ". Pass > Pass");
        setAll(buildSpec, StatusType.PASS);
        test.setExpectedStatus(StateType.PASS);
        i = i + 1;

        /**
         * Test 11: Permanent
         * Test 11: anterior: permanent, consecutivePasses = 5
         * Validate: PASS
         */
        test = new TestTriagePopulate();
        buildSpec = new HashMap<>();
        test.setBuildSpec(buildSpec);
        tests[i] = test;
        test.setTestCaseName(getRandomName(TEST_NAMES[i]));
        test.setDescription("Test " + (i + 1) + ". Passed 5, Fail All | Pass > NewPass");
        setAll(buildSpec, StatusType.FAIL);
        buildSpec.put(BUILD_AMOUNT - 1, StatusType.PASS);
        buildSpec.put(0, StatusType.PASS); // first 5 builds passed
        buildSpec.put(1, StatusType.PASS); // first 5 builds passed
        test.setAutomationIssue(true);
        test.setExpectedStatus(StateType.PASS);
        test.setNewTriageStatus(StateType.PASS);
        i = i + 1;


        for (int j = 0; j < 1; j++) {
            /**
             * Test N: Fail
             * Test 11: anterior: pass= 5
             * Validate: Fail
             */
            test = new TestTriagePopulate();
            buildSpec = new HashMap<>();
            test.setBuildSpec(buildSpec);
            test.setErrorDetails(testWithError.getErrorDetails());
            test.setErrorStackTrace(testWithError.getErrorStackTrace());
            tests[i] = test;
            test.setTestCaseName(realDataProvider.getBasicTest().getTestCaseName());
            test.setSuiteName(realDataProvider.getBasicTest().getSuiteName());
            test.setDescription("5 Tests With Same Error | Pass > NewPass");
            setAll(buildSpec, StatusType.PASS);
            buildSpec.put(BUILD_AMOUNT - 5, StatusType.FAIL); // last 5 failed
            buildSpec.put(BUILD_AMOUNT - 4, StatusType.FAIL); // last 5 failed
            buildSpec.put(BUILD_AMOUNT - 3, StatusType.FAIL); // last 5 failed
            buildSpec.put(BUILD_AMOUNT - 2, StatusType.FAIL); // last 5 failed
            buildSpec.put(BUILD_AMOUNT - 1, StatusType.FAIL); // last 5 failed
            test.setExpectedStatus(StateType.FAIL);
            i = i + 1;
        }


        return tests;

    }

    public static long getTimeAdd(int days) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());
        cal.add(Calendar.DAY_OF_MONTH, days);
        timestamp = new Timestamp(cal.getTime().getTime());
        return timestamp.getTime();
    }

    private static void setAll(Map<Integer, StatusType> builds, StatusType status) {
        TestTriagePopulate.setBuildSpec(builds, status, 0, BUILD_AMOUNT);
    }

    private void configureProductMinimal() {
        provider.setName("Min");
        provider.setUser(user);
        provider.getDeadline();
        provider.getTestExecutionFail();
        provider.setTestExecution(null);
        provider.getTestExecutionFail();
        provider.getBuildTriage();
        provider.getAutomatedTestIssue();
        provider.getIssueTicket();


    }

    private void populateTon(int amount) {
        String oldName = provider.getName();

        // Create a Populate Product
        provider.clear();
        provider.setUseRandom(false);

        provider.setName("Populate");
        provider.build(amount);

        for (int i = 0; i < amount; i++) {
            provider.setName(StringUtils.randomString(10));
            provider.setManualTestCase(null);
            provider.getManualTestCase(3);
        }

        provider.clear();
        provider.setUser(user);
        provider.setName(oldName);
    }

    private void populateSpecial() {
        String oldName = provider.getName();

        // Create a Populate Product
        provider.clear();

        // Creates a build without tests
        provider.setName("WithoutTests");
        provider.setTestExecution(null);
        provider.setBuildTriage(null);
        provider.setExecutor(null);
        provider.setBuild(null);
        provider.getBuild();
        provider.getBuildTriage();

        // Create lot of tests in 1 job
        provider.clear();
        provider.setUser(user);
        provider.setUseRandom(false);
        provider.setName("Analytics");
        provider.buildTests(DEFAULT_MAX_TESTCASES_PER_DAY + 2);
        provider.buildTests(2);

        provider.clear();
        provider.setUseRandom(false);
        provider.setName("WithoutJobs");
        provider.getContainer();

        provider.clear();
        provider.setUseRandom(false);
        provider.setName("JaneDoe");
        provider.getUser();
        provider.setName("Search");
        provider.getProduct();
        provider.setName("Deploy");
        provider.getTestExecution(StatusType.PASS);
        provider.getTestCaseTriage();
        provider.updateReport();


        provider.clear();
        provider.setUser(user);
        provider.setName(oldName);
    }

    public void populateOld(int amount, int values, boolean byMonth) {
        populateTon(amount);

        if (byMonth) {
            populateOldMonths(values);
        } else {
            populateOldDays(values);
        }

    }

    private void populateOldMonths(int monthsAgo) {
        List<TestTriage> allTestTriages = testTriageService.findAll();
        Calendar instance = Calendar.getInstance();
        int i = 0;
        for (TestTriage tt : allTestTriages) {

            instance.set(Calendar.MONTH, i);
            tt.setTimestamp(instance.getTimeInMillis());
            testTriageService.update(tt);

            TestCase testCase = tt.getTestCase();
            testCase.setTimestamp(instance.getTimeInMillis());
            testCaseService.update(testCase);

            i = (i + 1) % monthsAgo;
        }

        String[] criteria = {"enabled:true"};
        List<AutomatedTestIssue> automatedTestIssues = automatedTestIssueService.findAll(criteria, PageRequest.of(0, 10)).getContent();

        i = 0;
        for (AutomatedTestIssue ati : automatedTestIssues) {

            instance.set(Calendar.MONTH, i);
            ati.setTimestamp(instance.getTimeInMillis());
            automatedTestIssueService.update(ati);
            i = (i + 1) % monthsAgo;
        }
    }

    private void populateOldDays(int daysAgo) {
        List<TestTriage> allTestTriages = testTriageService.findAll();
        Calendar instance = Calendar.getInstance();
        int i = 0;
        for (TestTriage tt : allTestTriages) {

            instance.set(Calendar.DAY_OF_YEAR, i);
            tt.setTimestamp(instance.getTimeInMillis());
            testTriageService.update(tt);

            TestCase testCase = tt.getTestCase();
            testCase.setTimestamp(instance.getTimeInMillis());
            testCaseService.update(testCase);

            i = (i + 1) % daysAgo;
        }

        String[] criteria = {"enabled:true"};
        List<AutomatedTestIssue> automatedTestIssues = automatedTestIssueService.findAll(criteria, PageRequest.of(0, 10)).getContent();

        i = 0;
        for (AutomatedTestIssue ati : automatedTestIssues) {

            instance.set(Calendar.DAY_OF_YEAR, i);
            ati.setTimestamp(instance.getTimeInMillis());
            automatedTestIssueService.update(ati);
            i = (i + 1) % daysAgo;
        }
    }


    private void populateForPerformanceTest() {
        int amountOfExecutors = 1;
        int amountOfTests = 200;

        provider.clear();
        provider.setUseRandom(false);
        provider.setUser(user);
        provider.setName("Performance");
        provider.build(amountOfTests, 1, 1, 1, 1, amountOfExecutors);

        provider.clearForNewBuild();
        provider.setExecutor(null);

        // Test with lot of pass, and fewer fails
        provider.setName("PassLotOf");
        provider.getBuild(1);
        int amountOfFails = 50; // 500
        int amountOfPass = 20; // 2000

        for (int i = 0; i < amountOfFails; i++) {
            provider.setName("FailLotOf");
            provider.setTestExecution(null);
            provider.getTestExecutionFail();
        }

        for (int i = 0; i < amountOfPass; i++) {
            provider.setName("PassLotOf");
            provider.setTestExecution(null);
            provider.getTestExecution(StatusType.PASS);
        }

        provider.getBuildTriage();
    }

    private void configureProductJive() {

        // CREATES A NEW USER
        createUser("martin.barotto@lithium.com", "Martin Barotto");
        createUser("patricio.poratto@lithium.com", "Patricio Poratto");
        createUser("marcos.remolgao@lithium.com", "Marcos Remolgao");
        createUser("fernando.velazquez@lithium.com", "Fernando Velazquez");
        createUser("nicolas.pascual@lithium.com", "Nicolas Pascual");
        createUser("martin.sandoval@lithium.com", "Martin Sandoval");
        createUser("hernan.vinuesa@lithium.com", "Hernan Vinuesa");
        createUser("laura.perera@lithium.com", "Laura Perera");
        createUser("carolina.roncaglia@lithium.com", "Carolina Roncaglia");
        createUser("jonatan.lescano@lithium.com", "Jonatan Lescano");
        createUser("juan.conde@lithium.com", "Juan Pablo Conde");
        User tUser = createUser("rodrigo.rincon@lithium.com", "Rodrigo Rincon");
        User devUser = createUser("patricio.lugli@lithium.com", "Patricio Lugli");

        // CREATES PRODUCT AND DEADLINE
        Product tProduct = Product.builder()
                .name("LJX")
                .description("Connect with other customer community managers or partner engagement teams. Share best practices, challenges, successes, and help each other out with answers specific to your external community.")
                .enabled(true)
                .packageNames("com.jivesoftware")
                .build();

        // Creates a couple of future deadlines (in 8 days and 30 days)
        List<Deadline> deadlines = new ArrayList();

        Deadline deadline = null;
        try {
            deadline = DataProvider.getDeadline();
            deadline.setName("2019.4");
            deadline.setDeadlineDate(BaseDateFormat.parse("2019-04-23").getTime());
            deadline.setProduct(tProduct);

            deadlines.add(deadline);
            // deadlines.add(deadlineService.save(deadline));

            deadline = DataProvider.getDeadline();
            deadline.setName("2019.7");
            deadline.setProduct(tProduct);
            deadline.setDeadlineDate(BaseDateFormat.parse("2019-07-23").getTime());
            // deadlines.add(deadlineService.save(deadline));
            deadlines.add(deadline);

            deadline = DataProvider.getDeadline();
            deadline.setName("2019.10");
            deadline.setProduct(tProduct);
            deadline.setDeadlineDate(BaseDateFormat.parse("2019-10-20").getTime());
            deadlines.add(deadline);

        } catch (ParseException e) {
            log.log(Level.SEVERE, "Error parsing date to timestamp", e);
        }

        tProduct.setDeadlines(deadlines);

        tProduct = productService.save(tProduct);


        // CONNECTS TO SLACK
        SlackSpec slackSpec = createSlackSpec(tProduct);
        slackSpec.setToken("xoxp-47414621366-47412274852-93897987975-f0d116e4cd14d7dd54a90765a310ad64");
        slackSpec.setChannel("tt-ci-notification");
        slackSpec = slackSpecService.save(slackSpec);

        // CREATES CONNECTOR
        Connector tConnector = Connector.builder()
                .name("JENKINS_LITH")
                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080")
                .type(ConnectorType.JENKINS)
                .userName("rrincon")
                .userToken("94ab70cbeac2706bf5805ca852446a94")
                .enabled(true)
                .build();
        tConnector = connectorService.save(tConnector);

        //////////////////////////////////////////////////////////////////////////
        // CREATES CONTAINERS FROM VIEWS
        //////////////////////////////////////////////////////////////////////////
        // 1) QA API Pri1
        Container tContainer = Container.builder()
                .name("Pri1 API: PRI1_API_Jobs")
                .connector(tConnector)
                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/1_Singlejobs/view/Pri1_Jobs/view/API_Jobs/")
                .product(tProduct)
                .build();

        tContainer = containerService.save(tContainer);
        tConnector.add(tContainer);
        TriageSpec spec = createTriageFlowSpec(tContainer, null, tUser);
        triageSpecService.save(spec);


        // 2) QA UI Pri1
        tContainer = Container.builder()
                .name("Pri1 UI: Pri1_UI_Jobs")
                .connector(tConnector)
                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/1_Singlejobs/view/Pri1_Jobs/view/UI_Jobs/")
                .product(tProduct)
                .build();

        tContainer = containerService.save(tContainer);
        tConnector.add(tContainer);
        spec = createTriageFlowSpec(tContainer, null, tUser);
        triageSpecService.save(spec);

        // 3) QA API Pri2
        tContainer = Container.builder()
                .name("Pri2 API: Pri2_API_Jobs")
                .connector(tConnector)
                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/1_Singlejobs/view/Pri2_Jobs/view/API_Jobs/")
                .product(tProduct)
                .build();

        tContainer = containerService.save(tContainer);
        tConnector.add(tContainer);
        spec = createTriageFlowSpec(tContainer, null, tUser);
        triageSpecService.save(spec);

        // 4) QA UI Pri2
        tContainer = Container.builder()
                .name("Pri2 UI: Pri2_UI_Jobs")
                .connector(tConnector)
                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/1_Singlejobs/view/Pri2_Jobs/view/UI_Jobs/")
                .product(tProduct)
                .build();

        tContainer = containerService.save(tContainer);
        tConnector.add(tContainer);
        spec = createTriageFlowSpec(tContainer, null, tUser);
        triageSpecService.save(spec);

        tContainer = Container.builder()
                .name("Test")
                .connector(tConnector)
                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/Test/")
                .product(tProduct)
                .build();

        tContainer = containerService.save(tContainer);
        tConnector.add(tContainer);
        spec = createTriageFlowSpec(tContainer, null, tUser);
        triageSpecService.save(spec);

//        //////////////////////////////////////////////////////////////////////////
//        // CREATES CONTAINERS FROM MULTIJOBS PRI 1
//        //////////////////////////////////////////////////////////////////////////
//        tContainer = Container.builder()
//                .name("Multijob for Api Testing Pri1")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI1/view/Triggers/job/nxqe-api_testing-pri1-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for Cloudalytics Testing Pri1")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI1/view/Triggers/job/nxqe-cloudalytics-pri1-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for Content 'Part 1' Testing Pri1")
//                .connector(tConnector)
//                .url("hhttp://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI1/view/Triggers/job/nxqe-content_1-pri1-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for Content 'Part 2' Testing Pri1")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI1/view/Triggers/job/nxqe-content_2-pri1-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for Gamification Testing Pri1")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI1/view/Triggers/job/nxqe-gamification-pri1-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for Guest X Testing Pri1")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI1/view/Triggers/job/nxqe-guestx-pri1-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for I18N Testing Pri1")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI1/view/Triggers/job/nxqe-i18n-pri1-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for Integrations Testing Pri1")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI1/view/Triggers/job/nxqe-integrations-pri1-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for SMB Testing Pri1")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI1/view/Triggers/job/nxqe-jivesmb-pri1-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for LithX Testing Pri1")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI1/view/Triggers/job/nxqe-jivex-pri1-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for ModerationX Testing Pri1")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI1/view/Triggers/job/nxqe-moderationx-pri1-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for News 'On' Testing Pri1")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI1/view/Triggers/job/nxqe-news_on-pri1-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for Onboarding Testing Pri1")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI1/view/Triggers/job/nxqe-onboarding-pri1-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for People Places 'External Users' Testing Pri1")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI1/view/Triggers/job/nxqe-people_places-External-Users-pri1-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for People Places Testing Pri1")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/job/nxqe-people_places-pri1-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for Search Testing Pri1")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI1/view/Triggers/job/nxqe-search-pri1-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        //////////////////////////////////////////////////////////////////////////
//        // CREATES CONTAINERS FROM MULTIJOBS PRI 2
//        //////////////////////////////////////////////////////////////////////////
//
//        tContainer = Container.builder()
//                .name("Multijob for Activity News 'Part 1' Testing Pri2")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI2/view/Triggers/job/nxqe-activity-news-part1-pri2-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for Api Testing Pri2")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI2/view/Triggers/job/nxqe-api_testing-pri2-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for Content Abuse Report Testing Pri2")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI2/view/Triggers/job/nxqe-content_abuse_report-pri2-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for Content V1 'Part 1' Testing Pri2")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI2/view/Triggers/job/nxqe-content_v1-part1-pri2-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for Content V1 'Part 2' Testing Pri2")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI2/view/Triggers/job/nxqe-content_v1-part2-pri2-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for Content V2 Testing Pri2")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI2/view/Triggers/job/nxqe-content_v2-pri2-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for Gamification Testing Pri2")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI2/view/Triggers/job/nxqe-gamification-pri2-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for GDPR Testing Pri2")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI2/view/Triggers/job/nxqe-gdpr-pri2-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for I18N 'Email Templates' Testing Pri2")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI2/view/Triggers/job/nxqe-i18n-email-templates-pri2-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for I18N Testing Pri2")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI2/view/Triggers/job/nxqe-i18n-pri2-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for Integrations Testing Pri2")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI2/view/Triggers/job/nxqe-integrations-pri2-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for LithX Testing Pri2")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI2/view/Triggers/job/nxqe-jivex-pri2-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for News 'Off' Testing Pri2")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI2/view/Triggers/job/nxqe-news_off-pri2-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for News 'On' Testing Pri2")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI2/view/Triggers/job/nxqe-news_on-pri2-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for People Places 'External Users' Testing Pri2")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI2/view/Triggers/job/nxqe-people_places-External-Users-pri2-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
//        tContainer = Container.builder()
//                .name("Multijob for People Places Testing Pri2")
//                .connector(tConnector)
//                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/QA/view/DEVELOP/view/0_Multijobs/view/Multijobs_PRI2/view/Triggers/job/nxqe-people_places-pri2-develop/")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);

        //////////////////////////////////////////////////////////////////////////
        // CREATES CONTAINERS FROM DEVS
        //////////////////////////////////////////////////////////////////////////
        // 5) Dev Junits
        tContainer = Container.builder()
                .name("Dev: Junits Cloud")
                .connector(tConnector)
                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/CLOUD/view/DEV/view/develop/")
                .product(tProduct)
                .build();
        tContainer = containerService.save(tContainer);
        tConnector.add(tContainer);
        spec = createTriageFlowSpec(tContainer, null, devUser);
        spec = triageSpecService.save(spec);


        // 6) Dev Junits HOP 8
        tContainer = Container.builder()
                .name("Dev: Junits OnPrem 8.x")
                .connector(tConnector)
                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/HOP/view/DEV/view/stable_8.1.x-jx/")
                .product(tProduct)
                .build();
        tContainer = containerService.save(tContainer);
        tConnector.add(tContainer);
        spec = createTriageFlowSpec(tContainer, null, devUser);
        spec = triageSpecService.save(spec);

        // 7) Dev Junits HOP 10
        tContainer = Container.builder()
                .name("Dev: Junits OnPrem 10.x")
                .connector(tConnector)
                .url("http://lith-jnks-strict.phx1.jivehosted.com:8080/view/HOP/view/DEV/view/stable_10.0.x-jx/")
                .product(tProduct)
                .build();
        tContainer = containerService.save(tContainer);
        tConnector.add(tContainer);
        spec = createTriageFlowSpec(tContainer, null, devUser);
        spec = triageSpecService.save(spec);


        // BRING DATA FROM JENKINS
        // Lambda Runnable
        // Pull has to be done manually. otherwise it takes a lot and it is inside a transaction
        for (Container finalContainer : tConnector.getContainers()) {
            // pull(finalContainer);
        }


    }

    private void configureProductJenkinsClarolab() {

        // CREATES NEW USERS
        createUser("francisco.vives@clarolab.com", "Francisco Vives");
        createUser("martin.dugo@clarolab.com", "Martin Dugo");
        createUser("federico.naso@clarolab.com", "Federico Naso");
        createUser("sata@clarolab.com", "Juan Manuel Suarez");
        createUser("luis.cassih@clarolab.com", "Luis Cassih");
        createUser("rodrigo.rincon@clarolab.com", "Rodrigo Rincon");
        User tUser = createUser("info@ttriage.com", "Admin User");


        // CREATES PRODUCT AND DEADLINE
        Product tProduct = Product.builder()
                .name("Maven Clarolab")
                .description("Connect with other customer community managers or partner engagement teams. Share best practices, challenges, successes, and help each other out with answers specific to your external community.")
                .enabled(true)
                .packageNames("com.jivesoftware")
                .build();

        // Creates a couple of future deadlines (in 8 days and 30 days)
        List<Deadline> deadlines = new ArrayList();

        Deadline deadline = null;
        try {
            deadline = DataProvider.getDeadline();
            deadline.setName("Clarolab Deadline");
            deadline.setDeadlineDate(BaseDateFormat.parse("2019-07-23").getTime());
            deadline.setProduct(tProduct);

            deadlines.add(deadline);

        } catch (ParseException e) {
            log.log(Level.SEVERE, "Error parsing date to timestamp", e);
        }

        tProduct.setDeadlines(deadlines);

        tProduct = productService.save(tProduct);

        // CREATES CONNECTOR
        Connector tConnector = Connector.builder()
                .name("Clarolab Maven Connector")
                .url("http://dev.clarolab.com:12080")
                .type(ConnectorType.JENKINS)
                .userName("rodrigo_rincon")
                .userToken("afe066d6f6086a9dcb906d21e637f3d5")
                .enabled(true)
                .build();
        tConnector = connectorService.save(tConnector);

        //////////////////////////////////////////////////////////////////////////
        // CREATES CONTAINERS FROM VIEWS
        //////////////////////////////////////////////////////////////////////////
        Container tContainer = Container.builder()
                .name("Application")
                .connector(tConnector)
                .url("http://dev.clarolab.com:12080/view/Application")
                .product(tProduct)
                .build();

        tContainer = containerService.save(tContainer);
        tConnector.add(tContainer);
        TriageSpec spec = TriageSpec.builder()
                .expectedMinAmountOfTests(20)
                .frequencyCron(Constants.DEADLINE_FREQUENCY_2DAYS)
                .everyWeeks(2)
                .expectedPassRate(95)
                .priority(1)
                .executor(null)
                .container(tContainer)
                .triager(tUser)
                .build();
        triageSpecService.save(spec);

        tContainer = Container.builder()
                .name("Plugin")
                .connector(tConnector)
                .url("http://dev.clarolab.com:12080/view/Plugin")
                .product(tProduct)
                .build();

        tContainer = containerService.save(tContainer);
        tConnector.add(tContainer);
        spec = TriageSpec.builder()
                .expectedMinAmountOfTests(20)
                .frequencyCron(Constants.DEADLINE_FREQUENCY_2DAYS)
                .everyWeeks(2)
                .expectedPassRate(95)
                .priority(1)
                .executor(null)
                .container(tContainer)
                .triager(tUser)
                .build();
        triageSpecService.save(spec);

        tContainer = Container.builder()
                .name("Tests")
                .connector(tConnector)
                .url("http://dev.clarolab.com:12080/view/Tests")
                .product(tProduct)
                .build();

        tContainer = containerService.save(tContainer);
        tConnector.add(tContainer);
        spec = TriageSpec.builder()
                .expectedMinAmountOfTests(20)
                .frequencyCron(Constants.DEADLINE_FREQUENCY_2DAYS)
                .everyWeeks(2)
                .expectedPassRate(95)
                .priority(1)
                .executor(null)
                .container(tContainer)
                .triager(tUser)
                .build();
        triageSpecService.save(spec);
    }

    private void configureProductFlux() {

        // CREATES A NEW USER
        createUser("info@flux.com", "Info Info");
        User tUser = createUser("nicolas.valdesogo@clarolab.com", "Nicolas Valdesogo");

        // CREATES PRODUCT AND DEADLINE
        Product tProduct = Product.builder()
                .name("Flux")
                .description("Connect with other customer community managers or partner engagement teams. Share best practices, challenges, successes, and help each other out with answers specific to your external community.")
                .enabled(true)
                .packageNames("com.bancogalicia")
                .build();

        // Creates a couple of future deadlines (in 8 days and 30 days)
        List<Deadline> deadlines = new ArrayList();

        Deadline deadline = null;
        try {
            deadline = DataProvider.getDeadline();
            deadline.setName("Flux Deadline");
            deadline.setDeadlineDate(BaseDateFormat.parse("2019-12-31").getTime());
            deadline.setProduct(tProduct);

            deadlines.add(deadline);

        } catch (ParseException e) {
            log.log(Level.SEVERE, "Error parsing date to timestamp", e);
        }

        tProduct.setDeadlines(deadlines);

        tProduct = productService.save(tProduct);

        // CREATES CONNECTOR
        Connector tConnector = Connector.builder()
                .name("GitLab Flux Connector")
                .url("https://gitlab.extranet.fluxit.com.ar")
                .type(ConnectorType.GITLAB)
                .userName("nicolas.valdesogo")
                .userToken("YXyNxc5z8P7gb69Q7YEg")
                .enabled(true)
                .build();
        tConnector = connectorService.save(tConnector);

        //////////////////////////////////////////////////////////////////////////
        // CREATES CONTAINERS FROM VIEWS
        //////////////////////////////////////////////////////////////////////////
        Container tContainer = Container.builder()
                .name("Banco Galica Back Apps")
                .connector(tConnector)
                .url("https://gitlab.extranet.fluxit.com.ar/banco-galicia/back-apps")
                .hiddenData("banco-galicia/back-apps/553")
                .product(tProduct)
                .build();

        tContainer = containerService.save(tContainer);
        tConnector.add(tContainer);
        TriageSpec spec = TriageSpec.builder()
                .expectedMinAmountOfTests(20)
                .frequencyCron(Constants.DEADLINE_FREQUENCY_2DAYS)
                .everyWeeks(2)
                .expectedPassRate(95)
                .priority(1)
                .executor(null)
                .container(tContainer)
                .triager(tUser)
                .build();
        triageSpecService.save(spec);

    }

    private void configureProductLithium() {

        // CREATES A NEW USER
        User tUser = DataProvider.getUserAsAdmin();
        tUser.setUsername("francisco.vives@lithium.com");
        tUser.setRealname("Francisco Vives");
        tUser.setPassword(userService.getEncryptedPassword("Francisco1"));
        tUser.setAvatar(ImageModel
                .builder()
                .enabled(true)
                .name("DEMO IMAGE")
                .timestamp(DateUtils.now())
                .updated(DateUtils.now())
                .data("https://lh3.googleusercontent.com/6itEFCORQlcN17yqiZ25es8Khubvsg38EPrsCTVDxmGiOTy2E914BE0tpGB3LD_c-A")
                .build());

        tUser.setRoleType(RoleType.ROLE_ADMIN);

        tUser = userService.save(tUser);

        tUser = DataProvider.getUserAsAdmin();
        tUser.setUsername("raunak.bagri@lithium.com");
        tUser.setRealname("Raunak Bagri");
        tUser.setPassword(userService.getEncryptedPassword("RaunakB19"));
        tUser.setAvatar(ImageModel
                .builder()
                .enabled(true)
                .name("DEMO IMAGE")
                .timestamp(DateUtils.now())
                .updated(DateUtils.now())
                .data("https://lh3.googleusercontent.com/6itEFCORQlcN17yqiZ25es8Khubvsg38EPrsCTVDxmGiOTy2E914BE0tpGB3LD_c-A")
                .build());

        tUser.setRoleType(RoleType.ROLE_ADMIN);

        tUser = userService.save(tUser);


        tUser = DataProvider.getUserAsAdmin();
        tUser.setUsername("tina.chatterjee@lithium.com");
        tUser.setRealname("Tina Chatterjee");
        tUser.setPassword(userService.getEncryptedPassword("TinaC2019"));
        tUser.setAvatar(ImageModel
                .builder()
                .enabled(true)
                .name("DEMO IMAGE")
                .timestamp(DateUtils.now())
                .updated(DateUtils.now())
                .data("https://lh3.googleusercontent.com/6itEFCORQlcN17yqiZ25es8Khubvsg38EPrsCTVDxmGiOTy2E914BE0tpGB3LD_c-A")
                .build());

        tUser.setRoleType(RoleType.ROLE_ADMIN);

        tUser = userService.save(tUser);


        // CREATES PRODUCT AND DEADLINE
        Product tProduct = Product.builder()
                .name("LIA")
                .description("Lithium helps brands connect customers, content & conversations at the right digital moment.")
                .enabled(true)
                .packageNames("com.lithium,lithium.lqa,TEST-lithium.lqa")
                .build();

        // Creates a couple of future deadlines (in 8 days and 30 days)
        try {
            List<Deadline> deadlines = new ArrayList();

            Deadline deadline = null;

            deadline = Deadline.builder()
                    .description("")
                    .name("19.2")
                    .deadlineDate(BaseDateFormat.parse("2019-03-11").getTime())
                    .product(tProduct)
                    .enabled(true)
                    .build();
            deadlines.add(deadline);
            // deadlines.add(deadlineService.save(deadline));

            deadline = Deadline.builder()
                    .description("")
                    .name("19.3")
                    .product(tProduct)
                    .deadlineDate(BaseDateFormat.parse("2019-04-08").getTime())
                    .enabled(true)
                    .build();
            // deadlines.add(deadlineService.save(deadline));
            deadlines.add(deadline);

            deadline = Deadline.builder()
                    .description("")
                    .name("19.4")
                    .product(tProduct)
                    .deadlineDate(BaseDateFormat.parse("2019-05-13").getTime())
                    .enabled(true)
                    .build();
            // deadlines.add(deadlineService.save(deadline));
            deadlines.add(deadline);

            tProduct.setDeadlines(deadlines);
        } catch (ParseException e) {
            log.log(Level.SEVERE, "Error parsing date to timestamp", e);
        }

        tProduct = productService.save(tProduct);


        // CONNECTS TO SLACK
        SlackSpec slackSpec = createSlackSpec(tProduct);
        slackSpec.setToken("xoxp-546678226724-546101465248-548197344391-132f5c06df0be7ba24c435f35c412a3e");
        slackSpec.setChannel("notification");
        slackSpec = slackSpecService.save(slackSpec);

        // CREATES CONNECTOR
        Connector tConnector = Connector.builder()
                .name("JENKINS_LITH")
                .url("http://jenkins.dev.lithium.com/")
                .type(ConnectorType.JENKINS)
                .userName("francisco.vives")
                .userToken("596568df4d7ce61985d10cf6054937e6")
                .enabled(true)
                .build();
        tConnector = connectorService.save(tConnector);

        // QA LIA Mineraloil Tests
        Container tContainer = Container.builder()
                .name("QA LIA Mineraloil Tests")
                .connector(tConnector)
                .url("http://jenkins.dev.lithium.com/view/qa-lia-mineraloil-lia-testcases/")
                .product(tProduct)
                .build();
        tContainer = containerService.save(tContainer);
        tConnector.add(tContainer);
        TriageSpec spec = createTriageFlowSpec(tContainer, null, tUser);
        spec = triageSpecService.save(spec);

        // LIA FEATURE
        tContainer = Container.builder()
                .name("QA LIA Feature 19.x")
                .connector(tConnector)
                .url("http://jenkins.dev.lithium.com/view/qa-lia-feature-19.6/")
                .product(tProduct)
                .build();
        tContainer = containerService.save(tContainer);
        tConnector.add(tContainer);
        spec = createTriageFlowSpec(tContainer, null, tUser);
        spec = triageSpecService.save(spec);


        // LIA FEATURE
        tContainer = Container.builder()
                .name("Feature Custom Branch")
                .connector(tConnector)
                .url("http://jenkins.dev.lithium.com/view/qa-lia-feature-custom-branch/")
                .product(tProduct)
                .populateMode(PopulateMode.PUSH)
                .build();
        tContainer = containerService.save(tContainer);
        tConnector.add(tContainer);
        spec = createTriageFlowSpec(tContainer, null, tUser);
        spec = triageSpecService.save(spec);

        // LIA FEATURE
        tContainer = Container.builder()
                .name("Responsive Firefox")
                .connector(tConnector)
                .url("http://jenkins.dev.lithium.com/view/qa-lia-mineraloil-lia-testcases-firefox/")
                .product(tProduct)
                .build();
        tContainer = containerService.save(tContainer);
        tConnector.add(tContainer);
        spec = createTriageFlowSpec(tContainer, null, tUser);
        spec = triageSpecService.save(spec);

        // BRING DATA FROM JENKINS
        for (Container finalContainer : tConnector.getContainers()) {
            // Disabled by now, it takes lot of time all the lithium containers
            // pull(finalContainer);
        }


    }

    private void configureProductLithiumScreenshotsExample() {

        // CREATES A NEW USER
        User tUser = DataProvider.getUserAsAdmin();
        tUser.setUsername("francisco.vives@lithium.com");
        tUser.setRealname("Francisco Vives");
        tUser.setPassword(userService.getEncryptedPassword("123123"));
        tUser.setAvatar(ImageModel
                .builder()
                .enabled(true)
                .name("DEMO IMAGE")
                .timestamp(DateUtils.now())
                .updated(DateUtils.now())
                .data("https://lh3.googleusercontent.com/6itEFCORQlcN17yqiZ25es8Khubvsg38EPrsCTVDxmGiOTy2E914BE0tpGB3LD_c-A")
                .build());

        tUser.setRoleType(RoleType.ROLE_USER);

        tUser = userService.save(tUser);

        tUser = DataProvider.getUserAsAdmin();
        tUser.setUsername("yuri.kapulkin@lithium.com");
        tUser.setRealname("Yuri Kapulkin <yuri.kapulkin@lithium.com>");
        tUser.setPassword(userService.getEncryptedPassword("123123"));
        tUser.setAvatar(ImageModel
                .builder()
                .enabled(true)
                .name("DEMO IMAGE")
                .timestamp(DateUtils.now())
                .updated(DateUtils.now())
                .data("https://lh3.googleusercontent.com/6itEFCORQlcN17yqiZ25es8Khubvsg38EPrsCTVDxmGiOTy2E914BE0tpGB3LD_c-A")
                .build());

        tUser.setRoleType(RoleType.ROLE_ADMIN);

        tUser = userService.save(tUser);


        tUser = DataProvider.getUserAsAdmin();
        tUser.setUsername("tina.chatterjee@lithium.com");
        tUser.setRealname("Tina Chatterjee");
        tUser.setPassword(userService.getEncryptedPassword("123123"));
        tUser.setAvatar(ImageModel
                .builder()
                .enabled(true)
                .name("DEMO IMAGE")
                .timestamp(DateUtils.now())
                .updated(DateUtils.now())
                .data("https://lh3.googleusercontent.com/6itEFCORQlcN17yqiZ25es8Khubvsg38EPrsCTVDxmGiOTy2E914BE0tpGB3LD_c-A")
                .build());

        tUser.setRoleType(RoleType.ROLE_ADMIN);

        tUser = userService.save(tUser);


        // CREATES PRODUCT AND DEADLINE
        Product tProduct = Product.builder()
                .name("LIA")
                .description("Lithium helps brands connect customers, content & conversations at the right digital moment.")
                .enabled(true)
                .packageNames("com.lithium")
                .build();

        // Creates a couple of future deadlines (in 8 days and 30 days)
        try {
            List<Deadline> deadlines = new ArrayList();

            Deadline deadline = null;

            deadline = Deadline.builder()
                    .description("")
                    .name("19.2")
                    .deadlineDate(BaseDateFormat.parse("2019-03-11").getTime())
                    .product(tProduct)
                    .enabled(true)
                    .build();
            deadlines.add(deadline);
            // deadlines.add(deadlineService.save(deadline));

            deadline = Deadline.builder()
                    .description("")
                    .name("19.3")
                    .product(tProduct)
                    .deadlineDate(BaseDateFormat.parse("2019-04-08").getTime())
                    .enabled(true)
                    .build();
            // deadlines.add(deadlineService.save(deadline));
            deadlines.add(deadline);

            deadline = Deadline.builder()
                    .description("")
                    .name("19.4")
                    .product(tProduct)
                    .deadlineDate(BaseDateFormat.parse("2019-05-13").getTime())
                    .enabled(true)
                    .build();
            // deadlines.add(deadlineService.save(deadline));
            deadlines.add(deadline);

            tProduct.setDeadlines(deadlines);
        } catch (ParseException e) {
            log.log(Level.SEVERE, "Error parsing date to timestamp", e);
        }

        tProduct = productService.save(tProduct);


        // CONNECTS TO SLACK
        SlackSpec slackSpec = createSlackSpec(tProduct);
        slackSpec.setToken("xoxp-546678226724-546101465248-548197344391-132f5c06df0be7ba24c435f35c412a3e");
        slackSpec.setChannel("notification");
        slackSpec = slackSpecService.save(slackSpec);

        // CREATES CONNECTOR
        Connector tConnector = Connector.builder()
                .name("JENKINS_LITH")
                .url("http://jenkins.dev.lithium.com/")
                .type(ConnectorType.JENKINS)
                .userName("francisco.vives")
                .userToken("596568df4d7ce61985d10cf6054937e6")
                .enabled(true)
                .build();
        tConnector = connectorService.save(tConnector);

        Container tContainer = Container.builder()
                .name("QA LIA 19.4-master")
                .connector(tConnector)
                .url("http://jenkins.dev.lithium.com/job/19.4-master")
                .product(tProduct)
                .build();
        tContainer = containerService.save(tContainer);
        tConnector.add(tContainer);
        TriageSpec spec = createTriageFlowSpec(tContainer, null, tUser);
        spec = triageSpecService.save(spec);


        // BRING DATA FROM JENKINS
        for (Container finalContainer : tConnector.getContainers()) {
            // Disabled by now, it takes lot of time all the lithium containers
            // pull(finalContainer);
        }


    }

    // Configures the tTriage product
    private void configureProductTriage() {

        createUser("francisco.vives@clarolab.com", "Francisco Vives");
        createUser("martin.dugo@clarolab.com", "Martin Dugo");
        createUser("federico.naso@clarolab.com", "Federico Naso");
        createUser("sata@clarolab.com", "Juan Manuel Suarez");
        createUser("martin.calcaterra@clarolab.com", "Martin Calcaterra");
        createUser("luis.cassih@clarolab.com", "Luis Cassih");
        createUser("rodrigo.rincon@clarolab.com", "Rodrigo Rincon");
        User tUser = createUser("ttriage@clarolab.com", "Admin User");

        // CREATES PRODUCT AND DEADLINE
        Product tProduct = Product.builder()
                .name("t-Triage")
                .description("Real-time insights on software automation")
                .enabled(true)
                .packageNames("com.clarolab")
                .build();

        // Creates a couple of future deadlines (in 8 days and 30 days)
        List<Deadline> deadlines = new ArrayList();

        Deadline deadline = Deadline.builder()
                .description("Version 1 on Circle")
                .name("v1.0 Circle")
                .deadlineDate(getTimeAdd(8))
                .product(tProduct)
                .enabled(true)
                .build();
        deadlines.add(deadline);
        tProduct.setDeadlines(deadlines);
        tProduct = productService.save(tProduct);


        List<Deadline> deadlines2 = new ArrayList();
        deadline = Deadline.builder()
                .description("Version 1 on Jenkins")
                .name("v1.0 Jenkins")
                .product(tProduct)
                .deadlineDate(getTimeAdd(30))
                .enabled(true)
                .build();
        deadlines2.add(deadline);
        tProduct.setDeadlines(deadlines2);
        tProduct = productService.save(tProduct);


        // CONNECTS TO SLACK
        SlackSpec slackSpec = createSlackSpec(tProduct);
        slackSpec.setToken("xoxp-47414621366-47412274852-93897987975-f0d116e4cd14d7dd54a90765a310ad64");
        slackSpec.setChannel("tt-ci-notification");
        slackSpec = slackSpecService.save(slackSpec);

        // CREATES CONNECTOR
        Connector tConnectorCircle = Connector.builder()
                .name("Circle CI")
                .url("https://circleci.com/")
                .type(ConnectorType.CIRCLECI)
                .userName("TTriage")
                .userToken("a296eed321ab07d489eae6a2eed301eac5472b19")
                .enabled(true)
                .build();
        tConnectorCircle = connectorService.save(tConnectorCircle);
        // tConnectorCircle.setEnabled(false);
        // tConnectorCircle = connectorService.update(tConnectorCircle);

        Connector tConnectorJenkins = Connector.builder()
                .name("Jenkins CI")
                .url("http://dev.clarolab.com:12080/")
                .type(ConnectorType.JENKINS)
                .userName("admin")
                .userToken("115977a708eaccbfcc825e86ba0a368fb9")
                .enabled(true)
                .build();
        tConnectorJenkins = connectorService.save(tConnectorJenkins);

        // CREATES CONTAINER API
        //App from circle
        Container tContainerCircle = Container.builder()
                .name("QA Reports Application Circle")
                .hiddenData("bitbucket/TTriage/qa-reports")
                .connector(tConnectorCircle)
                .url("https://circleci.com/bb/TTriage/qa-reports")
                .product(tProduct)
                .build();
        tContainerCircle = containerService.save(tContainerCircle);
        tConnectorCircle.add(tContainerCircle);
        TriageSpec spec = createTriageFlowSpec(tContainerCircle, null, tUser);
        spec = triageSpecService.save(spec);

        //App from Jenkins in Clarolab Pull MODE
        Container tContainerJenkinsApplication = Container.builder()
                .name("QA Report Application Pull")
                .hiddenData("bitbucket/TTriage/qa-reports")
                .connector(tConnectorJenkins)
                .url("http://dev.clarolab.com:12080/view/Application/")
                .product(tProduct)
                .build();
        tContainerJenkinsApplication = containerService.save(tContainerJenkinsApplication);
        tConnectorJenkins.add(tContainerJenkinsApplication);
        spec = createTriageFlowSpec(tContainerJenkinsApplication, null, tUser);
        spec = triageSpecService.save(spec);

        //App from Jenkins in Clarolab Push MODE
        Container tContainerJenkinsApplicationPush = Container.builder()
                .name("QA Report Application Push")
                .hiddenData("bitbucket/TTriage/qa-reports")
                .connector(tConnectorJenkins)
                .populateMode(PopulateMode.PUSH)
                .url("http://dev.clarolab.com:12080/view/QA%20Report%20Application%20Push/")
                .product(tProduct)
                .build();
        tContainerJenkinsApplicationPush = containerService.save(tContainerJenkinsApplicationPush);
        tConnectorJenkins.add(tContainerJenkinsApplicationPush);
        spec = createTriageFlowSpec(tContainerJenkinsApplicationPush, null, tUser);
        spec = triageSpecService.save(spec);

        //Plugin from Jenkins in Clarolab
        Container tContainerJenkinsPlugin = Container.builder()
                .name("QA Report Plugin Push")
                .hiddenData("bitbucket/TTriage/qa-reports-plugin-jenkins")
                .connector(tConnectorJenkins)
                .url("http://dev.clarolab.com:12080/view/QA%20Report%20Plugin%20Push/")
                .populateMode(PopulateMode.PUSH)
                .product(tProduct)
                .build();
        tContainerJenkinsPlugin = containerService.save(tContainerJenkinsPlugin);
        tConnectorJenkins.add(tContainerJenkinsPlugin);
        spec = createTriageFlowSpec(tContainerJenkinsPlugin, null, tUser);
        spec = triageSpecService.save(spec);

        //Selenium Tests from Jenkins in Clarolab
        Container tContainerJenkinsSelenium = Container.builder()
                .name("QA Report Test Selenium")
                .hiddenData("bitbucket/TTriage/qa-reports-selenium")
                .connector(tConnectorJenkins)
                .url("http://dev.clarolab.com:12080/view/SeleniumTests/")
                .product(tProduct)
                .build();
        tContainerJenkinsSelenium = containerService.save(tContainerJenkinsSelenium);
        tConnectorJenkins.add(tContainerJenkinsSelenium);
        spec = createTriageFlowSpec(tContainerJenkinsSelenium, null, tUser);
        spec = triageSpecService.save(spec);


        // Folder for Junit Tests
        Container tContainerSampleTests = Container.builder()
                .name("Folder To Use By Junits")
                .hiddenData("bitbucket/TTriage/FolderToUseByJunits")
                .connector(tConnectorJenkins)
                .url("http://dev.clarolab.com:12080/view/FolderToUseByJunits/")
                .product(tProduct)
                .build();
        tContainerSampleTests = containerService.save(tContainerSampleTests);
        tConnectorJenkins.add(tContainerSampleTests);
        spec = createTriageFlowSpec(tContainerSampleTests, null, tUser);
        spec = triageSpecService.save(spec);

        // Folder for Junit Tests
        Container tContainerDeploy = Container.builder()
                .name("Deploy")
                .hiddenData("bitbucket/TTriage/Deploy")
                .connector(tConnectorJenkins)
                .url("http://dev.clarolab.com:12080/view/Deploy/")
                .product(tProduct)
                .build();
        tContainerDeploy = containerService.save(tContainerDeploy);
        tConnectorJenkins.add(tContainerDeploy);
        spec = createTriageFlowSpec(tContainerDeploy, null, tUser);
        spec = triageSpecService.save(spec);


        // BRING DATA FROM CIRCLECI
        //pull(tContainer);
    }

    // Configures the tTriage product
    private void configureProductTriageJenkins() {

        createUser("francisco.vives@clarolab.com", "Francisco Vives");
        createUser("martin.dugo@clarolab.com", "Martin Dugo");
        createUser("federico.naso@clarolab.com", "Federico Naso");
        createUser("sata@clarolab.com", "Juan Manuel Suarez");
        createUser("martin.calcaterra@clarolab.com", "Martin Calcaterra");
        createUser("luis.cassih@clarolab.com", "Luis Cassih");
        createUser("rodrigo.rincon@clarolab.com", "Rodrigo Rincon");
        User tUser = createUser("ttriage@clarolab.com", "Admin User");

        // CREATES PRODUCT AND DEADLINE
        Product tProduct = Product.builder()
                .name("t-Triage")
                .description("Real-time insights on software automation")
                .enabled(true)
                .packageNames("com.clarolab")
                .build();

        // Creates a couple of future deadlines (in 8 days and 30 days)
        List<Deadline> deadlines = new ArrayList();

        Deadline deadline = Deadline.builder()
                .description("Version 1 on Circle")
                .name("v1.0 Circle")
                .deadlineDate(getTimeAdd(8))
                .product(tProduct)
                .enabled(true)
                .build();
        deadlines.add(deadline);
        tProduct.setDeadlines(deadlines);
        tProduct = productService.save(tProduct);


        List<Deadline> deadlines2 = new ArrayList();
        deadline = Deadline.builder()
                .description("Version 1 on Jenkins")
                .name("v1.0 Jenkins")
                .product(tProduct)
                .deadlineDate(getTimeAdd(30))
                .enabled(true)
                .build();
        deadlines2.add(deadline);
        tProduct.setDeadlines(deadlines2);
        tProduct = productService.save(tProduct);


        // CONNECTS TO SLACK
        SlackSpec slackSpec = createSlackSpec(tProduct);
        slackSpec.setToken("xoxp-47414621366-47412274852-93897987975-f0d116e4cd14d7dd54a90765a310ad64");
        slackSpec.setChannel("tt-ci-notification");
        slackSpec = slackSpecService.save(slackSpec);

        Connector tConnectorJenkins = Connector.builder()
                .name("Jenkins CI")
                .url("http://dev.clarolab.com:12080/")
                .type(ConnectorType.JENKINS)
                .userName("admin")
                .userToken("115977a708eaccbfcc825e86ba0a368fb9")
                .enabled(true)
                .build();
        tConnectorJenkins = connectorService.save(tConnectorJenkins);

        //App from Jenkins in Clarolab Pull MODE
        Container tContainerJenkinsApplication = Container.builder()
                .name("QA Report Application Pull")
                .hiddenData("bitbucket/TTriage/qa-reports")
                .connector(tConnectorJenkins)
                .url("http://dev.clarolab.com:12080/view/Application/")
                .product(tProduct)
                .build();
        tContainerJenkinsApplication = containerService.save(tContainerJenkinsApplication);
        tConnectorJenkins.add(tContainerJenkinsApplication);
        TriageSpec spec = createTriageFlowSpec(tContainerJenkinsApplication, null, tUser);
        spec = triageSpecService.save(spec);
    }

    private void populateBambooTest() {
        User tUser = createUser("test@test.com", "Test Test");

        Product tProduct = Product.builder()
                .name("Bamboo")
                .description("Bamboo Product test")
                .enabled(true)
                .packageNames("com.clarolab")
                .build();
        tProduct = productService.save(tProduct);

        // CREATES CONNECTOR
        Connector tConnector = Connector.builder()
                .name("BAMBOO_CLAROLAB")
                .url("http://dev.clarolab.com:8085")
                .type(ConnectorType.BAMBOO)
                .userName("bamboo")
                .userToken("bamboo123")
                .enabled(true)
                .build();
        tConnector = connectorService.save(tConnector);
        TriageSpec spec;
        Container tContainer;

        //////////////////////////////////////////////////////////////////////////
        // CREATES CONTAINERS FROM VIEWS
        //////////////////////////////////////////////////////////////////////////
//        tContainer = Container.builder()
//                .name("IntegrationTestSuites")
//                .connector(tConnector)
//                .url("http://dev.clarolab.com:8085/browse/ITS")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
//
        tContainer = Container.builder()
                .name("Project Test 11")
                .connector(tConnector)
                .url("http://dev.clarolab.com:8085/browse/PT11")
                .product(tProduct)
                .build();
        tContainer = containerService.save(tContainer);
        tConnector.add(tContainer);
        spec = createTriageFlowSpec(tContainer, null, tUser);
        triageSpecService.save(spec);

//        tContainer = Container.builder()
//                .name("AIOIntegTest")
//                .connector(tConnector)
//                .url("http://dev.clarolab.com:8085/browse/AIOIT")
//                .product(tProduct)
//                .build();
//        tContainer = containerService.save(tContainer);
//        tConnector.add(tContainer);
//        spec = createTriageFlowSpec(tContainer, null, tUser);
//        triageSpecService.save(spec);
    }

    private void pullBackground(Container container) {
        Runnable populateTask = () -> {
            try {
                Thread.sleep(10 * DEFAULT_EVENT_PROCESS_DELAY);
                TransactionStatus transactionStatus = null;
                transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
                transactionStatus.setRollbackOnly();
                // containerService.populate(container.getId());
                transactionManager.commit(transactionStatus);
            } catch (InterruptedException e) {
                log.log(Level.WARNING, "Error while sleeping", e);
                throw new RuntimeException(e);
            }
        };
        // start the thread
        new Thread(populateTask).start();
    }

    private void pull(Container container) {
        try {
            Thread.sleep(10 * DEFAULT_EVENT_PROCESS_DELAY);
        } catch (InterruptedException e) {
            log.log(Level.WARNING, "Error while sleeping", e);
            throw new RuntimeException(e);
        }
        containerService.populate(container.getId());
    }

    private void populateFromSql(String fileName) {
        String filePath = TEST_SQL_PATH + fileName;
        log.info("Loading data from sql script: " + filePath);
        Resource initData = new ClassPathResource(filePath);
        try {
            ScriptUtils.executeSqlScript(datasource.getConnection(), initData);
            log.info("Data base was updated.");
        } catch (SQLException e) {
            log.log(Level.SEVERE, "SQL database population could not be completed.", e);
        }
    }

    public boolean shouldPopulateSQL() {
        return properties.getEnable() && (properties.getConfiguration().contains("all") || properties.getConfiguration().contains("sql"));
    }

    private void createDisabledData() {
        provider.clear();
        provider.setUseRandom(false);
        provider.setName("DONTDISPLAY1");
        User newUser = provider.getUser();

        Product product = provider.getProduct();
        provider.getDeadline();
        Executor executor = provider.getExecutor();
        provider.getTestExecution();
        provider.getTestCaseTriage();

        newUser.setEnabled(false);
        product.setEnabled(false);
        executor.setEnabled(false);

        userService.update(newUser);
        productService.update(product);
        executorService.update(executor);

        // case container disabled
        provider.clear();
        provider.setUseRandom(false);
        provider.setName("DONTDISPLAY2");
        newUser = provider.getUser();

        product = provider.getProduct();
        provider.getDeadline();
        Container container = provider.getContainer();
        provider.getTestExecution();
        provider.getTestCaseTriage();

        newUser.setEnabled(false);
        product.setEnabled(false);
        container.setEnabled(false);

        userService.update(newUser);
        productService.update(product);
        containerService.update(container);

    }

    private User createUser(String email, String name) {
        User tUser = DataProvider.getUserAsAdmin();
        tUser.setUsername(email);
        tUser.setRealname(name);
        tUser.setPassword(userService.getEncryptedPassword("123123"));
        tUser.setAvatar(ImageModel
                .builder()
                .enabled(true)
                .name("DEMO IMAGE")
                .timestamp(DateUtils.now())
                .updated(DateUtils.now())
                .data("https://lh3.googleusercontent.com/6itEFCORQlcN17yqiZ25es8Khubvsg38EPrsCTVDxmGiOTy2E914BE0tpGB3LD_c-A")
                .build());

        tUser.setRoleType(RoleType.ROLE_ADMIN);

        tUser = userService.save(tUser);

        return tUser;
    }

    private void populateSpecialCases() {
        specialCasesTestData.populate();
    }

    private void populateSecurity() {
        securityTestData.populate();
    }

    private boolean containProperty(String value) {
        if (properties.getConfiguration().contains(value)) {
            return true;
        }

        for (String config : properties.getConfiguration()) {
            if (config.contains(value)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasUsers() {
        // It is more than 1, because there is already the admin@ttriage.com
        return userService.count() > 1;
    }

    private void createWelcomeMessage() {
        Property property = propertyService.findByName(WELCOME_MESSAGE);
        String message = "<div style=\"\n" +
                "    font10RegularWhite-weight: 600;\n" +
                "\">Demo login information<br><div style=\"font10RegularWhite-size: 12px;margin-top: 5px;font10RegularWhite-weight: normal;/* margin-left: 10px; */\">Username: info@clarolab.com<br>Password: 123123</div></div>";
        if (property == null) {
            property = DataProvider.getProperty();
            property.setName(WELCOME_MESSAGE);
            property.setValue(message);

            property = propertyService.save(property);
        } else {
            property.setValue(property.getValue() + message);
            propertyService.update(property);
        }

        propertyService.setValue(GOOGLE_ANALYTICS_UA, "UA-145097787-5");

    }

    private void createManualTests() {
        String oldName = provider.getName();
        provider.getContainer();

        provider.setName("Full Regression Test");
        provider.getManualTestPlan();

        provider.setName("User");
        provider.getProductComponent();
        provider.setName("Check the response of entering a valid user and password ");
        ManualTestCase test = provider.getManualTestCase(4);

        ManualTestRequirement req = test.getRequirement();
        req.setName("Have a valid account");
        manualTestRequirementService.update(req);
        provider.setManualTestRequirement(null);

        ManualTestStep step = test.getSteps().get(0);
        step.setStep("Launch application");
        manualTestStepService.update(step);

        step = test.getSteps().get(1);
        step.setStep("Enter username");
        step.setData("username: 'valid_username'");
        manualTestStepService.update(step);

        step = test.getSteps().get(2);
        step.setStep("Enter password");
        step.setData("password: 'valid_password'");
        manualTestStepService.update(step);

        step = test.getSteps().get(3);
        step.setStep("Click login button");
        step.setExpectedResult("Login successful");
        manualTestStepService.update(step);

        test.setAutomationStatus(AutomationStatusType.NO);
        test.getComponent1().setName("Login");
        productComponentService.update(test.getComponent1());
        manualTestCaseService.update(test);

        provider.setManualTestCase(null);


        // Test 2
        provider.setName("Product");
        provider.getProductComponent();
        provider.setName("Check if new product is created ");
        test = provider.getManualTestCase(3);
        test.setNeedsUpdate(true);
        test.setSuite(SuiteType.REGRESSION);
        test.getComponent1().setName("Product");
        productComponentService.update(test.getComponent1());
        test.getComponent2().setName("Deployment");
        productComponentService.update(test.getComponent2());
        manualTestCaseService.update(test);

        req = test.getRequirement();
        req.setName("Have a valid product");
        manualTestRequirementService.update(req);

        step = test.getSteps().get(0);
        step.setStep("Launch application");
        manualTestStepService.update(step);

        step = test.getSteps().get(1);
        step.setStep("Navigate to products page");
        step.setData("example.com/product");
        manualTestStepService.update(step);

        step = test.getSteps().get(2);
        step.setStep("Use search bar to find desired product");
        step.setExpectedResult("Product should appear");
        manualTestStepService.update(step);


        provider.setManualTestCase(null);


        // 3rd test with automation association
        provider.setUser(null);
        User user = provider.getUser();
        provider.setUser(null);
        provider.getUser();

        provider.setName("Content");
        provider.getProductComponent();

        provider.setName("Create main content");
        test = provider.getManualTestCase(5);
        test.setAutomationStatus(AutomationStatusType.DONE);
        test.setSuite(SuiteType.INTEGRATION);
        test.setAutomatedTestCase(provider.getTestCase());
        test.setRequirement(null);
        test.setMainStep(test.getSteps().get(3));
        test.setPriority(TestPriorityType.LOW);
        test.setLastUpdater(user);
        test.getComponent1().setName("Content");
        productComponentService.update(test.getComponent1());
        test.getComponent2().setName("Documents");
        productComponentService.update(test.getComponent2());
        manualTestCaseService.update(test);

        step = test.getSteps().get(0);
        step.setStep("Launch application");
        manualTestStepService.update(step);

        step = test.getSteps().get(1);
        step.setStep("Navigate to content page");
        step.setData("main");
        manualTestStepService.update(step);

        step = test.getSteps().get(2);
        step.setStep("Press Create menu > Create Content");
        step.setData("Fill with all kinds of characters i18n míñç usernew/míñç üller馬ǎ Yǒu小宝añuet> อดนิยม Николай Юрий Геоий /test6@clarolab.com");
        step.setExpectedResult("Content should be created");
        manualTestStepService.update(step);

        step = test.getSteps().get(3);
        step.setStep("Press Create menu > Create Content");
        step.setData("Fill with all kinds of characters i18n míñç usernew/míñç üller馬ǎ Yǒu小宝añuet> อดนิยม Николай Юрий Геоий /test6@clarolab.com");
        step.setExpectedResult("Content should be created");
        manualTestStepService.update(step);

        step = test.getSteps().get(4);
        step.setStep("Go to home page");
        step.setData("");
        step.setExpectedResult("Content should appear");
        manualTestStepService.update(step);

        provider.setName(oldName);

    }

    private void createManualPlansAndExecutions() {
        String oldName = provider.getName();
        provider.getContainer();
        provider.setManualTestPlan(null);

        provider.setName("Smoke Test");
        ManualTestPlan manualTestPlan = provider.getManualTestPlan();

        List<ManualTestCase> manualTestCases = manualTestCaseService.findAll();

        provider.setManualTestExecution(null);
        ManualTestExecution manualTestExecution = provider.getManualTestExecution();
        manualTestExecution.setTestCase(manualTestCases.get(0));
        manualTestExecution.setTestPlan(manualTestPlan);
        manualTestExecution.setLastExecutionTime(DateUtils.offSetDays(-27));
        manualTestExecutionService.update(manualTestExecution);
        manualTestCases.get(0).setLastExecution(manualTestExecution);
        manualTestCaseService.update(manualTestCases.get(0));

        provider.setManualTestExecution(null);
        manualTestExecution = provider.getManualTestExecution();
        manualTestExecution.setTestCase(manualTestCases.get(1));
        manualTestExecution.setTestPlan(manualTestPlan);
        manualTestExecution.setLastExecutionTime(DateUtils.offSetDays(-13));
        manualTestExecutionService.update(manualTestExecution);
        manualTestCases.get(1).setLastExecution(manualTestExecution);
        manualTestCaseService.update(manualTestCases.get(1));

        provider.setManualTestPlan(null);
        provider.setManualTestExecution(null);
        provider.setName("Search Service Test Plan ");
        manualTestPlan = provider.getManualTestPlan();
        manualTestExecution = provider.getManualTestExecution();
        manualTestExecution.setTestCase(manualTestCases.get(2));
        manualTestExecution.setTestPlan(manualTestPlan);
        manualTestExecution.setStatus(ExecutionStatusType.PASS);
        manualTestExecution.setLastExecutionTime(DateUtils.offSetDays(-2));
        manualTestExecutionService.update(manualTestExecution);
        manualTestCases.get(2).setLastExecution(manualTestExecution);
        manualTestCaseService.update(manualTestCases.get(2));

        provider.setName(oldName);
    }

    private void createApplicationEvents() {
        ApplicationEvent event = provider.getApplicationEvent();
        event.setType(ApplicationEventType.TIME_NEW_DAY);
        applicationEventService.update(event);

        eventTimeAgent.createDailyEvent();
    }

    private void createEvolutionStats() {
        int amount = 10;
        int offsetDays = 7;

        List<Executor> executors = executorService.findAllByEnabled(true);

        for (Executor executor : executors) {
            TrendGoal goal = executor.getGoal();

            for (int x = 0; x < amount; ++x) {
                EvolutionStat evolutionStat = null;
                provider.setEvolutionStat(null);
                provider.setTimestamp(DateUtils.offSetDays(-offsetDays * x));
                evolutionStat = provider.getEvolutionStat();
                evolutionStat.setExecutor(executor);

                if (goal != null) {
                    evolutionStat.setGrowth((int) DataProvider.getRandomNumber(2, 2));
                    evolutionStat.setPassing((int) DataProvider.getRandomNumber(1, 1));
                    evolutionStat.setTriageDone((int) DataProvider.getRandomNumber(1, 1));
                    evolutionStat.setStability((int) DataProvider.getRandomNumber(1, 1));
                    evolutionStat.setCommits((int) DataProvider.getRandomNumber(1, 2));
                }

                evolutionStatService.update(evolutionStat);
            }
        }
    }

    private void createEventExecutions() {
        Type listType = new TypeToken<List<EventExecution>>() {
        }.getType();
        String file = "test_data/sample_event_executions.json";
        Gson gsonRead = new Gson();
        String fileContent = null;
        try {
            fileContent = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(file), Charset.defaultCharset());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error reading file: " + file, e);
        }
        List<EventExecution> events = gsonRead.fromJson(fileContent, listType);

        SearchExecutor search = SearchExecutor.builder()
                .timestamp(DateUtils.now())
                .enabled(true)
                .name("t-Triage errors")
                .pattern("%d %p %C{1.} [%t] %m%n")
                .packageNames("com.clarolab")
                .build();
        search = searchExecutorService.save(search);

        LogAlert alert = LogAlert.builder()
                .timestamp(DateUtils.now())
                .enabled(true)
                .sid("scheduler__search__RMD5281c65b874069b2f_at_1618344960_27")
                .appName("t-triage alert")
                .owner(realDataProvider.getUserRealName())
                .date(1618344962002L)
                .lastCheck(1618344962002L)
                .host("localhost")
                .url("https://127.0.0.1:8089")
                .searchExecutor(search)
                .build();
        alert = logAlertService.save(alert);

        for (EventExecution event : events) {
            ErrorCase eventError = event.getError();
            ErrorCase error = errorCaseService.findErrorBySimilarity(eventError.getLevel(), eventError.getPath(), eventError.getMessage(), eventError.getStackTrace());
            if (error == null)
                errorCaseService.save(event.getError());
            else
                event.setError(error);
            event.setAlert(alert);
            event = eventExecutionService.save(event);
        }
    }

    private void createLogs() {
        int firstTestAmount = 6;
        int secondTestAmount = 150;
        int sameCommitHash = 3;
        User user = provider.getUser();
        CVSLog cvsLog;
        int i = 0;
        long date = 0;

        for (TestTriagePopulate testPopulated : tests) {
            if (i == 0) {
                // Creating several entries for the first test case
                for (int j = 0; j < firstTestAmount; j++) {
                    date = DateUtils.now();
                    cvsLog = CVSLog.builder()
                            .author(user)
                            .authorRealname(user.getRealname())
                            .authorText(user.getUsername())
                            .commitHash(j < sameCommitHash ? "6c16b7b4" : "425a6813")
                            .commitDate(date)
                            .commitDay(DateUtils.covertToString(date, DateUtils.DATE_SMALL))
                            .test(testPopulated.getTestTriage().getTestCase())
                            .logType(LogType.GIT)
                            .updatedLines((int) DataProvider.getRandomNumber(0, 2))
                            .product(provider.getProduct())
                            .build();
                    cvsLog = logService.save(cvsLog);
                }
            }

            date = DateUtils.now();
            cvsLog = CVSLog.builder()
                    .author(testPopulated.getTestTriage().getTriager())
                    .authorRealname(testPopulated.getTestTriage().getTriager().getRealname())
                    .authorText(testPopulated.getTestTriage().getTriager().getUsername())
                    .commitHash(DataProvider.getRandomHash())
                    .commitDate(date)
                    .commitDay(DateUtils.covertToString(date, DateUtils.DATE_SMALL))
                    .test(testPopulated.getTestTriage().getTestCase())
                    .logType(LogType.GIT)
                    .updatedLines((int) DataProvider.getRandomNumber(0, 2))
                    .product(provider.getProduct())
                    .build();
            cvsLog = logService.save(cvsLog);

            i++;
        }

        provider.clear();
        int randomNumber = 0;
        int x = 0;
        String commitHash = DataProvider.getRandomHash();
        LogType logType;

        while (x < secondTestAmount) {

            if (x + randomNumber >= secondTestAmount)
                randomNumber = secondTestAmount - x;
            else
                randomNumber = (int) DataProvider.getRandomNumber(0, 1);

            for (int j = x + 1; j <= (randomNumber + x); ++j) {
                if (j == x + 1) {
                    provider.setUser(null);
                    commitHash = DataProvider.getRandomHash();
                }
                if (j - x == randomNumber / 2)
                    provider.setTestExecution(null);
                if (j - x == randomNumber / 3) {
                    commitHash = DataProvider.getRandomHash();
                    date = DateUtils.offSetDays(-(x / 5));
                }
                if (x < secondTestAmount * 0.8)
                    logType = LogType.GIT;
                else if (x < secondTestAmount * 0.15)
                    logType = LogType.SVN;
                else
                    logType = LogType.MERCURIAL;

                cvsLog = CVSLog.builder()
                        .author(provider.getUser())
                        .authorRealname(provider.getUser().getRealname())
                        .authorText(provider.getUser().getUsername())
                        .commitHash(commitHash)
                        .commitDate(date)
                        .commitDay(DateUtils.covertToString(date, DateUtils.DATE_SMALL))
                        .test(provider.getTestCase())
                        .logType(logType)
                        .updatedLines((int) DataProvider.getRandomNumber(0, 2))
                        .product(provider.getProduct())
                        .build();
                cvsLog = logService.save(cvsLog);
            }

            x += randomNumber;

        }

        log.log(Level.INFO, String.format("CVSLog: successfully populated %d logs!", logService.count()));
    }

    public List<CVSLog> createLogsPublic() {
        User user = provider.getUser();
        CVSLog log;
        Product product = provider.getProduct();
        long date = DateUtils.now();
        log = CVSLog.builder()
                .author(user)
                .authorRealname(user.getRealname())
                .authorText(user.getUsername())
                .test(provider.getTestCase())
                .updatedLines((int) DataProvider.getRandomNumber(0, 2))
                .commitHash(DataProvider.getRandomHash())
                .commitDate(date)
                .commitDay(DateUtils.covertToString(date, DateUtils.DATE_SMALL))
                .product(provider.getProduct())
                .build();
        List<CVSLog> list = new ArrayList<>();
        list.add(log);
        log = logService.save(log);
        //System.out.println("Lista de CVSLogs: " + log);

        return list;
    }


    public License createDemoLicense() {
        License license;
        long date = DateUtils.now();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        long expStamp = cal.getTimeInMillis();


        license = License.builder()
                .creationTime(date)
                .expirationTime(expStamp)
                .expired(false)
                .licenseCode("j2JxHD0xnPk0wOI3J37t1k4yiSD5epHTWDOi$XvvrvItfEEZWGyCyuiTcspJtSHI6PY7NJQFdli50nRTzIGnvVwXflA==")
                .free(false)
                .build();

        license = licenseService.save(license);

        return license;
    }

    public String getReportContentFromJson(InputStream inputStream) {
        try {
            return IOUtils.toString(inputStream, Charset.defaultCharset());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error getting content from json", e);
            return null;
        }
    }

    private Map<String, InputStream> getReport(String type) {
        reports = Maps.newHashMap();
        List<String> collection = null;
        if ("jest".equals(type.toLowerCase()))
            collection = jestFiles;
        for (String file : collection) {
            reports.put(file, getClass().getResourceAsStream(file));
        }
        return reports;
    }

    public InputStream getReport(String type, String key) {
        return this.getReport(type).get(key);
    }

    private void createPushReports() {
        provider.setUseRandom(false);
        provider.setName("Jest test suite");
        provider.setTestExecution(null);
        provider.setBuildTriage(null);
        provider.setExecutor(null);
        provider.setBuild(null);
        provider.getExecutor();
        executorService.update(provider.getExecutor());
        Container container = provider.getContainer();
        container.setName("aurora-smoke-suite-view/" + jestFiles.get(0));
        containerService.update(container);
        String[] aux = container.getName().split("/");
        String title = aux[0];
        log.info("Testing jest file: ");
        InputStream inputStream = RealDataProvider.class.getClassLoader().getResourceAsStream(jestFiles.get(0));
        String content = getReportContentFromJson(inputStream);
        ApplicationContextService context = getActualAppContext(provider.getExecutor());
        String version = JsonUtils.getApplicationVersionFromJson(content);

        InputStream inputStream2 = RealDataProvider.class.getClassLoader().getResourceAsStream(jestFiles.get(1));
        String content2 = getReportContentFromJson(inputStream2);
        ApplicationContextService context2 = getActualAppContext(provider.getExecutor());
        String version2 = JsonUtils.getApplicationVersionFromJson(content2);

        DataDTO dataDTO1 = new DataDTO();
        dataDTO1.setArtifacts(new ArrayList<>());
        dataDTO1.setViewName("aurora-smoke-suite-view/" + jestFiles.get(0));
        dataDTO1.setJobName("aurora-smoke-suite/" + jestFiles.get(0));

        dataDTO1.setJobId(0l);
        dataDTO1.setBuildNumber(213);
        dataDTO1.setJobUrl("https://jenkins.dev.lithium.com/job/aurora-smoke-suite/");
        // dataDTO1.setJobName("production-smoke-testsuite");
        dataDTO1.setBuildStatus("FAILURE");
        dataDTO1.setBuildUrl("https://jenkins.dev.lithium.com/job/aurora-smoke-suite/213/");

        ArtifactDTO artifact = new ArtifactDTO();
        artifact.setContent(content);
        artifact.setFileName(jestFiles.get(0));
        // artifact.setFileType("xml");
        artifact.setFileType("json");
        artifact.setUrl("https://jenkins.dev.lithium.com/job/aurora-smoke-suite/213/artifact/aurora/e2e-tests/reports/bundled-report.json");
        dataDTO1.getArtifacts().add(artifact);

        ArtifactDTO artifact2 = new ArtifactDTO();
        artifact2.setContent(content2);
        artifact2.setFileName(jestFiles.get(1));
        // artifact.setFileType("xml");
        artifact2.setFileType("json");
        artifact2.setUrl("https://jenkins.dev.lithium.com/job/aurora-smoke-suite/213/artifact/aurora/e2e-tests/reports/bundled-report.json");
        dataDTO1.getArtifacts().add(artifact2);

//            Report report = ReportUtils.builder().context(context).applicationTestingEnvironmentVersion(version).cvsLogs("").build().getReportData(dataDTO1);
//            reportService.update(report);
        dataDTO1.setTimestamp(DateUtils.now());
        pushService.push(dataDTO1);

        container.setName(title);
        containerService.update(container);

    }

    private void createPushReports2() {
        provider.setUseRandom(false);
        provider.setName("Jest test suite");
        provider.setTestExecution(null);
        provider.setBuildTriage(null);
        provider.setExecutor(null);
        provider.setBuild(null);
        provider.getExecutor();
        executorService.update(provider.getExecutor());
        Container container = provider.getContainer();
        container.setName("aurora-smoke-suite-view/" + jestFiles.get(0));
        containerService.update(container);
        String[] aux = container.getName().split("/");
        String title = aux[0];
        log.info("Testing jest file: ");
        InputStream inputStream = RealDataProvider.class.getClassLoader().getResourceAsStream(jestFiles.get(0));
        String content = getReportContentFromJson(inputStream);
        ApplicationContextService context = getActualAppContext(provider.getExecutor());
        String version = JsonUtils.getApplicationVersionFromJson(content);

        InputStream inputStream2 = RealDataProvider.class.getClassLoader().getResourceAsStream(jestFiles.get(1));
        String content2 = getReportContentFromJson(inputStream2);
        ApplicationContextService context2 = getActualAppContext(provider.getExecutor());
        String version2 = JsonUtils.getApplicationVersionFromJson(content2);

        DataDTO dataDTO1 = new DataDTO();
        dataDTO1.setArtifacts(new ArrayList<>());
        dataDTO1.setViewName("aurora-smoke-suite-view/" + jestFiles.get(0));
        dataDTO1.setJobName("aurora-smoke-suite/" + jestFiles.get(0));

        dataDTO1.setJobId(0l);
        dataDTO1.setBuildNumber(214);
        dataDTO1.setJobUrl("https://jenkins.dev.lithium.com/job/aurora-smoke-suite/");
        // dataDTO1.setJobName("production-smoke-testsuite");
        dataDTO1.setBuildStatus("FAILURE");
        dataDTO1.setBuildUrl("https://jenkins.dev.lithium.com/job/aurora-smoke-suite/213/");

        ArtifactDTO artifact = new ArtifactDTO();
        artifact.setContent(content);
        artifact.setFileName(jestFiles.get(0));
        // artifact.setFileType("xml");
        artifact.setFileType("json");
        artifact.setUrl("https://jenkins.dev.lithium.com/job/aurora-smoke-suite/213/artifact/aurora/e2e-tests/reports/bundled-report.json");
        dataDTO1.getArtifacts().add(artifact);

        ArtifactDTO artifact2 = new ArtifactDTO();
        artifact2.setContent(content2);
        artifact2.setFileName(jestFiles.get(1));
        // artifact.setFileType("xml");
        artifact2.setFileType("json");
        artifact2.setUrl("https://jenkins.dev.lithium.com/job/aurora-smoke-suite/213/artifact/aurora/e2e-tests/reports/bundled-report.json");
        dataDTO1.getArtifacts().add(artifact2);

//            Report report = ReportUtils.builder().context(context).applicationTestingEnvironmentVersion(version).cvsLogs("").build().getReportData(dataDTO1);
//            reportService.update(report);
        dataDTO1.setTimestamp(DateUtils.now());
        pushService.push(dataDTO1);

        container.setName(title);
        containerService.update(container);

    }
    private void pushCypressReport(){
        String file = jestBasePath+"cypress-results-B941.json";
        provider.setUseRandom(false);
        provider.setName("Cypress test suite");
        provider.setTestExecution(null);
        provider.setBuildTriage(null);
        provider.setExecutor(null);
        provider.setBuild(null);
        provider.getExecutor();
        executorService.update(provider.getExecutor());
        Container container = provider.getContainer();
        container.setName("aurora-smoke-suite-view/"+ file);
        containerService.update(container);
        container.setName("aurora-smoke-suite-view");
        containerService.update(container);

        log.info("Testing cypress file: " + file);
        InputStream inputStream = RealDataProvider.class.getClassLoader().getResourceAsStream(file);
        String content = getReportContentFromJson(inputStream);
        ApplicationContextService context = getActualAppContext(provider.getExecutor());
        String version = JsonUtils.getApplicationVersionFromJson(content);


        DataDTO dataDTO1 = new DataDTO();
        dataDTO1.setArtifacts(new ArrayList<>());
        dataDTO1.setViewName("aurora-smoke-suite-view");
        dataDTO1.setJobName("aurora-smoke-suite");
        dataDTO1.setJobId(0l);
        dataDTO1.setBuildNumber(213);
        dataDTO1.setJobUrl("https://jenkins.dev.lithium.com/job/aurora-smoke-suite/");
        // dataDTO1.setJobName("production-smoke-testsuite");
        dataDTO1.setBuildStatus("FAILURE");
        dataDTO1.setBuildUrl("https://jenkins.dev.lithium.com/job/aurora-smoke-suite/213/");

        ArtifactDTO artifact = new ArtifactDTO();
        artifact.setContent(content);
        artifact.setFileName("cypress-report.json");
        // artifact.setFileType("xml");
        artifact.setFileType("json");
        artifact.setUrl("https://jenkins.dev.lithium.com/job/aurora-smoke-suite/213/artifact/aurora/e2e-tests/reports/bundled-report.json");
        dataDTO1.getArtifacts().add(artifact);

        pushService.push(dataDTO1);
    }
    private void pushPythonReport(){
        String file = jestBasePath+"python1.json";
        provider.setUseRandom(false);
        provider.setName("Python test suite");
        provider.setTestExecution(null);
        provider.setBuildTriage(null);
        provider.setExecutor(null);
        provider.setBuild(null);
        provider.getExecutor();
        executorService.update(provider.getExecutor());
        Container container = provider.getContainer();
        container.setName("aurora-smoke-suite-view/"+ file);
        containerService.update(container);
        container.setName("aurora-smoke-suite-view");
        containerService.update(container);

        log.info("Testing Python file: " + file);
        InputStream inputStream = RealDataProvider.class.getClassLoader().getResourceAsStream(file);
        String content = getReportContentFromJson(inputStream);
        ApplicationContextService context = getActualAppContext(provider.getExecutor());
        String version = JsonUtils.getApplicationVersionFromJson(content);


        DataDTO dataDTO1 = new DataDTO();
        dataDTO1.setArtifacts(new ArrayList<>());
        dataDTO1.setViewName("aurora-smoke-suite-view");
        dataDTO1.setJobName("Python suite");
        dataDTO1.setJobId(0l);
        dataDTO1.setBuildNumber(213);
        dataDTO1.setJobUrl("https://jenkins.dev.lithium.com/job/aurora-smoke-suite/");
        // dataDTO1.setJobName("production-smoke-testsuite");
        dataDTO1.setBuildStatus("FAILURE");
        dataDTO1.setBuildUrl("https://jenkins.dev.lithium.com/job/aurora-smoke-suite/213/");

        ArtifactDTO artifact = new ArtifactDTO();
        artifact.setContent(content);
        artifact.setFileName("python-report.json");
        // artifact.setFileType("xml");
        artifact.setFileType("json");
        artifact.setUrl("https://jenkins.dev.lithium.com/job/aurora-smoke-suite/213/artifact/aurora/e2e-tests/reports/bundled-report.json");
        dataDTO1.getArtifacts().add(artifact);

        pushService.push(dataDTO1);
    }
    private ApplicationContextService getActualAppContext(Executor executor) {
        return ApplicationContextService
                .builder()
                .product(executor.getContainer().getProduct())
                .executorService(executorService)
                .buildService(buildService)
                .testCaseService(testCaseService)
                .propertyService(propertyService)
                .containerService(containerService)
                .transactionManager(transactionManager)
                .container(executor.getContainer())
                .build();
    }
    private void productionTest() {
        prodRuleTest.testProductionScenario();
    }

}
