/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.manual.repository;

import com.clarolab.dto.ManualTestPlanStatDTO;
import com.clarolab.model.manual.ManualTestPlan;
import com.clarolab.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManualTestPlanRepository extends BaseRepository<ManualTestPlan> {

    @Query("SELECT m FROM ManualTestPlan m WHERE LOWER (name) LIKE ?1 ORDER BY name")
    List<ManualTestPlan> search(String name);

    @Query("SELECT new com.clarolab.dto.ManualTestPlanStatDTO(p.id, p.name, p.fromDate, p.toDate, e.status, count(e.status)) FROM ManualTestExecution e, ManualTestPlan p WHERE e.testPlan = p.id AND (p.fromDate <= ?1 AND (p.toDate >= ?2 OR p.toDate = 0)) AND p.enabled = true AND e.enabled = true GROUP BY p.id, e.status ORDER BY p.name ASC, e.status DESC")
    List<ManualTestPlanStatDTO> getManualTestPlanStats(long startDate, long endDate);

    // TODO (benja): maybe find by Criteria
    List<ManualTestPlan> findAll();

}
