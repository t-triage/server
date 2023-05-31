/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.model.*;
import com.clarolab.model.helper.DeadlineData;
import com.clarolab.model.helper.WeekFrequency;
import com.clarolab.util.DateUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.NoSuchElementException;

@Log
@Service
public class TriageDeadlineService {

    @Autowired
    private TriageSpecService triageSpecService;

    @Autowired
    private BuildTriageService buildTriageService;

    public TriageDeadline computeTriageDeadline(BuildTriage buildTriage) {
        long previousDate = 0;
        TriageSpec triageSpec = buildTriage.getSpec();
        BuildTriage previousTriage = buildTriageService.getPreviousTriage(buildTriage);
        if (previousTriage != null) {
            previousDate = previousTriage.getDeadline();
        }
        return computeTriageDeadline(triageSpec, buildTriage, previousDate);
    }

    public TriageDeadline computeTriageDeadline(Pipeline pipeline) {
        long previousDate = 0;
        TriageSpec triageSpec = triageSpecService.getTriageSpec(pipeline);
        return computeTriageDeadline(triageSpec, pipeline, previousDate);
    }

    public TriageDeadline computeTriageDeadline(TriageSpec triageSpec, Entry buildTriageOrPipeline, long previousDeadline) {
        // We have to calculate the deadline based on:
        // 1 -> Product deadline -> This is a max date. (e.g release date)
        // 2 -> Triage Specification -> This a periodical date, this means it will vary depending on the upcoming date
        // and the rules set (e.g every monday)
        // While the Triage Specification date < Product date we will use it, until release date.

        if (triageSpec == null) {
            log.severe("Triage spec is null for build triage: " + buildTriageOrPipeline.getId());
            throw new NoSuchElementException("Triage spec must exists.");
        }

        Container container = triageSpec.getContainer();

        if (container == null) {
            log.severe("Container is null for build triage: " + buildTriageOrPipeline.getId());
            throw new NoSuchElementException("Container must exists");
        }

        Product product = container.getProduct();

        if (product == null) {
            log.severe("Container is null for build triage: " + buildTriageOrPipeline.getId());
            throw new NoSuchElementException("Container must exists");
        }

        if (triageSpec.getFrequencyCron() == null || triageSpec.getFrequencyCron().isEmpty()) {
            // This is the case the Executor has its own TriageSpec, but it doesn't have configured a new frequency
            // Therefore it looks at the container triageSpec
            triageSpec = triageSpecService.geTriageFlowSpecByContainer(container);

            if (triageSpec == null) {
                log.severe("Triage spec is null for container: " + container.getId());
                throw new NoSuchElementException("Triage spec must exists.");
            }

            if (triageSpec.getFrequencyCron() == null || triageSpec.getFrequencyCron().isEmpty()) {
                log.severe("Triage spec does not have configured frequency: " + triageSpec.getId());
                throw new NoSuchElementException("Triage spec must have frequency.");
            }
        }

        long now = DateUtils.now();
        Deadline upcommingDeadline = product.getDeadlines().stream()
                .filter(deadline -> deadline.getDeadlineDate() > now) //Filter old deadlines
                .min(Comparator.comparing(Deadline::getDeadlineDate)) //Get the closest upcoming deadline
                .orElse(null);

        TriageDeadline.TriageDeadlineBuilder triageDeadlineBuilder = TriageDeadline.builder();
        triageDeadlineBuilder.spec(triageSpec);

        long nextTriage = this.calculateNextDeadline(previousDeadline, triageSpec);

        if (upcommingDeadline == null) { //IF upcoming deadline is null -> triage spec must be set.
            triageDeadlineBuilder.deadline(nextTriage);
        } else {
            if (nextTriage < upcommingDeadline.getDeadlineDate()) {
                triageDeadlineBuilder.deadline(nextTriage);
            } else {
                triageDeadlineBuilder.deadline(upcommingDeadline.getDeadlineDate());
            }
        }

        return triageDeadlineBuilder.build();

    }

    private long calculateNextDeadline(long previousDeadline, TriageSpec triageSpec) {
        long now = DateUtils.now();

        if (previousDeadline > 0) {
            // If the previous deadline is still valid, the new triage will keep the deadline
            if (previousDeadline >= now) {
                return previousDeadline;
            }
        }
        long lastDeadline = triageSpec.getLastCalculatedDeadline();
        int lastWeek = triageSpec.getLastCalculatedWeek();

        if (lastDeadline >= now) {
            // The triage spec's already a valid deadline
            return lastDeadline;
        }

        DeadlineData deadlineData = DeadlineData.of(
                triageSpec.getFrequencyCron(),
                triageSpec.getEveryWeeks(),
                lastDeadline,
                lastWeek
        );

        deadlineData = WeekFrequency
                .valueOf(triageSpec.getEveryWeeks())
                .getNextDeadline(deadlineData);

        triageSpec.setLastCalculatedDeadline(deadlineData.getLastDeadline());
        triageSpec.setLastCalculatedWeek(deadlineData.getLastWeek());
        triageSpecService.update(triageSpec);
        return deadlineData.getLastDeadline();

    }

}
