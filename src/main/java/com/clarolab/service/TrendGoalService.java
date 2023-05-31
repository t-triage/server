/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.service;

import com.clarolab.model.TrendGoal;
import com.clarolab.repository.BaseRepository;
import com.clarolab.repository.TrendGoalRepository;
import com.clarolab.service.exception.ConfigurationError;
import com.clarolab.startup.LicenceValidator;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log
public class TrendGoalService extends BaseService<TrendGoal> {

    @Autowired
    private TrendGoalRepository trendGoalRepository;
    @Autowired
    private LicenceValidator licenseValidator;

    @Override
    public BaseRepository<TrendGoal> getRepository() {
        return trendGoalRepository;
    }

    @Override
    public TrendGoal save(TrendGoal trendGoal) {
        if (licenseValidator.validateLicenceType()) {
            log.info("License not valid...");
            throw new ConfigurationError("Unable to create Trend Goal, you have a free version of t-Triage.");
        }
        return super.save(trendGoal);
    }


}