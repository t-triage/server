/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.helper;

import lombok.ToString;

/**
 * Deadline payload object to hold calculations of following deadlines and prior deadlines.
 * It's used by WeekFrequency to data passage.
 */
@ToString
public class DeadlineData {

    //From Spec
    private String cronExp;
    private int everyWeeks;

    //From last calculation
    private long lastDeadline;
    private int lastWeek;

    //Return data
    private long nextTime;

    public static DeadlineData of(String cronExp, int everyWeeks, long lastDeadline, int lastWeek) {
        return new DeadlineData(cronExp, everyWeeks, lastDeadline, lastWeek, 0);
    }

    public static DeadlineData of(long lastDeadline, int lastWeek, long nextTime) {
        return new DeadlineData(null, 0, lastDeadline, lastWeek, nextTime);
    }

    private DeadlineData(String cronExp, int everyWeeks, long lastDeadline, int lastWeek, long nextTime) {
        this.cronExp = cronExp;
        this.everyWeeks = everyWeeks;
        this.lastDeadline = lastDeadline;
        this.lastWeek = lastWeek;
        this.nextTime = nextTime;
    }

    public String getCronExp() {
        return cronExp;
    }

    public int getEveryWeeks() {
        return everyWeeks;
    }

    public long getLastDeadline() {
        return lastDeadline;
    }

    public int getLastWeek() {
        return lastWeek;
    }

    public long getNextTime() {
        return nextTime;
    }

}
