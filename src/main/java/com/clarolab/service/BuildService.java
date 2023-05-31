/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.model.Build;
import com.clarolab.model.Executor;
import com.clarolab.model.Report;
import com.clarolab.model.TestExecution;
import com.clarolab.populate.DataProvider;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.BuildRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log
public class BuildService extends BaseService<Build> {

    @Autowired
    private BuildRepository buildRepository;

    @Autowired
    private ReportService reportService;

    @Override
    public BaseRepository<Build> getRepository() {
        return buildRepository;
    }

    public List<Build> getNonProcessedBuilds() {
        return buildRepository.findAllByProcessedAndEnabledOrderByNumberAscExecutor(false, true);
    }

    public List<Build> findAllIds(List<Long> entityIds) {
        long[] ids = new long[entityIds.size()];
        int i = 0;
        for (Long id : entityIds) {
            ids[i] = id;
            i = i + 1;
        }
        return buildRepository.findAllByIdInOrderByNumber(ids);
    }

    public List<Build> findAll(List<Long> entityIds) {
        return buildRepository.findAllById(entityIds);
    }

    public Build getLastBuild(Executor executor) {
        return buildRepository.getTopByExecutorOrderByNumberDesc(executor);
    }

    public List<Build> findAll(Executor executor) {
        return buildRepository.findAllByExecutorOrderByNumberDesc(executor);
    }

    public List<Build> getNonProcessedBuilds(Executor executor) {
        return buildRepository.findAllByExecutorAndProcessedAndEnabledOrderByNumberAscExecutor(executor, false, true);
    }

    public void setReport(Build build) {
        if (build.getReport() == null) {
            Report report = DataProvider.getReport();
            report.setFailCount(0);
            report.setPassCount(0);
            report.setSkipCount(0);
            report.setTotalTests();

            build.setReport(report);
        } else {
            List<TestExecution> tests = build.getTestCases();
            build.getReport().setFailCount((int) tests.stream().filter(TestExecution::isFailed).count());
            build.getReport().setPassCount((int) tests.stream().filter(TestExecution::isPassed).count());
            build.getReport().setSkipCount((int) tests.stream().filter(TestExecution::isSkipped).count());

            build.getReport().setTotalTests();
            reportService.update(build.getReport());
        }
    }
}
