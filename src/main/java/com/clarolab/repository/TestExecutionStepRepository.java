/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.model.TestExecution;
import com.clarolab.model.TestExecutionStep;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface TestExecutionStepRepository extends BaseRepository<TestExecutionStep> {
    @Transactional
    @Modifying(clearAutomatically = true)
    @Procedure(value = "DELETE_TEST_EXECUTION_STEP_BY_TIMESTAMP")
    int deleteTestExecutionStepsByTime(long timestamp);

    List<TestExecutionStep> findAllByTestExecutionAndEnabled(TestExecution testExecution, boolean enabled);

    Long deleteAllByTimestampLessThan(long timestamp);

}
