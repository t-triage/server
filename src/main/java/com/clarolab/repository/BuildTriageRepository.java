/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.model.*;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildTriageRepository extends BaseRepository<BuildTriage> {

    BuildTriage getFirstByBuild(Build build);

    BuildTriage getByBuildIsAndEnabled(Build build, boolean enabled);

    List<BuildTriage> findByTriagedAndExpiredAndEnabled(boolean triaged, boolean expired, boolean enabled);

    List<BuildTriage> findByContainerAndTriagedAndExpiredAndEnabledAndExecutor_Enabled(Container container, boolean triaged, boolean expired, boolean enabled, boolean executorEnabled );
    
    List<BuildTriage> findByContainerAndTriagedAndExpiredAndEnabledAndExecutor_EnabledAndTimestampBetween(Container container, boolean triaged, boolean expired, boolean enabled, boolean executorEnabled, Long timeStampFrom,  Long timeStampTo);

    List<BuildTriage> findByContainerAndTriagedAndEnabledAndExecutor_Enabled(Container container, boolean triaged, boolean enabled, boolean executorEnabled );

    List<BuildTriage> findAllByExecutorInAndTriagedAndEnabledAndExpired(List<Executor> executors, boolean triaged, boolean enabled, boolean expired );

    List<BuildTriage> findAllByExecutorInAndEnabledAndExpired(List<Executor> executors, boolean enabled, boolean expired );

    @Query("SELECT MAX(id) FROM BuildTriage WHERE executor = ?1 AND expired=false AND enabled=true")
    List<Long> findOngoing(Executor executor);

    @Query("SELECT MAX(id) FROM BuildTriage WHERE container = ?1 AND expired=false AND enabled=true GROUP BY executor")
    List<Long> findOngoing(Container container);

    @Query("SELECT MAX(id) FROM BuildTriage WHERE expired=false AND enabled=true GROUP BY executor")
    List<Long> findOngoing();

    List<BuildTriage> findAllByIdInOrderByRankDesc(List<Long> buildIds);
    
    List<BuildTriage> findAllByBuildIn(List<Build> builds);

    List<BuildTriage> findAllByEnabledAndTriagerAndTriagedAndDeadlineLessThanEqualOrderByRankDesc(boolean enabled, User user, boolean triaged, long toTime);

    List<BuildTriage> findAllByEnabledAndTriagerAndTriagedAndDeadlineGreaterThanOrderByDeadlineAsc(boolean enabled, User user, boolean triaged, long fromTime);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE BuildTriage SET enabled=false, triaged=true WHERE deadline < ?1 AND enabled=true AND triaged=false")
    int expireByDeadlineLessThan(long timestamp);

    List<BuildTriage> findAllByDeadlineLessThanAndEnabled(long timestamp, boolean enabled);

    List<BuildTriage> findAllByExecutorOrderByNumberAsc(Executor executor);

    @Query("SELECT u FROM BuildTriage bt join User u on bt.triager = u where executor_id=?1 group by u order by count(u) desc")
    List<User> findUserByExecutor(Executor executor);

    @Query("SELECT u FROM BuildTriage bt join User u on bt.triager = u where container_id=?1 group by u order by count(u) desc")
    List<User> findUserByContainer(Container container);

    List<BuildTriage> findTopByExecutorAndNumberLessThanAndEnabledOrderByNumberDesc(Executor executor, int number, boolean enabled);

    List<BuildTriage> findAllByExecutorAndNumberLessThanAndEnabledAndTriagedAndExpiredOrderByNumberDesc(Executor executor, int number, boolean enabled, boolean triaged, boolean expired);

    long countByTriagedAndExpiredAndEnabled(boolean triaged, boolean expired, boolean enabled);

    List<BuildTriage> findAllByTimestampBetween(long timestampFrom, long timestampTo);

    @Query("SELECT bt FROM BuildTriage bt, Container c where bt.container = c AND c.product=?1 AND bt.triaged = ?2")
    List<BuildTriage> findAllByProduct(Product product, boolean triaged);

    @Query("SELECT bt FROM BuildTriage bt WHERE bt.timestamp IN (SELECT MAX(b.timestamp) FROM BuildTriage b, Container c where b.container = c AND c.product=?1 AND b.triaged = ?2 AND b.timestamp > ?3 AND b.timestamp < ?4 GROUP BY b.executor)")
    List<BuildTriage> findAllByProduct(Product product, boolean triaged, Long timeStampFrom,  Long timeStampTo);

    List<BuildTriage> findAllByTriagerAndTriaged(User user, boolean triaged);

    @Query("SELECT bt FROM BuildTriage bt WHERE bt.timestamp IN (SELECT MAX(b.timestamp) FROM BuildTriage b WHERE b.triager = ?1 AND b.triaged = ?2 AND b.timestamp > ?3 AND b.timestamp < ?4 GROUP BY b.executor)")
    List<BuildTriage> findAllByTriagerAndTriagedAndTimestampBetween(User user, boolean triaged, Long timeStampFrom, Long timeStampTo);

    List<BuildTriage> findDistinctAllByContainerProductAndUpdatedGreaterThan(Product product, long updated);
}
