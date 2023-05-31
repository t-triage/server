/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.manual.repository;

import com.clarolab.model.User;
import com.clarolab.model.manual.ManualTestCase;
import com.clarolab.model.manual.ManualTestExecution;
import com.clarolab.model.manual.ManualTestPlan;
import com.clarolab.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManualTestExecutionRepository extends BaseRepository<ManualTestExecution> {


    List<ManualTestExecution> findAllByAssignee(User user);

    List<ManualTestExecution> findByTestPlanOrderById(ManualTestPlan manualTestPlan);

    Optional<ManualTestExecution> findByTestPlanAndTestCaseAndEnabledIsTrue(ManualTestPlan manualTestPlan, ManualTestCase manualTestCase);

    List<ManualTestExecution> findByLastExecutionTimeIsGreaterThanEqual(Long timeValue);

    List<ManualTestExecution> findManualTestExecutionsByTimestampIsGreaterThanEqual(long filter);

}
