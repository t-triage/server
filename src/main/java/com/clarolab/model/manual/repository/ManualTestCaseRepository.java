/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.manual.repository;

import com.clarolab.model.Product;
import com.clarolab.model.User;
import com.clarolab.model.manual.ManualTestCase;
import com.clarolab.model.manual.types.AutomationStatusType;
import com.clarolab.repository.BaseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManualTestCaseRepository extends BaseRepository<ManualTestCase> {

    List<ManualTestCase> findAllByProduct(Product product);

    @Query("SELECT m FROM ManualTestCase m WHERE LOWER (name) LIKE ?1 ORDER BY name")
    List<ManualTestCase> search(String name);

    List<ManualTestCase> findAllByName(String testName);
    
    List<ManualTestCase> findAllByOwner(User user);

    @Query("SELECT m FROM ManualTestCase m WHERE m.enabled = true")
    List<ManualTestCase> findAllFiltered(@Nullable Specification spec, @Nullable Sort sort);

    @Query("SELECT m FROM ManualTestCase m WHERE LOWER(m.functionality) LIKE ?1 ORDER BY m.functionality")
    List<ManualTestCase> searchFunctionality(String name);
    
    @Query("SELECT m FROM ManualTestCase m WHERE m.functionality <> ''")
    List<ManualTestCase> findAllByFunctionalityNotNull();

    List<ManualTestCase> findByAutomationStatusIn(List<AutomationStatusType> list);

    long countByProductAndAutomationStatusIn(Product product, List<AutomationStatusType> list);

    long countByProductAndAutomationStatusInAndTimestampGreaterThan(Product product, List<AutomationStatusType> list, long date);

    ManualTestCase findManualTestCaseByExternalId(String id);

    List<ManualTestCase> findManualTestCasesByTimestampIsGreaterThanEqual(long filter);
}