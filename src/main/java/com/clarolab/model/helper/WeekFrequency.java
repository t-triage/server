/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.helper;

import org.springframework.scheduling.support.CronSequenceGenerator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Weekly Deadline Frequency calculator, it encapsulates the logic to calculate the next deadline based on the weekly frequency
 * set on the TriageSpec, it uses a CRON expresion for weekly calculations and also depends on week interval and previous calculation
 * for getting the Two and Three weeks since CRON expresion it self cannot contain it.
 */
public enum WeekFrequency {

    EVERY_WEEK(1) {
        @Override
        public DeadlineData getNextDeadline(DeadlineData deadLineData) {
            CronSequenceGenerator generator = new CronSequenceGenerator(deadLineData.getCronExp());
            if (deadLineData.getLastDeadline() > 0) {
                Instant next = generator.next(new Date(deadLineData.getLastDeadline())).toInstant();
                return DeadlineData.of(next.toEpochMilli(), deadLineData.getLastWeek(), next.toEpochMilli());
            } else {
                Instant next = generator.next(new Date()).toInstant();
                int lastWeek = LocalDateTime.ofInstant(Instant.ofEpochMilli(next.toEpochMilli()), ZoneOffset.UTC).get(ChronoField.ALIGNED_WEEK_OF_YEAR);
                return DeadlineData.of(next.toEpochMilli(), lastWeek, next.toEpochMilli());
            }
        }
    },
    EVERY_TWO_WEEKS(2),
    EVERY_THREE_WEEKS(3),
    EVERY_FOUR_WEEKS(4);

    private final int weeks;

    WeekFrequency(int weeks) {
        this.weeks = weeks;
    }

    public int getWeeks() {
        return weeks;
    }

    public static WeekFrequency valueOf(int weeks) {
        for (WeekFrequency weekFrequency : WeekFrequency.values()) {
            if (weekFrequency.getWeeks() == weeks) {
                return weekFrequency;
            }
        }
        throw new IllegalArgumentException("Not support number of weeks.");
    }

    public DeadlineData getNextDeadline(DeadlineData deadlineData) {
        return this.defaultCalculation(deadlineData);
    }

    private DeadlineData defaultCalculation(DeadlineData deadlineData) {
        List<Long> times = new ArrayList<>();

        CronSequenceGenerator generator = new CronSequenceGenerator(deadlineData.getCronExp());

        long lastDeadline = deadlineData.getLastDeadline();
        int lastWeek = deadlineData.getLastWeek();
        while (times.isEmpty()) {
            if (lastDeadline > 0) {
                Instant next = generator.next(new Date(lastDeadline)).toInstant();
                int nextWeek = getWeekNumber(next);

                if (nextWeek == lastWeek) {
                    times.add(next.toEpochMilli());

                } else if ((nextWeek - lastWeek) >= this.getWeeks()) {
                    times.add(next.toEpochMilli());
                    // lastWeek = nextWeek;
                }
                lastDeadline = next.toEpochMilli();
            } else {
                Date nextDate = new Date();
                lastWeek = getWeekNumber(nextDate.getTime());
                nextDate = generator.next(nextDate);
                Instant next = nextDate.toInstant();
                lastDeadline = next.toEpochMilli();
                // lastWeek = getWeekNumber(lastDeadline);
                if (getWeeks() <= 1) {
                    times.add(next.toEpochMilli());
                }
            }
        }

        return DeadlineData.of(lastDeadline, lastWeek, lastDeadline);
    }

    public static int getWeekNumber(long timestamp) {
        return getWeekNumber(Instant.ofEpochMilli(timestamp));
    }

    public static int getWeekNumber(Instant time) {
        return LocalDateTime.ofInstant(time, ZoneOffset.UTC).get(ChronoField.ALIGNED_WEEK_OF_YEAR);
    }


}
