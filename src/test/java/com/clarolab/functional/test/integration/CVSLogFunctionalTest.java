/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */
package com.clarolab.functional.test.integration;

import com.clarolab.dto.LogCommitsPerDayDTO;
import com.clarolab.dto.LogCommitsPerPersonDTO;
import com.clarolab.functional.BaseFunctionalTest;
import com.clarolab.model.CVSLog;
import com.clarolab.populate.UseCaseDataProvider;
import com.clarolab.service.LogService;
import com.clarolab.service.StatsService;
import com.clarolab.service.UserService;
import com.clarolab.util.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

public class CVSLogFunctionalTest extends BaseFunctionalTest {

    @Autowired
    private UseCaseDataProvider provider;

    @Autowired
    private LogService logService;

    @Autowired
    private StatsService statsService;

    @Autowired
    private UserService userService;

    @Before
    public void clearProvider() {
        provider.clear();
    }

    @Test
    public void getCommits() {
        long date = DateUtils.now();

        CVSLog entity = CVSLog.builder()
                .authorText("Author")
                .author(provider.getUser())
                .commitDate(date)
                .build();
        entity = logService.save(entity);
        entity = CVSLog.builder()
                .authorText("Author2")
                .author(provider.getUser())
                .commitDate(date)
                .build();
        entity = logService.save(entity);

        List<LogCommitsPerPersonDTO> logs = logService.getCommitsPerPerson();
        Assert.assertNotNull(logs);
        Assert.assertFalse(logs.isEmpty());
    }
    @Test
    public void getStats() {
        long date = DateUtils.now();

        CVSLog entity = CVSLog.builder()
                .authorText("Author")
                .author(provider.getUser())
                .commitDate(date)
                .build();
        entity = logService.save(entity);

        List<LogCommitsPerPersonDTO> logs = statsService.getCommitsPerPerson();

        Assert.assertNotNull(logs);
        Assert.assertFalse(logs.isEmpty());
    }

    @Test
    public void getCommitsPerDay() {
        long date = DateUtils.now();

        CVSLog entity = CVSLog.builder()
                .author(provider.getUser())
                .commitDate(date)
                .commitDay(DateUtils.covertToString(date, DateUtils.DATE_SMALL))
                .build();
        entity = logService.save(entity);

        date = DateUtils.offSetDays(-100);

        CVSLog entity2 = CVSLog.builder()
                .author(provider.getUser())
                .commitDate(date)
                .commitDay(DateUtils.covertToString(date, DateUtils.DATE_SMALL))
                .build();
        entity2 = logService.save(entity2);

        List<LogCommitsPerDayDTO> logs = logService.getCommitsPerDay();

        Assert.assertNotNull(logs);
        Assert.assertFalse(logs.isEmpty());
    }

}
