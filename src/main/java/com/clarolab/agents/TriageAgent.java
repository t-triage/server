/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.agents;

import com.clarolab.agents.processors.StateProcessor;
import com.clarolab.agents.processors.UndefinedProcessor;
import com.clarolab.agents.rules.StaticRuleDispatcher;
import com.clarolab.event.process.ApplicationEventBuilder;
import com.clarolab.event.process.ApplicationEventType;
import com.clarolab.jira.service.JiraAutomationService;
import com.clarolab.model.*;
import com.clarolab.model.types.StatusType;
import com.clarolab.service.*;
import com.clarolab.service.exception.ConfigurationError;
import com.clarolab.startup.LicenceValidator;
import com.clarolab.util.DateUtils;
import com.google.common.collect.Maps;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.PessimisticLockException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static com.clarolab.util.Constants.*;


@Log
@Component
public class TriageAgent implements SmartAgent {

    private Map<StatusType, StateProcessor> stateProcessorsMap;

    @Autowired
    JiraAutomationService jiraAutomationService;

    @Autowired
    StaticRuleDispatcher staticRuleDispatcher;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private BuildTriageService buildTriageService;

    @Autowired
    private TestTriageService testTriageService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private TriageSpecService triageSpecService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private ApplicationEventBuilder applicationEventBuilder;

    @Autowired
    private AutomatedTestIssueService automatedTestIssueService;

    @Autowired
    private IssueTicketService issueTicketService;

    @Autowired
    private UserService userService;

    @Autowired
    private LicenceValidator licenceValidator;

    @Override
    @Scheduled(fixedRate = DEFAULT_AGENT_TRIAGE_JOB_TIMEOUT, initialDelay = DEFAULT_AGENT_DELAY)
    public synchronized void execute() {

        if (propertyService.valueOf("TRIAGE_SERVICE_ENABLED", true)) {
            try {
                executeByBuildsSync();
            } catch (Exception e) {
                log.log(Level.SEVERE, "Triage Agent failed while triaging", e);
                throw e;
            }

        }
    }

    public void executeByBuildsSync() {
        loadProperties();
        Instant start = DateUtils.instantNow();
        List<Build> builds = buildService.getNonProcessedBuilds();
        if (builds.isEmpty()) {
            log.info("Triage Agent Executed, no builds to process.");
            return;
        }

        log.info(String.format("Executing Triage Agent, to process %s builds.", builds.size()));
        Map<Executor, List<Build>> processingList = getBuildProcessingList(builds);

        for (Executor executor : processingList.keySet()) {
            this.processBuildsSync(processingList.get(executor));
        }

        triageAgentExecuted(builds);
        log.info(String.format("Triage Agent Executed in %s.", DateUtils.getElapsedTime(start, DateUtils.instantNow())));
    }

    private Map<Executor, List<Build>> getBuildProcessingList(List<Build> builds) {
        Map<Executor, List<Build>> answer = Maps.newHashMap();
        Executor previousExecutor = null;
        List<Build> buildsSameExecutor = new ArrayList<>();
        for (Build build : builds) {
            if (previousExecutor == null) {
                previousExecutor = build.getExecutor();
            }

            if (build == null || build.getExecutor() == null || previousExecutor == null) {
                // for debugging
                if (build == null) {
                    log.log(Level.SEVERE, "Build null");
                } else {
                    if (build.getExecutor() == null) {
                        log.log(Level.SEVERE, String.format("Build executor null: %d", build.getId()));
                    }
                    if (previousExecutor == null) {
                        log.log(Level.SEVERE, String.format("Previous executor null: %d", build.getId()));
                    }
                }
                throw new RuntimeException();
            }

            if (!build.getExecutor().getId().equals(previousExecutor.getId())) {
                answer.put(previousExecutor, buildsSameExecutor);
                buildsSameExecutor = new ArrayList<>();
                previousExecutor = build.getExecutor();
            }
            buildsSameExecutor.add(build);
        }
        answer.put(previousExecutor, buildsSameExecutor);

        return answer;
    }

    @Transactional
    public boolean processExecutor(Executor executor) {
        return processExecutorInTransaction(executor);
    }

    @Transactional
    // Method invoked from other places to process a specific executor
    public boolean processExecutorInTransaction(Executor executor) {
        loadProperties();
        Instant start = DateUtils.instantNow();
        // TODO switch implementation
        // List<Build> builds = buildService.getNonProcessedBuilds(executor);
        List<Build> builds = executor.getBuilds().stream().filter(build -> !build.isProcessed()).collect(Collectors.toList());
        processBuilds(builds);
        log.info(String.format("Triage Agent Executed in %s.", DateUtils.getElapsedTime(start, DateUtils.instantNow())));
        triageAgentExecuted(builds);
        return true;
    }

    private void processBuildsSync(List<Build> buildsParam) {
        // process
        processBuilds(buildsParam);
    }

    public void processBuilds(List<Build> builds) {
        if (builds.isEmpty()) {
            return;
        }
        Build someBuild = builds.get(0);
        synchronized (someBuild.getExecutor().getId()) {
            Build lastBuild = null;
            boolean processed = false;
            for (Build build : builds) {
                processed = generateBuildTriage(build);
                if (processed) {
                    lastBuild = build;
                }
            }
            if (lastBuild != null) {
                activateOnlyLastBuildTriage(lastBuild.getExecutor(), builds, lastBuild);
            }
        }
    }

    private void markBuildAsProcessed(Build build) {
        build.setProcessed(true);
        buildService.update(build);
    }

    private void updateMaxTestExecuted(BuildTriage buildTriage) {
        final Executor executor = buildTriage.getExecutor();
        final Report report = buildTriage.getReport();

        if (executor.getMaxTestExecuted() < report.getTotalTest()) {
            executor.setMaxTestExecuted(report.getTotalTest());
            executorService.update(executor);
        }
    }

    private boolean generateBuildTriage(final Build build) {
        boolean processed = generateBuildTriage(build, 5);
        if (processed)
            log.log(Level.INFO, (String.format("Build(number=%d) processed successfully for Executor(name=%s , id=%d)", build.getNumber(), build.getExecutor().getName(), build.getExecutor().getId())));

        return processed;
    }

    private boolean generateBuildTriage(final Build dbBuild, int retries) {
        TransactionStatus transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());

        Build build = buildService.find(dbBuild.getId());
        TriageSpec spec = triageSpecService.getTriageSpec(build.getExecutor());


        log.info(String.format("Attempts (%d) for Executor(name=%s) --> Build(number=%d)", retries, build.getExecutor().getName(), build.getNumber()));
        if (retries == 0) {
            log.log(Level.SEVERE, (String.format("There is a problem processing Executor(name=%s) --> Build(number=%d), that could not be solved.", build.getExecutor().getName(), build.getNumber())));
            return false;
        }

        try {
            generateBuildTriageAndTest(build, spec);

            transactionManager.commit(transactionStatus);
        } catch (PessimisticLockException e) {
            log.log(Level.SEVERE, String.format("PessimisticLockException found trying to process Executor(name=%s) --> Build(number=%d). Waiting 6 secs and trying again.", build.getExecutor().getName(), build.getNumber()), e);
            transactionManager.rollback(transactionStatus);
            try {
                Thread.sleep(DEFAULT_EVENT_PROCESS_DELAY * 5);
                return generateBuildTriage(build, --retries);
            } catch (InterruptedException e1) {
                throw new RuntimeException(e1);
            }
        } catch (Exception e1) {
            log.log(Level.SEVERE, (String.format("There is a problem processing Build(number=%d)", build.getNumber())), e1);
            transactionManager.rollback(transactionStatus);
            return false;
        }
        return true;
    }

    public void generateBuildTriageAndTest(Build build, TriageSpec spec) {
        StatusType status = build.getStatus();

        StateProcessor processor = stateProcessorsMap.getOrDefault(status, new UndefinedProcessor());
        BuildTriage detail = processor.process(build, spec);

        detail = buildTriageService.create(detail);

        updateMaxTestExecuted(detail);

        generateTestTriage(build, spec, detail);

        markBuildAsProcessed(build);

        buildProcessedEvent(detail);
    }

    public void activateOnlyLastBuildTriage(Executor executor, List<Build> builds, Build lastBuild) {
        if (builds.size() < 1 || lastBuild == null) {
            return;
        }

        buildTriageService.expireAllExcept(lastBuild);
    }

    private void generateTestTriage(final Build build, final TriageSpec spec, BuildTriage buildTriage) {
        List<TestExecution> testExecutions = build.getTestCasesUnique(true);
        boolean allTestTriaged = true;
        int testsValidated = licenceValidator.validateTestTriaged(testExecutions.size());

        if (licenceValidator.validateTestTriaged()) {
            for (TestExecution testCase : testExecutions) {
                if (testsValidated == 0)
                    break;

                try {
                    TestTriage testTriage = null;
                    testTriage = staticRuleDispatcher.process(testCase, build, spec);

                    testTriage.setBuildTriage(buildTriage);
                    testTriage.initialize();

                    testTriage = testTriageService.save(testTriage);

                    boolean automationModifiedTestTriage = automatedTestIssueService.testTriageCreated(testTriage);

                    boolean issueTicketModifiedTestTriage = issueTicketService.testTriageCreated(testTriage);

                    allTestTriaged = testTriage.isTriaged() && allTestTriaged;

                    if (automationModifiedTestTriage || issueTicketModifiedTestTriage) {
                        testTriageService.update(testTriage);
                    }

                    testsValidated--;
                } catch (Exception e) {
                    log.log(Level.SEVERE, (String.format("There is a problem processing Build(number=%d) (id=%d) for Executor(name=%s)", build.getNumber(), build.getId(), build.getExecutor().getName())), e);
                    throw new RuntimeException(e);
                }
            }
        } else {

            log.info("License not valid...");
            throw new ConfigurationError("Unable to create Test Triage, you have reached the limit of 200 triaged tests.");
        }

        if (allTestTriaged && testExecutions.size() > 0) {
            // Only perform the build triage if there are tests and all of them were triaged
            triageBuild(buildTriage, spec);
        }
    }

    @Autowired
    public void setStateProcessorsMap(List<StateProcessor> stateProcessorsMap) {
        this.stateProcessorsMap = stateProcessorsMap.stream().collect(Collectors.toMap(StateProcessor::processType, Function.identity()));
    }

    public void triageAgentExecuted(List<Build> builds) {
        if (builds.size() > 0) {
            ApplicationEvent event = applicationEventBuilder.newEvent();
            event.setType(ApplicationEventType.TRIAGE_AGENT_EXECUTED);
            String buildIds = builds.stream()
                    .map(build -> String.valueOf(build.getId()))
                    .collect(Collectors.joining(","));
            event.setExtraParameter(buildIds);
            applicationEventBuilder.appendExtraParameter(event, ",");
        }
    }

    private void buildProcessedEvent(BuildTriage build) {
        ApplicationEvent event = applicationEventBuilder.newEvent();
        event.setType(ApplicationEventType.BUILD_TRIAGE_GENERATED_FOR_EXECUTOR);
        event.setSource(build);
        event.setExtraParameter(String.valueOf(build.getId()));
        applicationEventBuilder.saveUnique(event, true);
    }

    public void containerProcessed(long containerId) {
        ApplicationEvent event = applicationEventBuilder.newEvent();
        event.setType(ApplicationEventType.BUILD_TRIAGE_GENERATED_FOR_CONTAINER);
        event.setExtraParameter(String.valueOf(containerId));
        applicationEventBuilder.saveUnique(event, true);
    }

    private void triageBuild(BuildTriage buildTriage, TriageSpec spec) {
        buildTriage.setTriaged();
        buildTriage.setTriager(spec.getTriager());

        buildTriageService.update(buildTriage);
    }

    @Bean()
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(DEFAULT_SCHEDULER_POOL_SIZE);
        return taskScheduler;
    }

    private void loadProperties() {
        propertyService.warmUp();
    }

}
