/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {

   // List<T> findAllByEnabled(@Nullable Specification spec, boolean enabled);

    long countByEnabled(boolean enable);

}
