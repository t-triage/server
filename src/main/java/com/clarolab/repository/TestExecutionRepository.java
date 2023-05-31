/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.dto.DateStatsDTO;
import com.clarolab.model.TestExecution;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestExecutionRepository extends BaseRepository<TestExecution> {

    @Query(value = "SELECT t FROM TestExecution t WHERE (errorDetails <> NULL AND errorDetails = :errorDetails) OR (errorStackTrace <> NULL AND errorStackTrace = :errorStackTrace)")
    List<TestExecution> findAllByErrorDetailsOrErrorStackTrace(@Param("errorDetails") String errorDetails, @Param("errorStackTrace") String errorStackTrace);

    @Query("SELECT t FROM TestExecution t WHERE (LOWER(testCase.name) like ?1 OR LOWER(testCase.name) like ?2) AND enabled = true ORDER BY testCase.name")
    List<TestExecution> searchTestNames(String name, String nameWithoutSpaces, boolean enabled);

    Long deleteByTimestampLessThan(long timeStamp);

    @Modifying
    @Query("update TestExecution t set t.errorStackTrace = null, t.errorDetails = null, t.standardOutput = null, t.screenshotURL = null, t.videoURL = null, t.skippedMessage = null where t.timestamp < ?1")
    int clearLogs(long timestamp);

    @Modifying
    @Query("delete from TestExecution t where t.timestamp < ?1 AND t NOT IN (select tt.testExecution from TestTriage tt)")
    int deleteOld(long timestamp);


    @Query("SELECT new com.clarolab.dto.DateStatsDTO(c.updated , count (c.id) ) FROM ManualTestStat c WHERE c.updated < ?1 AND c.fails <> 0L AND c.enabled = true GROUP BY c.updated ORDER BY count(c.id) DESC")
    List<DateStatsDTO> findTestWithErrors(Long current);

    //TODO QUERY


}
