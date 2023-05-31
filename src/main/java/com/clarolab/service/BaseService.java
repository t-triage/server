/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.config.properties.HibernateProperties;
import com.clarolab.model.Entry;
import com.clarolab.repository.BaseRepository;
import com.clarolab.service.exception.NotFoundServiceException;
import com.clarolab.service.exception.ServiceException;
import com.clarolab.util.DateUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.clarolab.util.SearchSpecificationUtil.getSearchSpec;


@Log
public abstract class BaseService<T extends Entry> implements TTriageService<T> {

    protected abstract BaseRepository<T> getRepository();

    private static final String[] criteria = {"enabled:true"};

    @Autowired
    private HibernateProperties hibernateProperties;

//    @Autowired
//    protected PropertyService propertyService;

    @Override
    public T save(T entry) throws ServiceException {
        T t = null;
        try {
            long now = DateUtils.now();
            entry.setUpdated(now);
            entry.setTimestamp(now);
            entry.setEnabled(true);
            t = getRepository().save(entry);
            log("CREATED: " + entry + " ID: " + t.getId());
        } catch (Exception e) {
            throw new NotFoundServiceException("Error creating", e);
        }
        return t;
    }

    @Override
    public T update(T entry) throws ServiceException {
        T t = null;
        try {
            entry.setUpdated(DateUtils.now());
            t = getRepository().save(entry);
            log("UPDATED: " + entry + " ID: " + t.getId());
        } catch (Exception e) {
            throw new NotFoundServiceException("Error updating", e);
        }
        return t;
    }

    @Override
    public void delete(Long id) throws ServiceException {
        try {
            getRepository().deleteById(id);
            log("DELETED: " + id);
        } catch (Exception e) {
            throw new NotFoundServiceException("Error deleting", e);
        }
    }

    @Override
    public void disable(Long id) throws ServiceException {
        try {
            T entity = find(id);
            entity.setEnabled(false);
            update(entity);
            log("DISABLED: " + id);
        } catch (Exception e) {
            throw new NotFoundServiceException("Error disabling", e);
        }
    }

    @Override
    public T find(Long id) throws ServiceException {
        T t;
        if (id == null || id == 0l) {
            return null;
        }
        try {
            t = getRepository().findById(id).orElse(null);
            log("FIND: " + (t == null ? "nothing" : t.toString()));
        } catch (Exception e) {
            throw new NotFoundServiceException("Error finding", e);
        }
        return t;
    }

    @Override
    public Page<T> findAll(@Nullable String[] criteria, @NotNull Pageable pageable) {
        return findAll(getSearchSpec(criteria), pageable);
    }

    @Override
    public Page<T> findAll(@Nullable Specification spec, Pageable pageable) {
        return getRepository().findAll(spec, pageable);
    }

    @Override
    public List<T> findAll(@Nullable String[] criteria, @Nullable Sort sort) {
        return findAll(getSearchSpec(criteria), sort);
    }

    @Override
    public List<T> findAll(@Nullable Specification spec, @Nullable Sort sort) {
        return getRepository().findAll(spec, sort);
    }

    @Override
    public List<T> findAll() {
        return findAll(criteria, Sort.unsorted()); // {"enabled:true"}; this method should be deprecated in the future. The service could receive the criteria.
    }

    @Override
    public long count(@Nullable Specification spec) {
        return getRepository().count(spec);
    }

    @Override
    public long count() {
        return getRepository().count();
    }

    @Override
    public long countEnabled(){
        return getRepository().countByEnabled(true);
    }

    @Override
    public long countDisabled(){
        return getRepository().countByEnabled(false);
    }

    private void log(String logText) {
        if (hibernateProperties == null) {
            System.out.println(logText);
        } else {
            if (hibernateProperties.getShowSql()) {
                log.info(logText);
            }
        }
    }
}

