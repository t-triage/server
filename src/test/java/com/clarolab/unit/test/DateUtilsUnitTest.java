/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.unit.test;

import com.clarolab.unit.BaseUnitTest;
import com.clarolab.util.DateUtils;
import org.junit.Assert;
import org.junit.Test;

public class DateUtilsUnitTest extends BaseUnitTest {

    @Test
    public void daysFromTodayForHsAgo() {
        long checkTime = DateUtils.now() - millsForHs(17l);

        long result = DateUtils.daysFromToday(checkTime);

        Assert.assertEquals(-1, result);
    }

    @Test
    public void daysFromTodayForHsAhead() {
        long checkTime = DateUtils.now() + millsForHs(17l);

        long result = DateUtils.daysFromToday(checkTime);

        Assert.assertEquals(1, result);
    }

    @Test
    public void daysFromTodayForIntraDayPlus() {
        long checkTime = DateUtils.now() + millsForHs(10l);

        long result = DateUtils.daysFromToday(checkTime);

        Assert.assertEquals(0, result);
    }

    @Test
    public void daysFromTodayForIntraDayLess() {
        long checkTime = DateUtils.now() - millsForHs(5l);

        long result = DateUtils.daysFromToday(checkTime);

        Assert.assertEquals(-1, result);
    }


    @Test
    public void daysFromToday2() {
        long checkTime = DateUtils.now() + millsForDay(2l);

        long result = DateUtils.daysFromToday(checkTime);

        Assert.assertEquals(2, result);
    }

    private long millsForDay(long days) {
        return days * 24l * 60l * 60l * 1000l;
    }

    private long millsForHs(long hs) {
        return hs * 60l * 60l * 1000l;
    }

}
