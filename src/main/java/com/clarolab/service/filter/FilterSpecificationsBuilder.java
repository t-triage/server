/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service.filter;

import com.google.common.collect.Lists;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specification.where;

public class FilterSpecificationsBuilder<T> {


    private final List<FilterCriteria> params;

    public FilterSpecificationsBuilder() {
        params = Lists.newArrayList();
    }

    public FilterSpecificationsBuilder with(String key, String operation, Object value) {
        params.add(new FilterCriteria(key, operation, parseValue(value)));
        return this;
    }

    private Object parseValue(Object value) {
        String s = value.toString();
        if (s.equalsIgnoreCase("true") ||
        s.equalsIgnoreCase("false"))
            return Boolean.valueOf(s);

        return value;
    }

    public FilterSpecificationsBuilder clean(){
        params.clear();
        return this;
    }

    public Specification build() {
        if (params.size() == 0) {
            return null;
        }

        List<Specification> specs = params.stream()
                .map(FilterSpecification::new)
                .collect(Collectors.toList());

        Specification result = specs.get(0);

        for (int i = 0; i < params.size(); i++) {
            result = params.get(i)
                    .isOrPredicate()
                    ? where(result)
                    .or(specs.get(i))
                    : where(result)
                    .and(specs.get(i));
        }
        return result;
    }

}

