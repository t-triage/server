/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.model.TestCase;
import com.clarolab.model.User;
import com.clarolab.model.types.StateType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestCaseRepository extends BaseRepository<TestCase> {

    List<TestCase> findAllByName(String name);

    List<TestCase> findAllByLocationPathContains(String locationPath);

    TestCase findTopByNameAndLocationPath(String name, String locationPath);

    TestCase findTopByLocationPath(String locationPath);

    @Query("SELECT c FROM TestCase c LEFT OUTER JOIN TestTriage t ON t.testCase.id = c.id WHERE (:lastExecution is null or t.executionDate >= :lastExecution)" +
            "and (:ignoreCurrentState = true or t.currentState IN (:currentState))" +
            "and (:executorName is null or lower( t.executorName) like :executorName)"+
            "and (:testCaseName is null or lower( c.name) like :testCaseName)" +
            "and (:tag is null or lower( t.tags) like :tag)" +
            "and (:triager is null or t.triager = :triager)" +
            "and (:hideNoSuite = false or (:hideNoSuite = true and t.testCase is not null))" +
            "and (c.enabled = true)" +
            "and (t.testCase is null or t.enabled=true and t.build in (SELECT MAX(b.id) as buildid FROM Build b WHERE b.enabled=true GROUP BY b.executor))" +
            "order by c.name ASC")
    List<TestCase> findAllByAutomatedTestFilter(@Param("testCaseName") String testCaseName,
                                                @Param("lastExecution") Long lastExecution,
                                                @Param("currentState") List<StateType> currentState,
                                                @Param("executorName") String executorName,
                                                @Param("ignoreCurrentState") Boolean ignoreCurrentState,
                                                @Param("triager") User triager,
                                                @Param("hideNoSuite") Boolean hideNoSuite,
                                                @Param("tag") String tag);

    @Query("SELECT t FROM TestCase t WHERE t.enabled = true and (:name is null or lower( t.name) like :name )" +
            " and (:filter =false or (:filter =true and t.id IN (:ids)))")
    List<TestCase> findAllFilteredByAutomatedTest(@Nullable Sort sort,
                                                  @Param("name") String name,
                                                  @Param("ids") List<Long> ids,
                                                  @Param("filter") Boolean filter );

    @Query("SELECT m FROM TestCase m WHERE m.enabled = true AND LOWER(m.name) like LOWER(?1)")
    List<TestCase> findAllByNameLike(String name);
}
