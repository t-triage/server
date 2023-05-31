/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.event.analytics;

import com.clarolab.event.process.AbstractEventHandler;
import com.clarolab.event.process.ApplicationEventType;
import com.clarolab.event.process.EventHandler;
import com.clarolab.model.*;
import com.clarolab.model.manual.ManualTestExecution;
import com.clarolab.model.manual.service.ManualTestCaseService;
import com.clarolab.model.manual.service.ManualTestExecutionService;
import com.clarolab.service.BuildTriageService;
import com.clarolab.service.ExecutorService;
import com.clarolab.service.LogService;
import com.clarolab.service.ProductService;
import com.clarolab.serviceDTO.ExecutorServiceDTO;
import com.clarolab.util.DateUtils;
import com.clarolab.view.ExecutorView;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.clarolab.util.DateUtils.BaseDateFormat;

@Component
@Log
public class AnalyticsDailyEventHandler extends AbstractEventHandler implements EventHandler {

    @Autowired
    private Environment environment;

    @Autowired
    private ExecutorServiceDTO executorServiceDTO;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private ExecutorStatService executorStatService;

    @Autowired
    private ProductStatService productStatService;

    @Autowired
    private LogService logService;

    @Autowired
    private UserStatService userStatService;

    @Autowired
    private ProductService productService;

    @Autowired
    private BuildTriageService buildTriageService;

    @Autowired
    private ManualTestCaseService manualTestCaseService;

    @Autowired
    private ManualTestExecutionService manualTestExecutionService;

    @Autowired
    private ManualTestStatService manualTestStatService;

    @Override
    public ApplicationEventType[] handleTypes() {
        ApplicationEventType[] handleTypes = {ApplicationEventType.TIME_NEW_DAY};

        if (environment.acceptsProfiles(Profiles.of("dev"))) {
            // This is only for development purposes in order not to wait one day.
            ApplicationEventType[] newHandleTypes = {ApplicationEventType.TIME_NEW_DAY, ApplicationEventType.BUILD_TRIAGE_GENERATED_FOR_EXECUTOR};
            return newHandleTypes;
        }

        return handleTypes;
    }

    @Override
    public boolean process(ApplicationEvent event) {
        if (event.getType().equals(ApplicationEventType.TIME_NEW_DAY)) {
            return processYesterdayEvents(event);
        }
        if (event.getType().equals(ApplicationEventType.BUILD_TRIAGE_GENERATED_FOR_EXECUTOR)) {
            return processSuite(event);
        }
        return true;
    }

    // Analyze the executions created the day before the event was created
    public boolean processYesterdayEvents(ApplicationEvent event) {
        long since = DateUtils.beginDay(-1);

        processExecutorStat(since, event);
        processProductStat(since, event);
        processUserStat(since, event);
        processManualTestStat(since);

        return true;
    }

    private void processExecutorStat(long sinceTime, ApplicationEvent event) {
        ExecutorStat executorStat;

        //List<ExecutorView> views = executorServiceDTO.getExecutorViewsBetween(cal.getTimeInMillis(), event.getTimestamp());
        List<Executor> executors = buildTriageService.findAllByTimestampBetween(sinceTime, event.getTimestamp()).stream().map(BuildTriage::getExecutor).collect(Collectors.toList());
        List<ExecutorView> views = executorServiceDTO.getExecutorListFrom(executors);
        for (ExecutorView view : views) {
            executorStat = getNewStat(event, view);
            // TODO populate with more insights data
            executorStatService.save(executorStat);
        }
    }

    private void processProductStat(long sinceTime, ApplicationEvent event) {
        ProductStat productStat = new ProductStat();

        long pass = 0;
        long skip = 0;
        long newFails = 0;
        long fails = 0;
        long nowPassing = 0;
        long toTriage = 0;
        long totalTests = 0;
        long autoTriaged = 0;
        int commits = 0;


        List<ExecutorStat> stats = executorStatService.findAllBetweenGroupByProduct(sinceTime, event.getTimestamp());
        for (int i=0; i < stats.size(); i++) {
            Product product = stats.get(i).getProduct();
            int productCommits = productService.countCvsLogsByProductId(product, true);
            if (i != stats.size() &&  (i == 0 || stats.get(i).getProductName().equals(stats.get(i-1).getProductName()))) {
                pass += stats.get(i).getPass();
                skip += stats.get(i).getSkip();
                newFails += stats.get(i).getNewFails();
                fails += stats.get(i).getFails();
                nowPassing += stats.get(i).getNowPassing();
                toTriage += stats.get(i).getToTriage();
                totalTests += stats.get(i).getTotalTests() == null ? 0 : stats.get(i).getTotalTests();
                autoTriaged += stats.get(i).getAutoTriaged() == null ? 0 : stats.get(i).getAutoTriaged();
                commits += productCommits;

            } else {
                productStat.setProduct(stats.isEmpty() ? null: productService.findProductByName(stats.get(i-1).getProductName()));
                productStat.setPass(pass);
                productStat.setSkip(skip);
                productStat.setNewFails(newFails);
                productStat.setFails(fails);
                productStat.setNowPassing(nowPassing);
                productStat.setToTriage(toTriage);
                productStat.setTotalTests(totalTests);
                productStat.setAutoTriaged(autoTriaged);
                productStat.setCommits(commits);
                productStat.setTimestamp(event.getTimestamp());

                String date = BaseDateFormat.format(event.getEventTime());
                productStat.setActualDate(date);
                productStat.setExecutionDate(date);
                productStat.setDeadline(date);

                productStatService.save(productStat);

                pass = 0;
                skip = 0;
                newFails = 0;
                fails = 0;
                nowPassing = 0;
                toTriage = 0;
                totalTests = 0;
                commits = 0;
            }
        }
    }

    private void processUserStat(long sinceTime, ApplicationEvent event) {
        List<CVSLog> logs = logService.findAllByTimestampBetween(sinceTime, event.getTimestamp());

        userStatService.processUserStat(logs);

    }

    private void processManualTestStat(long sinceTime) {
        long totalTests = manualTestCaseService.count();

        List<ManualTestExecution> executionsSince = manualTestExecutionService.findByLastExecutionTime(sinceTime);

        long totalExecuted = executionsSince.size();
        long passed = 0;
        long failed = 0;

        for (ManualTestExecution mte : executionsSince) {
            if (mte.isPass()) {
                passed += 1;
            } else if (mte.isFail()) {
                failed += 1;
            }
        }

        ManualTestStat manualTestStat = ManualTestStat.builder()
                .totalTests(totalTests)
                .executed(totalExecuted)
                .pass(passed)
                .fails(failed)
                .build();

        manualTestStatService.save(manualTestStat);
    }

    private boolean processSuite(ApplicationEvent event) {
        ExecutorStat executorStat;

        BuildTriage buildTriage = buildTriageService.find(Long.parseLong(event.getExtraParameter()));
        if (buildTriage == null) {
            // it shouldn't be null, it means no executor to process
            return false;
        }

        ExecutorView view = executorServiceDTO.getExecutorViewOfLatestBuildTriage(buildTriage, buildTriage.getBuild(), buildTriage.getExecutor(), buildTriage.getSpec(), buildTriage.getTriager(), true);

        if (view == null) {
            // Executor without any builds, nothing to register
            return false;
        }

        executorStat = getNewStat(event, view);
        executorStat = executorStatService.save(executorStat);

        return true;
    }

    private ExecutorStat getNewStat(ApplicationEvent event, ExecutorView view) {

        String date = BaseDateFormat.format(event.getEventTime());

        ExecutorStat executorStat = view.getNewExecutorStat();
        executorStat.setTimestamp(event.getTimestamp());
        executorStat.setActualDate(date);
        executorStat.setExecutionDate(date);
        executorStat.setDeadline(date);

        return executorStat;
    }

    @Override
    public Integer getPriority() {
        return 4;
    }
    
}
