/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.dto.db.TestTriageHistoryDTO;
import com.clarolab.model.*;
import com.clarolab.model.types.StateType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TestTriageRepository extends BaseRepository<TestTriage> {

    TestTriage findFirstByTestCaseOrderByBuildDesc(TestCase testCase);

    Long countAllByTestCaseAndCurrentState(TestCase testCase, StateType stateType);

    List<TestTriage> findAllByTestCase(TestCase automatedTestCaseId);

    List<TestTriage> findAllByBuild(Build build, Sort sort);

    Long countByEnabledAndTimestampGreaterThanEqual(boolean enabled, long timestamp);

    List<TestTriage> findAllByExecutorNameIgnoreCaseLike(String executorName);

    List<TestTriage> findAllByTestCaseIsNotNull();

    List<TestTriage> findAllByCurrentStateIn(StateType[] currentState);

    List<TestTriage> findByExecutionDateGreaterThanEqualOrderByExecutionDateDesc(long lastExecution);

    List<TestTriage> findAllByBuildAndTriagedAndExpiredAndEnabled(Build build, boolean triaged, boolean expired, boolean enabled);

    long countByBuildTriageAndTriagedAndExpiredAndEnabled(BuildTriage build, boolean triaged, boolean expired, boolean enabled);

    long countByTriagedAndExpiredAndEnabled(boolean triaged, boolean expired, boolean enabled);

    @Query("SELECT u FROM TestTriage tt JOIN User u ON tt.triager = u WHERE tt.updated > ?1 AND tt.updated < ?2 AND triaged = true AND tt.enabled = true AND tt.expired = false AND tt.expired = false GROUP BY u order by count(u) DESC")
    List<User> findMoreTriagedUsers(long fromDate, long toDate);
    
    long countByTestCaseAndCurrentStateInAndEnabled(TestCase testCase, StateType[] state, boolean enabled);

    long countByTestCaseAndEnabled(TestCase testCase, boolean enabled);

    List<TestTriage> findAllByBuildAndCurrentStateNot(Build build, Sort sort, StateType state);

    List<TestTriage> findAllByBuildAndCurrentState(Build build, Sort sort, StateType state);

    List<TestTriage> findAllByBuildTriage(BuildTriage build);

    TestTriage findFirstByTestExecutionAndBuild(TestExecution execution, Build build);

    TestTriage findFirstByTestCaseAndBuild(TestCase test, Build build);

    TestTriage findFirstByExecutorAndTestCaseAndCurrentStateNotInOrderByBuildNumberDesc(Executor executor, TestCase testCase, StateType[] state);

    // Query("SELECT tt FROM QA_TEST_EXECUTION te, QA_TEST_TRIAGE tt, (  SELECT MAX(b.id) as buildid FROM qa_build b GROUP BY b.executor_id)  bi WHERE tt.build_id = bi.buildid AND tt.test_id = te.id AND te.testCase_id = ?1")
    @Query("SELECT tt FROM TestTriage tt WHERE tt.enabled = true AND tt.testCase = ?1 and tt.build in (SELECT MAX(b.id) as buildid FROM Build b WHERE b.enabled=true GROUP BY b.executor)")
    List<TestTriage> findAllSameTriage(TestCase testCase);

    @Query("SELECT max(id) FROM TestTriage WHERE testCase = ?1 AND timestamp >= ?2 AND executor != ?3 GROUP BY (executor)")
    List<Long> findSameOngoingTests(TestCase testCase, long timestamp, Executor executor);

    List<TestTriage> findAllByTestCaseAndEnabled(TestCase testCase, boolean enabled);

    @Query("SELECT tt FROM TestExecution te, TestTriage tt WHERE tt.enabled = true AND tt.testExecution = te.id AND (te.errorDetails = ?1 OR te.errorStackTrace = ?2 OR REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(te.errorDetails, '0', ''), '1', ''), '2', ''), '3', ''), '4', ''), '5', ''), '6', ''), '7', ''), '8', ''), '9', '')  = ?3) AND tt.build in (SELECT MAX(b.id) as buildid FROM Build b GROUP BY b.executor)")
    List<TestTriage> findAllSameError(String errorDetail, String stackTrace, String errorDetailNoNumbers);

    @Query("SELECT tt FROM TestExecution te, TestTriage tt WHERE tt.enabled = true AND tt.testExecution = te.id AND (te.errorDetails = ?1 OR REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(te.errorDetails, '0', ''), '1', ''), '2', ''), '3', ''), '4', ''), '5', ''), '6', ''), '7', ''), '8', ''), '9', '')  = ?2) AND tt.build in (SELECT MAX(b.id) as buildid FROM Build b GROUP BY b.executor)")
    List<TestTriage> findAllSameError(String errorDetail, String errorDetailNoNumbers);

    long countByBuildAndCurrentStateAndTriaged(Build build, StateType stateType, boolean triaged);

    long countByBuild(Build build);

    long countByBuildAndTriaged(Build build, boolean triaged);

    long countByBuildAndCurrentStateNotAndTriaged(Build build, StateType stateType, boolean triaged);

    long countByBuildAndCurrentStateNotAndTagsContains(Build build, StateType stateType, String tag);

    long countByExecutorAndBuildNumberGreaterThanAndBuildNumberLessThan(Executor executor, int start, int end);

    /*long countByTriager(User user);

    long countByProduct(Product product);

    long countByProductAndTriaged(Product product, boolean triaged);*/

    long countByBuildAndTriagedAndExpiredAndEnabledAndIdNot(Build build, boolean triaged, boolean expired, boolean enabled, long id);

    @Query("SELECT currentState, count(currentState) FROM TestTriage WHERE build = ?1 GROUP BY currentState")
    List<TestTriage> summarizePendingStates(Build build);

    List<TestTriage> findTopByExecutorAndTriagedAndExpiredAndEnabledAndTestCaseAndBuildNumberLessThanOrderByIdDesc(Executor executor, boolean triaged, boolean expired, boolean enabled, TestCase testCase, int buildNumber);
    
    List<TestTriage> findAllByExecutorAndTriagedAndExpiredAndEnabledAndBuildNumberLessThanOrderByIdDesc(Executor executor, boolean triaged, boolean expired, boolean enabled, int buildNumber);

    List<TestTriage> findAllByExecutorAndTestCaseAndEnabledOrderByIdDesc(Executor executor, TestCase testCase, boolean enabled);

    @Query("SELECT id FROM TestTriage WHERE enabled = true AND executor=?1 AND testCase=?2 ORDER BY id DESC")
    List<Long> findAllIdsByExecutorAndTestCaseOrderByIdDesc(Executor executor, TestCase testCase);

    List<TestTriage> findFirstByExecutorAndTestCaseAndEnabledAndExpiredOrderByIdDesc(Executor executor, TestCase testCase, boolean enabled, boolean expired);

    // This should be optimized with a new DTO with:
    // SELECT currentState, triaged, count(currentState) FROM qa_Test_Triage tt WHERE tt.executor.enabled AND tt.container.enabled = true AND enabled=true AND tt.build_id in (SELECT MAX(b.id) as buildid FROM qa_Build b WHERE b.enabled=true GROUP BY b.executor_id) group by tt.currentState, tt.triaged order by currentState
    @Query("SELECT tt FROM TestTriage tt WHERE tt.enabled=true AND tt.executor.enabled = true AND currentState != ?1 AND tt.build in (SELECT MAX(b.id) as buildid FROM Build b WHERE b.enabled=true GROUP BY b.executor)")
    List<TestTriage> findAllOngoingTests(StateType notState);

    @Query("SELECT tt FROM TestTriage tt WHERE tt.enabled = true AND tt.executor.enabled = true AND tt.currentState IN ?1 AND tt.build in (SELECT MAX(b.id) as buildid FROM Build b WHERE b.enabled = true GROUP BY b.executor)")
    List<TestTriage> findAllOngoingTests(StateType[] states);

    @Query("SELECT tt FROM TestTriage tt WHERE tt.enabled=true AND tt.executor.enabled = true AND testCase = ?1 AND tt.build in (SELECT MAX(b.id) as buildid FROM Build b WHERE b.enabled=true GROUP BY b.executor)")
    List<TestTriage> findAllOngoingTests(TestCase testCase);

    @Query("SELECT tt FROM TestTriage tt WHERE tt.enabled=true AND tt.executor.enabled = true AND testCase in (?1) AND tt.build in (SELECT MAX(b.id) as buildid FROM Build b WHERE b.enabled=true GROUP BY b.executor)")
    List<TestTriage> findAllOngoingTestCases(List<TestCase> testCases);

    List<TestTriage> findAllByExecutorAndTestCaseAndEnabledAndTriagedAndExecutionDateGreaterThanOrderByIdDesc(Executor executor, TestCase testCase, boolean enabled, boolean triaged, long executionDate);

    @Query("SELECT new com.clarolab.dto.db.TestTriageHistoryDTO(t.id, t.currentState, t.applicationFailType, t.testFailType, t.triaged, t.expired, t.tags, t.buildNumber, t.timestamp) FROM TestTriage t WHERE t.executor = ?1 AND t.testCase = ?2 AND t.enabled = true ORDER BY t.buildNumber DESC")
    List<TestTriageHistoryDTO> getTestHistory(Executor executor, TestCase testCase, Pageable pageable);

    List<TestTriage> findAllByIdIn(List<Long> ids);

    @Query("SELECT tt FROM TestTriage tt, Container c WHERE c.product = ?1 AND tt.container = c.id GROUP BY tt.triager, tt.id")
    List<TestTriage> findAllByProductGoupedByUser(Product product);

    @Query("SELECT tt.triager.realname, count (distinct tt.testCase) FROM TestTriage tt, Container c WHERE c.product = ?1 AND tt.container = c.id GROUP BY tt.triager.realname")
    List<Object[]> findTestCasesGroupedByUser(Product product);

    @Query("SELECT tt.triager.realname, count (tt) FROM TestTriage tt, Container c WHERE c.product = ?1 AND tt.container = c.id AND tt.triaged = true AND tt.tags not like '%AUTO-TRIAGED%' GROUP BY tt.triager.realname")
    List<Object[]> findManualTriagesGroupedByUser(Product product);

    @Query("SELECT tt.triager.realname, count (tt) FROM TestTriage tt, Container c WHERE c.product = ?1 AND tt.container = c.id AND tt.triaged = true AND tt.tags like '%AUTO-TRIAGED%' GROUP BY tt.triager.realname")
    List<Object[]> findAutoTriagesGroupedByUser(Product product);


    @Query("SELECT tt.currentState FROM TestTriage tt WHERE tt.executor = ?1 AND tt.testCase = ?2 AND tt.buildNumber >= ?3 GROUP BY tt.id")
    List<StateType> findAllCurrentStateByExecutorAndTestCaseAndBuildNumberGreaterThanOrderByIdAsc(Executor executor, TestCase testCase, int buildNumber);

    List<TestTriage> findFirst500ByUpdatedLessThanOrderByIdDesc(long timestamp);
    List<TestTriage> findFirst500ByTimestampLessThanOrderByIdAsc(long timestamp);
    List<TestTriage> findFirst500ByUpdatedLessThanAndUpdatedGreaterThanOrderByIdAsc(long maxTime, long minTime);
    List<TestTriage> findFirst500ByTimestampLessThanAndUpdatedGreaterThanOrderByIdAsc(long maxTime, long minTime);


    List<TestTriage> findFirst500ByTimestampLessThanOrderByIdDesc(long timestamp);

    void deleteTestTriageById(long testTriage);

    @Modifying
    @Query("update TestTriage t set t.previousTriage = null where t = ?1")
    int clearPreviousTriage(TestTriage testTriage);


    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update qa_error_detail set previoustesttriage_id = null ", nativeQuery = true)
    void clearPreviousTriageOnErrorDetails();

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update qa_test_triage set previoustriage_id = null", nativeQuery = true)
    void clearPreviousTriage();
    
    List<TestTriage> findAllByPreviousTriage(TestTriage testTriage);

    @Query("SELECT tt FROM TestTriage tt WHERE tt.enabled=true AND tt.executor.enabled = true AND tt.build in (SELECT MAX(b.id) as buildid FROM Build b WHERE b.enabled=true AND b.updated > ?1 GROUP BY b.executor)")
    List<TestTriage> findAllOngoingTestsSince(long timestamp);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "CALL CLEANUP_TESTS(:timestamp);", nativeQuery = true)
    void cleanup(@Param("timestamp") long timestamp);
}
