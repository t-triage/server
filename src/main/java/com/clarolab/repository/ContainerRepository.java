/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import com.clarolab.dto.ContainerItemDTO;
import com.clarolab.model.Container;
import com.clarolab.model.Product;
import com.clarolab.model.User;
import com.clarolab.model.types.PopulateMode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContainerRepository extends BaseRepository<Container> {

    List<Container> findAllByName(String name);

    Container findByName(String name);

    Container findByProductAndNameLike(Product product, String name);

    @Query("SELECT id FROM Container WHERE enabled = true ORDER BY product")
    List<Long> findAllContainerIds();

    @Query("SELECT c FROM TriageSpec s, Container c WHERE s.triager = ?1 AND c.enabled = true AND c.id = s.container AND s.lastCalculatedDeadline > ?2 ORDER BY s.priority, c.name")
    List<Container> findAllByUserFrom(User user, long timestamp);

    @Query("SELECT c FROM TriageSpec s, Container c WHERE s.triager = ?1 AND c.enabled = true AND c.id = s.container AND s.lastCalculatedDeadline <= ?2 ORDER BY s.priority, c.name")
    List<Container> findAllByUserUntil(User user, long timestamp);

    List<Container> findTop50ByEnabledOrderByName(boolean enabled);

    @Query("SELECT e.container FROM Executor e WHERE e.enabled = true AND e.id in ?1")
    List<Container> findAllByExecutorIn(List<Long> executors);

    @Query("SELECT new com.clarolab.dto.ContainerItemDTO(t.container, count(t.executor), max(s.triager)) FROM BuildTriage t, Container c, TriageSpec s WHERE c.id = t.container AND c.id = s.container AND t.enabled = true AND t.executor.enabled = true AND t.expired = false AND t.triaged=false AND s.executor = null GROUP BY t.container")
    List<ContainerItemDTO> findPendingContainers();

    @Query("SELECT distinct c FROM Container c, Build b WHERE b.enabled = true AND b.processed = false AND c.id = b.container")
    List<Container> findContainersPendingToProcess();

    @Query("SELECT c.name, c.id FROM Container c WHERE c.enabled = true AND c.product = ?1 ORDER BY c.name asc ")
    List<Object[]> findAllNames(Product product);

    @Query("SELECT c FROM TriageSpec s, Container c WHERE s.triager = ?1 AND c.enabled = true AND c.id = s.container ORDER BY s.priority, c.name")
    List<Container> findAllByUser(User user);

    @Query("SELECT c.id FROM Container c WHERE c.enabled = ?1 AND c.populateMode = ?2 ORDER BY c.updated DESC")
    List<Long> findContainersToPopulate(boolean enabled, PopulateMode mode);

    Container findFirstByEnabled(boolean enabled);

}
