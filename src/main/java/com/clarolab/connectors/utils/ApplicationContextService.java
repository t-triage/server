/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.utils;

import com.clarolab.model.*;
import com.clarolab.model.types.ReportType;
import com.clarolab.service.*;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Collections;
import java.util.List;

import static com.clarolab.util.Constants.DEBUG_STEP_PROCESS;

@Setter
@Getter
@Log
public class ApplicationContextService {
    private Product product;
    private Container container;
    @Setter(AccessLevel.PRIVATE)
    private Executor executor;
    private Build build;
    private Report report;
    private BuildService buildService;
    private ExecutorService executorService;
    private TestCaseService testCaseService;
    private PropertyService propertyService;
    private int latestBuildOnDB = 0;
    private ReportType reportType;
    private PlatformTransactionManager transactionManager;
    private ContainerService containerService;
    private CVSRepositoryService cvsRepositoryService;

    @Builder
    private ApplicationContextService(Product product, Container container, Executor executor, Build newBuild, BuildService buildService, ExecutorService executorService, TestCaseService testCaseService, Report report, int latestBuildOnDB, PropertyService propertyService, ReportType reportType, PlatformTransactionManager transactionManager, ContainerService containerService, CVSRepositoryService cvsRepositoryService) {
        this.product = product;
        this.container = container;
        this.executor = executor;
        this.report = report;
        this.build = newBuild;
        this.buildService = buildService;
        this.executorService = executorService;
        this.testCaseService = testCaseService;
        this.latestBuildOnDB = latestBuildOnDB;
        this.propertyService = propertyService;
        this.reportType = reportType;
        this.transactionManager = transactionManager;
        this.containerService = containerService;
        this.cvsRepositoryService = cvsRepositoryService;
        setNewBuildNumber();
        setProduct();
    }


    public void sortBuilds(List<Comparable> builds) {
        Collections.sort(builds);
    }

    public void sortBuildsAscending(List<?> buildsFromJob) {
        Collections.reverse(buildsFromJob);
    }

    public Build save(Build build) {
        TransactionStatus transactionStatus = getTransactionManager().getTransaction(new DefaultTransactionDefinition());
        if (getExecutor().isPersistent()) {
            setExecutor(getExecutorService().find(getExecutor().getId()));
        }

        if (build.getNumber() <= getLatestBuildOnDB()) {
            return build;
        }

        setUniqueTestCase(build);

        if (build.getExecutor() == null) {
            getExecutor().add(build);
            if (getExecutor().getId() == null || getExecutor().getId() <= 1) {
                setContainer(getContainerService().find(getContainer().getId()));
                if (getExecutor().getContainer() == null) {
                    getContainer().add(getExecutor());
                }
                setExecutor(getExecutorService().save(getExecutor()));

                getContainerService().update(getContainer());
            }
        } else {
            if (build.getExecutor().getId() == null || build.getExecutor().getId() <= 1) {
                setExecutor(getExecutorService().save(build.getExecutor()));
                build.setExecutor(getExecutor());
            }
        }

        log.info(String.format("Trying to save Build(number=%d) --> Executor(name=%s)",build.getNumber(), build.getExecutor().getName()));
        Build savedBuild = getBuildService().save(build);
        getTransactionManager().commit(transactionStatus);
        log.info(String.format("Saved Build(id=%d, number=%d) --> Executor(name=%s)", savedBuild.getId(), savedBuild.getNumber(), build.getExecutor().getName()));
        return savedBuild;
    }

    public void setBuild(Build newBuild) {
        build = newBuild;
        buildChange();
    }

    private void buildChange() {
        getTestCaseService().cleanNewTests();
    }

    private void setNewBuildNumber() {
        setLatestBuildOnDB(0);
        if (getExecutor() != null && getExecutor().isPersistent()) {
            Build build = getBuildService().getLastBuild(getExecutor());
            if (build != null) {
                setLatestBuildOnDB(build.getNumber());
            }
        }
    }

    private void setProduct() {
        if (product != null) {
            if (executor != null) {
                product = executor.getProduct();
            } else if (container != null) {
                product = container.getProduct();
            }
        }
    }

    public void setExecutorToContext(Executor newExecutor) {
        setExecutor(newExecutor);
        setNewBuildNumber();
        buildChange();
    }

    public boolean debugStepProcess() {
        return getPropertyService().valueOf("DEBUG_STEP_PROCESS", DEBUG_STEP_PROCESS);
    }

    public ReportType getRecentlyUsedReport(){
        if(getReportType() == null)
            log.info("There is no information about recently used report yet.");
        else
            log.info(String.format("Getting recently used report: %s.", ReportType.getType(getReportType())));
        return getReportType();
    }

    public void configureRecentlyUsedReport(ReportType reportType){
        if(getReportType() == null || getReportType() == ReportType.UNKNOWN){
            log.info(String.format("Configuring recently used report: %s.", ReportType.getType(reportType)));
            setReportType(reportType);
        }
        buildChange();
    }

    public void cleanRecentlyUsedReport(){
        log.info("Cleaning recently used report.");
        setReportType(null);
    }

    public void setUniqueTestCase(Build build) {
        TestCase dbTest;
        List<TestCase> newTests = Lists.newArrayList();

        for (TestExecution testExecution : build.getReport().getTestExecutions()) {
            if (!testExecution.getTestCase().isPersistent()) {
                dbTest = getTestCaseService().find(testExecution.getTestCase());
                if (dbTest != null) {
                    testExecution.setTestCase(dbTest);
                } else {
                    dbTest = getTestCaseService().findNewTest(newTests, testExecution.getTestCase());
                    if (dbTest == null) {
                        newTests.add(testExecution.getTestCase());
                    } else {
                        testExecution.setTestCase(dbTest);
                    }
                }
            } else {
                testExecution.setTestCase(getTestCaseService().find(testExecution.getTestCase().getId()));
            }
        }
    }
}
