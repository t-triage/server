/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.unit.test;

import com.clarolab.model.helper.DeadlineData;
import com.clarolab.model.helper.WeekFrequency;
import com.clarolab.unit.BaseUnitTest;
import org.junit.Assert;
import org.junit.Test;

import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;

public class WeekFrequencyCalculationTest extends BaseUnitTest {

    private final static String TEST_CRON = "0 59 20 * * FRI"; //Every Friday at 23:59 (20 + 3 from utc)

    @Test
    public void testEveryWeekCalculation() {
        if (!isWeekend()) {
            DeadlineData input = DeadlineData.of(TEST_CRON, 1, 0, 0);
            DeadlineData ret = WeekFrequency.valueOf(1).getNextDeadline(input);

            Assert.assertNotNull(ret);

            LocalDateTime calculated = LocalDateTime.ofInstant(Instant.ofEpochMilli(ret.getLastDeadline()), ZoneOffset.UTC);
            LocalDateTime nextFriday = nextFriday();

            Assert.assertEquals(calculated.get(ChronoField.DAY_OF_YEAR), nextFriday.get(ChronoField.DAY_OF_YEAR));
            Assert.assertEquals(calculated.get(ChronoField.MONTH_OF_YEAR), nextFriday.get(ChronoField.MONTH_OF_YEAR));
            Assert.assertEquals(calculated.get(ChronoField.YEAR), nextFriday.get(ChronoField.YEAR));
        }
    }

    @Test
    public void testEveryTwoWeeksCalculation() {
        if (!isMonday()) {
            int weeks = 2;
            DeadlineData input = DeadlineData.of(TEST_CRON, weeks, 0, 0);
            DeadlineData ret = WeekFrequency.valueOf(weeks).getNextDeadline(input);

            Assert.assertNotNull(ret);

            LocalDateTime calculated = LocalDateTime.ofInstant(Instant.ofEpochMilli(ret.getLastDeadline()), ZoneOffset.UTC);
            LocalDateTime nextFriday = nextFriday(weeks + 1);

            Assert.assertEquals(calculated.get(ChronoField.DAY_OF_YEAR), nextFriday.get(ChronoField.DAY_OF_YEAR));
            Assert.assertEquals(calculated.get(ChronoField.MONTH_OF_YEAR), nextFriday.get(ChronoField.MONTH_OF_YEAR));
            Assert.assertEquals(calculated.get(ChronoField.YEAR), nextFriday.get(ChronoField.YEAR));
        }
    }

    @Test
    public void testEveryThreeWeeksCalculation() {
        if (!isMonday()) {
            int weeks = 3;
            DeadlineData input = DeadlineData.of(TEST_CRON, weeks, 0, 0);
            DeadlineData ret = WeekFrequency.valueOf(weeks).getNextDeadline(input);

            Assert.assertNotNull(ret);

            LocalDateTime calculated = LocalDateTime.ofInstant(Instant.ofEpochMilli(ret.getLastDeadline()), ZoneOffset.UTC);
            LocalDateTime nextFriday = nextFriday(weeks + 1);

            Assert.assertEquals(calculated.get(ChronoField.DAY_OF_YEAR), nextFriday.get(ChronoField.DAY_OF_YEAR));
            Assert.assertEquals(calculated.get(ChronoField.MONTH_OF_YEAR), nextFriday.get(ChronoField.MONTH_OF_YEAR));
            Assert.assertEquals(calculated.get(ChronoField.YEAR), nextFriday.get(ChronoField.YEAR));
        }
    }

    @Test
    public void testEveryFourWeeksCalculation() {
        if (!isMonday()) {
            int weeks = 4;
            DeadlineData input = DeadlineData.of(TEST_CRON, weeks, 0, 0);
            DeadlineData ret = WeekFrequency.valueOf(weeks).getNextDeadline(input);

            Assert.assertNotNull(ret);

            LocalDateTime calculated = LocalDateTime.ofInstant(Instant.ofEpochMilli(ret.getLastDeadline()), ZoneOffset.UTC);
            LocalDateTime nextFriday = nextFriday(weeks + 1);

            Assert.assertEquals(calculated.get(ChronoField.DAY_OF_YEAR), nextFriday.get(ChronoField.DAY_OF_YEAR));
            Assert.assertEquals(calculated.get(ChronoField.MONTH_OF_YEAR), nextFriday.get(ChronoField.MONTH_OF_YEAR));
            Assert.assertEquals(calculated.get(ChronoField.YEAR), nextFriday.get(ChronoField.YEAR));
        }
    }

    @Test
    public void testEveryWeekCalculationWithExistingPreCalculation() {
        if (!isWeekend()) {
            LocalDateTime nextFriday = nextFriday().plusDays(1);
            long fridayMilis = nextFriday.toInstant(ZoneOffset.UTC).toEpochMilli();
            int fridayWeek = LocalDateTime.ofInstant(Instant.ofEpochMilli(fridayMilis), ZoneOffset.UTC).get(ChronoField.ALIGNED_WEEK_OF_YEAR);

            DeadlineData input = DeadlineData.of(TEST_CRON, 1, fridayMilis, fridayWeek);
            DeadlineData ret = WeekFrequency.valueOf(1).getNextDeadline(input);

            Assert.assertNotNull(ret);

            //So next deadline should be in 7 days
            Assert.assertEquals(7, Period.between(nextFriday.toLocalDate(), LocalDateTime.ofInstant(Instant.ofEpochMilli(ret.getLastDeadline()), ZoneOffset.UTC).toLocalDate()).getDays() + 1); //We add 1 because "between" is exclusive.
        }
    }

    @Test
    public void testEveryTwoWeeksCalculationWithExistingPreCalculation() {

        if (!isWeekend()) {
            LocalDateTime nextFriday = nextFriday().plusDays(1);
            long fridayMilis = nextFriday.toInstant(ZoneOffset.UTC).toEpochMilli();
            int fridayWeek = LocalDateTime.ofInstant(Instant.ofEpochMilli(fridayMilis), ZoneOffset.UTC).get(ChronoField.ALIGNED_WEEK_OF_YEAR);

            DeadlineData input = DeadlineData.of(TEST_CRON, 2, fridayMilis, fridayWeek);
            DeadlineData ret = WeekFrequency.valueOf(2).getNextDeadline(input);

            Assert.assertNotNull(ret);

            //So next deadline should be in 14 days
            LocalDateTime time = nextFriday().plusDays(14);
            Period diff = Period.between(nextFriday.toLocalDate(), time.toLocalDate());
            Assert.assertEquals(String.format("Diff days dont match %s", time.toString()), 14, diff.getDays() + 1); //We add 1 because "between" is exclusive.
        }
    }


    @Test
    public void testEveryThreeWeeksCalculationWithExistingPreCalculation() {
        if (!isWeekend()) {
            LocalDateTime nextFriday = nextFriday().plusDays(1);
            long fridayMilis = nextFriday.toInstant(ZoneOffset.UTC).toEpochMilli();
            int fridayWeek = LocalDateTime.ofInstant(Instant.ofEpochMilli(fridayMilis), ZoneOffset.UTC).get(ChronoField.ALIGNED_WEEK_OF_YEAR);

            DeadlineData input = DeadlineData.of(TEST_CRON, 3, fridayMilis, fridayWeek);
            DeadlineData ret = WeekFrequency.valueOf(3).getNextDeadline(input);

            Assert.assertNotNull(ret);

            //So next deadline should be in 21 days
            Assert.assertEquals(21, Period.between(nextFriday.toLocalDate(), nextFriday().plusDays(21).toLocalDate()).getDays() + 1); //We add 1 because "between" is exclusive.
        }
    }

    @Test
    public void testEveryFourWeeksCalculationWithExistingPreCalculation() {
        if (!isWeekend()) {
            LocalDateTime nextFriday = nextFriday().plusDays(1);
            long fridayMilis = nextFriday.toInstant(ZoneOffset.UTC).toEpochMilli();
            int fridayWeek = LocalDateTime.ofInstant(Instant.ofEpochMilli(fridayMilis), ZoneOffset.UTC).get(ChronoField.ALIGNED_WEEK_OF_YEAR);

            DeadlineData input = DeadlineData.of(TEST_CRON, 4, fridayMilis, fridayWeek);
            DeadlineData ret = WeekFrequency.valueOf(4).getNextDeadline(input);

            Assert.assertNotNull(ret);

            //So next deadline should be in 28 days
            Period diff = Period.between(nextFriday.toLocalDate(), nextFriday().plusDays(28).toLocalDate());
            Assert.assertEquals(String.format("Difference dont match %s", diff.toString()), 28, diff.getDays()+1); //We add 1 because "between" is exclusive.
        }
    }

    private LocalDateTime nextFriday() {
        return nextFriday(1);
    }

    private LocalDateTime nextFriday(int weeks) {
        LocalDateTime time = LocalDateTime.now();

        int weekDay = time.get(ChronoField.DAY_OF_WEEK);
        if (weekDay >= 5) {
            // If it is friday or weekend, then it required less than a week
            weeks = weeks - 1;
        }

        for (int i = 0; i < weeks; i++) {
            time = time.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
        }
        return time;

    }

    private boolean isWeekend() {
        LocalDateTime time = LocalDateTime.now();
        return time.get(ChronoField.DAY_OF_WEEK) > 5;
    }

    private boolean isMonday() {
        LocalDateTime time = LocalDateTime.now();
        return time.get(ChronoField.DAY_OF_WEEK) == 1;
    }

}
