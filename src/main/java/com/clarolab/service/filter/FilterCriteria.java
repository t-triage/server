/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service.filter;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FilterCriteria {
    private String key;
    private String operation;
    private Object value;
    private boolean orPredicate;

    @Builder
    public FilterCriteria(String key, String operation, Object value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }


}

