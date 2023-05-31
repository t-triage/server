/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.agents.TriageAgent;
import com.clarolab.connectors.CIConnector;
import com.clarolab.connectors.impl.utils.report.ReportUtils;
import com.clarolab.connectors.services.exceptions.BuildServiceException;
import com.clarolab.connectors.services.exceptions.ExecutorServiceException;
import com.clarolab.connectors.services.exceptions.ReportServiceException;
import com.clarolab.connectors.utils.ApplicationContextService;
import com.clarolab.dto.ExecutorStatChartDTO;
import com.clarolab.event.analytics.EvolutionStat;
import com.clarolab.model.*;
import com.clarolab.model.types.ConnectorType;
import com.clarolab.model.types.PopulateMode;
import com.clarolab.model.types.ReportType;
import com.clarolab.model.types.StatusType;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.ExecutorRepository;
import com.clarolab.util.DateUtils;
import com.clarolab.util.StringUtils;
import com.clarolab.view.KeyValuePair;
import com.google.common.collect.Lists;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static com.clarolab.util.Constants.DEFAULT_MAX_BUILDS_TO_PROCESS;
import static com.clarolab.util.Constants.MIN_SEARCH_LENGHT_;
import static com.clarolab.util.StringUtils.containsIgnoreCase;

@Service
@Log
public class ExecutorService extends BaseService<Executor> {

    public static final String TRIAGEDONE = "TRIAGEDONE";
    public static final String NOWPASSING = "NOWPASSING";
    public static final String FAIL = "FAIL";
    public static final String NEWFAIL = "NEWFAIL";


    @Autowired
    private ExecutorRepository executorRepository;

    @Autowired
    private TriageSpecService triageSpecService;

    @Autowired
    private TriageAgent triageAgent;

    @Autowired
    private BuildService buildService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private EvolutionStatService evolutionStatService;

    @Autowired
    protected PropertyService propertyService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private ReportService reportService;

    @Autowired
    protected CVSRepositoryService cvsRepositoryService;

    @Override
    public BaseRepository<Executor> getRepository() {
        return executorRepository;
    }

    public Executor findExecutorByName(String name) {
        return executorRepository.findByName(name);
    }

    public Executor findExecutorByContainerAndName(Container container, String name) {
        Executor executor = executorRepository.findByContainerAndName(container, name);
        if (executor == null) {
            // Some executors were imported with Container name - Job name
            executor = executorRepository.findByContainerAndName(container, container.getName() + " - " + name);

            if (executor == null) {
                // Looking for deleted executors.
                executor = executorRepository.findByContainerAndName(container, Executor.getDeletedName(name));

                if (executor == null) {
                    // Looking for deleted executors. Some executors were imported with Container name - Job name
                    executor = executorRepository.findByContainerAndName(container,  Executor.getDeletedName(container.getName() + " - " + name));
                }
            }
        }
        return executor;
    }

    public Executor delete(Executor executor) {
        executor.setEnabled(false);
        return update(executor);
    }

    public boolean populate(Executor executorFromDb) throws ExecutorServiceException {
        log.info("To populate executor " + executorFromDb.getName());
        CIConnector connector = CIConnector.getConnector(executorFromDb.getContainer()).connect();
        populate(executorFromDb, connector);
        connector.disconnect();
        return true;
    }

    public boolean populate(Executor executorFromDb, CIConnector connector) throws ExecutorServiceException {
        try {
            Instant startTime = DateUtils.instantNow();
            Build lastExecutedBuildOnDb = buildService.getLastBuild(executorFromDb);
            buildContext(connector.getContext(), executorFromDb);

            if (lastExecutedBuildOnDb != null) {
                int latestBuildOnDB = lastExecutedBuildOnDb.getNumber();
                int latestBuildOnCI = connector.getExecutorLatestBuild(executorFromDb);
                log.info(String.format("For Executor(name=%s) was found as last executed Build(number=%d) on database and Build(number=%d) on CI tool.", executorFromDb.getName(), latestBuildOnDB, latestBuildOnCI));
                if (latestBuildOnDB < latestBuildOnCI) {
                    log.log(Level.INFO, "Trying to get newest builds for Executor(name=" + executorFromDb.getName() + ")");
                    //Recover new builds and save them to db
                    // TODO: This is an experimental usage for bamboo only. If it works, we can try to replicate same in other connectors
                    ConnectorType connectorType = executorFromDb.getContainer().getConnector().getType();
                    if (connectorType.equals(ConnectorType.BAMBOO) || connectorType.equals(ConnectorType.JENKINS))
                        connector.getExecutorBuildsGreaterThan(executorFromDb, DEFAULT_MAX_BUILDS_TO_PROCESS, latestBuildOnDB);
                    else
                        connector.getExecutorBuilds(executorFromDb, DEFAULT_MAX_BUILDS_TO_PROCESS).stream().filter(build -> build.getNumber() > latestBuildOnDB).collect(Collectors.toList());
                } else {
                    log.info(String.format("Nothing to do for Executor(name=%s).", executorFromDb.getName()));
                }
                return true;
                //TODO: if latestBuildOnDB > latestBuildOnCI means that some builds where deleted from CI
            } else {
                log.log(Level.INFO, String.format("The executor does not have any builds, trying to get recent '%s' builds.", DEFAULT_MAX_BUILDS_TO_PROCESS));
                // executorFromDb.add(connector.getExecutorBuilds(executorFromDb, DEFAULT_MAX_BUILDS_TO_PROCESS));
                // That sentence produces: org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: com.clarolab.model.Executor.builds, could not initialize proxy - no Session,

                List<Build> newBuilds = connector.getExecutorBuilds(executorFromDb, DEFAULT_MAX_BUILDS_TO_PROCESS);
                newBuilds.forEach(build -> build.setExecutor(executorFromDb));
            }
            log.info(String.format("Populate for Executor(name=%s) has been completed successfully in %s", executorFromDb.getName(), DateUtils.getElapsedTime(startTime, DateUtils.instantNow())));
            update(executorFromDb);
        } catch (BuildServiceException e) {
            throw new ExecutorServiceException(String.format("[populate] : An error occurred trying to populate data for Executor(name=%s)", executorFromDb.getName()), e);
        }
        return true;
    }

    public Executor populateRequest(Executor executorFromDb) throws ExecutorServiceException {
        populate(executorFromDb);
        triageAgent.processExecutorInTransaction(executorFromDb);

        return executorFromDb;
    }

    public List<Long> getAllExecutorIds(long containerId) {
        return executorRepository.findAllExecutorIdsByContainerAndEnabled(containerId);
    }

    public List<Long> getAllExecutorIds() {
        return executorRepository.findAllExecutorIdsByEnabled();
    }

    public Executor getExecutorById(long executorID) {
        return executorRepository.getOne(executorID);
    }

    public TriageSpec getTriageSpec(Executor executor) {
        return triageSpecService.getTriageSpec(executor);
    }

    public List<Executor> findAllByContainerAndEnabled(Container container, boolean value) {
        return executorRepository.findAllByContainerAndEnabled(container, value);
    }

    public boolean checkIfThereAreByContainerAndEnabled(Container container, boolean value) {
        return executorRepository.countAllByContainerAndEnabled(container, value) > 0;
    }

    public List<Executor> findAllByEnabled(boolean value) {
        return executorRepository.findAllByEnabled(value);
    }

    public List<Executor> findAllByTimestampBetween(long timestampFrom, long timestampTo) {
        return executorRepository.findAllByTimestampBetween(timestampFrom, timestampTo);
    }

    public List<String> search(String name) {
        if (name == null || name.length() < MIN_SEARCH_LENGHT_)
            return Lists.newArrayList();
        name = StringUtils.prepareStringForSearch(name);
        List<Executor> executors = executorRepository.findAllByNameIgnoreCaseLike(name);

        return executors
                .stream()
                .filter(executor -> executor.getContainer().isActive())
                .map(Executor::getName)
                .collect(Collectors.toList());
    }

    public List<Executor> findAllByExecutorName(String search) {
        if (search == null || search.length() < MIN_SEARCH_LENGHT_) {
            return Lists.newArrayList();
        }

        search = StringUtils.prepareStringForSearch(search);
        List<Executor> executors = executorRepository.findAllByNameIgnoreCaseLike(search);

        return executors
                .stream()
                .filter(executor -> executor.getContainer().isActive())
                .collect(Collectors.toList());
    }

    public boolean searchTests(TestTriage testTriage, String search) {
        if (containsIgnoreCase(testTriage.getTestExecutionDisplayName(), search))
            return true;

        if (containsIgnoreCase(testTriage.getTestName(), search))
            return true;

        if (containsIgnoreCase(testTriage.getTextExecutionErrorDetails(), search))
            return true;

        return false;
    }

    public void buildContext(ApplicationContextService context) {
        buildContext(context, null);
    }

    public void buildContext(ApplicationContextService context, Executor executor) {
        context.setExecutorService(this);
        context.setBuildService(buildService);
        context.setTestCaseService(testCaseService);
        context.setPropertyService(propertyService);
        context.setExecutorToContext(executor);
        context.setTransactionManager(transactionManager);
        context.setContainerService(containerService);
        context.setCvsRepositoryService(cvsRepositoryService);
    }

    public List<Executor> findAllWithTestAndExecutorName(String search) {
        search = StringUtils.prepareStringForSearch(search);
        Set<Executor> set = new LinkedHashSet<>(executorRepository.findAllLikeTest(search));
        set.addAll(executorRepository.findAllByNameIgnoreCaseLike(search));
        return new ArrayList<>(set);
    }

    public List<Executor> findAllWithTestAndExecutorName(String search, Container container) {
        search = StringUtils.prepareStringForSearch(search);
        Set<Executor> set = new LinkedHashSet<>(executorRepository.findAllLikeTest(search, container));
        set.addAll(executorRepository.findAllByNameIgnoreCaseLikeAndContainer(search, container));
        return new ArrayList<>(set);
    }

    public List<KeyValuePair> getExecutorNames(Long id) {
        Container container = containerService.find(id);
        List<Object[]> list = executorRepository.findAllNames(container);
        return StringUtils.getKeyValuePairList(list);
    }

    public List<KeyValuePair> findExecutorsGroupedByUser(Product product) {
        List<Object[]> list = executorRepository.findExecutorsGroupedByUser(product.getName());
        return StringUtils.getKeyValuePairList(list);
    }

    public Long sumTestExecutedByProduct(Product product, Long prev, Long now) {
        if (product == null || StringUtils.isEmpty(product.getName())) {
            return null;
        }
        List<Object[]> statIds = executorRepository.selectLastExecutorStat(product.getName().toLowerCase(), prev, now);
        List<Long> list = new ArrayList<>();
        StringUtils.getKeyValuePairList(statIds).forEach(keyValuePair -> {
            list.add((Long) keyValuePair.getValue());
        });
        if (list.isEmpty()) {
            return null;
        }
        return executorRepository.sumTestExecutedByProduct(list);
    }


    public List<Report> getReportHistory(Long executorId) {
        Executor executor = find(executorId);
        List<Build> allTopTen = buildService.findAll(executor);
        return allTopTen
                .stream()
                .limit(15)
                .map(Build::getReport).collect(Collectors.toList());
    }

    public List<Build> getBuildHistory(Long executorId) {
        Executor executor = find(executorId);
        List<Build> collect = buildService.findAll(executor)
                .stream()
                .limit(15)
                .sorted(Comparator.comparingLong(Build::getNumber))
                .collect(Collectors.toList());

        return collect;
    }

    public String upload(Executor executor, String content, ReportType reportType) {

        CIConnector connector = CIConnector.getConnector(executor.getContainer()).connect();

        Build lastExecutedBuild = buildService.getLastBuild(executor);
        int buildNumber = 1;
        if (lastExecutedBuild != null) {
            buildNumber = lastExecutedBuild.getNumber() + 1;
        }

        buildContext(connector.getContext(), executor);
        Build build = Build.builder()
                .number(buildNumber)
                .buildId(executor.getName())
                .displayName(executor.getName()+"#"+buildNumber)
                .url("uploaded")
                .executedDate(DateUtils.now())
                .status(StatusType.FAIL)
                .report(null)
                .executor(executor)
                .container(executor.getContainer())
                .artifacts(null)
                .enabled(true)
                .populateMode(PopulateMode.UPLOAD)
                .timestamp(DateUtils.now())
                .build();
        Report report = null;
        try {
            report = ReportUtils.builder().context(connector.getContext()).build().createReport(content, reportType, "");
        } catch (ReportServiceException e) {
            log.log(Level.SEVERE, String.format("Error trying to process uploaded text: %s with type: %s for executor: %d", content, reportType, executor.getId()));
            return e.getLocalizedMessage();
        }
        
        if (report == null) {
            return "Could not create report.";
        }

        report.setExecutiondate(DateUtils.now());
        reportService.save(report);
        build.setReport(report);
        build = buildService.save(build);
        executor.add(build);
        update(executor);
        triageAgent.processExecutor(executor);
        log.info( executor.getName() + " uploaded");
        
        return "SUCCESS";
    }

    public List<ExecutorStatChartDTO> getGrowthStats(Long executorid, Long from, Long to) {
        Executor executor = find(executorid);
        if (executor == null)
            return null;

        TrendGoal trendGoal = executor.getGoal();
        List<EvolutionStat> stats = evolutionStatService.findAllEvolutionStatByExecutorSince(executorid, from, to);

        List<ExecutorStatChartDTO> executorStatChartDTOS = new ArrayList<>();

        for (EvolutionStat stat: stats) {
            ExecutorStatChartDTO statChartDTO = ExecutorStatChartDTO.builder()
                    .executor(executorid)
                    .expected(trendGoal.getExpectedGrowth())
                    .required(trendGoal.getRequiredGrowth())
                    .actual(stat.getGrowth())
                    .date(DateUtils.covertToString(stat.getTimestamp(), DateUtils.DATE_SMALL))
                    .build();
            executorStatChartDTOS.add(statChartDTO);
        }

        return executorStatChartDTOS;
    }

    public List<ExecutorStatChartDTO> getCommitsStats(Long executorid, Long from, Long to) {
        Executor executor = find(executorid);
        if (executor == null)
            return null;

        TrendGoal trendGoal = executor.getGoal();
        List<EvolutionStat> stats = evolutionStatService.findAllEvolutionStatByExecutorSince(executorid, from, to);

        List<ExecutorStatChartDTO> executorStatChartDTOS = new ArrayList<>();

        for (EvolutionStat stat: stats) {
            ExecutorStatChartDTO statChartDTO = ExecutorStatChartDTO.builder()
                    .executor(executorid)
                    .expected(trendGoal.getExpectedCommits())
                    .required(trendGoal.getRequiredCommits())
                    .actual(stat.getCommits())
                    .date(DateUtils.covertToString(stat.getTimestamp(), DateUtils.DATE_SMALL))
                    .build();
            executorStatChartDTOS.add(statChartDTO);
        }

        return executorStatChartDTOS;
    }

    public List<ExecutorStatChartDTO> getPassingStats(Long executorid,  Long from, Long to) {
        Executor executor = find(executorid);
        if (executor == null)
            return null;

        TrendGoal trendGoal = executor.getGoal();
        List<EvolutionStat> stats = evolutionStatService.findAllEvolutionStatByExecutorSince(executorid, from, to);

        List<ExecutorStatChartDTO> executorStatChartDTOS = new ArrayList<>();

        for (EvolutionStat stat: stats) {
            ExecutorStatChartDTO statChartDTO = ExecutorStatChartDTO.builder()
                    .executor(executorid)
                    .expected(trendGoal.getExpectedPassing())
                    .required(trendGoal.getRequiredPassing())
                    .actual(stat.getPassing())
                    .date(DateUtils.covertToString(stat.getTimestamp(), DateUtils.DATE_SMALL))
                    .build();
            executorStatChartDTOS.add(statChartDTO);
        }

        return executorStatChartDTOS;
    }

    public List<ExecutorStatChartDTO> getStabilityStats(Long executorid,  Long from, Long to) {
        Executor executor = find(executorid);
        if (executor == null)
            return null;

        TrendGoal trendGoal = executor.getGoal();
        List<EvolutionStat> stats = evolutionStatService.findAllEvolutionStatByExecutorSince(executorid, from, to);

        List<ExecutorStatChartDTO> executorStatChartDTOS = new ArrayList<>();

        for (EvolutionStat stat: stats) {
            ExecutorStatChartDTO statChartDTO = ExecutorStatChartDTO.builder()
                    .executor(executorid)
                    .expected(trendGoal.getExpectedStability())
                    .required(trendGoal.getRequiredStability())
                    .actual(stat.getStability())
                    .date(DateUtils.covertToString(stat.getTimestamp(), DateUtils.DATE_SMALL))
                    .build();
            executorStatChartDTOS.add(statChartDTO);
        }

        return executorStatChartDTOS;
    }

    public List<ExecutorStatChartDTO> getTriageDoneStats(Long executorid,  Long from, Long to) {
        Executor executor = find(executorid);
        if (executor == null)
            return null;

        TrendGoal trendGoal = executor.getGoal();
        List<EvolutionStat> stats = evolutionStatService.findAllEvolutionStatByExecutorSince(executorid, from, to);

        List<ExecutorStatChartDTO> executorStatChartDTOS = new ArrayList<>();

        for (EvolutionStat stat: stats) {
            ExecutorStatChartDTO statChartDTO = ExecutorStatChartDTO.builder()
                    .executor(executorid)
                    .expected(trendGoal.getExpectedTriageDone())
                    .required(trendGoal.getRequiredTriageDone())
                    .actual(stat.getTriageDone())
                    .date(DateUtils.covertToString(stat.getTimestamp(), DateUtils.DATE_SMALL))
                    .build();
            executorStatChartDTOS.add(statChartDTO);
        }

        return executorStatChartDTOS;
    }

    public List<Executor> getExecutorEnabled() {
        return executorRepository.findAllByEnabled(true);
    }

}