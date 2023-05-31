/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import static com.clarolab.util.Constants.TABLE_PRODUCTIVITY;

@Entity
@Table(name = TABLE_PRODUCTIVITY, indexes = {
        @Index(name = "IDX_PRODUCTIVITY_TYPE", columnList = "item")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Productivity extends Entry {

    private String item;
    private long uniqueLoging;
    private long manualPull;
    private long autoPull;
    private long push;

    private long jobs;
    private long builds;
    private long connectors;
    private long containers;
    private long products;
    private long executors;

    @Builder
    private Productivity(Long id, boolean enabled, long updated, long timestamp, String item, long uniqueLoging, long manualPull, long autoPull, long push, long jobs, long builds, long connectors, long containers, long products, long executors) {
        super(id, enabled, updated, timestamp);
        this.item = item;
        this.uniqueLoging = uniqueLoging;
        this.manualPull = manualPull;
        this.autoPull = autoPull;
        this.push = push;
        this.jobs = jobs;
        this.builds = builds;
        this.connectors = connectors;
        this.containers = containers;
        this.products = products;
        this.executors = executors;
    }
}
