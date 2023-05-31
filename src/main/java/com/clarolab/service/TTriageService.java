/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.service.exception.ServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface TTriageService<T> {

    T save(T entry) throws ServiceException;

    T update(T entry) throws ServiceException;

    void delete(Long id) throws ServiceException;

    void disable(Long id) throws ServiceException;

    Page<T> findAll(@Nullable Specification spec, @NotNull Pageable pageable);

    Page<T> findAll(@Nullable String[] criteria, @NotNull Pageable pageable);

    List<T> findAll(@Nullable Specification spec, @Nullable Sort sort);

    List<T> findAll(@Nullable String[] criteria, @Nullable Sort sort);

    List<T> findAll();

    T find(Long id) throws ServiceException;

    long count(@Nullable Specification spec);

    long count();

    long countEnabled();

    long countDisabled();

}
