/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller.impl;

import com.clarolab.controller.BaseController;
import com.clarolab.dto.BaseDTO;
import com.clarolab.dto.FilterDTO;
import com.clarolab.service.TTriageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

public abstract class BaseControllerImpl<T extends BaseDTO> implements BaseController<T> {

    abstract protected TTriageService<T> getService();

    @Override
    public ResponseEntity<T> save(@RequestBody T entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(getService().save(entity));
    }

    public ResponseEntity<Long> delete(@PathVariable Long id) {
        getService().disable(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(id);
    }

    public ResponseEntity<T> find(@PathVariable Long id) {
        return ResponseEntity.ok(getService().find(id));
    }

    @Override
    public ResponseEntity<T> update(@RequestBody T entity) {
        return ResponseEntity.ok(getService().update(entity));
    }

    @Override
    public ResponseEntity<Page<T>> list(@RequestParam(value = "query", required = false) String[] criteria, Pageable page) {
        return ResponseEntity.ok(getService().findAll(criteria, page));
    }

    protected FilterDTO getFilterDTO(String filter) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        filter = filter.replaceAll("&quot;","\"");
        return objectMapper.readValue(filter, FilterDTO.class);
    }


}