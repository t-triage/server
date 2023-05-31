/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.jira.repository;


import com.clarolab.jira.model.JiraConfig;
import com.clarolab.model.Product;
import com.clarolab.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JiraConfigRepository extends BaseRepository<JiraConfig> {

    JiraConfig findFirstByProductId(Long productId);

    JiraConfig findByProductIdAndEnabledTrue(Long productId);

    JiraConfig findByProduct(Product product);

    JiraConfig findByProjectKey(String key);
}
