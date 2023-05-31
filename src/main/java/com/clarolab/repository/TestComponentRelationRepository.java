/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.model.TestCase;
import com.clarolab.model.component.AutomatedComponent;
import com.clarolab.model.component.TestComponentRelation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestComponentRelationRepository extends BaseRepository<TestComponentRelation> {

    TestComponentRelation findFirstByComponentAndTestCase(AutomatedComponent automatedComponent, TestCase testCase);

    List<TestComponentRelation> findAllByComponent(AutomatedComponent automatedComponent);

    TestComponentRelation findFirstByTestCase(TestCase testCase);

    long countByTestCase(TestCase testCase);

    List<TestComponentRelation> findAllByComponentIsIn(List<AutomatedComponent> automatedComponents);

}
