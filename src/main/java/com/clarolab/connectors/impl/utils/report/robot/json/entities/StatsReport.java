/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.connectors.impl.utils.report.robot.json.entities;

import lombok.Builder;
import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Builder
public class StatsReport {

    private List<StatsElementsReport> stat;

    public int getPassed(List<String> ids) {
        return this.getByIds(ids).stream().mapToInt(StatsElementsReport::getPass).sum();
    }

    public int getFailed(List<String> ids) {
        Optional<StatsElementsReport> report = this.getByIds(ids)
                .stream()
                .min(Comparator.comparing(StatsElementsReport::getFail));
        return report.map(StatsElementsReport::getFail).orElse(0);

    }

    private List<StatsElementsReport> getByIds(List<String> ids) {
        return stat.stream().filter(s -> ids.contains(s.getId())).collect(Collectors.toList());
    }


}
