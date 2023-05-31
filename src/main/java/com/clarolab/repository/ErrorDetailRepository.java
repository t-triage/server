/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.model.TestExecution;
import com.clarolab.model.TestTriage;
import com.clarolab.model.detail.ErrorDetail;
import org.aspectj.weaver.ast.Test;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ErrorDetailRepository extends BaseRepository<ErrorDetail> {

    @Query("SELECT ed.exceptionType, count(ed) FROM ErrorDetail ed where ed.timestamp > ?1 group by ed.exceptionType order by count(ed) desc")
    List<Object[]> getAllGroupedBy(long startinddate);
    
    void deleteByPreviousTestTriage(TestTriage testTriage);
    void deleteByTestExecution(TestExecution test);
    @Query(value = "SELECT * FROM qa_test_triage", nativeQuery = true)
    List<TestTriage> findTestTriages();
}
