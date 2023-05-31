/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.robot.json.entities;

import lombok.Data;

import java.util.List;

@Data
public class StatisticsReport {

    private StatsReport total;
    private StatsReport suite;
//    private StatsReport tag;

    public int getPassed(List<String> suitesIds) {
        return suite.getPassed(suitesIds);
    }

    public int getFailed(List<String> suitesIds) {
        return suite.getFailed(suitesIds);
    }
}
