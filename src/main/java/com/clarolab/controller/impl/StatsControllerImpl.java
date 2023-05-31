/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.controller.StatsController;
import com.clarolab.dto.DateStatsDTO;
import com.clarolab.dto.LogCommitsPerDayDTO;
import com.clarolab.dto.LogCommitsPerPersonDTO;
import com.clarolab.dto.ManualTestCaseDTO;
import com.clarolab.dto.chart.ChartSerieDTO;
import com.clarolab.model.CVSLog;
import com.clarolab.model.Productivity;
import com.clarolab.model.manual.ManualTestCase;
import com.clarolab.service.ProductivityService;
import com.clarolab.service.StatsService;
import com.clarolab.serviceDTO.LogServiceDTO;
import com.clarolab.serviceDTO.ManualTestCaseServiceDTO;
import com.clarolab.serviceDTO.TestExecutionServiceDTO;
import com.clarolab.view.GroupedStatView;
import com.clarolab.view.KeyValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
public class StatsControllerImpl implements StatsController {

    @Autowired
    private StatsService statsService;

    @Autowired
    private ProductivityService productivityService;

    @Autowired
    private LogServiceDTO logServiceDTO;

    @Autowired
    TestExecutionServiceDTO testExecutionServiceDTO;

    @Autowired
    ManualTestCaseServiceDTO manualTestCaseServiceDTO;


    @Override
    public ResponseEntity<List<GroupedStatView>> getBugsFiled() {
        return ResponseEntity.ok(statsService.getBugsFiled());
    }

    @Override
    public ResponseEntity<List<GroupedStatView>> getMissingDeadlines() {
        return ResponseEntity.ok(statsService.getMissingDeadlines());
    }

    @Override
    public ResponseEntity<List<GroupedStatView>> getPendingToTriage() {
        return ResponseEntity.ok(statsService.getPendingToTriage());
    }

    @Override
    public ResponseEntity<GroupedStatView> getCountTestsSummary() {
        return ResponseEntity.ok(statsService.getCountTestsSummary());
    }


    @Override
    public ResponseEntity<List<GroupedStatView>> getComponentBasedTestTriages() {
        return ResponseEntity.ok(statsService.getComponentBasedTestTriages());
    }

    @Override
    public ResponseEntity<List<GroupedStatView>> getGlobalBurndownFailNewFixes() {
        return ResponseEntity.ok(statsService.getGlobalBurndownFailNewFixes());
    }

    @Override
    public ResponseEntity<Double> getTotalSavedTime() {
        return ResponseEntity.ok(statsService.getTotalSavedTime());
    }

    @Override
    public ResponseEntity<Double> getDeadlinesCompleted() {
        return ResponseEntity.ok(statsService.getDeadlinesCompleted());
    }

    @Override
    public ResponseEntity<Double> getTotalTriagedFails() {
        return ResponseEntity.ok(statsService.getTotalTriagedFails());
    }

    @Override
    public ResponseEntity<Double> getTotalAutomationFixes() {
        return ResponseEntity.ok(statsService.getTotalAutomationFixes());
    }

    @Override
    public ResponseEntity<Double> getUniqueTests() {
        return ResponseEntity.ok(statsService.getUniqueTests());
    }

    @Override
    public ResponseEntity<List<KeyValuePair>> getFailExceptions() {
        return ResponseEntity.ok(statsService.getFailExceptions());
    }

    @Override
    public ResponseEntity<List<Productivity>> getProductivityList() {
        return ResponseEntity.ok(productivityService.findAll());
    }

    @Override
    public ResponseEntity<List<GroupedStatView>> getProductSummary() {
        return ResponseEntity.ok(statsService.getProductSummary());
    }

    @Override
    public ResponseEntity<GroupedStatView> getAutomationPendingAndFixesForUser() {
        return ResponseEntity.ok(statsService.getAutomationPendingsAndFixesForUser());
    }

    @Override
    public ResponseEntity<List<GroupedStatView>> getTriagesForDayForUser() {
        return ResponseEntity.ok(statsService.getTriagesForDayForUser());
    }

    @Override
    public ResponseEntity<HashMap<String, List<Integer>>> getEngineerEffort(Long productId) {
        return ResponseEntity.ok(statsService.getEngineerEffort(productId));
    }

    @Override
    public ResponseEntity<List<GroupedStatView>> getSuiteEvolutionForDay(Long productId) {
        return ResponseEntity.ok(statsService.getSuiteEvolutionForWeek(productId));
    }

    @Override
    public ResponseEntity<List<GroupedStatView>> getFailedTestsForWeek(Long productId) {
        return ResponseEntity.ok(statsService.getFailedTestsForWeek(productId));
    }

    @Override
    public ResponseEntity<List<KeyValuePair>> getAutomationFixedByUsers() {
        return ResponseEntity.ok(statsService.getAllAutomationIssuesFixedByUsers());
    }

    @Override
    public ResponseEntity<List<KeyValuePair>> getAutomationPendingByUsers() {
        return ResponseEntity.ok(statsService.getAllAutomationIssuesPendingByUsers());
    }

    @Override
    public ResponseEntity<List<GroupedStatView>> getAutomationFixedPendingByUsers() {
        return ResponseEntity.ok(statsService.getAllAutomationIssuesOutgoingByUsers());
    }

    @Override
    public ResponseEntity<List<LogCommitsPerPersonDTO>> getCommitsPerPerson() {
        return ResponseEntity.ok(statsService.getCommitsPerPerson());
    }

    @Override
    public ResponseEntity<List<LogCommitsPerDayDTO>> getCommitsPerDay() {
        return ResponseEntity.ok(statsService.getCommitsPerDay());
    }

    @Override
    public ResponseEntity<List<ChartSerieDTO>> getCommitsPerPersonAndPerDay() {
        return ResponseEntity.ok(statsService.getCommitsPerPersonAndPerDay());
    }

    @Override
    public ResponseEntity<List<CVSLog>> getCommits() {
        return ResponseEntity.ok(statsService.getCommits());
    }

    @Override
    public ResponseEntity<List<DateStatsDTO>> getErrorStats() {
        return ResponseEntity.ok(testExecutionServiceDTO.searchErrorTestStats());
    }
}