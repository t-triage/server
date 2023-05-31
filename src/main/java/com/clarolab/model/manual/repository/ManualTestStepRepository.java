/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.manual.repository;

import com.clarolab.model.manual.ManualTestCase;
import com.clarolab.model.manual.instruction.ManualTestStep;
import com.clarolab.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManualTestStepRepository extends BaseRepository<ManualTestStep> {

    @Query("SELECT m FROM ManualTestStep m WHERE m.testCase = ?1 AND m.externalId = ?2")
    ManualTestStep findStep(ManualTestCase testCase, long id);
    
    List<ManualTestStep> findAllByTestCaseAndExternalId(ManualTestCase testCase, long id);

    List<ManualTestStep> findAllByTestCase(ManualTestCase testCase);
}
