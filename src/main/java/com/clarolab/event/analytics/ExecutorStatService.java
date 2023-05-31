/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.event.analytics;

import com.clarolab.model.Container;
import com.clarolab.model.Executor;
import com.clarolab.repository.BaseRepository;
import com.clarolab.service.BaseService;
import com.clarolab.service.ProductService;
import com.clarolab.util.DateUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Log
public class ExecutorStatService extends BaseService<ExecutorStat> {

    @Autowired
    private ExecutorStatRepository executorStatRepository;

    @Autowired
    private ProductService productService;

    @Override
    public BaseRepository<ExecutorStat> getRepository() {
        return executorStatRepository;
    }

    public List<ExecutorStat> findAllBetweenGroupByProduct(long prev, long now) {
        return executorStatRepository.findAllBetweenGroupByProduct(prev, now);
    }

    public Map<Executor, List<ExecutorStat>> findAllByContainerBetween(Container container, long prev, long lastDate) {
        String from = DateUtils.covertToString(prev, null);
        String to = DateUtils.covertToString(lastDate, null);

        List<ExecutorStat> allStats = executorStatRepository.findAllByExecutorContainerAndActualDateGreaterThanEqualAndActualDateLessThanEqualOrderByExecutorAscIdDesc(container, from, to);

        Map<Executor, List<ExecutorStat>> answer = new HashMap<>();
        List<ExecutorStat> stats;

        for (Executor executor : container.getExecutors()) {
            if (executor.isHierarchicalyEnabled()) {
                stats = new ArrayList<>();
                for (ExecutorStat stat : allStats) {
                    if (executor == stat.getExecutor()) {
                        stats.add(stat);
                    }
                }
                answer.put(executor, stats);
            }
        }

        return answer;
    }

    public EvolutionStat getAgileIndex(List<ExecutorStat> stats) {
        if (stats.isEmpty()) {
            return null;
        }
        EvolutionStat answer = new EvolutionStat();
        ExecutorStat first = stats.get(0);

        answer.setExecutionDate(DateUtils.convertDate(first.getActualDate(), "yyyy-MM-dd"   ));
        answer.setExecutor(first.getExecutor());
        answer.setTotalTests(first.getTotalTests());

        if (stats.size() == 1) {
            answer.setGrowth(first);
            answer.setTriageDone(first);
            answer.setPassing(first);
            answer.setStability(first);

        } else {
            answer.setGrowth(stats);
            answer.setTriageDone(stats);
            answer.setPassing(stats);
            answer.setStability(stats);
            setCommits(stats, answer);
        }
        answer.initStabilityIndex();

        return answer;
    }

    public void setCommits(List<ExecutorStat> stats, EvolutionStat answer){
        List<ExecutorStat> recent = stats.subList(0, (int) (stats.size() / 2));
        List<ExecutorStat> old = stats.subList((int) (stats.size() / 2), stats.size());


        IntSummaryStatistics recentStats = recent.stream()
                .mapToInt(value -> productService.countCvsLogsByProductId(value.getProduct(), true))
                .summaryStatistics();
        IntSummaryStatistics oldStats = old.stream()
                .mapToInt(value -> productService.countCvsLogsByProductId(value.getProduct(), true))
                .summaryStatistics();

        answer.initCommits(recentStats.getAverage() - oldStats.getAverage());
    }

}
