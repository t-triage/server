/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service.filter;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class FilterSpecification<T> implements Specification<T> {


    private FilterCriteria criteria;

    public FilterCriteria getCriteria() {
        return criteria;
    }

    public FilterSpecification() {
    }

    public FilterSpecification(FilterCriteria filterCriteria) {
        this.criteria = filterCriteria;
    }

    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        switch (criteria.getOperation()) {
            case ">":
                return getGreaterOrEqual(root, builder);
            case "<":
                return getLessOrEqual(root, builder);
            case ":":
                return getEqual(root, builder);
            case "!=":
                return getNotEqual(root, builder);
            default:
                return null;
        }
    }

    private Predicate getNotEqual(Root<T> root, CriteriaBuilder builder) {
        return builder.not(getEqual(root, builder));
    }

    private Predicate getEqual(Root<T> root, CriteriaBuilder builder) {
        if (root.get(criteria.getKey()).getJavaType() == String.class) {
            return builder.like(root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
        } else {
            return builder.equal(root.get(criteria.getKey()), criteria.getValue());
        }
    }

    private Predicate getLessOrEqual(Root<T> root, CriteriaBuilder builder) {
        return builder.lessThanOrEqualTo(
                root.<String>get(criteria.getKey()), criteria.getValue().toString());
    }

    private Predicate getGreaterOrEqual(Root<T> root, CriteriaBuilder builder) {
        return builder.greaterThanOrEqualTo(
                root.<String>get(criteria.getKey()), criteria.getValue().toString());
    }

}
