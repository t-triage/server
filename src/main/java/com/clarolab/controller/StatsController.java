/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.controller.error.ErrorInfo;
import com.clarolab.dto.DateStatsDTO;
import com.clarolab.dto.LogCommitsPerDayDTO;
import com.clarolab.dto.LogCommitsPerPersonDTO;
import com.clarolab.dto.ManualTestCaseDTO;
import com.clarolab.dto.chart.ChartSerieDTO;
import com.clarolab.model.CVSLog;
import com.clarolab.model.Productivity;
import com.clarolab.model.manual.ManualTestCase;
import com.clarolab.view.GroupedStatView;
import com.clarolab.view.KeyValuePair;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import liquibase.logging.LoggerFactory;
import org.apache.commons.logging.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;

import static com.clarolab.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping(API_STATS_URI)
@Api(value = "Statistics", description = "Here you will find all those operations related with the statistics", tags = {"Statistics"})
public interface StatsController extends SecuredController{

    ;
    @ApiOperation(value = "", notes = "Return Total Stats")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = GroupedStatView.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_COMPONENT_BASED_TRIAGE, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<GroupedStatView>> getComponentBasedTestTriages();

    @ApiOperation(value = "", notes = "List pending jobs to be triaged")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_EXECUTORS_TO_TRIAGE, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<GroupedStatView>> getPendingToTriage();


    @ApiOperation(value = "", notes = "Return Total Stats")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = GroupedStatView.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_TESTS_SUMMARY, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<GroupedStatView> getCountTestsSummary();


    @ApiOperation(value = "", notes = "Return by month the fixes, fails and new tests")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_GLOBAL_BURNDOWN, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<GroupedStatView>> getGlobalBurndownFailNewFixes();


    @ApiOperation(value = "", notes = "Return by month the Bugs Filed")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_BUG_FILED, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<GroupedStatView>> getBugsFiled();

    @ApiOperation(value = "", notes = "Return by month the Missing Deadlined")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_MISSING_DEADLINES, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<GroupedStatView>> getMissingDeadlines();


    // SHORT SUMMARY NUMBERS

    @ApiOperation(value = "", notes = "Return Total time saved by the applications")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = Double.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_SAVED_TIME, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Double> getTotalSavedTime();


    @ApiOperation(value = "", notes = "Return the total amount of deadlines you achieved on time")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = Double.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_DEADLINES_COMPLETED, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Double> getDeadlinesCompleted();


    @ApiOperation(value = "", notes = "Return the total triage amount of fails")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = Double.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_TOTAL_TRIAGED_FAILS, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Double> getTotalTriagedFails();

    @ApiOperation(value = "", notes = "Return the total amount of automation fails")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = Double.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_TOTAL_AUTOMATION_FIXES, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Double> getTotalAutomationFixes();


    @ApiOperation(value = "", notes = "Return the total triage amount of fails")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = Double.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_UNIQUE_TESTS, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Double> getUniqueTests();

    @ApiOperation(value = "", notes = "Return the total triage amount of fails")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_FAIL_EXCEPTIONS, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<KeyValuePair>>  getFailExceptions();

    @ApiOperation(value = "", notes = "Return a list of Productivities items")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_PRODUCTIVITY, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<Productivity>>  getProductivityList();


    @ApiOperation(value = "", notes = "Return Total Stats")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_PRODUCTS_SUMMARY, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<GroupedStatView>> getProductSummary();

    @ApiOperation(value = "", notes = "Return the total amount of automation pending  and fixes for the logged user")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = GroupedStatView.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_TOTAL_AUTOMATION_PENDING_AND_FIXES_FOR_USER, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<GroupedStatView> getAutomationPendingAndFixesForUser();

    @ApiOperation(value = "", notes = "Return by day the triages done")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_TOTAL_TRIAGES_FOR_DAY_FOR_USER, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<GroupedStatView>> getTriagesForDayForUser();

    @ApiOperation(value = "", notes = "Return engineer effort")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = HashMap.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_ENGINEER_EFFORT, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<HashMap<String, List<Integer>>> getEngineerEffort(@RequestParam(value = "productId", required = true) Long productId);

    @ApiOperation(value = "", notes = "Return by day the amount of coverage incremented")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_SUITE_EVOLUTION, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<GroupedStatView>> getSuiteEvolutionForDay(Long id);

    @ApiOperation(value = "", notes = "Return by week the tendency of failed tests")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_FAILED_TESTS, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<GroupedStatView>> getFailedTestsForWeek(Long id);

    @ApiOperation(value = "", notes = "Return all automation fixes count grouped by users")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_TOTAL_AUTOMATION_FIXES_USERS, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<KeyValuePair>>  getAutomationFixedByUsers();

    @ApiOperation(value = "", notes = "Return all automation pending count grouped by users")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_TOTAL_AUTOMATION_PENDING_USERS, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<KeyValuePair>> getAutomationPendingByUsers();

    @ApiOperation(value = "", notes = "Return all automation fixed and pending count grouped by users")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_TOTAL_AUTOMATION_FIXED_PENDING_USERS, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<GroupedStatView>> getAutomationFixedPendingByUsers();

    @ApiOperation(value = "", notes = "Return all authors of commits and the total count of commits")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_TOTAL_COMMITS_PER_USER, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<LogCommitsPerPersonDTO>> getCommitsPerPerson();


    @ApiOperation(value = "", notes = "Return all authors of commits and the total count of commits")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_TOTAL_COMMITS_PER_DAY, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<LogCommitsPerDayDTO>> getCommitsPerDay();

    @ApiOperation(value = "", notes = "Return all authors of commits and the total count of commits")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_TOTAL_COMMITS_PER_PERSON_AND_PER_DAY, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<ChartSerieDTO>> getCommitsPerPersonAndPerDay();

    @ApiOperation(value = "", notes = "Return all authors of commits and the total count of commits")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics", response = List.class),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_TOTAL_COMMITS, method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<CVSLog>> getCommits();

    @ApiOperation(value = "" , notes = "return date and count of AutomatedTestIssue ")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Statistics"),
                    @ApiResponse(code = 404, response = ErrorInfo.class, message = "Operation not completed")
            })
    @RequestMapping(value = STATS_TOTAL_TEST_ERRORS , method = GET , produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<DateStatsDTO>> getErrorStats();

}
