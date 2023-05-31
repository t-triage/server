/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.model.Container;
import com.clarolab.model.Executor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExecutorRepository extends BaseRepository<Executor> {

    Executor findByName(String name);
    Executor findByContainerAndName(Container container, String name);

    @Query("SELECT id FROM Executor WHERE container_id = ?1 AND enabled = true")
    List<Long> findAllExecutorIdsByContainerAndEnabled(long container);

    @Query("SELECT id FROM Executor WHERE enabled = true ORDER BY maxTestExecuted")
    List<Long> findAllExecutorIdsByEnabled();

    List<Executor> findAllByTimestampBetween(long timestampFrom, long timestampTo);

    List<Executor> findAllByContainerAndEnabled(Container container, boolean enabled);

    int countAllByContainerAndEnabled(Container container, boolean enabled);

    List<Executor> findAllByNameIgnoreCaseLike(String name);

    List<Executor> findAllByNameIgnoreCaseLikeAndContainer(String name, Container container);

    @Query("SELECT distinct(t.executor) FROM TestTriage t, TestCase c WHERE t.enabled=true AND c.id = t.testCase AND LOWER(c.name) like ?1")
    List<Executor> findAllLikeTest(String name);

    @Query("SELECT distinct(t.executor) FROM TestTriage t, TestCase c WHERE t.enabled=true AND t.container = ?2 AND c.id = t.testCase AND LOWER(c.name) like ?1")
    List<Executor> findAllLikeTest(String name, Container container);

    @Query("SELECT e.name, e.id FROM Executor e WHERE e.enabled = true AND e.container = ?1 ORDER BY e.name asc ")
    List<Object[]> findAllNames(Container container);

    @Query("SELECT e.assignee, count(e) FROM ExecutorStat e WHERE e.productName = ?1 AND e.enabled = true group by e.assignee")
    List<Object[]> findExecutorsGroupedByUser(String product);

   /* @Query("SELECT sum(e.totalTests), extract(WEEK from to_timestamp(timestamp/1000)) FROM ExecutorStat e WHERE e.id IN (SELECT max(e.id) FROM ExecutorStat e group by e.suitename, extract(WEEK from to_timestamp(timestamp/1000))) group by productName, weekNumber order by productName, weekNumber")
    List<Long> sumTestExecutedByProduct(Product product);*/

    @Query("SELECT e.suiteName, max(e.id) FROM ExecutorStat e WHERE LOWER(e.productName) = ?1 AND e.timestamp > ?2 AND e.timestamp < ?3 GROUP BY e.suiteName")
    List<Object[]> selectLastExecutorStat(String product, Long prev, Long now);

    @Query("SELECT sum(e.totalTests) FROM ExecutorStat e WHERE e.id IN :ids")
    Long sumTestExecutedByProduct(List<Long> ids);

    List<Executor> findAllByEnabled(boolean enabled);


}
